package udacityscholarship.rada.raul.inventoryapp;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import udacityscholarship.rada.raul.inventoryapp.data.ProductContract;

public class InsertProductActivity extends AppCompatActivity {

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
     * default price is 0
     */
    private static final int NO_PRICE = 0;

    /**
     * default quantity is 0
     */
    private static final int NO_QUANTITY = 0;

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

                // set quantity to default value
                int productQuantity = NO_QUANTITY;
                String productQuantityString =
                        productQuantityEditText.getText().toString().trim();

                // don't parse an empty String, otherwise the app will crash!!!
                if(!TextUtils.isEmpty(productQuantityString)) {
                    productQuantity = Integer.parseInt(productQuantityString);
                }

                // if product supplier EditText is empty, let user know that product supplier name
                // must be provided and return without saving the product
                String productSupplier = productSupplierEditText.getText().toString().trim();
                if(TextUtils.isEmpty(productSupplier)){
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.product_supplier_required),Toast.LENGTH_SHORT).show();
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

                // clear EditTexts once the product was inserted, to facilitate the insertion of a
                // new product.
                productNameEditText.setText("");
                productPriceEditText.setText("");
                productQuantityEditText.setText("");
                productSupplierEditText.setText("");
                productSupplierPhoneEditText.setText("");
            }
        });
    }
}
