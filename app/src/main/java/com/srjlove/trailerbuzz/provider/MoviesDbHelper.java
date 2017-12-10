package com.srjlove.trailerbuzz.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Suraj on 11/22/2017.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";
    private static MoviesDbHelper mInstance;

    // required for instantiating
    private MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static synchronized MoviesDbHelper getInstance(Context mContext) {
        /*
          use mContext.getApplicationContext(), which will ensures that
          u don't accidentally leak an Activity context.
          referring from (@link):https://android-developers.googleblog.com/2009/01/avoiding-memory-leaks.html
         */
        if (mInstance == null) mInstance = new MoviesDbHelper(mContext.getApplicationContext());
        return mInstance;
    }

    // creating table schema
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MoviesReaderConstantContract.MovieEntry.TABLE_NAME
                + "("
                + MoviesReaderConstantContract.MovieEntry.MOVIE_ID_COLUMN + " INTEGER PRIMARY KEY,"
                + MoviesReaderConstantContract.MovieEntry.MOVIE_ORIGINAL_TITLE_COLUMN + " TEXT NOT NULL,"
                + MoviesReaderConstantContract.MovieEntry.MOVIE_OVERVIEW_COLUMN + " TEXT NOT NULL,"
                + MoviesReaderConstantContract.MovieEntry.MOVIE_RUNTIME_COLUMN + " TEXT NOT NULL,"
                + MoviesReaderConstantContract.MovieEntry.MOVIE_RELEASE_DATE_COLUMN + " TEXT NOT NULL,"
                + MoviesReaderConstantContract.MovieEntry.MOVIE_VOTE_AVERAGE_COLUMN + " TEXT NOT NULL,"
                + MoviesReaderConstantContract.MovieEntry.MOVIE_POSTER_COLUMN + " TEXT NOT NULL"
                + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesReaderConstantContract.MovieEntry.TABLE_NAME);
        onCreate(db);

    }
}
