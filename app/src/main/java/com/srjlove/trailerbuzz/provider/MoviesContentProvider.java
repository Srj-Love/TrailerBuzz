package com.srjlove.trailerbuzz.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Suraj on 11/22/2017.
 */

public class MoviesContentProvider extends ContentProvider {

    private static final String TAG = "MoviesContentProvider";

    private static final UriMatcher mURI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int ALL_TABLE = 1;
    private static final int WILDCARD_NUMERIC = 2;
    private static final int WILDCARD_STRING_NUMERIC = 3;
    private MoviesDbHelper moviesDbHelper;

    static {
        mURI_MATCHER.addURI(MoviesReaderConstantContract.AUTHORITY, MoviesReaderConstantContract.PATH_NAME, ALL_TABLE);
        mURI_MATCHER.addURI(MoviesReaderConstantContract.AUTHORITY, MoviesReaderConstantContract.PATH_NAME + "/#", WILDCARD_STRING_NUMERIC);
        mURI_MATCHER.addURI(MoviesReaderConstantContract.AUTHORITY, MoviesReaderConstantContract.PATH_NAME + "/*", WILDCARD_NUMERIC);
    }


    @Override
    public boolean onCreate() {
        moviesDbHelper = MoviesDbHelper.getInstance(getContext());
        return true; // return true; :means successfully completed the initialization process
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase mDatabase = moviesDbHelper.getReadableDatabase();
        Cursor mCursor = null;
        switch (mURI_MATCHER.match(uri)) {
            case ALL_TABLE:
                mCursor = mDatabase.query(MoviesReaderConstantContract.MovieEntry.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case WILDCARD_NUMERIC:
                break;
            case WILDCARD_STRING_NUMERIC:
                break;
            default:
                throw new IllegalArgumentException("Please choose rigth one " + uri.toString());
        }

        assert mCursor != null;
        mCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return mCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        switch (mURI_MATCHER.match(uri)) {
            case ALL_TABLE:
                return insertRow(uri, values);
            default:
                throw new IllegalArgumentException("Inserting Unknown Uri " + uri);
        }

    }

    private Uri insertRow(Uri uri, ContentValues values) {
        SQLiteDatabase mDatabase = moviesDbHelper.getWritableDatabase();
        long rowId = mDatabase.insert(MoviesReaderConstantContract.MovieEntry.TABLE_NAME, null, values);
        if (rowId == -1) {
            Log.i(TAG, "Something wrong while inserting row" + uri);
            return null;
        } else {
            getContext().getContentResolver().notifyChange(uri, null); // this simply notifes our activity that new row has inserted and pls update the cursor
        }
        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        return deleteRecords(uri, selection,selectionArgs);
    }

    private int deleteRecords(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase mDatabase = moviesDbHelper.getWritableDatabase();
        int rowDeleted = mDatabase.delete(MoviesReaderConstantContract.MovieEntry.TABLE_NAME,selection,selectionArgs);
        if (rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null); // this simply notifes our activity that row has deleted and pls update the cursor
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {


        return rowUpdated(uri,values,selection,selectionArgs);
    }

    private int rowUpdated(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase mDatabase = moviesDbHelper.getWritableDatabase();
        int rowUpdated = mDatabase.update(MoviesReaderConstantContract.MovieEntry.TABLE_NAME,values,selection,selectionArgs);
        if (rowUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null); // this simply notifes our activity that row has deleted and pls update the cursor
        }
        return rowUpdated;
    }
}
