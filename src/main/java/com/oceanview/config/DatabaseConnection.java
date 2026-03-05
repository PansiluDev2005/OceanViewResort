package com.oceanview.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;

    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    private static String buildDbUrl() {
        String host = getEnv("MYSQLHOST", "mysql.railway.internal");
        String port = getEnv("MYSQLPORT", "3306");
        String dbName = getEnv("MYSQLDATABASE", "railway");
        // Railway's SQL proxy creates a default 'railway' db, but our script creates
        // 'ocean_view_resort'
        if ("railway".equals(dbName)) {
            dbName = "ocean_view_resort";
        }
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName;
    }

    private static final String URL = buildDbUrl();
    private static final String USER = getEnv("MYSQLUSER", "root");
    private static final String PASSWORD = getEnv("MYSQLPASSWORD", "ocGsnPQLTzPMmOQpJuXrCLSoLXxsLjYC");

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
