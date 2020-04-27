package edu.lwtech.csd299.honeypot;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.time.*;

import org.apache.log4j.*;
import freemarker.core.*;
import freemarker.template.*;



@WebServlet(name = "HoneypotServlet", urlPatterns = {"/"}, loadOnStartup = 0)
public class HoneypotServlet extends HttpServlet {

    private static final long serialVersionUID = 2020111122223333L;
    private static final Logger logger = Logger.getLogger(HoneypotServlet.class);

    private static final String TEMPLATE_DIR = "/WEB-INF/classes/templates";
    private static final Configuration freemarker = new Configuration(Configuration.getVersion());

    private static final boolean USE_SQL_DAO = true;  // Set to false if you want to test with the memory-based DAL

    private EventDAO eventDao = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        logger.warn("=========================================");
        logger.warn("  HoneypotServlet init() started");
        logger.warn("    http://localhost:8080");
        logger.warn("=========================================");

        logger.info("Getting real path for templateDir");
        String templateDir = config.getServletContext().getRealPath(TEMPLATE_DIR);
        logger.info("...real path is: " + templateDir);
        
        logger.info("Initializing Freemarker. templateDir = " + templateDir);
        try {
            freemarker.setDirectoryForTemplateLoading(new File(templateDir));
        } catch (IOException e) {
            logger.error("Template directory not found in directory: " + templateDir, e);
        }
        logger.info("Successfully Loaded Freemarker");
        
        //addEventData();
        if (USE_SQL_DAO){
            eventDao = new EventSqlDAO();
        }
        else{
            eventDao = new EventMemoryDAO();
        }
            

        if(eventDao.init() == false){
            throw new UnavailableException("unable to create database connection!!!");
        }

        logger.warn("Initialize complete!");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("IN - GET " + request.getRequestURI());

        long startTime = System.currentTimeMillis();

        String command = request.getParameter("cmd");
        if (command == null) command = "";

        String template = "";
        Map<String, Object> model = new HashMap<>();

        //TODO: Add more URL commands to the servlet
        switch (command) {

            case "show":

                template = "show.tpl";

                if(eventDao.getNumItems()==0) {
                    template = "showEmpty.tpl";
                }
                else {

                //getting a list of all events to pass to the template
                List<Event> eventList = eventDao.getAllItems();
                model.put("eventList", eventList);
                }
                break;

            case "clearEventList":
                
                eventDao.reset();
                template = "showEmpty.tpl";
                break;
        
            default:

                template = "honeypot.tpl";

                int id = eventDao.getNumItems();
                String ipAddress = request.getRemoteAddr(); 
                if(ipAddress==null) ipAddress = "ip address not found";
                String countryCode = request.getLocale().toString(); 
                String userAgent = request.getHeader("User-Agent");
                if(userAgent==null) userAgent = "User Agent not found";
                String time = LocalDateTime.now().toString();
                String uri = request.getRequestURI();
                String queryString = request.getQueryString();
                if(queryString==null) queryString = "not available";

                Event item = new Event(id, ipAddress, countryCode, userAgent, time, uri, queryString);

                addEventData(item);
      
                break;
        }
        processTemplate(response, template, model);

        long time = System.currentTimeMillis() - startTime;
        logger.info("OUT- GET " + request.getRequestURI() + " " + time + "ms");
    }

    //TODO: doPost() goes here - if needed.
    
    @Override
    public void destroy() {

        logger.info("Disconnecting from the database.");
        eventDao.disconnect();
        logger.info("Disconneced!");

        logger.warn("-----------------------------------------");
        logger.warn("  HoneypotServlet destroy() completed");
        logger.warn("-----------------------------------------");
    }

    @Override
    public String getServletInfo() {
        return "honeypottery Servlet";
    }

    // ========================================================================

    private void processTemplate(HttpServletResponse response, String template, Map<String, Object> model) {
        logger.debug("Processing Template: " + template);
        
        try (PrintWriter out = response.getWriter()) {
            Template view = freemarker.getTemplate(template);
            view.process(model, out);
            
        } catch (TemplateException | MalformedTemplateNameException | ParseException e) {
            logger.error("Template Error: ", e);
        } catch (IOException e) {
            logger.error("IO Error: ", e);
        } 
    }    

    private void addEventData(Event event) {
        logger.info("...inserting event");
        eventDao.insertItem(event);
    }

}
