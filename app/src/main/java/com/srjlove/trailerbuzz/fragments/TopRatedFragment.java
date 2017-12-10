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
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.activities.MovieDetailsActivity;
import com.srjlove.trailerbuzz.adapters.MainRecyclerViewAdapter;
import com.srjlove.trailerbuzz.adapters.Utils;
import com.srjlove.trailerbuzz.databinding.FragmentTopRatedBinding;
import com.srjlove.trailerbuzz.model.MovieModel;
import com.srjlove.trailerbuzz.networkHelper.NetworkAsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Suraj on 11/23/2017.
 */

public class TopRatedFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, MainRecyclerViewAdapter.MainListItemClickListener {

    private static final String TAG = TopRatedFragment.class.getSimpleName();
    private final int TOP_RATED_LOADER_ID = 22;
    private FragmentTopRatedBinding mBinding;
    private ProgressBar mBar;
    private Context mContext;
    private Parcelable mLayoutManagerSavedState;
    private StaggeredGridLayoutManager mLManager;
    private ArrayList<MovieModel> mList;
    private FragmentActivity mActivity;

    public TopRatedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_rated, container, false);
        mActivity = getActivity();
        mContext = getContext();
        mBar = mActivity.findViewById(R.id.progress_bar_main_activity);
        FrameLayout mFrameLayout = mActivity.findViewById(R.id.main_container);

        // check for network
        if (Utils.isNetworkAvailable(mActivity)) {
            mFrameLayout.removeAllViewsInLayout();// this simply remove all views from view-group
            makeNetworkRequest();
        } else {
            Utils.inflateOfflineLayout(mContext, mFrameLayout);
        }

        return mBinding.getRoot();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mLManager != null) {
            outState.putParcelable((mActivity.getResources().getString(R.string.recycler_parcelable)), mLManager.onSaveInstanceState());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mLayoutManagerSavedState = savedInstanceState.getParcelable(mActivity.getResources().getString(R.string.recycler_parcelable));
        }
    }

    /* This () will simply make the network call by initialising loader callback in force mode*/
    private void makeNetworkRequest() {
        mBar.setVisibility(View.VISIBLE);
        LoaderManager manager = mActivity.getSupportLoaderManager();
        Loader<String> top_rated_loader = manager.getLoader(TOP_RATED_LOADER_ID);
        if (top_rated_loader == null)
            manager.initLoader(TOP_RATED_LOADER_ID, null, this).forceLoad();
        else manager.restartLoader(TOP_RATED_LOADER_ID, null, this).forceLoad();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new NetworkAsyncTaskLoader(getContext(), Utils.URLs[1]);// load the url n fetch json string
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        JSONObject mJsonObject;
        try {
            mJsonObject = new JSONObject(data);
            JSONArray mMoviesJsonArray = mJsonObject.getJSONArray("results");
            mList = Utils.extractMovieDataFromJSON(mMoviesJsonArray);
            mBinding.rvTopRated.setAdapter(new MainRecyclerViewAdapter(this, getContext(), mList));
            mLManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mBinding.rvTopRated.setLayoutManager(mLManager);
            scrollTOTargetPosition();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mBar.setVisibility(View.INVISIBLE);
    }

    /*  This () simply retrieve the position saved in Parcelable */
    private void scrollTOTargetPosition() {
        if (mLayoutManagerSavedState != null) {
            mLManager.onRestoreInstanceState(mLayoutManagerSavedState);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    @Override
    public void onListItemClicked(int position) {
            startActivity(new Intent(mActivity, MovieDetailsActivity.class).putExtra(getString(R.string.movie_id_string), mList.get(position).getMovie_id()));

    }
}
