package servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import db.DBConnection;
import db.DBConnectionFactory;
import db.mysql.MySQLConnection;

public class CloseLogTableServlet implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Nothing to do on context initialization
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do on context destruction
    }

    public void run() {
        DBConnection conn = DBConnectionFactory.getDBConnection();
        conn.closeLogTable();
    }
}
