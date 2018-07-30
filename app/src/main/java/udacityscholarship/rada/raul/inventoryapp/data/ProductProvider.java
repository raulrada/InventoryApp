// inspired by code within the pet app by Udacity,
// at https://github.com/udacity/ud845-Pets/blob/lesson-three/app/src/main/java/com/example/android/pets/data/PetProvider.java

package udacityscholarship.rada.raul.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import udacityscholarship.rada.raul.inventoryapp.R;

/**
 * {@link ContentProvider} for the Inventory app
 */
public class ProductProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
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

    /**
     * Strings used for throwing various errors.
     */
    private static String CANNOT_QUERY_URI = "Cannot query unknown URI ";
    private static String CANNOT_INSERT_URI = "Insertion is not supported for ";
    private static String CANNOT_DELETE_URI = "Deletion is not supported for ";
    private static String CANNOT_UPDATE_URI = "Update is not supported for ";
    private static String INSERTION_FAILED = "Failed to insert row for ";
    private static String UNKNOWN_URI = "Unknown URI ";
    private static String WITH_URI_MATCH = " with URI match ";

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
                throw new IllegalArgumentException(CANNOT_QUERY_URI + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // return the cursor
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        // Figure out if the URI matcher can match the URI to a specific code
        final int uriMatch = sUriMatcher.match(uri);

        switch (uriMatch){
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(UNKNOWN_URI + uri + WITH_URI_MATCH + uriMatch);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Figure out if the URI matcher can match the URI to a specific code
        final int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException(CANNOT_INSERT_URI + uri);
        }
    }

    /**
     * helper method inserting a product in the database
     *
     * @param uri    general product URI to be used in order to construct the URI for the newly
     *               inserted product
     * @param values to be inserted in the database in relation to the product
     * @return URI for the newly inserted product
     */
    private Uri insertProduct(Uri uri, ContentValues values) {

        // Check that the name is not null
        String productName = values.getAsString(
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_name_required));
        }

        // Check that product price is not null and that it is positive
        Integer productPrice = values.getAsInteger
                (ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if (productPrice == null) {
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_price_not_null));
        }
        if (productPrice < 0) {
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_price_positive));
        }

        // If a product quantity is provided, check that it is not negative
        Integer productQuantity = values.getAsInteger
                (ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (productQuantity != null && productQuantity < 0) {
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_quantity_positive));
        }

        // Check that the name of the supplier is not null
        String productSupplier = values.getAsString(
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        if (productSupplier == null) {
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_supplier_required));
        }

        // Check that the phone number of the supplier is not null
        String productSupplierPhoneNumber = values.getAsString(
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if (productSupplierPhoneNumber == null) {
            throw new IllegalArgumentException(
                    getContext().getString(R.string.product_supplier_phone_required));
        }

        // Get writeable database
        SQLiteDatabase db = productDbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long newRowId = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);

        // If the newRowId is -1, then the insertion failed. Log an error and return null.
        if (newRowId == -1) {
            Log.e(LOG_TAG, INSERTION_FAILED + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the newRowId (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // get a writable database;
        SQLiteDatabase db = productDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int uriMatch = sUriMatcher.match(uri);

        switch (uriMatch) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductContract.ProductEntry._ID + SINGLE_PRODUCT_PLACEHOLDER;
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(CANNOT_DELETE_URI + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductContract.ProductEntry._ID + SINGLE_PRODUCT_PLACEHOLDER;
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(CANNOT_UPDATE_URI + uri);
        }
    }

    /**
     * Helper method for updating one or more products in the database with the given ContentValues.
     * Apply the changes to the rows specified in the selection and selection arguments
     * (which could be 0 or 1 or more products).
     *
     * @return number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection,
                              String[] selectionArgs) {
        // If the {@link ProductEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the product name value is not null.
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
            String productName = values.getAsString(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException(
                        getContext().getString(R.string.product_name_required));
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_PRICE} key is present,
        // check that the product price value is not null and that the product price
        // is not negative.
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer productPrice = values.getAsInteger(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            if (productPrice == null) {
                throw new IllegalArgumentException(
                        getContext().getString(R.string.product_price_not_null));
            }
            if (productPrice < 0) {
                throw new IllegalArgumentException(
                        getContext().getString(R.string.product_price_positive));
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_QUANTITY} key is present,
        // check that the product quantity value is not negative, in case the product quantity
        // value is not null.
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer productQuantity = values.getAsInteger(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (productQuantity != null && productQuantity < 0) {
                throw new IllegalArgumentException(
                        getContext().getString(R.string.product_quantity_positive));
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_SUPPLIER} key is present,
        // check that the product supplier value is not null.
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER)) {
            String productSupplier = values.getAsString(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            if (productSupplier == null) {
                throw new IllegalArgumentException(
                        getContext().getString(R.string.product_supplier_required));
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER} key is present,
        // check that the product supplier phone number value is not null.
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            String productSupplierPhoneNumber = values.getAsString(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (productSupplierPhoneNumber == null) {
                throw new IllegalArgumentException(
                        getContext().getString(R.string.product_supplier_phone_required));
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase db = productDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = db.update(ProductContract.ProductEntry.TABLE_NAME, values, selection,
                selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
