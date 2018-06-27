package com.example.willothewisp.finalapp_abnd.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.willothewisp.finalapp_abnd.data.ItemContract.BooksEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 1;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE =  " CREATE TABLE " + BooksEntry.TABLE_NAME + " ("
                + BooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BooksEntry.COLUMN_ITEM + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + BooksEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + BooksEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_PHONE + " INTEGER NOT NULL );";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
