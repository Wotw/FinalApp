package com.example.willothewisp.finalapp_abnd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.willothewisp.finalapp_abnd.data.ItemContract.BooksEntry;
import com.example.willothewisp.finalapp_abnd.data.ItemDbHelper;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String URI_CALL = "Call:";
    private static final int URI_LOADER = 0;

    private Uri itemUri;

    private EditText mTitle;
    private EditText mPrice;
    private EditText mQuantity;
    private EditText mSupplier;
    private EditText mPhone;
    private EditText priceEditText;


    private boolean productHasChanged = false;

    private String productName;
    private int productQuantity;
    /**
     * TextView to show current product quantity
     */
    private TextView quantityTextView;
    /**
     * Button to order more units from the supplier
     */
    private Button orderButton;
    /**
     * Four Buttons that will be used to update quantity
     */
    private Button increaseQuantityByOne;          // Increase by one
    private Button decreaseQuantityByOne;          // Decrease by one
    private Button increaseQuantityByManyUnits;    // Increase by many (n)
    private Button decreaseQuantityByManyUnits;    // Decrease by many (n)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        itemUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (itemUri == null) {
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle(getString(R.string.detail_activity_add));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.detail_activity_edit));

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(URI_LOADER, null, this);
        }

        // Order from supplier statement
        orderFromSupplier();

        // Find all relevant views that we will need to read user input from
        mTitle = findViewById(R.id.edit_product_name);
        mQuantity = findViewById(R.id.edit_product_quantity);
        priceEditText = findViewById(R.id.edit_price);
        //supplierEditText = findViewById(R.edit_supplier);
        //supplierPhoneEditText = findViewById(R.id.phone);


        quantityTextView = findViewById(R.id.quantity_final);


        increaseQuantityByOne = findViewById(R.id.button_increase_by_one);
        increaseQuantityByOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productQuantity++;
                quantityTextView.setText(String.valueOf(productQuantity));
            }
        });

        decreaseQuantityByOne = findViewById(R.id.button_decrease_by_one);
        decreaseQuantityByOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productQuantity > 0) {
                    productQuantity--;
                    quantityTextView.setText(String.valueOf(productQuantity));
                } else {
                    Toast.makeText(DetailsActivity.this, getString(R.string.toast_invalid_quantity), Toast.LENGTH_SHORT).show();
                }
            }
        });

        increaseQuantityByManyUnits = findViewById(R.id.button_increase_qty_by_many_units);
        increaseQuantityByManyUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantity.getText()) && Integer.valueOf(mQuantity.getText().toString()) > 0) {
                    productQuantity += Integer.valueOf(mQuantity.getText().toString());

                    quantityTextView.setText(String.valueOf(productQuantity));
                } else {
                    Toast.makeText(DetailsActivity.this, getString(R.string.toast_missing_quantity), Toast.LENGTH_SHORT).show();
                    productQuantity++;

                    quantityTextView.setText(String.valueOf(productQuantity));
                }
            }
        });

        decreaseQuantityByManyUnits = findViewById(R.id.button_decrease_qty_by_many_units);
        decreaseQuantityByManyUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantity.getText()) && Integer.valueOf(mQuantity.getText().toString()) > 0) {
                    int newQuantity = productQuantity - Integer.valueOf(mQuantity.getText().toString());
                    if (newQuantity < 0) {
                        Toast.makeText(DetailsActivity.this, getString(R.string.toast_invalid_quantity), Toast.LENGTH_SHORT).show();
                    } else {
                        productQuantity -= Integer.valueOf(mQuantity.getText().toString());

                        quantityTextView.setText(String.valueOf(productQuantity));
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, getString(R.string.toast_missing_quantity), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void orderFromSupplier() {
        // Check if there is an existing product to make button visible so the user can order more from the existing product
        if (itemUri != null) {
            // Initialise Order Button to order more from the supplier
            orderButton = findViewById(R.id.button_order);
            // Make Button visible
            orderButton.setVisibility(View.VISIBLE);
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        //callIntent.setData(Uri.parse("tel:" + supplierPhoneEditText));

                        if (ActivityCompat.checkSelfPermission(DetailsActivity.this,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(callIntent);
                    }
                });
                }
        }

    private void saveProduct() {
        ItemDbHelper mDbHelper = new ItemDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

            String name = mTitle.getText().toString().trim();
            double price = Double.parseDouble(mPrice.getText().toString().trim());
            String supplier = mSupplier.getText().toString().trim();
            double phone = Double.parseDouble(mPhone.getText().toString().trim());


            ContentValues contentValues = new ContentValues();
            contentValues.put(BooksEntry.COLUMN_ITEM, name);
            contentValues.put(BooksEntry.COLUMN_QUANTITY, productQuantity);
            contentValues.put(BooksEntry.COLUMN_PRICE, price);
            contentValues.put(BooksEntry.COLUMN_SUPPLIER, supplier);
            contentValues.put(BooksEntry.COLUMN_PHONE, phone);

            if (itemUri == null) {
                Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, contentValues);

                if (newUri != null) {
                    Toast.makeText(this, getString(R.string.toast_add_product_successful),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else {
                int newUri = getContentResolver().update(itemUri, contentValues, null, null);

                if (newUri != -1) {
                    Toast.makeText(this, getString(R.string.toast_update_item_successful),
                            Toast.LENGTH_SHORT).show();
                    finish();

            }
        }
    }

    private boolean checkFieldEmpty(String string) {
        return TextUtils.isEmpty(string) || string.equals(".");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (itemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                saveProduct();
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                } else {

                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                                }
                            };
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_ITEM,
                BooksEntry.COLUMN_QUANTITY,
                BooksEntry.COLUMN_PRICE,
                BooksEntry.COLUMN_SUPPLIER,
                BooksEntry.COLUMN_PHONE};

        switch (id) {
            case URI_LOADER:
                return new CursorLoader(this,   // Parent activity context
                        itemUri,         // Query the content URI for the current product
                        projection,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            productName = cursor.getString(cursor.getColumnIndex(BooksEntry.COLUMN_ITEM));
            mTitle.setText(productName);
            productQuantity = cursor.getInt(cursor.getColumnIndex(BooksEntry.COLUMN_QUANTITY));
            mQuantity.setText(cursor.getString(cursor.getColumnIndex(BooksEntry.COLUMN_QUANTITY)));
            priceEditText.setText(cursor.getString(cursor.getColumnIndex(BooksEntry.COLUMN_PRICE)));
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitle.getText().clear();
        mQuantity.getText().clear();
        quantityTextView.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.option_leave_without_saving));
        builder.setPositiveButton(getString(R.string.option_yes), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.option_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (itemUri != null) {
            int rowsDeleted = getContentResolver().delete(itemUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.toast_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.option_delete_item));
        builder.setPositiveButton(getString(R.string.option_delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.option_cancel, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}