package com.mycompany.wandshop.view;

import com.mycompany.wandshop.database.WandStoreDAO;
import com.mycompany.wandshop.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.*;
/**
 *
 * @author lihac
 */
public class MainFrame extends JFrame {

    private WandStoreDAO dao = new WandStoreDAO();

    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JTextArea customersArea;

    private JTextField wandPriceField;
    private JComboBox<String> wandStatusBox;
    private JComboBox<String> wandWoodBox;
    private JComboBox<String> wandCoreBox;
    private JTextArea wandsArea;

    private JTextField supplySupplierField;
    private JTextField woodQuantityField;
    private JTextField coreQuantityField;
    private JComboBox<String> supplyWoodBox;
    private JComboBox<String> supplyCoreBox;

    private JTextArea purchasesArea;

    private JComboBox<Customer> customerComboBox;
    private JComboBox<Wand> availableWandsComboBox;
    private JTextField purchaseDateField;
    private JTextField purchaseCostField;

    private DefaultTableModel wandsTableModel;
    private JTable wandsTable;
    
    private DefaultTableModel suppliesTableModel;
    private JTable suppliesTable;
    
    private JTextArea suppliesArea;
    private JTextArea woodSuppliesArea;
    private JTextArea coreSuppliesArea;    

    public MainFrame() {
        setTitle("Магазин волшебных палочек");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel customersPanel = createCustomersPanel();
        tabbedPane.addTab("Покупатели", customersPanel);

        JPanel wandsPanel = createWandsPanel();
        tabbedPane.addTab("Палочки", wandsPanel);

        JPanel suppliesPanel = createSuppliesPanel();
        tabbedPane.addTab("Поставки", suppliesPanel);

        JPanel purchasesPanel = createPurchasesPanel();
        tabbedPane.addTab("Покупки", purchasesPanel);
        
        UIManager.put("TableHeader.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("TableHeader.background", new Color(70, 130, 180));
        UIManager.put("TableHeader.foreground", Color.WHITE);

        add(tabbedPane, BorderLayout.CENTER);
        JButton clearDbBtn = new JButton("Очистить базу данных");
        clearDbBtn.addActionListener(this::clearDatabase);
        add(clearDbBtn, BorderLayout.SOUTH);
        loadCustomers();
        loadWands();
        loadSuppliesData();
        loadWoodSupplies();
        loadCoreSupplies();
        loadPurchases();
        loadWoodTypes();
        loadCoreTypes();
        loadSupplyWoodTypes();
        loadSupplyCoreTypes();
        loadCustomersForPurchase();
        loadAvailableWands();
        refreshPurchaseData();
        initDefaultComboBoxes();
        purchaseDateField.setText(LocalDate.now().toString());
        
    }

    private JPanel createPurchasesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        purchaseDateField = new JTextField(10);
        purchaseDateField.setText(LocalDate.now().toString());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        customerComboBox = new JComboBox<>();
        availableWandsComboBox = new JComboBox<>();
        purchaseCostField = new JTextField(10);

        JButton addButton = new JButton("Добавить покупку");
        JButton refreshButton = new JButton("Обновить данные");

        addButton.addActionListener(e -> {
            addPurchase();
        });

        refreshButton.addActionListener(e -> {
            loadPurchases();
            loadCustomersForPurchase();
            loadAvailableWands();
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Покупатель:"), gbc);
        gbc.gridx = 1;
        formPanel.add(customerComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Палочка:"), gbc);
        gbc.gridx = 1;
        formPanel.add(availableWandsComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Дата (ГГГГ-ММ-ДД):"), gbc);
        gbc.gridx = 1;
        formPanel.add(purchaseDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Стоимость:"), gbc);
        gbc.gridx = 1;
        formPanel.add(purchaseCostField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        formPanel.add(buttonPanel, gbc);

        purchasesArea = new JTextArea(15, 60);
        purchasesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(purchasesArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("История покупок"));
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        loadPurchases();
        loadCustomersForPurchase();
        loadAvailableWands();

        return panel;
    }

    private void loadPurchases() {
        SwingUtilities.invokeLater(() -> {
            List<Purchase> purchases = dao.getAllPurchases();
            List<Customer> customers = dao.getAllCustomers();
            List<Wand> wands = dao.getAllWands();
            StringBuilder sb = new StringBuilder();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Purchase p : purchases) {
                String customerName = customers.stream()
                        .filter(c -> c.getId() == p.getCustomerId())
                        .findFirst()
                        .map(Customer::getName)
                        .orElse("Неизвестный");

                String wandInfo = wands.stream()
                        .filter(w -> w.getId() == p.getWandId())
                        .findFirst()
                        .map(w -> w.getWoodType() + "/" + w.getCoreType())
                        .orElse("Неизвестная");

                sb.append(String.format(
                        "ID: %d | Дата: %s\nПокупатель: %s\nПалочка: %s\nСтоимость: %.2f\n\n",
                        p.getId(),
                        p.getPurchaseDate().format(dateFormatter),
                        customerName,
                        wandInfo,
                        p.getCost()
                ));
            }
            purchasesArea.setText(sb.toString());
        });
    }

    private void addPurchase() {
        try {
            Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
            Wand selectedWand = (Wand) availableWandsComboBox.getSelectedItem();
            String dateText = purchaseDateField.getText().trim();
            String costText = purchaseCostField.getText().trim();

            if (selectedCustomer == null || selectedWand == null || dateText.isEmpty() || costText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены!");
                return;
            }

            LocalDate purchaseDate = LocalDate.parse(dateText);
            double cost = Double.parseDouble(costText);

            Purchase newPurchase = new Purchase(
                    selectedCustomer.getId(),
                    purchaseDate,
                    cost,
                    selectedWand.getId()
            );

            dao.addPurchase(newPurchase);
            JOptionPane.showMessageDialog(this, "Покупка успешно добавлена!");

            loadPurchases();
            loadAvailableWands();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Неверный формат даты. Используйте ГГГГ-ММ-ДД",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Неверный формат стоимости",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createSuppliesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        suppliesTableModel = new DefaultTableModel(
                new Object[]{"ID", "Дата", "Поставщик", "Тип материала", "Количество", "Тип материала", "Количество"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        suppliesTable = new JTable(suppliesTableModel);
        customizeTable(suppliesTable);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel addSupplyPanel = createAddSupplyFormPanel();
        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPanel.add(new JScrollPane(suppliesTable), BorderLayout.CENTER);
        tabbedPane.addTab("Добавить поставку", addSupplyPanel);
        tabbedPane.addTab("Просмотр поставок", viewPanel);
        panel.add(tabbedPane, BorderLayout.CENTER);
        loadSuppliesData();
        return panel;
    }

    private JPanel createAddSupplyFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        supplySupplierField = new JTextField(20);
        supplyWoodBox = new JComboBox<>(new String[]{"Дуб", "Ясень", "Сосна"});
        supplyCoreBox = new JComboBox<>(new String[]{"Феникс", "Дракон", "Единорог"});
        woodQuantityField = new JTextField(10);
        coreQuantityField = new JTextField(10);
        loadSupplyWoodTypes();
        loadSupplyCoreTypes();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Поставщик:"), gbc);
        gbc.gridx = 1;
        panel.add(supplySupplierField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Тип древесины:"), gbc);
        gbc.gridx = 1;
        panel.add(supplyWoodBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Количество:"), gbc);
        gbc.gridx = 1;
        panel.add(woodQuantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Тип сердцевины:"), gbc);
        gbc.gridx = 1;
        panel.add(supplyCoreBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Количество:"), gbc);
        gbc.gridx = 1;
        panel.add(coreQuantityField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Добавить поставку");
        addButton.addActionListener(e -> addSupply());

        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> loadSuppliesData());

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void customizeTable(JTable table) {
        table.setRowHeight(25);
        table.setAutoCreateRowSorter(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
    }
    private void addSupply() {
        try {
            String supplier = supplySupplierField.getText().trim();
            String woodType = (String) supplyWoodBox.getSelectedItem();
            int woodQty = Integer.parseInt(woodQuantityField.getText().trim());
            String coreType = (String) supplyCoreBox.getSelectedItem();
            int coreQty = Integer.parseInt(coreQuantityField.getText().trim());

            if (supplier.isEmpty()) {
                throw new IllegalArgumentException("Укажите поставщика");
            }
            Supply supply = new Supply(LocalDate.now(), supplier);
            int supplyId = dao.addSupply(supply);

            if (supplyId == -1) {
                throw new RuntimeException("Не удалось сохранить поставку");
            }
            dao.addWoodSupply(new WoodSupply(supplyId, woodType, woodQty));
            dao.addCoreSupply(new CoreSupply(supplyId, coreType, coreQty));
            supplySupplierField.setText("");
            woodQuantityField.setText("");
            coreQuantityField.setText("");
            loadSuppliesData();

            JOptionPane.showMessageDialog(this, "Поставка успешно добавлена!");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Некорректное количество",
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void loadSuppliesData() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Supply> supplies = dao.getAllSupplies();
                List<WoodSupply> woodSupplies = dao.getAllWoodSupplies();
                List<CoreSupply> coreSupplies = dao.getAllCoreSupplies();
                suppliesTableModel.setRowCount(0);
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                for (Supply supply : supplies) {
                    List<WoodSupply> woods = woodSupplies.stream()
                            .filter(ws -> ws.getSupplyId() == supply.getId())
                            .collect(Collectors.toList());

                    List<CoreSupply> cores = coreSupplies.stream()
                            .filter(cs -> cs.getSupplyId() == supply.getId())
                            .collect(Collectors.toList());
                    int rows = Math.max(woods.size(), cores.size());

                    for (int i = 0; i < rows; i++) {
                        Object[] row = new Object[7];
                        if (i == 0) {
                            row[0] = supply.getId();
                            row[1] = supply.getDate() != null
                                    ? supply.getDate().format(dateFormat)
                                    : "нет даты";
                            row[2] = supply.getSupplier();
                        }
                        if (i < woods.size()) {
                            WoodSupply ws = woods.get(i);
                            row[3] = ws.getWoodType();
                            row[4] = ws.getQuantity();
                        }
                        if (i < cores.size()) {
                            CoreSupply cs = cores.get(i);
                            row[5] = cs.getCoreType();
                            row[6] = cs.getQuantity();
                        }
                        suppliesTableModel.addRow(row);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка загрузки поставок: " + e.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private void loadWoodSupplies() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<WoodSupply> woodSupplies = dao.getAllWoodSupplies();
                StringBuilder sb = new StringBuilder();
                for (WoodSupply ws : woodSupplies) {
                    sb.append("ID поставки: ").append(ws.getSupplyId())
                            .append(", Вид древесины: ").append(ws.getWoodType())
                            .append(", Количество: ").append(ws.getQuantity())
                            .append("\n");
                }
                woodSuppliesArea.setText(sb.toString());
            } catch (Exception e) {
                woodSuppliesArea.setText("Ошибка при загрузке данных о поставках древесины");
                e.printStackTrace();
            }
        });
    }

    private void loadCoreSupplies() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<CoreSupply> coreSupplies = dao.getAllCoreSupplies();
                StringBuilder sb = new StringBuilder();
                for (CoreSupply cs : coreSupplies) {
                    sb.append("ID поставки: ").append(cs.getSupplyId())
                            .append(", Вид сердцевины: ").append(cs.getCoreType())
                            .append(", Количество: ").append(cs.getQuantity())
                            .append("\n");
                }
                coreSuppliesArea.setText(sb.toString());
            } catch (Exception e) {
                coreSuppliesArea.setText("Ошибка при загрузке данных о поставках сердцевины");
                e.printStackTrace();
            }
        });
    }

    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        customerNameField = new JTextField();
        customerPhoneField = new JTextField();
        inputPanel.add(new JLabel("Имя:"));
        inputPanel.add(customerNameField);
        inputPanel.add(new JLabel("Телефон:"));
        inputPanel.add(customerPhoneField);
        JButton addCustomerBtn = new JButton("Добавить покупателя");
        addCustomerBtn.addActionListener(e -> {
            addCustomer();
        });
        inputPanel.add(addCustomerBtn);
        JButton loadCustomersBtn = new JButton("Загрузить покупателей");
        loadCustomersBtn.addActionListener(e -> {
            loadCustomers();
        });
        inputPanel.add(loadCustomersBtn);
        panel.add(inputPanel, BorderLayout.NORTH);
        customersArea = new JTextArea();
        customersArea.setEditable(false);
        panel.add(new JScrollPane(customersArea), BorderLayout.CENTER);
        return panel;
    }

    private void addCustomer() {
        String name = customerNameField.getText().trim();
        String phone = customerPhoneField.getText().trim();
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, заполните все поля.");
            return;
        }
        Customer customer = new Customer(name, phone);
        dao.addCustomer(customer);
        JOptionPane.showMessageDialog(this, "Покупатель добавлен: " + customer);
        customerNameField.setText("");
        customerPhoneField.setText("");
        loadCustomers();
        loadCustomersForPurchase();
    }

    private void loadCustomers() {
        List<Customer> customers = dao.getAllCustomers();
        StringBuilder sb = new StringBuilder();
        for (Customer c : customers) {
            sb.append(c).append("\n");
        }
        customersArea.setText(sb.toString());
    }

    private JPanel createWandsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        wandPriceField = new JTextField(10);
        wandStatusBox = new JComboBox<>(new String[]{"На складе", "Продана"});
        wandWoodBox = new JComboBox<>();
        wandCoreBox = new JComboBox<>();

        loadWoodTypes();
        loadCoreTypes();

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Цена:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(wandPriceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Статус:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(wandStatusBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Древесина:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(wandWoodBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Сердцевина:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(wandCoreBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton addButton = new JButton("Добавить палочку");
        addButton.addActionListener(e -> addWand());
        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> loadWands());
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        inputPanel.add(buttonPanel, gbc);

        String[] columnNames = {"ID", "Цена", "Статус", "Древесина", "Сердцевина", "Наличие"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Double.class : String.class;
            }
        };

        JTable wandsTable = new JTable(model);
        wandsTable.setAutoCreateRowSorter(true);
        wandsTable.setRowHeight(25);
        wandsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        wandsTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setText(String.format("%.2f", value));
                setHorizontalAlignment(SwingConstants.RIGHT);
                return this;
            }
        });

        wandsTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("На складе".equals(value)) {
                    setForeground(new Color(0, 100, 0)); 
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(Color.RED);
                }
                return this;
            }
        });

        JTableHeader header = wandsTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(wandsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Список волшебных палочек"));
        this.wandsTableModel = model;
        this.wandsTable = wandsTable;
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadWands();

        return panel;
    }

    private void addWand() {
        String priceText = wandPriceField.getText().trim();
        String status = (String) wandStatusBox.getSelectedItem();
        String woodType = (String) wandWoodBox.getSelectedItem();
        String coreType = (String) wandCoreBox.getSelectedItem();
        if (priceText.isEmpty() || woodType == null || coreType == null) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, заполните все поля.");
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат цены.");
            return;
        }
        Wand wand = new Wand(price, status, woodType, coreType);
        dao.addWand(wand);
        JOptionPane.showMessageDialog(this, "Палочка добавлена: " + wand);
        wandPriceField.setText("");
        loadWands();
        loadAvailableWands();
    }

    private void loadWands() {
        SwingUtilities.invokeLater(() -> {
            List<Wand> wands = dao.getAllWands();
            wandsTableModel.setRowCount(0);

            for (Wand wand : wands) {
                wandsTableModel.addRow(new Object[]{
                    wand.getId(),
                    wand.getPrice(),
                    wand.getStatus(),
                    wand.getWoodType(),
                    wand.getCoreType(),
                    "На складе".equals(wand.getStatus()) ? "В наличии" : "Продана"
                });
            }
        });
    }

    private void loadWoodTypes() {
        SwingUtilities.invokeLater(() -> {
            String selected = (String) wandWoodBox.getSelectedItem();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("Дуб");
            model.addElement("Ясень");
            model.addElement("Сосна");
            List<Wood> woods = dao.getAllWoods();
            for (Wood wood : woods) {
                if (!modelContains(model, wood.getType())) {
                    model.addElement(wood.getType());
                }
            }

            wandWoodBox.setModel(model);
            if (selected != null && modelContains(model, selected)) {
                wandWoodBox.setSelectedItem(selected);
            }
        });
    }

    private void loadCoreTypes() {
        SwingUtilities.invokeLater(() -> {
            String selected = (String) wandCoreBox.getSelectedItem();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("Феникс");
            model.addElement("Дракон");
            model.addElement("Единорог");
            List<Core> cores = dao.getAllCores();
            for (Core core : cores) {
                if (!modelContains(model, core.getType())) {
                    model.addElement(core.getType());
                }
            }
            wandCoreBox.setModel(model);
            if (selected != null && modelContains(model, selected)) {
                wandCoreBox.setSelectedItem(selected);
            }
        });
    }

    private boolean modelContains(DefaultComboBoxModel<String> model, String value) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void loadCustomersForPurchase() {
        SwingUtilities.invokeLater(() -> {
            customerComboBox.removeAllItems();
            List<Customer> customers = dao.getAllCustomers();
            for (Customer c : customers) {
                customerComboBox.addItem(c);
            }
        });
    }

    private void loadAvailableWands() {
        SwingUtilities.invokeLater(() -> {
            availableWandsComboBox.removeAllItems();
            List<Wand> wands = dao.getAllWands();
            for (Wand w : wands) {
                if ("На складе".equals(w.getStatus())) {
                    availableWandsComboBox.addItem(w);
                }
            }
        });
    }

    private void clearDatabase(ActionEvent e) {
        int confirmed = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите очистить базу данных?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            dao.clearAllData();
            JOptionPane.showMessageDialog(this, "База данных очищена.");
            customersArea.setText("");
            wandsArea.setText("");
            suppliesArea.setText("");
            woodSuppliesArea.setText("");
            coreSuppliesArea.setText("");
            purchasesArea.setText("");

            loadCustomers();
            loadWands();
            loadSuppliesData();
            loadWoodSupplies();
            loadCoreSupplies();
            loadPurchases();
            loadWoodTypes();
            loadCoreTypes();
            loadSupplyWoodTypes();
            loadSupplyCoreTypes();
            loadCustomersForPurchase();
            loadAvailableWands();
        }
    }

    private void loadSupplyWoodTypes() {
        SwingUtilities.invokeLater(() -> {
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) supplyWoodBox.getModel();
            if (model.getSize() == 0) {
                for (String wood : new String[]{"Дуб", "Ясень", "Сосна"}) {
                    model.addElement(wood);
                }
            }
            List<Wood> woods = dao.getAllWoods();
            for (Wood w : woods) {
                if (model.getIndexOf(w.getType()) == -1) {
                    model.addElement(w.getType());
                }
            }
        });
    }

    private void loadSupplyCoreTypes() {
        SwingUtilities.invokeLater(() -> {
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) supplyCoreBox.getModel();
            if (model.getSize() == 0) {
                for (String core : new String[]{"Феникс", "Дракон", "Единорог"}) {
                    model.addElement(core);
                }
            }
            List<Core> cores = dao.getAllCores();
            for (Core c : cores) {
                if (model.getIndexOf(c.getType()) == -1) {
                    model.addElement(c.getType());
                }
            }
        });
    }

    private void refreshPurchaseData() {
        loadPurchases();
        loadCustomersForPurchase();
        loadAvailableWands();
        loadWands();
    }
    
    private void initDefaultComboBoxes() {
        String[] defaultWoodTypes = {"Дуб", "Ясень", "Сосна"};
        supplyWoodBox.setModel(new DefaultComboBoxModel<>(defaultWoodTypes));
        String[] defaultCoreTypes = {"Феникс", "Дракон", "Единорог"};
        supplyCoreBox.setModel(new DefaultComboBoxModel<>(defaultCoreTypes));
    }
}
