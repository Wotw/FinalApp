package com.example.willothewisp.finalapp_abnd;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.willothewisp.finalapp_abnd.data.ItemContract.BooksEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URI_LOADER = 0;
    private ItemCursorAdapter itemCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLoaderManager().initLoader(URI_LOADER, null, this);

        initialiseListView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case URI_LOADER:
                String projection[] = {
                        BooksEntry._ID,
                        BooksEntry.COLUMN_ITEM,
                        BooksEntry.COLUMN_PRICE,
                        BooksEntry.COLUMN_QUANTITY,
                        BooksEntry.COLUMN_SUPPLIER,
                        BooksEntry.COLUMN_PHONE};


                // Define sort order
                String sortOrder =
                        BooksEntry._ID + " DESC";
                // Return cursor loader
                return new CursorLoader(
                        this,
                        BooksEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder
                );
            default:
                return null;
        }
    }

    private void initialiseListView() {
        ListView itemListView = findViewById(R.id.list_view);

        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        itemCursorAdapter = new ItemCursorAdapter(this, null, false);
        itemListView.setAdapter(itemCursorAdapter);

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.setData(ContentUris.withAppendedId(BooksEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        try {
            itemCursorAdapter.swapCursor(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemCursorAdapter.swapCursor(null);
    }
}