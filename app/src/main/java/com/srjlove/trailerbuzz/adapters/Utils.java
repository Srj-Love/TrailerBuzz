package com.srjlove.trailerbuzz.adapters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.model.MovieModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Suraj on 11/14/2017.
 */

public class Utils {
    public static final String TAG = "mi5a";
    public static String URLs[];

    // checking network available or not
    public static boolean isNetworkAvailable(FragmentActivity mActivity) {
//        ConnectivityManager manager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (manager != null) {
//            NetworkInfo mrActiveNetworkInfo = manager.getActiveNetworkInfo();
//            return (mrActiveNetworkInfo != null && mrActiveNetworkInfo.isConnected());
//        } else {
//            Toast.makeText(mActivity, "Connection not Available", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        ConnectivityManager connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // setting URL for PF, TRF
    public static void ConstructURLs(Context mContext) {
        String API_KEY = mContext.getResources().getString(R.string.THE_MOVIE_DB_API_TOKEN);
        URLs = new String[2];
        URLs[0] = new String("http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY);
        URLs[1] = new String("http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY);
    }

    // configuring URL in fully formated path in StringBuilder Format
    // eg: = https://api.themoviedb.org/3/movie/22?api_key=60ccacdd28ba7052cf2de5b35ff020a9&append_to_response=videos,reviews
    public static StringBuilder getStringBuilderOfDetailsURL(Context mContext, int moive_id) {

        final String API_KEY = mContext.getResources().getString(R.string.THE_MOVIE_DB_API_TOKEN);
        Toast.makeText(mContext, "Movie_id :"+moive_id, Toast.LENGTH_SHORT).show();
        return new StringBuilder
                ("https://api.themoviedb.org/3/movie/").append(moive_id).append("?api_key=").append(API_KEY).append("&append_to_response=videos,reviews");
    }

    // refreshing the layout if connection is offline
    public static void inflateOfflineLayout(Context context, FrameLayout frameLayout) {
        frameLayout.removeAllViewsInLayout();
        frameLayout.addView(LayoutInflater.from(context).inflate(R.layout.offline_layout, frameLayout, false));
    }

    // inflating no fav layout
    public static void inflateNoFavouritesLayout(Context context, FrameLayout frameLayout) {
        frameLayout.removeAllViewsInLayout();
        frameLayout.addView(LayoutInflater.from(context).inflate(R.layout.no_favourites, frameLayout, false));
    }


    /**
     *
     * @param mJsonArray getting the Array of json data from url
     * @return list of movies in which have set only poster and id
     * poppular query : https://developers.themoviedb.org/3/movies/get-popular-movies
     * top rated query : https://developers.themoviedb.org/3/movies/get-top-rated-movies
     */
    public static ArrayList<MovieModel> extractMovieDataFromJSON(JSONArray mJsonArray) {

        int arrayLength = mJsonArray.length();
        ArrayList<MovieModel> mList = new ArrayList<>(arrayLength);// fixed size
        // extracting data
        for (int i = 0; i < arrayLength; i++) {
            MovieModel model = new MovieModel();
            try {
                JSONObject mMovie = mJsonArray.getJSONObject(i);
                // http://image.tmdb.org/t/p/w185/9E2y5Q7WlCVNEhP5GiVTjhEhx1o.jpg
                StringBuilder mBuilder = new StringBuilder("http://image.tmdb.org/t/p/");//all posteers of movies are resides here
                mBuilder.append("w185/").append(mMovie.getString("poster_path"));
                model.setMovie_poster(mBuilder.toString());
                model.setMovie_id(mMovie.getInt("id"));
                mList.add(model);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mList;
    }
}
