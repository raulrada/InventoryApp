// inspired by Udacity code at https://github.com/udacity/ud845-Pets/blob/lesson-four/app/src/main/java/com/example/android/pets/EditorActivity.java

package udacityscholarship.rada.raul.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
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
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;
    /**
     * String value used for the call intent
     */
    private static final String TELEPHONE = "tel:";
    /**
     * String key for saving the state of the method in onSaveInstanceState.
     */
    private static final String KEY_MODE = "mode";
    /**
     * String key for saving currentProductUri in onSaveInstanceState
     */
    private static final String KEY_URI = "uri";
    /**
     * String key for saving productHasChanged in onSaveInstanceState
     */
    private static final String KEY_PRODUCT_CHANGED = "product has changed";
    /**
     * Variable showing whether the menu should be displayed or not.
     */
    private static boolean shouldShowMenu;
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
     * Button allowing the user to increase the quantity of a product.
     */
    private Button increaseQuantityButton;
    /**
     * Button allowing the user to decrease the quantity of a product.
     */
    private Button decreaseQuantityButton;
    /**
     * Relative Layout holding the increase / decrease quantity buttons.
     */
    private RelativeLayout adjustQuantityRL;
    /**
     * Uri from the data field of the intent used to lauch InsertProductActivity
     */
    private Uri currentProductUri;
    /**
     * Shows whether the product details have changed.
     */
    private boolean productHasChanged;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the productHasChanged boolean to true.
     */
    private View.OnTouchListener viewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

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
        increaseQuantityButton = (Button) findViewById(R.id.button_increase_quantity);
        decreaseQuantityButton = (Button) findViewById(R.id.button_decrease_quantity);
        adjustQuantityRL = (RelativeLayout) findViewById(R.id.adjust_quantity_relative_layout);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        productNameEditText.setOnTouchListener(viewTouchListener);
        productPriceEditText.setOnTouchListener(viewTouchListener);
        productQuantityEditText.setOnTouchListener(viewTouchListener);
        productSupplierEditText.setOnTouchListener(viewTouchListener);
        productSupplierPhoneEditText.setOnTouchListener(viewTouchListener);

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
        if (currentProductUri == null) {
            setInInsertMode();
        } else {
            setInDisplayMode();
        }

        /**
         * Determine the behaviour of the save button
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
                if (TextUtils.isEmpty(productName)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.product_name_required), Toast.LENGTH_SHORT).show();
                    return;
                }

                String productPriceString =
                        productPriceEditText.getText().toString().trim();
                int productPrice;

                // don't parse an empty String, otherwise the app will crash!!!
                if (!TextUtils.isEmpty(productPriceString)) {
                    productPrice = Integer.parseInt(productPriceString);
                } else {
                    // let user know that price must be provided
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.product_price_not_null), Toast.LENGTH_SHORT).show();
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
                if (!TextUtils.isEmpty(productQuantityString)) {
                    productQuantity = Integer.parseInt(productQuantityString);
                } else {
                    // let user know that quantity must be provided
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.product_quantity_not_null), Toast.LENGTH_SHORT).show();
                    // return without saving the product
                    return;
                }

                // if product supplier EditText is empty, let user know that product supplier name
                // must be provided and return without saving the product
                String productSupplier = productSupplierEditText.getText().toString().trim();
                if (TextUtils.isEmpty(productSupplier)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.product_supplier_required), Toast.LENGTH_SHORT).show();
                    // return without saving the product
                    return;
                }

                String productSupplierPhoneNumber =
                        productSupplierPhoneEditText.getText().toString().trim();
                // if product supplier phone number EditText is empty, let user know that product
                // supplier phone number must be provided and return without saving the product
                if (TextUtils.isEmpty(productSupplierPhoneNumber)) {
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
                        // Otherwise, the insertion was successful, we have a current URI for the
                        // product being displayed after it was saved into the database, and we can
                        // display a toast confirming the saving of the product.
                        currentProductUri = newUri;
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

                // Once the Save button has been pressed, the changes to the product details have
                // been saved - there are no other unsaved changes the user should be aware about.
                productHasChanged = false;

                // Once the product details have been saved, set up particular views in the right
                // mode for displaying product information
                setViewsInDisplayMode();
            }
        });

        /**
         * Action to be performed when user clicks the order button
         */
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get supplier phone number
                String productSupplierPhoneNumber =
                        productSupplierPhoneEditText.getText().toString().trim();

                // check if the user is viewing a dummy product
                if (productSupplierPhoneNumber.equalsIgnoreCase(InventoryActivity.PRODUCT_SUPPLIER_PHONE_NUMBER)) {
                    // let the user know they cannot call the supplier of a dummy product
                    Toast.makeText(InsertProductActivity.this,
                            getString(R.string.call_dummy_product_supplier), Toast.LENGTH_SHORT).show();
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
                                getString(R.string.no_phone_app), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        /**
         * Action to be performed when user clicks the increase quantity button
         */
        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current quantity from the relevant EditText
                String productQuantityString =
                        productQuantityEditText.getText().toString().trim();

                // get an integer value for the product quantity. Since we are guaranteed to see
                // the increase quantity button only in product display mode, the value of
                // productQuantityEditText is quaranteed not to be null.
                int productQuantity = Integer.parseInt(productQuantityString);

                // increment product quantity.
                productQuantity++;

                // Create a ContentValues object where column names are the keys, and the parameters
                // supplied to the insertProduct method are the values.
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);

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
        });

        /**
         * Action to be performed when user clicks the decrease quantity button
         */
        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current quantity from the relevant EditText
                String productQuantityString =
                        productQuantityEditText.getText().toString().trim();

                // get an integer value for the product quantity. Since we are guaranteed to see
                // the increase quantity button only in product display mode, the value of
                // productQuantityEditText is quaranteed not to be null.
                int productQuantity = Integer.parseInt(productQuantityString);

                // decrement product quantity, as long as it is >0.
                if (productQuantity > 0) {
                    productQuantity--;
                } else {
                    // let the user know quantity cannot be lower than 0.
                    Toast.makeText(getApplicationContext(), getString(R.string.quantity_error),
                            Toast.LENGTH_SHORT).show();
                    // no need to update the product details, so bail out.
                    return;
                }

                // Create a ContentValues object where column names are the keys, and the parameters
                // supplied to the insertProduct method are the values.
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);

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
        });

        /**
         * Action to be performed when the user clicks the delete button
         */
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ask user to confirm intention to delete the current product
                showDeleteConfirmationDialog();
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
     * Helper method for setting up particular views in the right mode for displaying product
     * information
     */
    private void setViewsInDisplayMode() {
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
        adjustQuantityRL.setVisibility(View.VISIBLE);
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
     * Helper method for setting up particular views in the right mode for editing / inserting
     * product information
     */
    private void setViewsInEditMode() {
        shouldShowMenu = false;
        // Declare that the options menu has changed, so it should be recreated.
        invalidateOptionsMenu();

        // if we are in insert mode, the Save button should be displayed, the orderButton and the
        // deleteButton should not be displayed, and the EditTexts should be enabled (and they
        // should have the right InputTypes).
        saveButton.setVisibility(View.VISIBLE);
        orderButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        adjustQuantityRL.setVisibility(View.GONE);
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
        if (cursor.moveToFirst()) {
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
        // If this is a new product, hide the menu items.
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
        switch (item.getItemId()) {
            case R.id.action_edit_product:
                getSupportActionBar().setTitle(R.string.edit_product_details_label);
                setViewsInEditMode();
                return true;
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link InventoryActivity}.
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(InsertProductActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(
                                        InsertProductActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialogue_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Ask for user's confirmation that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the product if user so chooses
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User chose not to delete the product, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Method deleting the current product
     */
    private void deleteProduct() {
        // If the Delete button is visible, we know for sure that the user is in product info
        // display mode, and that {@link currentProductUri} is not null
        int rowsDeleted = getContentResolver().delete(currentProductUri,
                null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_failure),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_successful),
                    Toast.LENGTH_SHORT).show();
        }

        // Close the activity
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // save the layout mode for the current activity
        savedInstanceState.putBoolean(KEY_MODE, shouldShowMenu);

        // save the currentProductUri - in case it was not passed by the activity calling
        // InsertProductActivity, but rather it was generated in the current activity.

        savedInstanceState.putParcelable(KEY_URI, currentProductUri);
        // save the state of the productHasChanged variable
        savedInstanceState.putBoolean(KEY_PRODUCT_CHANGED, productHasChanged);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore the state of various variables
        shouldShowMenu = savedInstanceState.getBoolean(KEY_MODE);
        currentProductUri = savedInstanceState.getParcelable(KEY_URI);
        productHasChanged = savedInstanceState.getBoolean(KEY_PRODUCT_CHANGED);

        // determine the mode in which the layout should be presented - in display mode or in
        // edit mode
        if (shouldShowMenu) {
            setViewsInDisplayMode();
        } else {
            if (currentProductUri == null) {
                // in insert product mode
                getSupportActionBar().setTitle(R.string.insert_product_activity_label);
            } else {
                // in edit product mode
                getSupportActionBar().setTitle(R.string.edit_product_details_label);
            }
            setViewsInEditMode();
        }
        // Declare that the options menu has changed, so should be recreated.
        invalidateOptionsMenu();
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}
