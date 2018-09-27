package com.example.android.inventoryapplication.data;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    private InventoryContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapplication";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_Inventory = "inventoryItems";

    public static final class InventoryEntry implements BaseColumns{
//        table constants to be used throughout the application

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_Inventory);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of Items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Inventory;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single inventoryitem.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Inventory;

        public static final String TABLE_NAME = "inventoryItems";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "productName";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplierName";
        public static final String COLUMN_PHONE_NUMBER = "phoneNumber";

    }
}
