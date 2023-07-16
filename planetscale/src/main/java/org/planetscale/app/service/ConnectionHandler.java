package org.planetscale.app.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class ConnectionHandler {


    private final String className;
    private final String url;
    private final String user;
    private final String password;

    public ConnectionHandler(Map<String, String> config) {

        this.className = config.get("driverClassName");
        this.url = config.get("url");
        this.user = config.get("user");
        this.password = config.get("password");

        loadDriver();
    }

    private void loadDriver() {
        try {
            Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Connection openConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
