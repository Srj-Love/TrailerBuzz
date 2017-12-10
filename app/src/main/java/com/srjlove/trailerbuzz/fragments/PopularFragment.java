package com.srjlove.trailerbuzz.fragments;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.activities.MovieDetailsActivity;
import com.srjlove.trailerbuzz.adapters.MainRecyclerViewAdapter;
import com.srjlove.trailerbuzz.adapters.Utils;
import com.srjlove.trailerbuzz.databinding.FragmentPopularBinding;
import com.srjlove.trailerbuzz.model.MovieModel;
import com.srjlove.trailerbuzz.networkHelper.NetworkAsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.srjlove.trailerbuzz.adapters.Utils.URLs;

/**
 * A simple {@link Fragment} subclass.
 * loading data specifically for display in your Activity or Fragment with Loaders.
 * referring loaders from: https://medium.com/google-developers/making-loading-data-on-android-lifecycle-aware-897e12760832
 */

public class PopularFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, MainRecyclerViewAdapter.MainListItemClickListener {


    public PopularFragment() {
        // Required empty public constructor
    }

    private static final int POPULAR_LOADER_ID = 11;
    private ArrayList<MovieModel> mList;
    private FragmentPopularBinding mBinding;
    private Context mContext;
    private ProgressBar mBar;
    private Parcelable mLayoutManagerSavedState;
    private GridLayoutManager mGridLayoutManager;
    private FragmentActivity mActivity;

    private static final String TAG = "PopularFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_popular, container, false);
        Log.d(TAG, "onCreateView: enterrs in OnCreateView");
        mActivity = getActivity();
        mContext = getContext();
        mBar = mActivity.findViewById(R.id.progress_bar_main_activity);
        FrameLayout mFrameLayout = mActivity.findViewById(R.id.main_container);
        if (Utils.isNetworkAvailable(mActivity)) { // check if network available
            mFrameLayout.removeAllViewsInLayout();
            makeNetworkRequest();
        } else {
            Utils.inflateOfflineLayout(mContext, mFrameLayout); // inflating offline layout
        }
        return mBinding.getRoot();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mGridLayoutManager != null) {
            outState.putParcelable(mActivity.getResources()
                    .getString(R.string.recycler_parcelable), mGridLayoutManager.onSaveInstanceState());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mLayoutManagerSavedState =
                    savedInstanceState.getParcelable(mActivity.getResources().getString(R.string.recycler_parcelable));
        }
    }


    /*
    getting reference from https://stackoverflow.com/questions/20279216/asynctaskloader-basic-example-android
        for Loader, LoaderCallback, AsyncTasktLoader<>
     */
    // here i'minitialising AsyncTaskLoader
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
//        onCreateLoader() — here’s where you construct the actual Loader instance
        // http://api.themoviedb.org/3/movie/popular?api_key=my_api_key
        return new NetworkAsyncTaskLoader(mContext, URLs[0]);
    }


    /**
     * Think of this as AsyncTask onPostExecute method, the result from onCreateLoader will
     * be available in operationResult variable and here you can update UI with the data fetched
     * <p>
     * here i'm getting String result of json data through new NetworkAsyncTaskLoader(mContext, URLs[0])
     * and sending to extractMovieDataFromJSON which holds the responsiblity to convert json data in string readable format
     */
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        JSONObject mJsonObject;
        try {
            mJsonObject = new JSONObject(data);
            JSONArray mMoviesJsonArray = mJsonObject.getJSONArray("results");
            mList = Utils.extractMovieDataFromJSON(mMoviesJsonArray);
            setAdapterRecyclerView();
            scrollToTargetPosition();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mBar.setVisibility(View.INVISIBLE);
    }


    // srj don't bother about it,  Android Studio will override it for you
    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    @Override
    public void onListItemClicked(int position) {
        // as got the Id of respected movies from the internet in Utils.extractMovieDataFromJSON now send it to MDActivity
        startActivity(new Intent(mActivity, MovieDetailsActivity.class).putExtra(getString(R.string.movie_id_string), mList.get(position).getMovie_id()));
    }

    private void makeNetworkRequest() {
        mBar.setVisibility(View.VISIBLE);
        LoaderManager manager = getActivity().getSupportLoaderManager(); // getting instance
        Loader<String> popLoader = manager.getLoader(POPULAR_LOADER_ID);
        if (popLoader == null) {

            Log.d(TAG, "makeNetworkRequest: Init loader");
            manager.initLoader(POPULAR_LOADER_ID, null, this).forceLoad();
            /* (@link initLoader):Ensures a loader is initialized and active.
               If the loader doesn't already exist,  one is created and (if the activity/fragment is currently started)                     starts the loader. Otherwise the last created loader is re-used.
             */
        } else {
            //If a loader with the same id has previously been started it will automatically be destroyed when the new loader completes its work.
            Log.d(TAG, "makeNetworkRequest: restarting loader");
            manager.restartLoader(POPULAR_LOADER_ID, null, this).forceLoad();
        }
    }

    private void scrollToTargetPosition() {
        if (mLayoutManagerSavedState != null)
            mGridLayoutManager.onRestoreInstanceState(mLayoutManagerSavedState);
    }

    private void setAdapterRecyclerView() {

        //  mList contain only id and poster
        mBinding.rvPopular.setAdapter(new MainRecyclerViewAdapter(this, getContext(), mList));
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);// putting all in gridview

        mBinding.rvPopular.setLayoutManager(mGridLayoutManager);
    }

}
