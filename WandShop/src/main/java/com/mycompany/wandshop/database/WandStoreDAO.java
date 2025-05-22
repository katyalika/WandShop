package com.mycompany.wandshop.database;

import com.mycompany.wandshop.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lihac
 */
public class WandStoreDAO {

    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO customers(name, phone) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    customer.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT id, name, phone FROM customers";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Customer(rs.getInt("id"), rs.getString("name"), rs.getString("phone")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addWand(Wand wand) {
        String sql = "INSERT INTO wands(price, status, wood_type, core_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, wand.getPrice());
            pstmt.setString(2, wand.getStatus());
            pstmt.setString(3, wand.getWoodType());
            pstmt.setString(4, wand.getCoreType());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    wand.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Wand> getAllWands() {
        List<Wand> list = new ArrayList<>();
        String sql = "SELECT id, price, status, wood_type, core_type FROM wands";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Wand w = new Wand(
                        rs.getInt("id"),
                        rs.getDouble("price"),
                        rs.getString("status"),
                        rs.getString("wood_type"),
                        rs.getString("core_type"));
                list.add(w);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addWood(Wood wood) {
        String sql = "INSERT OR IGNORE INTO wood(type, quantity_in_stock) VALUES (?, ?)";
        String sqlUpdate = "UPDATE wood SET quantity_in_stock = quantity_in_stock + ? WHERE type = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, wood.getType());
                pstmt.setInt(2, wood.getQuantityInStock());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                pstmt.setInt(1, wood.getQuantityInStock());
                pstmt.setString(2, wood.getType());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Wood> getAllWoods() {
        List<Wood> list = new ArrayList<>();
        String sql = "SELECT type, quantity_in_stock FROM wood";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Wood(rs.getString("type"), rs.getInt("quantity_in_stock")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Wood getWoodByType(String type) {
        String sql = "SELECT type, quantity_in_stock FROM wood WHERE type = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Wood(rs.getString("type"), rs.getInt("quantity_in_stock"));
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }

    public void addCore(Core core) {
        String sql = "INSERT OR IGNORE INTO core(type, quantity_in_stock) VALUES (?, ?)";
        String sqlUpdate = "UPDATE core SET quantity_in_stock = quantity_in_stock + ? WHERE type = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, core.getType());
                pstmt.setInt(2, core.getQuantityInStock());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                pstmt.setInt(1, core.getQuantityInStock());
                pstmt.setString(2, core.getType());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Core> getAllCores() {
        List<Core> list = new ArrayList<>();
        String sql = "SELECT type, quantity_in_stock FROM core";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Core(rs.getString("type"), rs.getInt("quantity_in_stock")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Core getCoreByType(String type) {
        String sql = "SELECT type, quantity_in_stock FROM core WHERE type = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Core(rs.getString("type"), rs.getInt("quantity_in_stock"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addSupply(Supply supply) {
        String sql = "INSERT INTO supply(date, supplier) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, supply.getDate() != null
                    ? supply.getDate().toString()
                    : null);

            pstmt.setString(2, supply.getSupplier());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    supply.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<Supply> getAllSupplies() {
        List<Supply> list = new ArrayList<>();
        String sql = "SELECT id, date, supplier FROM supply";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                try {
                    String dateStr = rs.getString("date");
                    LocalDate date = null;

                    if (dateStr != null && !dateStr.isEmpty()) {
                        try {
                            date = LocalDate.parse(dateStr);
                        } catch (DateTimeParseException e1) {
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                date = LocalDate.parse(dateStr, formatter);
                            } catch (DateTimeParseException e2) {
                                System.err.println("Не удалось распознать дату: " + dateStr);
                            }
                        }
                    }
                    list.add(new Supply(
                            rs.getInt("id"),
                            date,
                            rs.getString("supplier")
                    ));
                } catch (Exception e) {
                    System.err.println("Ошибка обработки поставки: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void addWoodSupply(WoodSupply woodSupply) {
        String sql = "INSERT INTO wood_supply(supply_id, wood_type, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, woodSupply.getSupplyId());
            pstmt.setString(2, woodSupply.getWoodType());
            pstmt.setInt(3, woodSupply.getQuantity());
            pstmt.executeUpdate();

            Wood wood = getWoodByType(woodSupply.getWoodType());
            if (wood != null) {
                String updateSql = "UPDATE wood SET quantity_in_stock = ? WHERE type = ?";
                try (PreparedStatement upstmt = conn.prepareStatement(updateSql)) {
                    upstmt.setInt(1, wood.getQuantityInStock() + woodSupply.getQuantity());
                    upstmt.setString(2, wood.getType());
                    upstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCoreSupply(CoreSupply coreSupply) {
        String sql = "INSERT INTO core_supply(supply_id, core_type, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, coreSupply.getSupplyId());
            pstmt.setString(2, coreSupply.getCoreType());
            pstmt.setInt(3, coreSupply.getQuantity());
            pstmt.executeUpdate();

            Core core = getCoreByType(coreSupply.getCoreType());
            if (core != null) {
                String updateSql = "UPDATE core SET quantity_in_stock = ? WHERE type = ?";
                try (PreparedStatement upstmt = conn.prepareStatement(updateSql)) {
                    upstmt.setInt(1, core.getQuantityInStock() + coreSupply.getQuantity());
                    upstmt.setString(2, core.getType());
                    upstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearAllData() {
        String[] tables = {
            "purchases",
            "wands",
            "customers",
            "wood_supply",
            "core_supply",
            "wood",
            "core",
            "supply"
        };
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            for (String table : tables) {
                stmt.execute("DELETE FROM " + table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<WoodSupply> getAllWoodSupplies() {
        List<WoodSupply> list = new ArrayList<>();
        String sql = "SELECT supply_id, wood_type, quantity FROM wood_supply";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new WoodSupply(
                        rs.getInt("supply_id"),
                        rs.getString("wood_type"),
                        rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CoreSupply> getAllCoreSupplies() {
        List<CoreSupply> list = new ArrayList<>();
        String sql = "SELECT supply_id, core_type, quantity FROM core_supply";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new CoreSupply(
                        rs.getInt("supply_id"),
                        rs.getString("core_type"),
                        rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addPurchase(Purchase purchase) {
        String sql = "INSERT INTO purchases(customer_id, purchase_date, cost, wand_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, purchase.getCustomerId());
            pstmt.setString(2, purchase.getPurchaseDate().toString()); 
            pstmt.setDouble(3, purchase.getCost());
            pstmt.setInt(4, purchase.getWandId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    purchase.setId(rs.getInt(1));
                }
            }
            String updateWandSql = "UPDATE wands SET status = 'Продана' WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateWandSql)) {
                updateStmt.setInt(1, purchase.getWandId());
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Purchase> getAllPurchases() {
        List<Purchase> list = new ArrayList<>();
        String sql = "SELECT id, customer_id, purchase_date, cost, wand_id FROM purchases";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LocalDate purchaseDate = LocalDate.parse(rs.getString("purchase_date"));
                list.add(new Purchase(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        purchaseDate,
                        rs.getDouble("cost"),
                        rs.getInt("wand_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
