package udacityscholarship.rada.raul.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.net.URI;
import java.security.Provider;

import udacityscholarship.rada.raul.inventoryapp.R;

/**
 * {@link ContentProvider} for the Inventory app
 */
public class ProductProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the products table
     */
    private static final int PRODUCTS = 10;
    /**
     * URI matcher code for the content URI for a single product in the products table
     */
    private static final int PRODUCT_ID = 11;
    private static final String URI_INDIVIDUAL_PRODUCT_CONSTRUCTOR = "/#";
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    /**
     * placeholder for single product selection, used in the query method of {@link ProductProvider}
     */
    private static String SINGLE_PRODUCT_PLACEHOLDER = "=?";

    /** Tag for the log messages */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    // Static initializer. This is run the first time anything is called from this class.
    static {
        /**
         * The calls to addURI() go here, for all of the content URI patterns that the provider
         * should recognize. All paths added to the UriMatcher have a corresponding code to return
         * when a match is found.
         * The content URI of the form "content://udacityscholarship.rada.raul.inventoryapp/products"
         * will map to the integer code {@link #PRODUCTS}. This URI is used to provide access to
         * MULTIPLE rows of the products table.
         */
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS,
                PRODUCTS);

        /**
         * The content URI of the form
         * "content://udacityscholarship.rada.raul.inventoryapp/products/#" will map to the
         * integer code {@link #PRODUCT_ID}. This URI is used to provide access to ONE single row
         * of the products table.
         * In this case, the "#" wildcard is used where "#" can be substituted for an integer.
         * For example, "content://udacityscholarship.rada.raul.inventoryapp/products/3" matches,
         * but "content://udacityscholarship.rada.raul.inventoryapp/products"
         * (without a number at the end) doesn't match.
         */
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCTS + URI_INDIVIDUAL_PRODUCT_CONSTRUCTOR, PRODUCT_ID);
    }

    /**
     * Database helper object that will provide us access to the database
     */
    private ProductDbHelper productDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        productDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection,
     * selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {


        // Create and/or open a database to read from it
        SQLiteDatabase db = productDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int uriMatch = sUriMatcher.match(uri);

        switch (uriMatch) {
            case PRODUCTS:
                /**
                 * For the {@link PRODUCTS} code, query the products table directly with the given
                 // projection, selection, selection arguments, and sort order. The cursor
                 // could contain multiple rows of the products table.
                 */
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case PRODUCT_ID:
                /**
                 * For the {@link PRODUCT_ID} code, extract out the ID from the URI.
                 * For an example URI such as
                 * "content://udacityscholarship.rada.raul.inventoryapp/products/3",
                 * the selection will be "_id=?" and the selection argument will be a
                 * String array containing the actual ID of 3 in this case.
                 * For every "?" in the selection, we need to have an element in the
                 * selection arguments that will fill in the "?". Since we have 1 question mark
                 * in the selection, we have 1 String in the selection arguments' String array
                 * (in our example, the selection arguments shall be {"3"}).
                 */
                selection = ProductContract.ProductEntry._ID + SINGLE_PRODUCT_PLACEHOLDER;
                // ContentUris.parseId(uri) converts the last segment of the URI path to a number -
                // for instance, 3. Then, this number is converted into a String using
                // String.valueOf().
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                /**
                 * This will perform a query on the products table where the _id equals 3 to return a
                 // Cursor containing that row of the table.
                 */
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * helper method inserting a product in the database
     * @param uri general product URI to be used in order to construct the URI for the newly
     *            inserted product
     * @param values to be inserted in the database in relation to the product
     * @return URI for the newly inserted product
     */
    private Uri insertProduct(Uri uri, ContentValues values) {

        // Check that the name is not null
        String productName = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (productName == null){
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_name_required));
        }

        // Check that product price is not null and that it is positive
        Integer productPrice = values.getAsInteger
                (ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if (productPrice == null){
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_price_not_null));
        }
        if (productPrice<0){
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_price_positive));
        }

        // If a product quantity is provided, check that it is not negative
        Integer productQuantity = values.getAsInteger
                (ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (productQuantity != null && productQuantity < 0){
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_quantity_positive));
        }

        // Check that the name of the supplier is not null
        String productSupplier = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        if (productSupplier == null){
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_supplier_required));
        }

        // Check that the phone number of the supplier is not null
        String productSupplierPhoneNumber = values.getAsString(
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if (productSupplierPhoneNumber == null){
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_supplier_phone_required));
        }

        // Get writeable database
        SQLiteDatabase db = productDbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long newRowId = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);

        // If the newRowId is -1, then the insertion failed. Log an error and return null.
        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Return the new URI with the newRowId (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
