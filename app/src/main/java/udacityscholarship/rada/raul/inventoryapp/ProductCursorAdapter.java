// Inspired by Udacity code at https://github.com/udacity/ud845-Pets/blob/lesson-four/app/src/main/java/com/example/android/pets/PetCursorAdapter.java

package udacityscholarship.rada.raul.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import udacityscholarship.rada.raul.inventoryapp.data.ProductContract;

/**
 * {@link ProductCursorAdapter} is an adapter for a list that uses a {@link Cursor} of product data
 * as its data source. This adapter knows how to create list items for each row of product data in
 * the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    /**
     * the context
     */
    private Context mContext;

    /**
     * constructor of a new {@link ProductCursorAdapter}
     * @param context of the app
     * @param c the cursor containing the products data
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     * @param context of the app
     * @param cursor The cursor from which to get the data. The cursor is already
     *               moved to the correct position.
     * @param parent The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout.
     * @param view Existing view, returned earlier by newView() method
     * @param context of the app
     * @param cursor The cursor from which to get the data. The cursor is already moved to the
     *               correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // find individual views in the current list item
        TextView productNameTextView = (TextView) view.findViewById(
                R.id.list_item_product_name_text_view);
        TextView productPriceTextView = (TextView) view.findViewById(
                R.id.list_item_product_price_text_view);
        TextView productQuantityTextView = (TextView) view.findViewById(
                R.id.list_item_product_quantity_text_view);
        Button sellButton = (Button) view.findViewById(R.id.list_item_sell_button);

        // Find the indices of the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry._ID);

        // Read the product attributes from the Cursor for the current pet
        String currentProductName = cursor.getString(nameColumnIndex);
        int currentProductPrice = cursor.getInt(priceColumnIndex);
        int currentProductQuantity = cursor.getInt(quantityColumnIndex);
        final int currentProductId = cursor.getInt(idColumnIndex);

        // Update the TextViews with the attributes for the current product
        productNameTextView.setText(context.getString(R.string.list_item_product,
                currentProductName));
        productPriceTextView.setText(context.getString(R.string.list_item_price,
                currentProductPrice));
        productQuantityTextView.setText(context.getString(R.string.list_item_quantity,
                currentProductQuantity));

        // final variable holding the quantity of the current product - necessary for the
        // sellButton's onClickListener.
        final int quantity = currentProductQuantity;

        final TextView finalProductQuantityTextView = productQuantityTextView;

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri currentProductUri = ContentUris.withAppendedId(
                        ProductContract.ProductEntry.CONTENT_URI, currentProductId);

                // variable holding the current product quantity - not final, so it can be updated.
                int innerQuantity = quantity;

                // decrement product quantity, as long as it is >0.
                if (innerQuantity > 0) {
                    innerQuantity--;
                } else {
                    // let the user know quantity cannot be lower than 0.
                    Toast.makeText(mContext, mContext.getString(R.string.quantity_error),
                            Toast.LENGTH_SHORT).show();
                    // no need to update the product details, so bail out.
                    return;
                }

                // Create a ContentValues object where column names are the keys, and the parameters
                // supplied to the insertProduct method are the values.
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, innerQuantity);

                // this is an existing product, so update the product with content URI
                // currentProductUri and pass in the new ContentValues. Pass in null for the
                // selection and selection args because currentProductUri will already identify
                // the correct row in the database that we want to modify.
                int rowsAffected = mContext.getContentResolver().update(
                        currentProductUri, values, null, null);

                // check if the update failed
                if (rowsAffected == 0) {
                    // product update failed
                    Toast.makeText(mContext, mContext.getString(R.string.product_update_error),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // product sold successful
                    Toast.makeText(mContext, mContext.getString(R.string.product_sold),
                            Toast.LENGTH_SHORT).show();
                    finalProductQuantityTextView.setText(mContext.getString(R.string.list_item_quantity,
                            innerQuantity));
                }
            }
        });
    }
}
