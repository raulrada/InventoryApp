//Inspired by Udacity code from the Pets app, at https://github.com/udacity/ud845-Pets/blob/lesson-two/app/src/main/java/com/example/android/pets/CatalogActivity.java

package udacityscholarship.rada.raul.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.ListPreference;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
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
public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

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
     * Constant value used when inserting dummy product name
     */
    private static final String PRODUCT_SUPPLIER_PHONE_NUMBER = "n/a";
    /**
     * Constant value for maximum price of a dummy product
     */
    private static final int PRODUCT_MAX_PRICE = 100;
    /**
     * Constant value for maximum quantity of a dummy product
     */
    private static final int PRODUCT_MAX_QUANTITY = 10;

    /** Identifier for the product data loader */
    private static final int PRODUCT_LOADER = 0;

    /** Adapter for the ListView */
    ProductCursorAdapter productCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //set activity label programatically, instead of within AndroidManifest, in order to have
        //a different app label displayed on user's device vs. the label of the launcher activity
        getSupportActionBar().setTitle(R.string.inventory_activity_label);

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
            Toast.makeText(getApplicationContext(), getString(R.string.product_save_error),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(getApplicationContext(), getString(R.string.product_save_successful),
                    Toast.LENGTH_SHORT).show();
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
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //determine what item was clicked by user and perform appropriate action
        switch (item.getItemId()) {
            case R.id.action_insert_individual_product:
                // intent to go to InsertProductActivity
                Intent startInsertProductActivityIntent = new Intent(
                        InventoryActivity.this, InsertProductActivity.class);

                // launch InsertProductActivity
                startActivity(startInsertProductActivityIntent);
                return true;

            case R.id.action_insert_dummy_products:
                // insert a number of dummy products
                for (int i = 0; i < MAX_DUMMY_PRODUCTS; i++) {
                    String productName = PRODUCT_NAME + i;
                    int productPrice = getRandomNumber(PRODUCT_MAX_PRICE);
                    int productQuantity = getRandomNumber(PRODUCT_MAX_QUANTITY);
                    String productSupplier = PRODUCT_SUPPLIER + i;
                    insertProduct(productName, productPrice, productQuantity, productSupplier,
                            PRODUCT_SUPPLIER_PHONE_NUMBER);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
