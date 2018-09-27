package com.example.android.inventoryapplication.data;

/**
 * Created by Szymon on 27.06.2018.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.inventoryapplication.data.InventoryContract.InventoryEntry;
/**
 * {@link ContentProvider} for the Inventory  app.
 */

public class InventoryItemProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryItemProvider.class.getSimpleName();

    private static final int INVENTORY_ITEMS = 100;
    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_Inventory, INVENTORY_ITEMS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_Inventory + "/#", INVENTORY_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
//    Database helper object
    private InventoryDBHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new InventoryDBHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        Log.v("Test uri", uri.toString());
        Log.v("Test match", Integer.toString(match));

        switch (match) {

            case INVENTORY_ITEMS:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

        /**
         * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
         */

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ITEMS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {

        /**
         * Insert an item into the database with the given content values. Return the new content URI
         * for that specific row in the database.
         */
        String productName = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("Inventory item requires a name");
        }

        Integer productPrice = values.getAsInteger(InventoryEntry.COLUMN_PRICE);

        if (productPrice == null || productPrice < 0) {
            throw new IllegalArgumentException("Inventory item requires a price and a non-negative value");
        }

        Integer productQuantity = values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);

        if (productQuantity == null || productQuantity < 0) {
            throw new IllegalArgumentException("Inventory item requires a quantity and a non-negative value");
        }

        String supplierName = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("Inventory item requires a supplier name ");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Long newRowId = database.insert(InventoryEntry.TABLE_NAME, null, values);

        Log.v("Inventory Item Provider", "New row id" + newRowId);

        if (newRowId > 0) {
            Toast.makeText(getContext(), "Item saved with Id " + newRowId, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Error experienced while saving the item", Toast.LENGTH_LONG).show();
        }
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        
        return ContentUris.withAppendedId(uri, newRowId);
    }



    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case INVENTORY_ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);

            case INVENTORY_ID:
                // For the inventory id code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                Log.v("Text", selectionArgs.toString());

                return updateItem(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the name key is present,
        // check that the name value is not null.

        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_NAME)) {
            String productName = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Inventory item requires a name");
            }
        }

        // If the key price is present,
        // check that the price value is valid.

            if (values.containsKey(InventoryEntry.COLUMN_PRICE)) {
                Integer productPrice = values.getAsInteger(InventoryEntry.COLUMN_PRICE);
                if (productPrice == null || productPrice < 0) {
                    throw new IllegalArgumentException("Inventory item requires a price and a non-negative value");
                }
            }

            // If the key quantity is present,
            // check that the quantity value is valid.

            if (values.containsKey(InventoryEntry.COLUMN_QUANTITY)) {
                Integer productQuantity = values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
                if (productQuantity == null || productQuantity < 0) {
                    throw new IllegalArgumentException("Inventory item requires a quantity and a non-negative value");
                }
            }

                // If the key supplier name is present,
                // check that the supplier name value is valid.

                if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_NAME)) {
                    String supplierName = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
                    if (supplierName == null) {
                        throw new IllegalArgumentException("Inventory item requires a supplier name ");
                    }
                }

        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
//        / Perform the update on the database and get the number of rows affected

        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
                    // Return the number of rows updated

        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case INVENTORY_ITEMS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case INVENTORY_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted

        return rowsDeleted;
    }
}

