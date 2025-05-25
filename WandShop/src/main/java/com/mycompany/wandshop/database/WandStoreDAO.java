package com.mycompany.wandshop.database;

import com.mycompany.wandshop.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

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
        }
        return list;
    }

    public void addWand(Wand wand) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            checkMaterialsAvailability(conn, wand.getWoodType(), wand.getCoreType());
            String sql = "INSERT INTO wands(price, status, wood_type, core_type) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            }
            decreaseMaterialQuantity(conn, wand.getWoodType(), "wood");
            decreaseMaterialQuantity(conn, wand.getCoreType(), "core");
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new RuntimeException("Ошибка при создании палочки: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }


     private void decreaseMaterialQuantity(Connection conn, String materialType, String materialTable) throws SQLException {
        String sql = "UPDATE " + materialTable + " SET quantity_in_stock = quantity_in_stock - 1 WHERE type = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, materialType);
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Материал не найден: " + materialType);
            }
        }
    }
    
    private void checkMaterialsAvailability(Connection conn, String woodType, String coreType) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT quantity_in_stock FROM wood WHERE type = ?")) {
            stmt.setString(1, woodType);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() || rs.getInt(1) < 1) {
                throw new SQLException("Недостаточно древесины типа: " + woodType);
            }
        }}

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
                }
            }
        } catch (SQLException e) {
        }
        return list;
    }
    
    public void addWoodSupply(WoodSupply woodSupply) {
        String sql = "INSERT INTO wood_supply(supply_id, wood_type, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, woodSupply.getSupplyId());
                pstmt.setString(2, woodSupply.getWoodType());
                pstmt.setInt(3, woodSupply.getQuantity());
                pstmt.executeUpdate();
            }
            String updateSql = "UPDATE wood SET quantity_in_stock = quantity_in_stock + ? WHERE type = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, woodSupply.getQuantity());
                pstmt.setString(2, woodSupply.getWoodType());
                int updated = pstmt.executeUpdate();
                if (updated == 0) {
                    String insertSql = "INSERT INTO wood(type, quantity_in_stock) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, woodSupply.getWoodType());
                        insertStmt.setInt(2, woodSupply.getQuantity());
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
        }
    }

    public void addCoreSupply(CoreSupply coreSupply) {
        String sql = "INSERT INTO core_supply(supply_id, core_type, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, coreSupply.getSupplyId());
                pstmt.setString(2, coreSupply.getCoreType());
                pstmt.setInt(3, coreSupply.getQuantity());
                pstmt.executeUpdate();
            }
            String updateSql = "UPDATE core SET quantity_in_stock = quantity_in_stock + ? WHERE type = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, coreSupply.getQuantity());
                pstmt.setString(2, coreSupply.getCoreType());
                int updated = pstmt.executeUpdate();
                if (updated == 0) {
                    String insertSql = "INSERT INTO core(type, quantity_in_stock) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, coreSupply.getCoreType());
                        insertStmt.setInt(2, coreSupply.getQuantity());
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
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
        }
        return list;
    }

    public void addPurchase(Purchase purchase) {
        try {
            Validator.validatePrice(purchase.getCost());
            String dateStr = purchase.getPurchaseDate().toString();
            Validator.validateDate(dateStr);

            String sql = "INSERT INTO purchases(customer_id, purchase_date, cost, wand_id) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, purchase.getCustomerId());
                pstmt.setString(2, dateStr);
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
            }
        } catch (IllegalArgumentException | DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
            throw e;
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
        }
        return list;
    }
}
