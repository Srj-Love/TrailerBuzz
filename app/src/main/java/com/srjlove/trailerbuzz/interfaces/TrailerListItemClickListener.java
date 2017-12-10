package com.srjlove.trailerbuzz.interfaces;

import com.srjlove.trailerbuzz.databinding.TrailerRecyclerviewItemBinding;

/**
 * Created by Suraj on 11/24/2017.
 */

public interface TrailerListItemClickListener {

    // using it for watch trailer clicking items
    void onTrailerListItemClicked(int position, TrailerRecyclerviewItemBinding mBinding);
}
