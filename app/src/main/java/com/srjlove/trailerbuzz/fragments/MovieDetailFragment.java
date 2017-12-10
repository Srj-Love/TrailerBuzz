package com.srjlove.trailerbuzz.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.adapters.ReviewRecyclerViewAdapter;
import com.srjlove.trailerbuzz.adapters.TrailerRecyclerViewAdapter;
import com.srjlove.trailerbuzz.adapters.Utils;
import com.srjlove.trailerbuzz.databinding.FragmentMovieDetailBinding;
import com.srjlove.trailerbuzz.databinding.ReviewRecyclerviewItemBinding;
import com.srjlove.trailerbuzz.databinding.TrailerRecyclerviewItemBinding;
import com.srjlove.trailerbuzz.interfaces.ReviewListItemClickListener;
import com.srjlove.trailerbuzz.interfaces.TrailerListItemClickListener;
import com.srjlove.trailerbuzz.model.MovieModel;
import com.srjlove.trailerbuzz.model.Review;
import com.srjlove.trailerbuzz.model.Trailer;
import com.srjlove.trailerbuzz.networkHelper.NetworkAsyncTaskLoader;
import com.srjlove.trailerbuzz.provider.MoviesReaderConstantContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, TrailerListItemClickListener, ReviewListItemClickListener {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    // defining
    private final int MOVIES_DETAIL_LOADER_ID = 33;
    private FragmentMovieDetailBinding mBinding;
    private ProgressBar mBar;
    private JSONObject response;
    private Context mContext;
    private FragmentActivity mActivity;
    private int MOVIE_ID;
    private boolean isFavorite;
    private Toast mToast;
    private ArrayList<Trailer> mTrailerArrayList;
    private ArrayList<Review> mReviewArrayList;
    private StringBuilder URL;
    private MovieModel model;

    public MovieDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MOVIE_ID = getArguments().getInt(getString(R.string.movie_id_string));
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false);
        mActivity = getActivity();
        mContext = getContext();

        mBar = mBinding.progressBarMovieDetailFragment;
        FrameLayout mFrameLayout = mActivity.findViewById(R.id.detail_fragment_container);

        if (Utils.isNetworkAvailable(mActivity)) {
            if (mFrameLayout != null) { // if there is something on container
                mFrameLayout.removeAllViewsInLayout(); // removing all views
                makeNetworkRequest();
            }
        } else { // network is not Available
            if (mFrameLayout != null) {
                Utils.inflateOfflineLayout(mContext, mFrameLayout); // inflating offline layout
            }
        }

        setOnClickListenerFavouriteMeImage(); // if user clicked favorite

        return mBinding.getRoot();
    }

    private void setOnClickListenerFavouriteMeImage() {
        mBinding.ivAddToFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite) {
                    //  user is not set movie to favorite, set it favorite
                    ContentValues cv = new ContentValues(); // model: we don't have to initialise const.it has in makeNetwork()

                    cv.put(MoviesReaderConstantContract.MovieEntry.MOVIE_ID_COLUMN, model.getMovie_id());
                    cv.put(MoviesReaderConstantContract.MovieEntry.MOVIE_OVERVIEW_COLUMN, model.getMovie_overview());
                    cv.put(MoviesReaderConstantContract.MovieEntry.MOVIE_POSTER_COLUMN, model.getMovie_poster());
                    cv.put(MoviesReaderConstantContract.MovieEntry.MOVIE_RUNTIME_COLUMN, model.getMovie_runtime());
                    cv.put(MoviesReaderConstantContract.MovieEntry.MOVIE_ORIGINAL_TITLE_COLUMN, model.getMovie_original_title());
                    cv.put(MoviesReaderConstantContract.MovieEntry.MOVIE_RELEASE_DATE_COLUMN, "2017-02-05");
                    cv.put(MoviesReaderConstantContract.MovieEntry.MOVIE_VOTE_AVERAGE_COLUMN, model.getMovie_vote_average());
                    try {
                        Uri mUri = mContext.getContentResolver().insert(MoviesReaderConstantContract.MovieEntry.CONTENT_URI, cv);
                        if (mUri != null) { // successfully added in favourite


                            isFavorite = true;
                            mBinding.ivAddToFavourite.setImageResource(R.drawable.ic_favorite_green_24dp);
                            Toast.makeText(mContext, "Movie is added to Favorite :)", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onClick: added movie from db");
                        }
                    } catch (UnsupportedOperationException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, getString(R.string.some_thing_wrong), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int deleteRecord = mContext.getContentResolver().delete(MoviesReaderConstantContract.MovieEntry.CONTENT_URI, MoviesReaderConstantContract.MovieEntry.MOVIE_ORIGINAL_TITLE_COLUMN + " =?", new String[]{model.getMovie_original_title()});
                    isFavorite = false;
                    if (deleteRecord >= 1) {
                        mBinding.ivAddToFavourite.setImageResource(R.drawable.ic_favorite_orange_24dp);
                        Log.d(TAG, "onClick: Removed movie from db");
                        Toast.makeText(mContext, "Movie Removed from Favourites!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, getString(R.string.some_thing_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //  retrieving  URL in fully formatted path in StringBuilder Format
    private void makeNetworkRequest() {
        /*
         * (@link:URL): retrieving  URL in fully formatted path in StringBuilder Format
          * eg:https://api.themoviedb.org/3/movie/22?api_key=60ccacdd28ba7052cf2de5b35ff020a9&append_to_response=videos,reviews
         */
        URL = Utils.getStringBuilderOfDetailsURL(mContext, MOVIE_ID);
        Log.i(TAG, "makeNetworkRequest" + URL.toString());
        LoaderManager manager = mActivity.getSupportLoaderManager();
        Loader<String> mLoader = manager.getLoader(MOVIES_DETAIL_LOADER_ID);
        if (mLoader == null) { // there is problem with loader either inti or restart it
            manager.initLoader(MOVIES_DETAIL_LOADER_ID, null, this).forceLoad();
        } else {
            manager.restartLoader(MOVIES_DETAIL_LOADER_ID, null, this).forceLoad();
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new NetworkAsyncTaskLoader(mContext, URL.toString()); // will give me json in string format
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) { // data give me json in string format

        try {
            response = new JSONObject(data);
            setBasicDataOfMovie(); // setting up all the views in fragment_movie_details
            setTrailerMovie(); // setting up all the Trailer views in fragment_movie_details
            setReviewMovie(); // setting up all the Review views in fragment_movie_details


            mBar.setVisibility(View.INVISIBLE);
            mBinding.clMovieDetails.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // handling body section
    private void setBasicDataOfMovie() throws JSONException {
        model = new MovieModel();

        // getting all responses from Loader<String> data finished callback
        String poster_path = response.getString("poster_path");
        String release_date = response.getString("release_date");
        double vote_average = response.getDouble("vote_average");// this is in numeric format
        String runtime = response.getString("runtime");
        String original_title = response.getString("original_title");
        String overview = response.getString("overview");
        StringBuilder base_url = new StringBuilder("http://image.tmdb.org/t/p/");
        base_url.append("w185/").append(poster_path);

        // assigning all response to views
        model.setMovie_id(MOVIE_ID);
        model.setMovie_poster(base_url.toString());
        model.setMovie_original_title(original_title);
        model.setMovie_runtime(runtime);
        model.setMovie_vote_average(Float.toString((float) (vote_average / 2))); // convert into float
        model.setMovie_overview(overview);

        mBinding.tvRealseDate.setText(release_date);
        mBinding.tvMovieDuration.setText(String.format("%s min.", runtime));
        mBinding.rbMovieRating.setRating((float) (vote_average / 2));// convert into float
        mBinding.tvOverviewOfMovie.setText(overview);

        Toolbar toolbar = mActivity.findViewById(R.id.tb_movie_details_activity);
        toolbar.setTitle(model.getMovie_original_title());


        Log.i(TAG, "Movie_Poster_Url" + base_url.toString());

        // tag: followed picasso tag for associate request
        Picasso.with(mContext).load(model.getMovie_poster()).fit().tag(mActivity.getBaseContext()).into(mBinding.ivMoviePoster);


        android.support.v7.widget.Toolbar mToolbar = mActivity.findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle(original_title);
        }

    }


    // handling trailer section
    private void setTrailerMovie() {
        try {
            JSONArray mTrailerArray = response.getJSONObject("videos").getJSONArray("results");
            mTrailerArrayList = new ArrayList<>(mTrailerArray.length());
            for (int i = 0; i < mTrailerArray.length(); i++) {
                Trailer mTrailer = new Trailer();
                JSONObject mLooping_Array = (JSONObject) mTrailerArray.get(i); // will fetch data from no. of JsonArray index
                String mKey = mLooping_Array.getString("key");

                //http://img.youtube.com/vi/Wfql_DoHRKc/0.jpg
                StringBuilder mThumbURL = new StringBuilder("http://img.youtube.com/vi/").append(mKey).append("/0.jpg");
                StringBuilder mVideoUrl = new StringBuilder("https://www.youtube.com/watch?v=").append(mKey);
                mTrailer.setVideoThumbURL(mThumbURL.toString());
                mTrailer.setVideoURL(mVideoUrl.toString());

                mTrailerArrayList.add(mTrailer); // adding in mList for RecyclerView Adapter
            }

            if (mTrailerArray.length() != 0) {
                mBinding.rvTrailors.setAdapter(new TrailerRecyclerViewAdapter(this, mContext, mTrailerArrayList));
                mBinding.rvTrailors.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.GAP_HANDLING_NONE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    // handling review section
    private void setReviewMovie() {
        mReviewArrayList = new ArrayList<>();
        JSONArray mReviewArray = null;
        try {
            mReviewArray = response.getJSONObject("reviews").getJSONArray("results");
            for (int i = 0; i < mReviewArray.length(); i++) {
                Review mReview = new Review();
                JSONObject mLooping_Array = (JSONObject) mReviewArray.get(i);
                String author = mLooping_Array.getString("author");
                String content = mLooping_Array.getString("content");

                mReview.setAuthor(author);
                mReview.setReviewContent(content);
                mReviewArrayList.add(mReview);
            }
            if (mReviewArray.length() != 0) {
                mBinding.rvReviews.setAdapter(new ReviewRecyclerViewAdapter(this, mContext, mReviewArrayList));
                mBinding.rvReviews.setLayoutManager(new LinearLayoutManager(mContext));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onTrailerListItemClicked(int position, TrailerRecyclerviewItemBinding mBinding) {
        String url = mTrailerArrayList.get(position).getVideoURL();
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (i.resolveActivity(mContext.getPackageManager()) != null) {
            startActivity(i);
        }

    }

    @Override
    public void onReviewListItemClicked(int position, ReviewRecyclerviewItemBinding mBinding) {

        if (mBinding.expandableLayout.isExpanded()) {
            mBinding.expandableLayout.collapse();
            mBinding.ivArrowDropArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        } else {
            mBinding.expandableLayout.expand();
            mBinding.ivArrowDropArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
        }
    }
}
