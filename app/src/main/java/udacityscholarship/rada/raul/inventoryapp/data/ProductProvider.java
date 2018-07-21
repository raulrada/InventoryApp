package udacityscholarship.rada.raul.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * {@link ContentProvider} for the Inventory app
 */
public class ProductProvider extends ContentProvider {

    /**
     * Database helper object that will provide us access to the database
     */
    private ProductDbHelper productDbHelper;

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
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        productDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
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
