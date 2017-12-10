package com.srjlove.trailerbuzz.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.srjlove.trailerbuzz.fragments.MovieDetailFragment;
import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.databinding.ActivityMovieDetailsBinding;

public class MovieDetailsActivity extends AppCompatActivity {

    ActivityMovieDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        setSupportActionBar(mBinding.tbMovieDetailsActivity);
        mBinding.tbMovieDetailsActivity.setTitle("Movie");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // get Intent movies_id from respected Fragment
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            if (mBundle.containsKey(getString(R.string.movie_id_string))) {
                int id = mBundle.getInt(getString(R.string.movie_id_string));// get the id and send to fragment
                Bundle id_bundle = new Bundle();
                id_bundle.putInt(getString(R.string.movie_id_string), id);
                android.support.v4.app.Fragment mFragment = new MovieDetailFragment();
                mFragment.setArguments(mBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, mFragment).commit();

            }
        }

    }
}
