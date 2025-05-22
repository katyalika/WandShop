package com.mycompany.wandshop.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author lihac
 */
public class DatabaseInitializer {

    public static void createTablesAndInsertInitialData() {
        String sqlCustomers = "CREATE TABLE IF NOT EXISTS customers ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "phone TEXT NOT NULL"
                + ");";

        String sqlWands = "CREATE TABLE IF NOT EXISTS wands ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "price REAL NOT NULL,"
                + "status TEXT NOT NULL,"
                + "wood_type TEXT NOT NULL,"
                + "core_type TEXT NOT NULL"
                + ");";

        String sqlPurchases = "CREATE TABLE IF NOT EXISTS purchases ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "customer_id INTEGER,"
                + "purchase_date TEXT NOT NULL,"
                + "cost REAL NOT NULL,"
                + "wand_id INTEGER,"
                + "FOREIGN KEY(customer_id) REFERENCES customers(id),"
                + "FOREIGN KEY(wand_id) REFERENCES wands(id)"
                + ");";

        String sqlWood = "CREATE TABLE IF NOT EXISTS wood ("
                + "type TEXT PRIMARY KEY,"
                + "quantity_in_stock INTEGER NOT NULL"
                + ");";

        String sqlCore = "CREATE TABLE IF NOT EXISTS core ("
                + "type TEXT PRIMARY KEY,"
                + "quantity_in_stock INTEGER NOT NULL"
                + ");";

        String sqlSupply = "CREATE TABLE IF NOT EXISTS supply ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "date TEXT NOT NULL,"
                + "supplier TEXT NOT NULL"
                + ");";

        String sqlWoodSupply = "CREATE TABLE IF NOT EXISTS wood_supply ("
                + "supply_id INTEGER,"
                + "wood_type TEXT,"
                + "quantity INTEGER NOT NULL,"
                + "PRIMARY KEY(supply_id, wood_type),"
                + "FOREIGN KEY(supply_id) REFERENCES supply(id),"
                + "FOREIGN KEY(wood_type) REFERENCES wood(type)"
                + ");";

        String sqlCoreSupply = "CREATE TABLE IF NOT EXISTS core_supply ("
                + "supply_id INTEGER,"
                + "core_type TEXT,"
                + "quantity INTEGER NOT NULL,"
                + "PRIMARY KEY(supply_id, core_type),"
                + "FOREIGN KEY(supply_id) REFERENCES supply(id),"
                + "FOREIGN KEY(core_type) REFERENCES core(type)"
                + ");";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCustomers);
            stmt.execute(sqlWands);
            stmt.execute(sqlPurchases);
            stmt.execute(sqlWood);
            stmt.execute(sqlCore);
            stmt.execute(sqlSupply);
            stmt.execute(sqlWoodSupply);
            stmt.execute(sqlCoreSupply);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
