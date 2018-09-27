package com.example.android.inventoryapplication.data;

/**
 * Created by Szymon on 18.06.2018.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventoryapplication.data.InventoryContract.InventoryEntry;

/**
 * Created by Szymon on 17.06.2018.
 */

public class InventoryDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;

    //    Implement a constructor for the DB helper
    public InventoryDBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // create a DB database with the use of the Create Table method
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + "( " + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL,"
                + InventoryEntry.COLUMN_PHONE_NUMBER + " TEXT);"
                ;
        Log.v("Test string", "Test" + SQL_CREATE_INVENTORY_TABLE);
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
