package com.mycompany.wandshop.view;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author lihac
 */
class IntegerInputVerifier extends InputVerifier {

    @Override
    public boolean verify(JComponent input) {
        JTextField field = (JTextField) input;
        try {
            int value = Integer.parseInt(field.getText());
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        boolean valid = verify(input);
        if (!valid) {
            JOptionPane.showMessageDialog(input,
                    "Пожалуйста, введите целое число ≥ 0",
                    "Некорректный ввод",
                    JOptionPane.WARNING_MESSAGE);
            input.requestFocus();
        }
        return valid;
    }
}
