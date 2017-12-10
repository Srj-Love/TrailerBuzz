package com.srjlove.trailerbuzz.interfaces;

import com.srjlove.trailerbuzz.databinding.ReviewRecyclerviewItemBinding;

/**
 * Created by Suraj on 11/24/2017.
 */

public interface ReviewListItemClickListener {
    // using it for Review Expandable Layout clicking items
     void onReviewListItemClicked(int position, ReviewRecyclerviewItemBinding mBinding);
}
