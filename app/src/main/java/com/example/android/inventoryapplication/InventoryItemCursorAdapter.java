package com.example.android.inventoryapplication;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;


import com.example.android.inventoryapplication.data.InventoryContract;

/**
 * Created by Szymon on 27.06.2018.
 */

public class InventoryItemCursorAdapter extends CursorAdapter{

    /**
     * Constructs a new {@link InventoryItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_constraintlayout, parent, false);

    }

    /**
     * This method binds the inventory item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        // Find fields to populate in inflated template
        TextView productNameTextView = (TextView) view.findViewById(R.id.list_item_product_name);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.list_item_product_price_value);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.list_item_quantity_value);


//        find the columns of pet attributes that we are interested in
        int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);

        // Extract properties from cursor
        String productName = cursor.getString(productNameColumnIndex);
        String productPrice = cursor.getString(productPriceColumnIndex);
        final String productQuantity = cursor.getString(productQuantityColumnIndex);

       // Populate fields with extracted properties

        productNameTextView.setText(productName);
        productPriceTextView.setText(productPrice);
        productQuantityTextView.setText(productQuantity);
        String position = cursor.getString(0);
        final Long positionLong = Long.parseLong(position);
        Log.v("Cursor Position is "," cursor position is" + position);
        Log.v("Curs Position long is "," cursor position long is " + positionLong);
        final String [] cursorPositionString = new String [] {String.valueOf(position)};
        Button sellButton = (Button) view.findViewById(R.id.buttonSell);

        sellButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                    UpdateQuantity(productQuantity, context, cursorPositionString, positionLong );
            }
        });}

            private void UpdateQuantity(String productQuantity, Context context, String [] cursorPositionString, Long positionLongId){

                int productQuantityNumber = Integer.parseInt(productQuantity);
                if  (productQuantityNumber == 0 || productQuantityNumber <0){
                }

                else
                    {productQuantityNumber --;
                        ContentValues values = new ContentValues();
                        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, productQuantityNumber);
                        int UpdateRowID =context.getContentResolver().update(ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, positionLongId), values,InventoryContract.InventoryEntry._ID, cursorPositionString);
                        Log.v("Test update", UpdateRowID + " " + cursorPositionString + " " + values + " " + InventoryContract.InventoryEntry._ID + " " + cursorPositionString);

                }
            }
        }

