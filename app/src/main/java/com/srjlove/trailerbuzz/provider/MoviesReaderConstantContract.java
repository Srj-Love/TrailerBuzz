package com.srjlove.trailerbuzz.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Suraj on 11/22/2017.
 */

public class MoviesReaderConstantContract {

    public static final String AUTHORITY = "com.srjlove.trailerbuzz.MoviesContentProvider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_NAME = MovieEntry.TABLE_NAME;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.


    public MoviesReaderConstantContract() {
    }

    // Inner class that defines the movie database table contents

    /**
     * BaseColumns is ussually used for db creation for monitoring the table with _ID
     */
    public static class MovieEntry implements BaseColumns {

        //main CONTENT_URI
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_NAME).build();

        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_ID_COLUMN = "_id";
        public static final String MOVIE_OVERVIEW_COLUMN = "movieOverview";
        public static final String MOVIE_RELEASE_DATE_COLUMN = "releaseDate";
        public static final String MOVIE_RUNTIME_COLUMN = "movieRuntime";
        public static final String MOVIE_ORIGINAL_TITLE_COLUMN = "originalTitle";
        public static final String MOVIE_VOTE_AVERAGE_COLUMN = "voteAverage";
        public static final String MOVIE_POSTER_COLUMN = "moviePoster";

    }
}
