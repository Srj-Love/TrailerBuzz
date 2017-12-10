package com.srjlove.trailerbuzz.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.activities.DetailFavouriteActivity;
import com.srjlove.trailerbuzz.adapters.MainRecyclerViewAdapter;
import com.srjlove.trailerbuzz.adapters.Utils;
import com.srjlove.trailerbuzz.databinding.FragmentFavoriteBinding;
import com.srjlove.trailerbuzz.model.MovieModel;
import com.srjlove.trailerbuzz.provider.MoviesReaderConstantContract;

import org.parceler.Parcels;

import java.util.ArrayList;


/**
 * Created by Suraj on 11/23/2017.
 */

public class FavoriteFragment extends Fragment implements MainRecyclerViewAdapter.MainListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = FavoriteFragment.class.getSimpleName();
    private final int FAVORITE_LOADER_ID = 44;
    private FragmentFavoriteBinding mBinding;
    private ProgressBar mBar;
    private Context mContext;
    private Parcelable mLayoutManagerSavedState;
    private GridLayoutManager mLManager;
    private ArrayList<MovieModel> mList;
    private FragmentActivity mActivity;
    private SQLiteDatabase mDatabase;

    public FavoriteFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);
        mActivity = getActivity();
        mContext = getContext();
        mBar = mActivity.findViewById(R.id.progress_bar_main_activity);
        return mBinding.getRoot();
    }

    // load the mBar onStarting fragment
    @Override
    public void onStart() {
        super.onStart();
        mBar.setVisibility(View.INVISIBLE);
        LoaderManager manager = mActivity.getSupportLoaderManager();
        Loader<String> favorite_loader = manager.getLoader(FAVORITE_LOADER_ID);
        if (favorite_loader == null)
            manager.initLoader(FAVORITE_LOADER_ID, null, this).forceLoad();
        else manager.restartLoader(FAVORITE_LOADER_ID, null, this).forceLoad();
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


    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(mContext) {
            @Override
            public Cursor loadInBackground() {
                try {
                    return mContext.getContentResolver().
                            query(MoviesReaderConstantContract.MovieEntry.CONTENT_URI, null, null, null, MoviesReaderConstantContract.MovieEntry.MOVIE_RELEASE_DATE_COLUMN);
                } catch (UnsupportedOperationException e) {

                    e.printStackTrace();
                    return null;
                }
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {

        if (mCursor == null) Toast.makeText(mContext, "Something wrong", Toast.LENGTH_SHORT).show();
        int nomber_of_movies = mCursor != null ? mCursor.getCount() : 0;
        if (nomber_of_movies == 0) {
            return;
        } else {
            mList = new ArrayList<>(nomber_of_movies);  // capacity of ArrayList
            mCursor.moveToFirst();
            for (int i = 0; i < nomber_of_movies; i++) {
                MovieModel model = new MovieModel();
                model.setMovie_id(mCursor.getInt(mCursor.getColumnIndex(MoviesReaderConstantContract.MovieEntry.MOVIE_ID_COLUMN)));
                model.setMovie_original_title(mCursor.getString(mCursor.getColumnIndex(MoviesReaderConstantContract.MovieEntry.MOVIE_ORIGINAL_TITLE_COLUMN)));
                model.setMovie_poster(mCursor.getString(mCursor.getColumnIndex(MoviesReaderConstantContract.MovieEntry.MOVIE_POSTER_COLUMN)));
                model.setMovie_overview(mCursor.getString(mCursor.getColumnIndex(MoviesReaderConstantContract.MovieEntry.MOVIE_OVERVIEW_COLUMN)));
                model.setMovie_reales_date(mCursor.getString(mCursor.getColumnIndex(MoviesReaderConstantContract.MovieEntry.MOVIE_RELEASE_DATE_COLUMN)));
                model.setMovie_runtime(mCursor.getString(mCursor.getColumnIndex(MoviesReaderConstantContract.MovieEntry.MOVIE_RUNTIME_COLUMN)));
                model.setMovie_vote_average(mCursor.getString(mCursor.getColumnIndex(MoviesReaderConstantContract.MovieEntry.MOVIE_VOTE_AVERAGE_COLUMN)));
                mList.add(model);
                mCursor.moveToNext();
            }
            mCursor.close();
        }

        FrameLayout mFrameLayout = mActivity.findViewById(R.id.main_container);
        if (mList != null) {
            mBinding.rvFavorite.setAdapter(new MainRecyclerViewAdapter(this, mContext, mList));
            mLManager = new GridLayoutManager(mContext, 2);
            mBinding.rvFavorite.setLayoutManager(mLManager);
            scrollTOTargetPosition();
        } else {
            Utils.inflateNoFavouritesLayout(mContext, mFrameLayout);
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
    public void onLoaderReset(Loader<Cursor> loader) {
        // nothing have to do special here
    }


    //refering from : https://github.com/johncarl81/parceler easy to use
    @Override
    public void onListItemClicked(int position) {

            Intent intent = new Intent(mActivity, DetailFavouriteActivity.class);
            // sending mList using the feature of Parcels.wrap(Movie) / Parcels.wrap(mList.get(position)));
            intent.putExtra(getString(R.string.movie_id_string_parcel), Parcels.wrap(mList.get(position)));
            startActivity(intent);

    }


}
