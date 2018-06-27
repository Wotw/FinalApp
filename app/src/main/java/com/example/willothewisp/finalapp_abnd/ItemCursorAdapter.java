package com.example.willothewisp.finalapp_abnd;

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

import com.example.willothewisp.finalapp_abnd.data.ItemContract.BooksEntry;

public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.product_name_text_view);
        TextView priceTextView = view.findViewById(R.id.price_text_view);
        TextView quantityTextView = view.findViewById(R.id.quantity_text_view);

        int nameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_ITEM);
        int priceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PRICE);
        int supplierColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER);
        int phoneColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_PHONE);

        final int quantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_QUANTITY);
        String currentQuantity = cursor.getString(quantityColumnIndex);
        final int quantityIntCurrent = Integer.valueOf(currentQuantity);


        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        String itemQuantity = cursor.getString(quantityColumnIndex);

        nameTextView.setText(itemName);
        priceTextView.setText(context.getString(R.string.price) + " " + itemPrice);
        quantityTextView.setText(context.getString(R.string.quantity) + itemQuantity);

        final int itemId = cursor.getInt(cursor.getColumnIndex(BooksEntry._ID));

        Button saleButton = view.findViewById(R.id.button_sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (quantityIntCurrent > 0) {
                    int newQuantity = quantityIntCurrent - 1;
                    Uri quantityUri = ContentUris.withAppendedId(BooksEntry.CONTENT_URI, itemId);

                    ContentValues values = new ContentValues();
                    values.put(BooksEntry.COLUMN_QUANTITY, newQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);
                } else {
                    Toast.makeText(context, "This book is out of stock!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


