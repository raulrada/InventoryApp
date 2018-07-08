package udacityscholarship.rada.raul.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for Inventory app. Manages database creation and version management.
 */
public class ProductDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Type of data introduced in column headed by _ID and related constraints
     */
    private static final String _ID_TYPE_AND_CONSTRAINTS = " INTEGER PRIMARY KEY AUTOINCREMENT";

    /**
     * Type of data introduced in column headed by COLUMN_PRODUCT_NAME and related constraints
     */
    private static final String PRODUCT_NAME_TYPE_AND_CONSTRAINTS = " TEXT NOT NULL";

    /**
     * Type of data introduced in column headed by COLUMN_PRODUCT_PRICE and related constraints
     */
    private static final String PRODUCT_PRICE_TYPE_AND_CONSTRAINTS = " INTEGER NOT NULL";

    /**
     * Type of data introduced in column headed by COLUMN_PRODUCT_QUANTITY and related constraints
     */
    private static final String PRODUCT_QUANTITY_TYPE_AND_CONSTRAINTS =
            " INTEGER NOT NULL DEFAULT 0";

    /**
     * Type of data introduced in column headed by COLUMN_PRODUCT_SUPPLIER and related constraints
     */
    private static final String PRODUCT_SUPPLIER_TYPE_AND_CONSTRAINTS =
            " STRING NOT NULL DEFAULT \"n/a\"";

    /**
     * Type of data introduced in column headed by COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER and
     * related constraints
     */
    private static final String PRODUCT_SUPPLIER_PHONE_NUMBER_TYPE_AND_CONSTRAINTS =
            " STRING NOT NULL DEFAULT \"n/a\"";

    /**
     * Create a helper object to create, open, and/or manage a database.
     *
     * @param context of the app.
     */
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                ProductContract.ProductEntry.TABLE_NAME + " (" +
                ProductContract.ProductEntry._ID + _ID_TYPE_AND_CONSTRAINTS + ", " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME +
                PRODUCT_NAME_TYPE_AND_CONSTRAINTS + ", " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE +
                PRODUCT_PRICE_TYPE_AND_CONSTRAINTS + ", " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY +
                PRODUCT_QUANTITY_TYPE_AND_CONSTRAINTS + ", " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER +
                PRODUCT_SUPPLIER_TYPE_AND_CONSTRAINTS + ", " +
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER +
                PRODUCT_SUPPLIER_PHONE_NUMBER_TYPE_AND_CONSTRAINTS + ");";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
