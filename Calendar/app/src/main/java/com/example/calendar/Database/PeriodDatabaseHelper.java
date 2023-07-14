package com.example.calendar.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.calendar.Model.PeriodEntry;

import java.util.ArrayList;
import java.util.List;

public class PeriodDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "period_tracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PERIODS = "periods";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";
    private static final String COLUMN_PERIOD_FLOW = "period_flow";

    public PeriodDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_PERIODS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_START_DATE + " TEXT, " +
                COLUMN_END_DATE + " TEXT, " +
                COLUMN_PERIOD_FLOW + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_PERIODS;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public void addPeriodEntry(String startDate, String endDate, String periodFlow) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);
        values.put(COLUMN_PERIOD_FLOW, periodFlow);
        db.insert(TABLE_PERIODS, null, values);
        db.close();
    }

    // Add methods to retrieve period entries from the database
// ...
    @SuppressLint("Range")
    public List<PeriodEntry> getPeriodEntries() {
        List<PeriodEntry> periodEntries = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {COLUMN_ID, COLUMN_START_DATE, COLUMN_END_DATE, COLUMN_PERIOD_FLOW};

        Cursor cursor = db.query(TABLE_PERIODS, columns, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String startDate = cursor.getString(cursor.getColumnIndex(COLUMN_START_DATE));
                String endDate = cursor.getString(cursor.getColumnIndex(COLUMN_END_DATE));
                String periodFlow = cursor.getString(cursor.getColumnIndex(COLUMN_PERIOD_FLOW));

                // Convert the date strings into LocalDate objects
                // LocalDate startDate = LocalDate.parse(startDateString);
                //  LocalDate endDate = LocalDate.parse(endDateString);

                // Create a PeriodEntry object and add it to the list
                PeriodEntry periodEntry = new PeriodEntry(id, startDate, endDate, periodFlow);
                periodEntries.add(periodEntry);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return periodEntries;
    }
    public void deleteAllPeriodEntries() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PERIODS, null, null);
        db.close();
    }
}
