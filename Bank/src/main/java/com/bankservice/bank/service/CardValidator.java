package com.bankservice.bank.service;

import java.time.LocalDate;

public class CardValidator {

    public static boolean validateLuhn(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        cardNumber = cardNumber.replaceAll("\\s+", "");
        if (!cardNumber.matches("\\d+")) {
            return false;
        }
        if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public static boolean validateExpiryDate(String expiryDate) {
        if (expiryDate == null || expiryDate.isEmpty()) {
            return false;
        }
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        if (month < 1 || month > 12) {
            return false;
        }
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear() % 100;
        int currentMonth = now.getMonthValue();
        if (year < currentYear) {
            return false;
        }
        if (year == currentYear && month < currentMonth) {
            return false;
        }
        return true;
    }
}
