package udacityscholarship.rada.raul.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app
 * class defined as final to prevent sub-classing
 */
public final class ProductContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device. The String is hardcoded, as it does not need to be displayed to the user /
     * translated.
     */
    public static final String CONTENT_AUTHORITY = "udacityscholarship.rada.raul.inventoryapp";

    /**
     * Scheme part of the content URI
     */
    public static final String URI_SCHEME = "content://";

    /**
     * used when constructing MIME types
     */
    public static final String SLASH = "/";

    /**
     * Use URI_SCHEME and CONTENT_AUTHORITY to create the base of all URI's which apps will
     * use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse(URI_SCHEME + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://udacityscholarship.rada.raul.inventoryapp/products is a valid path
     * for looking at product data. content://udacityscholarship.rada.raul.inventoryapp/objects/
     * will fail, as the ContentProvider hasn't been given any information on what to do
     * with "objects".
     */
    public static final String PATH_PRODUCTS = "products";

    /**
     * Empty private constructor to prevent the instantiation of ProductContract class.
     * The purpose of the class is only to serve as a BlankContract.
     */
    private ProductContract() {
    }

    /**
     * BlankEntry for the products table in the database.
     */
    public static final class ProductEntry implements BaseColumns {

        /**
         * The content URI to access the product data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + SLASH + CONTENT_AUTHORITY +
                        SLASH + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + SLASH + CONTENT_AUTHORITY +
                        SLASH + PATH_PRODUCTS;

        /**
         * Name of the database table for products
         */
        public static final String TABLE_NAME = "products";

        /**
         * Unique ID number for each product in the database. This constant is used only as column
         * header in the database.
         * The data in this column of the database is of type INTEGER.
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Name of the product. This constant is used only as column header in the database.
         * The data in this column of the database is of type TEXT.
         */
        public static final String COLUMN_PRODUCT_NAME = "product";

        /**
         * Price of the product. This constant is used only as column header in the database.
         * The data in this column of the database is of type INTEGER.
         */
        public static final String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Quantity of product in inventory. This constant is used only as column header in the
         * database.
         * The data in this column of the database is of type INTEGER.
         */
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";

        /**
         * Name of the supplier of  the product. This constant is used only as column header in
         * the database.
         * The data in this column of the database is of type TEXT.
         */
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier";

        /**
         * Phone number of the supplier of  the product. This constant is used only as column
         * header in the database.
         * The data in this column of the database is of type TEXT.
         */
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "number";
    }
}
