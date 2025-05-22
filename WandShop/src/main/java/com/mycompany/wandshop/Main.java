package com.mycompany.wandshop;

import com.mycompany.wandshop.database.DatabaseInitializer;
import com.mycompany.wandshop.view.MainFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author lihac
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseInitializer.createTablesAndInsertInitialData();
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
