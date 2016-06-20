package com.net.alone.ila.Basic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.net.alone.ila.R;

/**
 * Created by Mari on 2015-12-02.
 */
public class SQLite extends SQLiteOpenHelper
{
    /* SQLite */
    public static final String DATABASE_NAME = "alone.db";
    public static final int DATABASE_VERSION= 1;

    public SQLite(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        /* Timer Create Table */
        db.execSQL("CREATE TABLE timer_db ( _id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, hour INTEGER, minute INTEGER, seconds INTEGER, power INTEGER, img INTEGER);");

        /* Alarm Create Table */
        db.execSQL("CREATE TABLE alarm_db ( _id INTEGER, name TEXT, hour INTEGER, minute INTEGER, repeat INTEGER, activity INTEGER, A INTEGER, B INTEGER, C INTEGER, D INTEGER, E INTEGER, F INTEGER, G INTEGER, CR INTEGER, CG INTEGER, CB INTEGER, power INTEGER);");

        /* Life Create Table */
        db.execSQL("CREATE TABLE life_db ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, R INTEGER, G INTEGER, B INTEGER, L INTEGER);");

        /* Kids Create Table */
        db.execSQL("CREATE TABLE kids_db ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, color1 TEXT, color2 TEXT, color3, img TEXT, sort TEXT);");

        /* Create Default Method */
        createDefaultValue(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME); onCreate(db); }

    /* Create Default Value Method */
    private void createDefaultValue(SQLiteDatabase db)
    {
        /* Timer Value */
        db.execSQL("INSERT INTO timer_db VALUES(0, '계란', 0, 12, 0, 1, " + R.mipmap.mip_egg + ");");
        db.execSQL("INSERT INTO timer_db VALUES(1, '라면', 0, 10, 0, 1, " + R.mipmap.mip_ramen + ");");
        db.execSQL("INSERT INTO timer_db VALUES(2, '자격증', 1, 0, 0, 1, " + R.mipmap.mip_license + ");");
        db.execSQL("INSERT INTO timer_db VALUES(3, '마스크팩', 0, 20, 0, 1, " + R.mipmap.mip_masktime + ");");
        db.execSQL("INSERT INTO timer_db VALUES(4, '운동', 0, 30, 0, 1, " + R.mipmap.mip_dumbbell + ");");
        db.execSQL("INSERT INTO timer_db VALUES(5, '빨래', 1, 0, 0, 1, " + R.mipmap.mip_laundry + ");");

        /* BookMark Value */
        db.execSQL("INSERT INTO life_db VALUES(0, '다이어트 색깔', 0, 0, 255, 200);");
        db.execSQL("INSERT INTO life_db VALUES(1, '식욕 돋는 색깔', 255, 0, 0, 150);");
        db.execSQL("INSERT INTO life_db VALUES(2, '정신 안정 색깔', 153, 112, 0, 140);");
    }

    /* Insert Value Method  */
    public static void InsertValue(Context mContext, String mTable, ContentValues mContentValues)
    {
        /* SQLite */
        final SQLite mSQLite = new SQLite(mContext);
        /* SQLiteDatabase */
        SQLiteDatabase mSQLiteDatabase = null;

        try
        {
            mSQLiteDatabase = mSQLite.getWritableDatabase();
            mSQLiteDatabase.insert(mTable, null, mContentValues);
            Log.e("Create SQL", "Create Success!");
        }
        catch (SQLiteException ex) { ex.printStackTrace(); }
        finally { mSQLite.close(); mSQLiteDatabase.close(); }
    }

    /* Update Value Method */
    public static void UpdateValue(Context mContext, String mTable, String mQuery, ContentValues mContextValues)
    {
        /* SQLite */
        final SQLite mSQLite = new SQLite(mContext);
        /* SQLiteDatabase */
        SQLiteDatabase mSQLiteDatabase = null;

        try
        {
            mSQLiteDatabase = mSQLite.getWritableDatabase();
            mSQLiteDatabase.update(mTable, mContextValues, mQuery, null);
            Log.e("Update SQL", "Update Success!");
        }
        catch(SQLiteException ex) { ex.printStackTrace(); }
        finally { mSQLite.close(); mSQLiteDatabase.close(); }
    }

    /* Select Value Method */
    public static Cursor SelectValue(Context mContext, String mQuery)
    {
        /* SQLite */
        final SQLite mSQLite = new SQLite(mContext);

        /* SQLiteDatabase */
        SQLiteDatabase mSQLiteDatabase = null;

        try
        {
            mSQLiteDatabase = mSQLite.getReadableDatabase();
            Log.e("Select SQL", "Select Success!");
            return mSQLiteDatabase.rawQuery(mQuery, null);
        }
        catch(SQLiteException ex) { ex.printStackTrace(); return null; }
    }

    /* Delete Value Method */
    public static void DeleteValue(Context mContext, String mTable, String mQuery, String[] mValue)
    {
        /* SQLite */
        final SQLite mSQLite = new SQLite(mContext);
        /* SQLiteDatabase */
        SQLiteDatabase mSQLiteDatabase = null;

        try
        {
            mSQLiteDatabase = mSQLite.getWritableDatabase();
            mSQLiteDatabase.delete(mTable, mQuery, mValue);
            Log.e("Delete SQL", "Delete Success!");
        }
        catch(SQLiteException ex) { ex.printStackTrace(); }
        finally { mSQLite.close(); mSQLiteDatabase.close(); }
    }
}
