package servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CloseLogTableContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Nothing to do on context initialization
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do on context destruction
    }

    public void run() {
        new CloseLogTableServlet().run();
    }
}
