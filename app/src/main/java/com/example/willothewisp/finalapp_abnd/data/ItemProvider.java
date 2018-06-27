package com.example.willothewisp.finalapp_abnd.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.willothewisp.finalapp_abnd.data.ItemContract.BooksEntry;

public class ItemProvider extends ContentProvider {

    public static final String LOG_TAG = ItemProvider.class.getSimpleName();

    private static final int BOOKS = 100;

    private static final int BOOKS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_BOOKS,BOOKS);

        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_BOOKS + "/", BOOKS_ID);
    }

    private ItemDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            case BOOKS_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertItem (Uri uri, ContentValues values) {
        String name = values.getAsString(BooksEntry.COLUMN_ITEM);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }
        Integer price = values.getAsInteger(BooksEntry.COLUMN_PRICE);
        if (price ==null){
            throw new IllegalArgumentException("invalid price");
        }
        Integer quantity = values.getAsInteger(BooksEntry.COLUMN_QUANTITY);
        if (quantity == null){
            throw new IllegalArgumentException("invalid quantity");

        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(BooksEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        } return ContentUris.withAppendedId(uri, id);

    }
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BooksEntry.COLUMN_ITEM)) {
            String name = values.getAsString(BooksEntry.COLUMN_ITEM);
            if (name == null) {
                throw new IllegalArgumentException("invalid name");
            }
        }

        if (values.containsKey(BooksEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(BooksEntry.COLUMN_PRICE);
            if (price != null && price < 0){
                throw new IllegalArgumentException("invalid price");
            }
        }
        if (values.containsKey(BooksEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(BooksEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("invalid quantity");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        return database.update(BooksEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
            case BOOKS_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BooksEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BooksEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}