package com.srjlove.trailerbuzz.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.activities.DetailFavouriteActivity;
import com.srjlove.trailerbuzz.databinding.FragmentFavouriteMovieDetailBinding;
import com.srjlove.trailerbuzz.model.MovieModel;
import com.srjlove.trailerbuzz.provider.MoviesReaderConstantContract;

import org.parceler.Parcels;

/**
 * Created by Suraj on 11/27/2017.
 */

public class FavouriteMovieDetailFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "FavouriteMovieDetailFra";

    private FragmentFavouriteMovieDetailBinding mBinding;
    private MovieModel model;
    private FragmentActivity mActivity;
    private Context mContext;
    private boolean isFavorite;

    public FavouriteMovieDetailFragment() {
        // required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = Parcels.unwrap(getArguments().getParcelable(getString(R.string.movie_id_string_parcel)));
        Log.d(TAG, "onCreateView: got parcel from DetailedfavoriteActivity");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_movie_detail, container, false);
        mActivity = getActivity();
        mContext = getContext();

        Picasso.with(mContext.getApplicationContext()).load(model.getMovie_poster()).fit().into(mBinding.ivMoviePosterFavourite);

        mBinding.ivAddToFavouriteInFavourite.setImageResource(R.drawable.ic_favorite_green_24dp);
        mBinding.tvMovieDurationFavourite.setText(model.getMovie_runtime());
        mBinding.tvOverviewOfMovieFavourite.setText(model.getMovie_overview());
        mBinding.rbMovieRatingFavourite.setRating(Float.parseFloat(model.getMovie_vote_average()));
        mBinding.tvRealseDateFavourite.setText(model.getMovie_reales_date());
        Toolbar toolbar = mActivity.findViewById(R.id.tb_movie_details_activity);
        toolbar.setTitle(model.getMovie_original_title());

        setOnClickListenerFavouriteMeImage();

        return mBinding.getRoot();
    }

    private void setOnClickListenerFavouriteMeImage() {
        mBinding.ivAddToFavouriteInFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int deleteRecord = mContext.getContentResolver().delete(MoviesReaderConstantContract.MovieEntry.CONTENT_URI, MoviesReaderConstantContract.MovieEntry.MOVIE_ORIGINAL_TITLE_COLUMN + " =?", new String[]{model.getMovie_original_title()});
                if (deleteRecord >= 1) {
                    mBinding.ivAddToFavouriteInFavourite.setImageResource(R.drawable.ic_favorite_orange_24dp);
                    Log.d(TAG, "onClick: Removed movie from db");
                    Toast.makeText(mContext, "Movie Removed from Favourites!", Toast.LENGTH_SHORT).show();
                    mActivity.getSupportFragmentManager().beginTransaction().remove(new FavouriteMovieDetailFragment()).commit();
                    startActivity(new Intent(mActivity, DetailFavouriteActivity.class));
                } else {
                    Toast.makeText(mContext, getString(R.string.some_thing_wrong), Toast.LENGTH_SHORT).show();
                }
                //}
            }
        });
    }

}
