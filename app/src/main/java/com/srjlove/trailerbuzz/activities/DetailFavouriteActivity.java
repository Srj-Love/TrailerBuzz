package com.srjlove.trailerbuzz.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.databinding.ActivityDetailFavouriteBinding;
import com.srjlove.trailerbuzz.fragments.FavouriteMovieDetailFragment;
import com.srjlove.trailerbuzz.model.MovieModel;

import org.parceler.Parcels;

public class DetailFavouriteActivity extends AppCompatActivity {

    private ActivityDetailFavouriteBinding mBinding;
    private static final String TAG = "DetailFavouriteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_favourite);
        setSupportActionBar(mBinding.tbMovieDetailsActivity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        Intent intent = getIntent();
        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(TAG, "onCreate: extra bundle is not null  ");

            if (extras.containsKey(getString(R.string.movie_id_string_parcel))) { // is my key right which I assuming to be

                // unwrapping mList which is type of model.class from FF
                MovieModel model = Parcels.unwrap(intent.getParcelableExtra(getString(R.string.movie_id_string_parcel)));

                // sending movie parcel to FMDF
                Bundle bundle = new Bundle();
                bundle.putParcelable(getString(R.string.movie_id_string_parcel), Parcels.wrap(model));

                android.support.v4.app.Fragment mFragment = new FavouriteMovieDetailFragment();
                mFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.details_fragment_container_favourites, mFragment)
                        .commit();
            }
        }
    }
}
