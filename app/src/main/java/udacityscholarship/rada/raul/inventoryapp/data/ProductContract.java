package udacityscholarship.rada.raul.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app
 * class defined as final to prevent sub-classing
 */
public final class ProductContract {

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
