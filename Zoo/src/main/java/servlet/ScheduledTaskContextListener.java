package servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ScheduledTaskContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ScheduledTaskServlet taskServlet = new ScheduledTaskServlet();
        taskServlet.contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ScheduledTaskServlet taskServlet = new ScheduledTaskServlet();
        taskServlet.contextDestroyed(sce);
    }
}
