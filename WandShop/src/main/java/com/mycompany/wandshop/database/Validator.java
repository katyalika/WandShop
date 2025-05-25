package com.mycompany.wandshop.database;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 *
 * @author lihac
 */
public class Validator {

    public static void validatePrice(double price) throws IllegalArgumentException {
        if (price <= 0) {
            throw new IllegalArgumentException("Введите корректное число");
        }
    }

    public static LocalDate validateDate(String dateStr) throws DateTimeParseException {
        return LocalDate.parse(dateStr);
    }
}
