package com.example.haftalikharajat.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyUtils {

    private static final DecimalFormat FORMATTER;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        FORMATTER = new DecimalFormat("#,###", symbols);
    }

    public static String formatAmount(double amount) {
        long rounded = Math.round(amount);
        return FORMATTER.format(rounded) + " so'm";
    }

    public static String formatWithSign(double amount, boolean isExpense) {
        long rounded = Math.round(Math.abs(amount));
        String formatted = FORMATTER.format(rounded) + " so'm";
        if (isExpense) {
            return "-" + formatted;
        } else {
            return "+" + formatted;
        }
    }

    public static double parseAmount(String text) {
        if (text == null) return 0;
        String clean = text.replaceAll("[^0-9.]", "").trim();
        if (clean.isEmpty()) return 0;
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
