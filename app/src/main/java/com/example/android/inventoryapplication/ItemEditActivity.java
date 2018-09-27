package com.example.android.inventoryapplication;


import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.app.AlertDialog;
import com.example.android.inventoryapplication.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapplication.data.InventoryDBHelper;

/**
 * Created by Szymon on 17.06.2018.
 */

public class ItemEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

//    initiate text views to be used later in the activity
    /**
     * EditText field to enter the  product name
     */
    private EditText mProductNameEditText;

    /**
     * EditText field to enter the product price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the product quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mSupplierPhoneNumberEditText;

    private Button mDecreaseButton;

    private Button mAddButton;

    private Button mCallSupplierButton;

    private Uri currentURI;

    private boolean mItemChanged = false;

    private int currentQuantity;

    private String currentQuantityString;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemChanged = true;
            return false;
        }
    };

    private static final int loaderId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);
        Intent intent = getIntent();
        currentURI = intent.getData();

        // Find all relevant views that we will need to read user input from
        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);
        mAddButton = (Button) findViewById(R.id.buttonadd);
        mDecreaseButton = (Button) findViewById(R.id.button);
        mCallSupplierButton = (Button) findViewById(R.id.buttoncall);


        if (currentURI == null) {
            setTitle("Add inventory item");
            mAddButton.setVisibility(View.GONE);
            mCallSupplierButton.setVisibility(View.GONE);
            mDecreaseButton.setVisibility(View.GONE);
        } else {
            setTitle("Edit inventory item");
            getLoaderManager().initLoader(loaderId, null, this);
        }

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mAddButton.setOnTouchListener(mTouchListener);
        mDecreaseButton.setOnTouchListener(mTouchListener);
        mCallSupplierButton.setOnTouchListener(mTouchListener);
        mCallSupplierButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = mSupplierPhoneNumberEditText.getText().toString();
                if (!(phoneNumber == null || TextUtils.isEmpty(phoneNumber))) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
                }
            }
        });

        mAddButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentQuantityString = mQuantityEditText.getText().toString().trim();
                currentQuantity = Integer.parseInt(currentQuantityString);
                currentQuantity ++;
                mQuantityEditText.setText(String.valueOf(currentQuantity));
            }
        });

        mDecreaseButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentQuantityString = mQuantityEditText.getText().toString().trim();
                currentQuantity = Integer.parseInt(currentQuantityString);
                if (currentQuantity == 0 || currentQuantity <0){
                }
                else {
                    currentQuantity --;
                    mQuantityEditText.setText(String.valueOf(currentQuantity));
                }
            }
        });
    }

    //    create the menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    //    allow the user to save the input or bo back to the mainactivity

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu

        switch (item.getItemId()) {

            // Respond to a click on the "Save" menu option

            case R.id.action_save:
//                Insert item to database

                int effect = InsertItem();
                if (effect == 1 || effect == 2) {
//                exit activity
                    finish();
                    return true;
                }
                if (effect == 0) {
                    return true;
                }

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar

            case android.R.id.home:
                // Navigate back to parent activity (MainActivity)
                if (!mItemChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ItemEditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (currentURI == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // User clicked the "Delete" button, so delete the item.

                DeleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mItemChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // User clicked "Discard" button, close the current activity.

                        finish();
                    }
                };

        // Show dialog that there are unsaved changes

        showUnsavedChangesDialog(discardButtonClickListener);
    }


    //    insert a new row to the table prepare the response depending on the result shown by the integer

    public int InsertItem() {

        String productNameString = mProductNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(productNameString)){
            Toast.makeText(this, "Action unsuccessful product name is required to save the item",
                    Toast.LENGTH_SHORT).show();
            return 0;
        }
        String productPriceString = mPriceEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productPriceString)){
            Toast.makeText(this, "Action unsuccessful price is required to save the item",
                    Toast.LENGTH_SHORT).show();
            return 0;
        }

        int productPrice = Integer.parseInt(productPriceString);
        int productQuantity = 0;
        String productQuantityString = mQuantityEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(productQuantityString)){
            productQuantity = Integer.parseInt(productQuantityString);
        }

        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(supplierNameString)){
            Toast.makeText(this, "Action unsuccessful supplier name is required to save the item",
                    Toast.LENGTH_SHORT).show();
            return 0;
        }

        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRICE, productPrice);
        values.put(InventoryEntry.COLUMN_QUANTITY, productQuantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryEntry.COLUMN_PHONE_NUMBER, supplierPhoneNumberString);

//        If fields are empty there is no need to try to insert
        if (currentURI == null &&
                TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(productPriceString) &&
                TextUtils.isEmpty(productQuantityString) && TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneNumberString)) {
            return 1;
        }

        if (currentURI == null) {

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {

                // If the new content URI is null, then there was an error with insertion.

                Toast.makeText(this, "Adding new item failed",
                        Toast.LENGTH_SHORT).show();
            } else {

                // Otherwise, the insertion was successful and we can display a toast.

                Toast.makeText(this, "Adding new item was successful",
                        Toast.LENGTH_SHORT).show();
            }
        } else {

//             Otherwise this is an EXISTING item, so update the pet with content URI: mCurrentURI
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentUri will already identify the correct row in the database that
            // we want to modify

            int rowsAffected = getContentResolver().update(currentURI, values, null, null);

            // Show a toast message depending on whether or not the update was successful.

            if (rowsAffected == 0) {

                // If the new content URI is null, then there was an error with insertion.

                Toast.makeText(this, "Editing item failed",
                        Toast.LENGTH_SHORT).show();

            } else {

                // Otherwise, the insertion was successful and we can display a toast.

                Toast.makeText(this, "Editing item was successful",
                        Toast.LENGTH_SHORT).show();
            }
        }
        return 2;
    }

    private void DeleteItem() {

        // Only perform the delete if this is an existing item.

        if (currentURI != null) {

            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentUri
            // content URI already identifies the item that we want.

            int rowsDeleted = getContentResolver().delete(currentURI, null, null);

            // Show a toast message depending on whether or not the delete was successful.

            if (rowsDeleted == 0) {

                // If no rows were deleted, then there was an error with the delete.

                Toast.makeText(this, "Deleting item failed",
                        Toast.LENGTH_SHORT).show();
            } else {

                // Otherwise, the delete was successful and we can display a toast.

                Toast.makeText(this, "Deleting item was successful",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PHONE_NUMBER,
        };

        // This loader will execute the ContentProvider's query method on a background thread

        return new CursorLoader(this,   // Parent activity context
                currentURI,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor

        if (cursor == null || cursor.getCount() < 1) {
            return;}

        if (cursor.moveToFirst()) {

            int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String productName = cursor.getString(productNameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhoneNr = cursor.getString(supplierPhoneNumberColumnIndex);


            // Update the views on the screen with the values from the database
            mProductNameEditText.setText(productName);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhoneNr);
            }
        }

    @Override
    public void onLoaderReset(Loader loader) {

        mProductNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");

    }
}
