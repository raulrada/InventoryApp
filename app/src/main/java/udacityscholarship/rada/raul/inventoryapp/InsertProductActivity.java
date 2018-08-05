package udacityscholarship.rada.raul.inventoryapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import udacityscholarship.rada.raul.inventoryapp.data.ProductContract;

public class InsertProductActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * EditText field to enter the product name
     */
    private EditText productNameEditText;

    /**
     * EditText field to enter the product price
     */
    private EditText productPriceEditText;

    /**
     * EditText field to enter the product quantity
     */
    private EditText productQuantityEditText;

    /**
     * EditText field to enter the product supplier name
     */
    private EditText productSupplierEditText;

    /**
     * EditText field to enter the product supplier phone number
     */
    private EditText productSupplierPhoneEditText;

    /**
     * Button allowing user to save the information about a product
     */
    private Button saveButton;

    /**
     * Button allowing user to call the supplier of a product
     */
    private Button orderButton;

    /**
     * Button allowing user to delete a product
     */
    private Button deleteButton;

    /**
     * Uri from the data field of the intent used to lauch InsertProductActivity
     */
    private Uri currentProductUri;

    /**
     * Variable showing whether the menu should be displayed or not.
     */
    private static boolean shouldShowMenu;

    /**
     * default quantity is 0
     */
    private static final int NO_QUANTITY = 0;

    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /**
     * String value used for the call intent
     */
    private static final String TELEPHONE = "tel:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        // Find all relevant views that we will need to read user input from, and to save such
        // user input.
        productNameEditText = (EditText) findViewById(R.id.edit_text_product_name);
        productPriceEditText = (EditText) findViewById(R.id.edit_text_product_price);
        productQuantityEditText = (EditText) findViewById(R.id.edit_text_product_quantity);
        productSupplierEditText = (EditText) findViewById(R.id.edit_text_product_supplier);
        productSupplierPhoneEditText = (EditText) findViewById(R.id.edit_text_product_supplier_phone);
        saveButton = (Button) findViewById(R.id.button_save_insert);
        orderButton = (Button) findViewById(R.id.button_order_insert);
        deleteButton = (Button) findViewById(R.id.button_delete_insert);

        // Get the intent used to launch the InsertProductActivity
        Intent intent = getIntent();

        // Get the Uri from the data field of the intent, if such Uri was attached (otherwise
        // currentProductUri shall be null, signalling that the InsertProductActivity should be
        // set into the mode allowing for the insertion of a new product
        currentProductUri = intent.getData();

        // The layout will be different depending on whether InsertProductActivity is launched as
        // a result of the user's trying to insert a new product in the database
        // (currentProductUri == null) or due to the client selecting an existing product in order
        // to view its details.
        if (currentProductUri == null){
            setInInsertMode();
        } else {
            setInDisplayMode();
        }

        /**
         * determine the behaviour of the save button
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read from input fields
                // Use trim to eliminate leading or trailing white space
                // Where case, get numerical value of Strings read from EditTexts
                String productName = productNameEditText.getText().toString().trim();
                // if product name is empty, let user know that product name must be provided
                // and return without saving the product
                if(TextUtils.isEmpty(productName)){
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.product_name_required),Toast.LENGTH_SHORT).show();
                    return;
                }

                String productPriceString =
                        productPriceEditText.getText().toString().trim();
                int productPrice;

                // don't parse an empty String, otherwise the app will crash!!!
                if (!TextUtils.isEmpty(productPriceString)){
                    productPrice = Integer.parseInt(productPriceString);
                } else {
                    // let user know that price must be provided
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.product_price_not_null),Toast.LENGTH_SHORT).show();
                    // return without saving the product
                    return;
                }

                // Set quantity to default value.
                // It is logical to allow the introduction in the database of a product with a
                // current quantity of 0 - the code allows for this on purpose, and therefore
                // the code allows the edit text containing the product quantity information to be
                // empty.
                int productQuantity;
                String productQuantityString =
                        productQuantityEditText.getText().toString().trim();

                // don't parse an empty String, otherwise the app will crash!!!
                if(!TextUtils.isEmpty(productQuantityString)) {
                    productQuantity = Integer.parseInt(productQuantityString);
                } else {
                        // let user know that quantity must be provided
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.product_quantity_not_null),Toast.LENGTH_SHORT).show();
                    // return without saving the product
                    return;
                }

                // if product supplier EditText is empty, let user know that product supplier name
                // must be provided and return without saving the product
                String productSupplier = productSupplierEditText.getText().toString().trim();
                if(TextUtils.isEmpty(productSupplier)){
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.product_supplier_required),Toast.LENGTH_SHORT).show();
                    // return without saving the product
                    return;
                }

                String productSupplierPhoneNumber =
                        productSupplierPhoneEditText.getText().toString().trim();
                // if product supplier phone number EditText is empty, let user know that product
                // supplier phone number must be provided and return without saving the product
                if(TextUtils.isEmpty(productSupplierPhoneNumber)){
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.product_supplier_phone_required),
                            Toast.LENGTH_SHORT).show();
                    // return without saving the product
                    return;
                }

                // Create a ContentValues object where column names are the keys, and the parameters
                // supplied to the insertProduct method are the values.
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER,
                        productSupplierPhoneNumber);

                // determine whether this is a new or an existing product
                // (if currentProductUri == null, then it's a new product)
                if (currentProductUri == null) {
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
                } else {
                    // this is an existing product, so update the product with content URI
                    // currentProductUri and pass in the new ContentValues. Pass in null for the
                    // selection and selection args because currentProductUri will already identify
                    // the correct row in the database that we want to modify.
                    int rowsAffected = getContentResolver().update(
                            currentProductUri, values, null, null);

                    // check if the update failed
                    if (rowsAffected == 0) {
                        // product update failed
                        Toast.makeText(getApplicationContext(), getString(R.string.product_update_error),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // product update successful
                        Toast.makeText(getApplicationContext(), getString(R.string.product_update_successful),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                // set up particular views in the right mode for displaying product information
                setViewsInDisplayMode();
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get supplier phone number
                String productSupplierPhoneNumber =
                        productSupplierPhoneEditText.getText().toString().trim();

                // check if the user is viewing a dummy product
                if (productSupplierPhoneNumber.equalsIgnoreCase(InventoryActivity.PRODUCT_SUPPLIER_PHONE_NUMBER)){
                    // let the user know they cannot call the supplier of a dummy product
                    Toast.makeText(InsertProductActivity.this,
                            getString(R.string.call_dummy_product_supplier),Toast.LENGTH_SHORT).show();
                } else {
                    // prepare intent to start phone app to call the supplier phone number
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse(TELEPHONE + productSupplierPhoneNumber));

                    // check if there is an app on the user's device which can handle phone calls
                    if (callIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(callIntent);
                    } else {
                        // let the user know the device doesn't have an app to handle making phone calls
                        Toast.makeText(InsertProductActivity.this,
                                getString(R.string.no_phone_app),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Method setting up the layout for displaying product information
     */
    private void setInDisplayMode() {
        // set up particular views in the right mode for displaying product information
        setViewsInDisplayMode();

        // Initialize a loader to read the product data from the database
        // and display the current values in the editor
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
    }

    /**
     * helper method for setting up particular views in the right mode for displaying product
     * information
     */
    private void setViewsInDisplayMode(){
        // in product display mode, the menu should be visible
        shouldShowMenu = true;
        // Declare that the options menu has changed, so should be recreated.
        invalidateOptionsMenu();

        // set the title in the action bar
        getSupportActionBar().setTitle(R.string.display_product_details_label);

        // if we just display the information about the product, the Save button should not be
        // displayed, and the EditTexts should be disabled. inputType for the edit texts should
        // also be set to TYPE_NULL, in order to prevent the keyboard from showing on screen.
        saveButton.setVisibility(View.GONE);
        orderButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        productNameEditText.setEnabled(false);
        productNameEditText.setInputType(InputType.TYPE_NULL);
        productPriceEditText.setEnabled(false);
        productPriceEditText.setInputType(InputType.TYPE_NULL);
        productQuantityEditText.setEnabled(false);
        productQuantityEditText.setInputType(InputType.TYPE_NULL);
        productSupplierEditText.setEnabled(false);
        productSupplierEditText.setInputType(InputType.TYPE_NULL);
        productSupplierPhoneEditText.setEnabled(false);
        productSupplierPhoneEditText.setInputType(InputType.TYPE_NULL);
    }

    /**
     * Method setting up the layout for inserting information about a new product
     */
    private void setInInsertMode() {
        // set the title in the action bar
        getSupportActionBar().setTitle(R.string.insert_product_activity_label);

        // set up particular views in the right mode for inserting / editing product information
        setViewsInEditMode();
    }

    /**
     * helper method for setting up particular views in the right mode for editing / inserting
     * product information
     */
    private void setViewsInEditMode(){
        shouldShowMenu = false;
        // Declare that the options menu has changed, so should be recreated.
        invalidateOptionsMenu();

        // if we are in insert mode, the Save button should be displayed, the orderButton and the
        // deleteButton should not be displayed, and the EditTexts should be enabled (and they
        // should have the right InputTypes).
        saveButton.setVisibility(View.VISIBLE);
        orderButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        productNameEditText.setEnabled(true);
        productNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        productPriceEditText.setEnabled(true);
        productPriceEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        productQuantityEditText.setEnabled(true);
        productQuantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        productSupplierEditText.setEnabled(true);
        productSupplierEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        productSupplierPhoneEditText.setEnabled(true);
        productSupplierPhoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Define a projection that specifies which columns from the database which are relevant -
        // in our case, all of the columns are relevant
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,                // Parent activity context
                currentProductUri,                          // query the content URI for the current product
                projection,                                 // Columns to include in the resulting Cursor
                null,                               // No selection clause
                null,                            // No selection arguments
                null);                             // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor - we are looking at one single product)
        if (cursor.moveToFirst()){
            // Find the indices of the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String productName = cursor.getString(nameColumnIndex);
            int productPrice = cursor.getInt(priceColumnIndex);
            int productQuantity = cursor.getInt(quantityColumnIndex);
            String productSupplier = cursor.getString(supplierColumnIndex);
            String productSupplierPhoneNumber = cursor.getString(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            productNameEditText.setText(productName);
            productPriceEditText.setText(Integer.toString(productPrice));
            productQuantityEditText.setText(Integer.toString(productQuantity));
            productSupplierEditText.setText(productSupplier);
            productSupplierPhoneEditText.setText(productSupplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        productNameEditText.setText("");
        productPriceEditText.setText("");
        productQuantityEditText.setText("");
        productSupplierEditText.setText("");
        productSupplierPhoneEditText.setText("");
    }

    /**
     * Inflate menu options from res/menu/menu_insert.xml
     *
     * @param menu to be inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_edit_product);
        // If this is a new pet, hide the "Delete" menu item.
        if (!shouldShowMenu) {
            menuItem.setVisible(false);
        } else {
            menuItem.setVisible(true);
        }
        return true;
    }

    /**
     * Determines what happens when user clicks on item in the menu
     *
     * @param item selected by user from the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_product){
            getSupportActionBar().setTitle(R.string.edit_product_details_label);
            setViewsInEditMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
