package com.example.haftalikharajat.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.haftalikharajat.data.model.CategoryStat;
import com.example.haftalikharajat.data.model.DayStat;
import com.example.haftalikharajat.data.model.Debt;
import com.example.haftalikharajat.data.model.ExpenseLimit;
import com.example.haftalikharajat.data.model.Transaction;
import com.example.haftalikharajat.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppRepository {

    private static AppRepository instance;
    private final DatabaseHelper dbHelper;

    private AppRepository(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
        checkAndSeedInitialData();
    }

    public static synchronized AppRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AppRepository(context);
        }
        return instance;
    }

    // ==========================================
    // TRANSACTIONS
    // ==========================================

    public long insertTransaction(Transaction t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_TRANS_TYPE, t.getType());
        cv.put(DatabaseHelper.COL_TRANS_TITLE, t.getTitle());
        cv.put(DatabaseHelper.COL_TRANS_AMOUNT, t.getAmount());
        cv.put(DatabaseHelper.COL_TRANS_CATEGORY, t.getCategory());
        cv.put(DatabaseHelper.COL_TRANS_DATE, t.getDate());
        cv.put(DatabaseHelper.COL_TRANS_TIME, t.getTime());
        cv.put(DatabaseHelper.COL_TRANS_ACCOUNT, t.getAccountType());
        cv.put(DatabaseHelper.COL_TRANS_NOTE, t.getNote());
        cv.put(DatabaseHelper.COL_TRANS_TIMESTAMP, System.currentTimeMillis());
        return db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, cv);
    }

    public int updateTransaction(Transaction t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_TRANS_TYPE, t.getType());
        cv.put(DatabaseHelper.COL_TRANS_TITLE, t.getTitle());
        cv.put(DatabaseHelper.COL_TRANS_AMOUNT, t.getAmount());
        cv.put(DatabaseHelper.COL_TRANS_CATEGORY, t.getCategory());
        cv.put(DatabaseHelper.COL_TRANS_DATE, t.getDate());
        cv.put(DatabaseHelper.COL_TRANS_TIME, t.getTime());
        cv.put(DatabaseHelper.COL_TRANS_ACCOUNT, t.getAccountType());
        cv.put(DatabaseHelper.COL_TRANS_NOTE, t.getNote());
        return db.update(DatabaseHelper.TABLE_TRANSACTIONS, cv,
                DatabaseHelper.COL_TRANS_ID + " = ?", new String[]{String.valueOf(t.getId())});
    }

    public int deleteTransaction(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_TRANSACTIONS,
                DatabaseHelper.COL_TRANS_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<Transaction> getRecentExpenses(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Transaction> list = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_TRANSACTIONS
                + " WHERE " + DatabaseHelper.COL_TRANS_TYPE + " = ?"
                + " ORDER BY " + DatabaseHelper.COL_TRANS_DATE + " DESC, "
                + DatabaseHelper.COL_TRANS_TIME + " DESC, "
                + DatabaseHelper.COL_TRANS_ID + " DESC LIMIT " + limit;
        Cursor cursor = db.rawQuery(query, new String[]{Transaction.TYPE_EXPENSE});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToTransaction(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public List<Transaction> getIncomes(String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Transaction> list = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_TRANSACTIONS
                + " WHERE " + DatabaseHelper.COL_TRANS_TYPE + " = ?"
                + (startDate != null && endDate != null ? " AND " + DatabaseHelper.COL_TRANS_DATE + " BETWEEN ? AND ?" : "")
                + " ORDER BY " + DatabaseHelper.COL_TRANS_DATE + " DESC, " + DatabaseHelper.COL_TRANS_ID + " DESC";
        String[] args = (startDate != null && endDate != null)
                ? new String[]{Transaction.TYPE_INCOME, startDate, endDate}
                : new String[]{Transaction.TYPE_INCOME};
        Cursor cursor = db.rawQuery(query, args);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToTransaction(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public List<Transaction> getTransactions(String type, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Transaction> list = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " WHERE 1=1");
        List<String> argsList = new ArrayList<>();

        if (type != null) {
            query.append(" AND ").append(DatabaseHelper.COL_TRANS_TYPE).append(" = ?");
            argsList.add(type);
        }
        if (startDate != null && endDate != null) {
            query.append(" AND ").append(DatabaseHelper.COL_TRANS_DATE).append(" BETWEEN ? AND ?");
            argsList.add(startDate);
            argsList.add(endDate);
        }
        query.append(" ORDER BY ").append(DatabaseHelper.COL_TRANS_DATE).append(" DESC, ")
                .append(DatabaseHelper.COL_TRANS_TIME).append(" DESC, ")
                .append(DatabaseHelper.COL_TRANS_ID).append(" DESC");

        Cursor cursor = db.rawQuery(query.toString(), argsList.toArray(new String[0]));
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToTransaction(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public double getTotalAmount(String type, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuilder query = new StringBuilder("SELECT SUM(" + DatabaseHelper.COL_TRANS_AMOUNT + ") FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " WHERE 1=1");
        List<String> argsList = new ArrayList<>();

        if (type != null) {
            query.append(" AND ").append(DatabaseHelper.COL_TRANS_TYPE).append(" = ?");
            argsList.add(type);
        }
        if (startDate != null && endDate != null) {
            query.append(" AND ").append(DatabaseHelper.COL_TRANS_DATE).append(" BETWEEN ? AND ?");
            argsList.add(startDate);
            argsList.add(endDate);
        }

        Cursor cursor = db.rawQuery(query.toString(), argsList.toArray(new String[0]));
        double sum = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                sum = cursor.getDouble(0);
            }
            cursor.close();
        }
        return sum;
    }

    public List<CategoryStat> getCategoryBreakdown(String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<CategoryStat> list = new ArrayList<>();
        double total = getTotalAmount(Transaction.TYPE_EXPENSE, startDate, endDate);
        if (total <= 0) return list;

        String query = "SELECT " + DatabaseHelper.COL_TRANS_CATEGORY + ", SUM(" + DatabaseHelper.COL_TRANS_AMOUNT + ") as total_cat FROM "
                + DatabaseHelper.TABLE_TRANSACTIONS + " WHERE " + DatabaseHelper.COL_TRANS_TYPE + " = ?"
                + (startDate != null && endDate != null ? " AND " + DatabaseHelper.COL_TRANS_DATE + " BETWEEN ? AND ?" : "")
                + " GROUP BY " + DatabaseHelper.COL_TRANS_CATEGORY
                + " ORDER BY total_cat DESC";

        String[] args = (startDate != null && endDate != null)
                ? new String[]{Transaction.TYPE_EXPENSE, startDate, endDate}
                : new String[]{Transaction.TYPE_EXPENSE};

        Cursor cursor = db.rawQuery(query, args);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String cat = cursor.getString(0);
                if (cat == null || cat.trim().isEmpty()) cat = "Boshqa";
                double catSum = cursor.getDouble(1);
                int pct = (int) Math.round((catSum / total) * 100);
                list.add(new CategoryStat(cat, catSum, pct));
            }
            cursor.close();
        }
        return list;
    }

    public List<DayStat> getWeeklyDayStats(Calendar weekCal) {
        List<DayStat> list = new ArrayList<>();
        Calendar c = (Calendar) weekCal.clone();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int diff = (dayOfWeek == Calendar.SUNDAY) ? -6 : (Calendar.MONDAY - dayOfWeek);
        c.add(Calendar.DAY_OF_MONTH, diff);

        String today = DateUtils.getTodayDate();
        String[] dayNames = {"Dush", "Sesh", "Chor", "Pay", "Juma", "Shan", "Yak"};

        for (int i = 0; i < 7; i++) {
            String dateStr = DateUtils.formatDate(c.getTime());
            double sum = getTotalAmount(Transaction.TYPE_EXPENSE, dateStr, dateStr);
            boolean isHighlighted = dateStr.equals(today);
            list.add(new DayStat(dayNames[i], dateStr, sum, isHighlighted));
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    // ==========================================
    // DEBTS
    // ==========================================

    public long insertDebt(Debt debt) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_DEBT_PERSON, debt.getPersonName());
        cv.put(DatabaseHelper.COL_DEBT_AMOUNT, debt.getAmount());
        cv.put(DatabaseHelper.COL_DEBT_REASON, debt.getReason());
        cv.put(DatabaseHelper.COL_DEBT_PHONE, debt.getPhone());
        cv.put(DatabaseHelper.COL_DEBT_GIVEN_DATE, debt.getGivenDate());
        cv.put(DatabaseHelper.COL_DEBT_DUE_DATE, debt.getDueDate());
        cv.put(DatabaseHelper.COL_DEBT_TYPE, debt.getType());
        cv.put(DatabaseHelper.COL_DEBT_STATUS, debt.getStatus());
        cv.put(DatabaseHelper.COL_DEBT_CLOSED_DATE, debt.getClosedDate());
        return db.insert(DatabaseHelper.TABLE_DEBTS, null, cv);
    }

    public int updateDebt(Debt debt) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_DEBT_PERSON, debt.getPersonName());
        cv.put(DatabaseHelper.COL_DEBT_AMOUNT, debt.getAmount());
        cv.put(DatabaseHelper.COL_DEBT_REASON, debt.getReason());
        cv.put(DatabaseHelper.COL_DEBT_PHONE, debt.getPhone());
        cv.put(DatabaseHelper.COL_DEBT_GIVEN_DATE, debt.getGivenDate());
        cv.put(DatabaseHelper.COL_DEBT_DUE_DATE, debt.getDueDate());
        cv.put(DatabaseHelper.COL_DEBT_TYPE, debt.getType());
        cv.put(DatabaseHelper.COL_DEBT_STATUS, debt.getStatus());
        cv.put(DatabaseHelper.COL_DEBT_CLOSED_DATE, debt.getClosedDate());
        return db.update(DatabaseHelper.TABLE_DEBTS, cv,
                DatabaseHelper.COL_DEBT_ID + " = ?", new String[]{String.valueOf(debt.getId())});
    }

    public int deleteDebt(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_DEBTS,
                DatabaseHelper.COL_DEBT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int markDebtClosed(long id, String closedDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_DEBT_STATUS, Debt.STATUS_CLOSED);
        cv.put(DatabaseHelper.COL_DEBT_CLOSED_DATE, closedDate);
        return db.update(DatabaseHelper.TABLE_DEBTS, cv,
                DatabaseHelper.COL_DEBT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<Debt> getDebtsByStatus(String status) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Debt> list = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_DEBTS
                + " WHERE " + DatabaseHelper.COL_DEBT_STATUS + " = ?"
                + " ORDER BY " + DatabaseHelper.COL_DEBT_DUE_DATE + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{status});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToDebt(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public double getTotalActiveDebtAmount(String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT SUM(" + DatabaseHelper.COL_DEBT_AMOUNT + ") FROM " + DatabaseHelper.TABLE_DEBTS
                + " WHERE " + DatabaseHelper.COL_DEBT_STATUS + " = ?"
                + " AND " + DatabaseHelper.COL_DEBT_TYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{Debt.STATUS_ACTIVE, type});
        double sum = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                sum = cursor.getDouble(0);
            }
            cursor.close();
        }
        return sum;
    }

    public int countActiveDebts(String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_DEBTS
                + " WHERE " + DatabaseHelper.COL_DEBT_STATUS + " = ?"
                + " AND " + DatabaseHelper.COL_DEBT_TYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{Debt.STATUS_ACTIVE, type});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public int countOverdueDebts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String today = DateUtils.getTodayDate();
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_DEBTS
                + " WHERE " + DatabaseHelper.COL_DEBT_STATUS + " = ?"
                + " AND " + DatabaseHelper.COL_DEBT_DUE_DATE + " < ?";
        Cursor cursor = db.rawQuery(query, new String[]{Debt.STATUS_ACTIVE, today});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public List<Debt> getDueDebtsForNotification() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String today = DateUtils.getTodayDate();
        List<Debt> list = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_DEBTS
                + " WHERE " + DatabaseHelper.COL_DEBT_STATUS + " = ?"
                + " AND " + DatabaseHelper.COL_DEBT_DUE_DATE + " <= ?";
        Cursor cursor = db.rawQuery(query, new String[]{Debt.STATUS_ACTIVE, today});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToDebt(cursor));
            }
            cursor.close();
        }
        return list;
    }

    // ==========================================
    // EXPENSE LIMITS
    // ==========================================

    public void setExpenseLimit(String type, double amount, boolean enabled) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_LIMIT_TYPE, type);
        cv.put(DatabaseHelper.COL_LIMIT_AMOUNT, amount);
        cv.put(DatabaseHelper.COL_LIMIT_ENABLED, enabled ? 1 : 0);
        db.insertWithOnConflict(DatabaseHelper.TABLE_LIMITS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ExpenseLimit getExpenseLimit(String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_LIMITS
                + " WHERE " + DatabaseHelper.COL_LIMIT_TYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{type});
        ExpenseLimit limit = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                limit = new ExpenseLimit(
                        cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LIMIT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LIMIT_TYPE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LIMIT_AMOUNT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LIMIT_ENABLED)) == 1
                );
            }
            cursor.close();
        }
        return limit;
    }

    // ==========================================
    // HELPERS & INITIAL SEEDING
    // ==========================================

    private Transaction cursorToTransaction(Cursor cursor) {
        return new Transaction(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_TITLE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_AMOUNT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_TIME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_ACCOUNT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_NOTE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TRANS_TIMESTAMP))
        );
    }

    private Debt cursorToDebt(Cursor cursor) {
        return new Debt(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEBT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEBT_PERSON)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEBT_AMOUNT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEBT_REASON)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEBT_PHONE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEBT_GIVEN_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEBT_DUE_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEBT_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEBT_STATUS)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEBT_CLOSED_DATE))
        );
    }

    private void checkAndSeedInitialData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TRANSACTIONS, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }

        if (count == 0) {
            String today = DateUtils.getTodayDate();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            String yesterday = DateUtils.formatDate(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, -1);
            String dayBefore = DateUtils.formatDate(cal.getTime());

            // Seed initial transactions matching Figma design
            insertTransaction(new Transaction(0, Transaction.TYPE_INCOME, "Asosiy oylik maosh", 5000000, "Ish haqi", today, "10:00", "Karta (Uzcard/Humo)", "Kompaniya HR", System.currentTimeMillis()));
            insertTransaction(new Transaction(0, Transaction.TYPE_INCOME, "Frilans loyiha", 1800000, "Qo'shimcha ish", yesterday, "14:20", "Payme orqali", "Web dizayn loyihasi", System.currentTimeMillis()));
            insertTransaction(new Transaction(0, Transaction.TYPE_INCOME, "Bank depozit foizi", 650000, "Savdo", dayBefore, "11:20", "Bank hisobi", "Kapitalbank oylik foiz", System.currentTimeMillis()));

            insertTransaction(new Transaction(0, Transaction.TYPE_EXPENSE, "Bozorlik (Supermarket)", 185000, "Oziq-ovqat", today, "14:30", "Karta", "Korzinka supermarket", System.currentTimeMillis()));
            insertTransaction(new Transaction(0, Transaction.TYPE_EXPENSE, "Yandex Go (Taksi)", 32000, "Transport", today, "09:15", "Karta", "Taksi shaharga", System.currentTimeMillis()));
            insertTransaction(new Transaction(0, Transaction.TYPE_EXPENSE, "Qahvaxona va tushlik", 65000, "Oziq-ovqat", yesterday, "13:00", "Naqd", "Black Bear Cafe", System.currentTimeMillis()));
            insertTransaction(new Transaction(0, Transaction.TYPE_EXPENSE, "Kiyim-kechak", 350000, "Kiyim-kechak", yesterday, "16:45", "Karta", "Terra Pro", System.currentTimeMillis()));
            insertTransaction(new Transaction(0, Transaction.TYPE_EXPENSE, "Tushlik", 100000, "Oziq-ovqat", yesterday, "12:30", "Naqd", "Osh Markazi", System.currentTimeMillis()));
            insertTransaction(new Transaction(0, Transaction.TYPE_EXPENSE, "Internet to'lovi", 140000, "Kommunal to'lovlar", dayBefore, "11:00", "Karta", "Sarkor Telecom", System.currentTimeMillis()));

            // Seed initial debts matching Figma design
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -5);
            String overdueDate = DateUtils.formatDate(cal.getTime());
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 10);
            String futureDate = DateUtils.formatDate(cal.getTime());

            // Jasur Rustamov - Men berishim kerak (BORROWED), due today
            insertDebt(new Debt(0, "Jasur Rustamov", 350000, "Qarz qaytarish", "+998901234567", yesterday, today, Debt.TYPE_BORROWED, Debt.STATUS_ACTIVE, null));
            // Alisher Vohidov - Menga berishadi (LENT), overdue
            insertDebt(new Debt(0, "Alisher Vohidov", 600000, "Telefon ta'miri", "+998977654321", dayBefore, overdueDate, Debt.TYPE_LENT, Debt.STATUS_ACTIVE, null));
            // Nodirbek Mahmudov - Menga berishadi (LENT), future
            insertDebt(new Debt(0, "Nodirbek Mahmudov", 1000000, "Avto xarajatlari", "+998935554433", today, futureDate, Debt.TYPE_LENT, Debt.STATUS_ACTIVE, null));
            // Dilshod Toirov - Closed
            insertDebt(new Debt(0, "Dilshod Toirov", 500000, "Do'kondan qarz", "+998912223344", dayBefore, yesterday, Debt.TYPE_BORROWED, Debt.STATUS_CLOSED, yesterday));

            // Seed default weekly limit (3 000 000 so'm)
            setExpenseLimit(ExpenseLimit.TYPE_WEEKLY, 3000000, true);
        }
    }
}
