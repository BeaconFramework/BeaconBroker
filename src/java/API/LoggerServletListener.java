/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package API;


import java.io.File;
 
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


 
import org.apache.log4j.PropertyConfigurator;
 
/**
 * Web application lifecycle listener.
 *
 * @author giusimone
 */
public class LoggerServletListener implements ServletContextListener {

    
    /**
     * Initialize log4j when the application is being started
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        // initialize log4j here
        ServletContext context = event.getServletContext();
        String log4jConfigFile = context.getInitParameter("WEB-INF/logger.properties");
        String fullPath = context.getRealPath("") + File.separator + "WEB-INF/logger.properties";
        //System.out.println(context.getRealPath(""));
        //System.out.println(log4jConfigFile);
        PropertyConfigurator.configure(fullPath);
         
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
         //To change body of generated methods, choose Tools | Templates.
    }
  
}
