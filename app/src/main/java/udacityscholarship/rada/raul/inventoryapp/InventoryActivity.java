//Inspired by Udacity code from the Pets app, at https://github.com/udacity/ud845-Pets/blob/lesson-two/app/src/main/java/com/example/android/pets/CatalogActivity.java

package udacityscholarship.rada.raul.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import udacityscholarship.rada.raul.inventoryapp.data.ProductContract;

/**
 * Main activity of the app. Shows a list of available products and permits navigation to other
 * activities within the app
 */
public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Constant value used when inserting dummy product name
     */
    public static final String PRODUCT_SUPPLIER_PHONE_NUMBER = "n/a";
    /**
     * Number of dummy products which should be inserted in the database every time the user selects
     * the insertion of dummy products
     */
    private static final int MAX_DUMMY_PRODUCTS = 10;
    /**
     * Constant value used when inserting dummy product name
     */
    private static final String PRODUCT_NAME = "Product ";
    /**
     * Constant value used when inserting dummy product name
     */
    private static final String PRODUCT_SUPPLIER = "Supplier ";
    /**
     * Constant value for maximum price of a dummy product
     */
    private static final int PRODUCT_MAX_PRICE = 100;
    /**
     * Constant value for maximum quantity of a dummy product
     */
    private static final int PRODUCT_MAX_QUANTITY = 10;
    /**
     * Identifier for the product data loader
     */
    private static final int PRODUCT_LOADER = 0;
    /**
     * Constant value used as offset of the id of the last product in the database, used when
     * inserting dummy products (in order to account for indexing starting at 0, and not at 1).
     */
    private static final int POSITION_OFFSET = 1;
    /**
     * Adapter for the ListView
     */
    ProductCursorAdapter productCursorAdapter;
    /**
     * ID of the last product in the database - useful when inserting dummy products.
     */
    private int lastProductId;
    /**
     * Flag showing whether the saving of dummy products was successful or not
     */
    private boolean saveSuccessful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //set activity label programatically, instead of within AndroidManifest, in order to have
        //a different app label displayed on user's device vs. the label of the launcher activity
        getSupportActionBar().setTitle(R.string.inventory_activity_label);

        // find the add_product button
        Button addProductButton = (Button) findViewById(R.id.add_product_button);

        // find the ListView displaying data about products and the text view which will be
        // displayed when the ListView is empty
        ListView productsListView = (ListView) findViewById(R.id.products_list_view);
        TextView emptyTextView = (TextView) findViewById(R.id.empty_text_view);
        productsListView.setEmptyView(emptyTextView);

        // Setup a CursorAdapter to create list items for each row of product data in the cursor.
        // There is no product data yet (until the loader finishes) so pass in null for the Cursor.
        productCursorAdapter = new ProductCursorAdapter(this, null);

        // Attach the adapter to the ListView
        productsListView.setAdapter(productCursorAdapter);

        // click listener for the list view containing products information
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             *
             * @param parent the list view
             * @param view containing one product - the view on which the user clicks
             * @param position of the item in the list view
             * @param id of the item
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // intent to go to InsertProductActivity
                Intent startInsertProductActivityIntent = new Intent(
                        InventoryActivity.this, InsertProductActivity.class);

                // Form the content URI that represents the specific product that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ProductContract.ProductEntry#CONTENT_URI}.
                Uri currentProductUri = ContentUris.withAppendedId(
                        ProductContract.ProductEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                startInsertProductActivityIntent.setData(currentProductUri);

                // launch InsertProductActivity
                startActivity(startInsertProductActivityIntent);
            }
        });

        // set action to be completed when the user clicks on the add_product button
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // intent to go to InsertProductActivity
                Intent startInsertProductActivityIntent = new Intent(
                        InventoryActivity.this, InsertProductActivity.class);

                // launch InsertProductActivity
                startActivity(startInsertProductActivityIntent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    /**
     * Insert a product in the products database
     *
     * @param productName                name of the product
     * @param productPrice               price of the product
     * @param productQuantity            quantity of the product
     * @param productSupplier            supplier of the product
     * @param productSupplierPhoneNumber phone number of the product supplier
     *                                   entries - only when the user chooses to enter dummy data
     */
    public void insertProduct(String productName, int productPrice, int productQuantity,
                              String productSupplier, String productSupplierPhoneNumber) {

        // Create a ContentValues object where column names are the keys, and the parameters
        // supplied to the insertProduct method are the values.
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER,
                productSupplierPhoneNumber);

        // Insert a new product into the provider, returning the content URI for the
        // new product.
        Uri newUri = getContentResolver().insert(
                ProductContract.ProductEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            saveSuccessful = false;
        } else {
            // Otherwise, the insertion was successful.
            saveSuccessful = true;
        }
    }

    /**
     * Generates a random number between 1 and n
     *
     * @param n maximum value of the randomly generated number
     * @return generated random number
     */
    private int getRandomNumber(int n) {
        Random random = new Random();
        return random.nextInt(n) + 1;
    }

    /**
     * Inflate menu options from res/menu/menu_inventory.xml
     *
     * @param menu to be inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    /**
     * Determines what happens when user clicks on item in the menu
     *
     * @param item selected by user from the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //determine what item was clicked by user and perform appropriate action
        switch (item.getItemId()) {
            // The user chose the insert dummy products option:
            case R.id.action_insert_dummy_products:
                // insert a number of dummy products
                for (int i = lastProductId + POSITION_OFFSET;
                     i < MAX_DUMMY_PRODUCTS + lastProductId + POSITION_OFFSET; i++) {
                    String productName = PRODUCT_NAME + i;
                    int productPrice = getRandomNumber(PRODUCT_MAX_PRICE);
                    int productQuantity = getRandomNumber(PRODUCT_MAX_QUANTITY);
                    String productSupplier = PRODUCT_SUPPLIER + i;
                    insertProduct(productName, productPrice, productQuantity, productSupplier,
                            PRODUCT_SUPPLIER_PHONE_NUMBER);
                }
                // Show a toast message depending on whether or not the insertion was successful
                if (!saveSuccessful) {
                    Toast.makeText(getApplicationContext(), getString(R.string.products_save_error),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.products_save_successful),
                            Toast.LENGTH_SHORT).show();
                }
                return true;

            // The user chose the delete all products option:
            case R.id.action_delete_all_products:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Ask for user's confirmation that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Check if the products list is already empty
        if (productCursorAdapter.getCount() == 0) {
            // let the user know there is nothing to delete
            Toast.makeText(this, getString(R.string.nothing_to_delete),
                    Toast.LENGTH_SHORT).show();
        } else { // there are products in the database, which can be deleted
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the postivie and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_all_dialog_msg);
            builder.setPositiveButton(R.string.delete_all, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Delete all products if user so chooses
                    deleteAllProducts();
                }
            });
            builder.setNegativeButton(R.string.cancel_all, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User chose not to delete all products, so dismiss the dialog
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

    /**
     * Method deleting all products in the database
     */
    private void deleteAllProducts() {
        // try to delete all items in the database
        int rowsDeleted = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI,
                null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.database_delete_failure),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.database_delete_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies which columns from the database which are relevant.
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,                // Parent activity context
                ProductContract.ProductEntry.CONTENT_URI,   // Provider content URI to query
                projection,                                 // Columns to include in the resulting Cursor
                null,                               // No selection clause
                null,                            // No selection arguments
                null);                             // Default sort order
    }

    /**
     * Called after the completion of onCreateLoader(), and it updates the UI with the new
     * information
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the {@link ProductCursorAdapter} with this new cursor containing updated
        // product data.
        productCursorAdapter.swapCursor(data);

        // Get the id of the last product in the database - useful when inserting dummmy products
        // Proceed with moving to the last row of the cursor and reading data from it
        if (data.moveToLast()) {
            // Find the index of the id column of product attributes
            int idColumnIndex = data.getColumnIndex(
                    ProductContract.ProductEntry._ID);
            // Get the ID of the last product in the database
            lastProductId = data.getInt(idColumnIndex);
        }
    }

    /**
     * Called when information needs to be refreshed. Remove old information, so that new data can
     * be swapped in.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        productCursorAdapter.swapCursor(null);
    }
}
