package com.mycompany.wandshop.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author lihac
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:wandshop1.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
