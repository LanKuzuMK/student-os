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
        InitDB.init();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
