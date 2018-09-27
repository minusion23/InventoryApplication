package com.example.android.inventoryapplication;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.content.CursorLoader;
import com.example.android.inventoryapplication.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    InventoryItemCursorAdapter mCursorAdapter;
    private static final int loaderID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Intent intent = new Intent(MainActivity.this, ItemEditActivity.class);
                                       startActivity(intent);
                                   }});

        mCursorAdapter = new InventoryItemCursorAdapter(this, null);
        ListView inventoryItemListView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        inventoryItemListView.setEmptyView(emptyView);
        inventoryItemListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(loaderID, null, this);
        inventoryItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ItemEditActivity.class);
                Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                Log.v("Main activity data", currentUri.toString());
                startActivity(intent);
            }
        });
    }

//    Upgrade the view after a new item has been saved

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.

        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu

        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                 showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY
        };

        return new CursorLoader(this, InventoryEntry.CONTENT_URI,projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void DeleteItems(){
        int rowNumber = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);

        if (rowNumber == 0) {
            Toast.makeText(this, getString(R.string.failed_deletion_toast),
                    Toast.LENGTH_SHORT).show();
        } else {

            // Otherwise, the delete was successful and we can display a toast.

            Toast.makeText(this, getString(R.string.toast_deletion_successful) + rowNumber + " row(s) deleted",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void showDeleteConfirmationDialog() {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_deletion_text));
        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
               DeleteItems();
                           }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

