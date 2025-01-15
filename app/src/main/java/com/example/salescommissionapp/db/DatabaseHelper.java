package com.example.salescommissionapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.salescommissionapp.models.Sale;
import com.example.salescommissionapp.models.SalesPerson;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sales_manager.db";
    private static final int DATABASE_VERSION = 3;

    // Tables
    public static final String TABLE_SALES_PERSONS = "sales_persons";
    public static final String TABLE_SALES = "sales";
    public static final String TABLE_COMMISSIONS = "commissions";

    // Common columns
    public static final String COLUMN_ID = "id";
    
    // Sales Persons columns
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMPLOYEE_NUMBER = "employee_number";
    public static final String COLUMN_REGION = "region";
    public static final String COLUMN_PHOTO_PATH = "photo_path";
    public static final String COLUMN_REGISTRATION_DATE = "registration_date";

    // Sales columns
    public static final String COLUMN_SALES_ID = "id";
    public static final String COLUMN_SALES_PERSON_ID = "sales_person_id";
    public static final String COLUMN_LEBANON_SALES = "lebanon_sales";
    public static final String COLUMN_COASTAL_SALES = "coastal_sales";
    public static final String COLUMN_NORTHERN_SALES = "northern_sales";
    public static final String COLUMN_SOUTHERN_SALES = "southern_sales";
    public static final String COLUMN_EASTERN_SALES = "eastern_sales";
    public static final String COLUMN_SALES_MONTH = "month";
    public static final String COLUMN_SALES_YEAR = "year";

    // Commissions columns
    public static final String COLUMN_COMMISSION_ID = "id";
    public static final String COLUMN_COMMISSION_SALES_ID = "sales_id";
    public static final String COLUMN_COMMISSION_AMOUNT = "amount";

    // Create table statements
    private static final String CREATE_TABLE_SALES_PERSONS = "CREATE TABLE " + TABLE_SALES_PERSONS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_EMPLOYEE_NUMBER + " TEXT NOT NULL, "
            + COLUMN_REGION + " TEXT NOT NULL, "
            + COLUMN_PHOTO_PATH + " TEXT, "
            + COLUMN_REGISTRATION_DATE + " INTEGER)";

    private static final String CREATE_TABLE_SALES = "CREATE TABLE " + TABLE_SALES + "("
            + COLUMN_SALES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SALES_PERSON_ID + " INTEGER NOT NULL, "
            + COLUMN_LEBANON_SALES + " REAL NOT NULL DEFAULT 0, "
            + COLUMN_COASTAL_SALES + " REAL NOT NULL DEFAULT 0, "
            + COLUMN_NORTHERN_SALES + " REAL NOT NULL DEFAULT 0, "
            + COLUMN_SOUTHERN_SALES + " REAL NOT NULL DEFAULT 0, "
            + COLUMN_EASTERN_SALES + " REAL NOT NULL DEFAULT 0, "
            + COLUMN_SALES_MONTH + " INTEGER NOT NULL, "
            + COLUMN_SALES_YEAR + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_SALES_PERSON_ID + ") REFERENCES " 
            + TABLE_SALES_PERSONS + "(" + COLUMN_ID + "))";

    private static final String CREATE_TABLE_COMMISSIONS = "CREATE TABLE " + TABLE_COMMISSIONS + "("
            + COLUMN_COMMISSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_COMMISSION_SALES_ID + " INTEGER NOT NULL, "
            + COLUMN_COMMISSION_AMOUNT + " REAL NOT NULL, "
            + "FOREIGN KEY(" + COLUMN_COMMISSION_SALES_ID + ") REFERENCES " 
            + TABLE_SALES + "(" + COLUMN_SALES_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SALES_PERSONS);
        db.execSQL(CREATE_TABLE_SALES);
        db.execSQL(CREATE_TABLE_COMMISSIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMISSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES_PERSONS);
        
        // Recreate all tables
        onCreate(db);
    }

    // CRUD Operations
    public long insertSalesPerson(SalesPerson salesPerson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, salesPerson.getName());
        values.put(COLUMN_EMPLOYEE_NUMBER, salesPerson.getEmployeeNumber());
        values.put(COLUMN_REGION, salesPerson.getRegion());
        values.put(COLUMN_PHOTO_PATH, salesPerson.getPhotoPath());
        values.put(COLUMN_REGISTRATION_DATE, salesPerson.getRegistrationDate());
        return db.insert(TABLE_SALES_PERSONS, null, values);
    }

    public List<SalesPerson> getAllSalesPersons() {
        List<SalesPerson> salesPersonList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SALES_PERSONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SalesPerson salesPerson = new SalesPerson(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMPLOYEE_NUMBER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REGION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_PATH))
                );
                salesPersonList.add(salesPerson);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return salesPersonList;
    }

    public int updateSalesPerson(SalesPerson salesPerson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, salesPerson.getName());
        values.put(COLUMN_EMPLOYEE_NUMBER, salesPerson.getEmployeeNumber());
        values.put(COLUMN_REGION, salesPerson.getRegion());
        values.put(COLUMN_PHOTO_PATH, salesPerson.getPhotoPath());
        return db.update(TABLE_SALES_PERSONS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(salesPerson.getId())});
    }

    public void deleteSalesPerson(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SALES_PERSONS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Add methods for sales operations
    public long insertSale(int salesPersonId, double lebanonSales, double coastalSales,
                          double northernSales, double southernSales, double easternSales,
                          int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SALES_PERSON_ID, salesPersonId);
        values.put(COLUMN_LEBANON_SALES, lebanonSales);
        values.put(COLUMN_COASTAL_SALES, coastalSales);
        values.put(COLUMN_NORTHERN_SALES, northernSales);
        values.put(COLUMN_SOUTHERN_SALES, southernSales);
        values.put(COLUMN_EASTERN_SALES, easternSales);
        values.put(COLUMN_SALES_MONTH, month);
        values.put(COLUMN_SALES_YEAR, year);
        
        return db.insert(TABLE_SALES, null, values);
    }

    public long insertCommission(long saleId, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMMISSION_SALES_ID, saleId);
        values.put(COLUMN_COMMISSION_AMOUNT, amount);
        
        return db.insert(TABLE_COMMISSIONS, null, values);
    }

    // Add this method to fetch sales with commissions
    public List<Sale> getSalesByPersonMonthYear(int salesPersonId, int month, int year) {
        List<Sale> sales = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s.*, c." + COLUMN_COMMISSION_AMOUNT + " as commission_amount FROM " 
                + TABLE_SALES + " s "
                + "LEFT JOIN " + TABLE_COMMISSIONS + " c ON s." + COLUMN_SALES_ID 
                + " = c." + COLUMN_COMMISSION_SALES_ID
                + " WHERE s." + COLUMN_SALES_PERSON_ID + " = ? "
                + "AND s." + COLUMN_SALES_MONTH + " = ? "
                + "AND s." + COLUMN_SALES_YEAR + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
            String.valueOf(salesPersonId),
            String.valueOf(month),
            String.valueOf(year)
        });

        if (cursor.moveToFirst()) {
            do {
                Sale sale = new Sale(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALES_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALES_PERSON_ID)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LEBANON_SALES)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COASTAL_SALES)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_NORTHERN_SALES)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SOUTHERN_SALES)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EASTERN_SALES)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALES_MONTH)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALES_YEAR))
                );
                
                int commissionIndex = cursor.getColumnIndex("commission_amount");
                if (commissionIndex != -1 && !cursor.isNull(commissionIndex)) {
                    sale.setCommission(cursor.getDouble(commissionIndex));
                }
                
                sales.add(sale);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sales;
    }

    public boolean hasCommissionEntry(int salesPersonId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_SALES + " s "
                + "INNER JOIN " + TABLE_COMMISSIONS + " c ON s." + COLUMN_SALES_ID 
                + " = c." + COLUMN_COMMISSION_SALES_ID
                + " WHERE s." + COLUMN_SALES_PERSON_ID + " = ? "
                + "AND s." + COLUMN_SALES_MONTH + " = ? "
                + "AND s." + COLUMN_SALES_YEAR + " = ?";
                
        Cursor cursor = db.rawQuery(query, new String[]{
            String.valueOf(salesPersonId),
            String.valueOf(month),
            String.valueOf(year)
        });
        
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        
        return count > 0;
    }

    public void deleteExistingCommission(int salesPersonId, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // First find the sale ID
        String query = "SELECT s." + COLUMN_SALES_ID + " FROM " + TABLE_SALES + " s "
                + "INNER JOIN " + TABLE_COMMISSIONS + " c ON s." + COLUMN_SALES_ID 
                + " = c." + COLUMN_COMMISSION_SALES_ID
                + " WHERE s." + COLUMN_SALES_PERSON_ID + " = ? "
                + "AND s." + COLUMN_SALES_MONTH + " = ? "
                + "AND s." + COLUMN_SALES_YEAR + " = ?";
                
        Cursor cursor = db.rawQuery(query, new String[]{
            String.valueOf(salesPersonId),
            String.valueOf(month),
            String.valueOf(year)
        });
        
        if (cursor.moveToFirst()) {
            long saleId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SALES_ID));
            
            // Delete commission first (due to foreign key constraint)
            db.delete(TABLE_COMMISSIONS, 
                COLUMN_COMMISSION_SALES_ID + " = ?",
                new String[]{String.valueOf(saleId)});
                
            // Then delete sale
            db.delete(TABLE_SALES,
                COLUMN_SALES_ID + " = ?",
                new String[]{String.valueOf(saleId)});
        }
        cursor.close();
    }

    public SalesPerson getSalesPerson(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SALES_PERSONS,
            new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_EMPLOYEE_NUMBER, 
                        COLUMN_REGION, COLUMN_PHOTO_PATH, COLUMN_REGISTRATION_DATE},
            COLUMN_ID + "=?", new String[]{String.valueOf(id)},
            null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            SalesPerson person = new SalesPerson(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMPLOYEE_NUMBER)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REGION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_PATH))
            );
            person.setRegistrationDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_REGISTRATION_DATE)));
            cursor.close();
            return person;
        }
        return null;
    }

    public long addSalesPerson(SalesPerson salesPerson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_NAME, salesPerson.getName());
        values.put(COLUMN_EMPLOYEE_NUMBER, salesPerson.getEmployeeNumber());
        values.put(COLUMN_REGION, salesPerson.getRegion());
        values.put(COLUMN_PHOTO_PATH, salesPerson.getPhotoPath());
        values.put(COLUMN_REGISTRATION_DATE, salesPerson.getRegistrationDate());
        
        return db.insert(TABLE_SALES_PERSONS, null, values);
    }
} 