package com.example.haftalikharajat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.haftalikharajat.utils.CurrencyUtils;

import org.junit.Test;

public class CurrencyUtilsTest {

    @Test
    public void testFormatAmount() {
        String formatted = CurrencyUtils.formatAmount(185000);
        assertTrue(formatted.contains("185") && formatted.contains("000") && formatted.contains("so'm"));
    }

    @Test
    public void testFormatWithSign() {
        String expense = CurrencyUtils.formatWithSign(2250000, true);
        assertTrue(expense.startsWith("-"));
        assertTrue(expense.contains("so'm"));

        String income = CurrencyUtils.formatWithSign(5000000, false);
        assertTrue(income.startsWith("+"));
        assertTrue(income.contains("so'm"));
    }

    @Test
    public void testParseAmount() {
        assertEquals(185000.0, CurrencyUtils.parseAmount("185 000 so'm"), 0.001);
        assertEquals(5000000.0, CurrencyUtils.parseAmount("+5 000 000"), 0.001);
        assertEquals(0.0, CurrencyUtils.parseAmount("abc"), 0.001);
    }
}
