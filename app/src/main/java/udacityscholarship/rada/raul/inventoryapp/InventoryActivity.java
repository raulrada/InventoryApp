//Inspired by Udacity code from the Pets app, at https://github.com/udacity/ud845-Pets/blob/lesson-two/app/src/main/java/com/example/android/pets/CatalogActivity.java

package udacityscholarship.rada.raul.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import udacityscholarship.rada.raul.inventoryapp.data.ProductContract;
import udacityscholarship.rada.raul.inventoryapp.data.ProductDbHelper;

/**
 * Main activity of the app. Shows a list of available products and permits navigation to other
 * activities within the app
 */
public class InventoryActivity extends AppCompatActivity {

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
    /**
     * Constant value showing that all previous entries should be deleted from the products table
     * when inserting dummy data.
     */
    private static final boolean DELETE_PREVIOUS_ENTRIES = true;
    /**
     * Constant value showing that a successful entry in the products table does not need to be
     * confirmed when inserting dummy data.
     */
    private static final boolean CONFIRM_TABLE_ENTRY = false;
    /**
     * Database helper that will provide us access to the database
     */
    private ProductDbHelper productDbHelper;
    private TextView tableContentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        /**
         * In order to work with the database, instantiate ProductDbHelper and pass the context,
         * which is the current activity.
         */
        productDbHelper = new ProductDbHelper(this);

        // find the TextView in activity_inventory.xml
        tableContentTextView = (TextView) findViewById(R.id.table_content_text_view);
    }

    /**
     * Displays all entries in the products table within the inventory database
     */
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = productDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        // perform query on the products table
        Cursor cursor = db.query(
                ProductContract.ProductEntry.TABLE_NAME,    // The table to query
                projection,                                 // The columns to be returned from the query
                null,                               // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                               // Don't group the rows
                null,                                // Don't filter by row groups
                null                                // The sort order
        );

        try {
            // Create a header in the Text View that looks like this:
            //
            // The products table contains <number of rows in Cursor> products.
            // _id - product - price - quantity - supplier - supplier phone number
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            // Since this code sequence is only temporary, various String values are hard-coded
            tableContentTextView.setText("The products table contains " + cursor.getCount() + "products. \n");
            tableContentTextView.append(ProductContract.ProductEntry._ID + " - " +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + " - " +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER + " - " +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or int value of the product
                // at the current row the cursor is on.
                int currentId = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(nameColumnIndex);
                int currentProductPrice = cursor.getInt(priceColumnIndex);
                int currentProductQuantity = cursor.getInt(quantityColumnIndex);
                String currentProductSupplier = cursor.getString(supplierColumnIndex);
                String currentProductSupplierNumber = cursor.getString(phoneColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                tableContentTextView.append(currentId + " - " + currentProductName + " - " +
                        currentProductPrice + " - " + currentProductQuantity + " - " +
                        currentProductSupplier + " - " + currentProductSupplierNumber + "\n");
            }
        } finally {
            cursor.close();
        }
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
     * @param confirmSuccessfulEntry     switch value showing whether the successful entry in the
     *                                   database should be confirmed or not - meaningful only when
     *                                   inserting real products, as the dummy products should not
     *                                   create problems.
     */
    public void insertProduct(String productName, int productPrice, int productQuantity,
                              String productSupplier, String productSupplierPhoneNumber,
                              boolean confirmSuccessfulEntry) {

        // gets a database in write mode
        SQLiteDatabase db = productDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys, and the parameters
        // supplied to the insertProduct method are the values.
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumber);

        // Insert a new row for the product in the database, returning the ID of that new row.
        // The first argument for db.insert() is the products table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for the product.
        long newRowId = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);

        // check whether the successful entry of a product in the database should be confirmed
        if (confirmSuccessfulEntry) {
            // if newRowId == -1, the entry in the database was unsuccessful
            if (newRowId == -1) {
                Toast.makeText(this, "Error with saving product",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product saved with row id: " + newRowId,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Override the default onStart() method of the activity in order to make sure
     * tableContentTextView is updated when returning from other activities.
     */
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
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
                Intent startInsertProductActivityIntent = new Intent(InventoryActivity.this,
                        InsertProductActivity.class);
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
                            PRODUCT_SUPPLIER_PHONE_NUMBER, CONFIRM_TABLE_ENTRY);
                }
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
