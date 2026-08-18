package com.studentos.config;

import com.studentos.util.InitDB;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Student OS Starting... Initializing Database.");
        try {
            InitDB.init();
            System.out.println("Database Initialized Successfully.");
        } catch (Exception e) {
            System.err.println("WARNING: Database failed to initialize on startup. This is common if the free tier DB is asleep.");
            e.printStackTrace();
            // Do NOT throw an exception here, otherwise Tomcat will abort the entire deployment!
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
