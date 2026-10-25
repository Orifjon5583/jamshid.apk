package com.example.haftalikharajat.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "haftalik_xarajat.db";
    private static final int DATABASE_VERSION = 1;

    // Table Transactions
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COL_TRANS_ID = "id";
    public static final String COL_TRANS_TYPE = "type";
    public static final String COL_TRANS_TITLE = "title";
    public static final String COL_TRANS_AMOUNT = "amount";
    public static final String COL_TRANS_CATEGORY = "category";
    public static final String COL_TRANS_DATE = "date";
    public static final String COL_TRANS_TIME = "time";
    public static final String COL_TRANS_ACCOUNT = "account_type";
    public static final String COL_TRANS_NOTE = "note";
    public static final String COL_TRANS_TIMESTAMP = "timestamp";

    // Table Debts
    public static final String TABLE_DEBTS = "debts";
    public static final String COL_DEBT_ID = "id";
    public static final String COL_DEBT_PERSON = "person_name";
    public static final String COL_DEBT_AMOUNT = "amount";
    public static final String COL_DEBT_REASON = "reason";
    public static final String COL_DEBT_PHONE = "phone";
    public static final String COL_DEBT_GIVEN_DATE = "given_date";
    public static final String COL_DEBT_DUE_DATE = "due_date";
    public static final String COL_DEBT_TYPE = "type";
    public static final String COL_DEBT_STATUS = "status";
    public static final String COL_DEBT_CLOSED_DATE = "closed_date";

    // Table Limits
    public static final String TABLE_LIMITS = "limits";
    public static final String COL_LIMIT_ID = "id";
    public static final String COL_LIMIT_TYPE = "type";
    public static final String COL_LIMIT_AMOUNT = "amount";
    public static final String COL_LIMIT_ENABLED = "enabled";

    private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + TABLE_TRANSACTIONS + " ("
            + COL_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TRANS_TYPE + " TEXT NOT NULL, "
            + COL_TRANS_TITLE + " TEXT NOT NULL, "
            + COL_TRANS_AMOUNT + " REAL NOT NULL, "
            + COL_TRANS_CATEGORY + " TEXT, "
            + COL_TRANS_DATE + " TEXT NOT NULL, "
            + COL_TRANS_TIME + " TEXT, "
            + COL_TRANS_ACCOUNT + " TEXT, "
            + COL_TRANS_NOTE + " TEXT, "
            + COL_TRANS_TIMESTAMP + " INTEGER"
            + ");";

    private static final String CREATE_TABLE_DEBTS = "CREATE TABLE " + TABLE_DEBTS + " ("
            + COL_DEBT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_DEBT_PERSON + " TEXT NOT NULL, "
            + COL_DEBT_AMOUNT + " REAL NOT NULL, "
            + COL_DEBT_REASON + " TEXT, "
            + COL_DEBT_PHONE + " TEXT, "
            + COL_DEBT_GIVEN_DATE + " TEXT NOT NULL, "
            + COL_DEBT_DUE_DATE + " TEXT NOT NULL, "
            + COL_DEBT_TYPE + " TEXT NOT NULL, "
            + COL_DEBT_STATUS + " TEXT NOT NULL, "
            + COL_DEBT_CLOSED_DATE + " TEXT"
            + ");";

    private static final String CREATE_TABLE_LIMITS = "CREATE TABLE " + TABLE_LIMITS + " ("
            + COL_LIMIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_LIMIT_TYPE + " TEXT UNIQUE NOT NULL, "
            + COL_LIMIT_AMOUNT + " REAL NOT NULL, "
            + COL_LIMIT_ENABLED + " INTEGER NOT NULL"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_DEBTS);
        db.execSQL(CREATE_TABLE_LIMITS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Safe migration if needed
    }
}
