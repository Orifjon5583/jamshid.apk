package com.example.haftalikharajat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.haftalikharajat.utils.DateUtils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class DateUtilsTest {

    @Test
    public void testUzbekDateFormatting() {
        String formatted = DateUtils.formatUzbekDate("2024-10-25");
        assertTrue(formatted.contains("25"));
        assertTrue(formatted.contains("oktabr"));
        assertTrue(formatted.contains("2024"));
    }

    @Test
    public void testTodayAndYesterdayRelative() {
        String today = DateUtils.getTodayDate();
        assertEquals("Bugun", DateUtils.formatRelativeDate(today));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String yesterday = DateUtils.formatDate(cal.getTime());
        assertEquals("Kecha", DateUtils.formatRelativeDate(yesterday));
    }

    @Test
    public void testDaysDiff() {
        String today = DateUtils.getTodayDate();
        assertEquals(0, DateUtils.getDaysDiffFromToday(today));
    }
}
