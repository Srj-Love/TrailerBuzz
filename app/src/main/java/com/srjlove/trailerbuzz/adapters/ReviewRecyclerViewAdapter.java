package com.srjlove.trailerbuzz.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.databinding.ReviewRecyclerviewItemBinding;
import com.srjlove.trailerbuzz.interfaces.ReviewListItemClickListener;
import com.srjlove.trailerbuzz.model.Review;

import java.util.ArrayList;

/**
 * Created by Suraj on 11/24/2017.
 */

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.MyViewHolder> {


    final private ReviewListItemClickListener mClickListener;
    private Context mContext;
    private ArrayList<Review> mList;

    public ReviewRecyclerViewAdapter(ReviewListItemClickListener mClickListener, Context mContext, ArrayList<Review> mList) {
        this.mClickListener = mClickListener;
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReviewRecyclerviewItemBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.review_recyclerview_item, parent, false);
        return new MyViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mBinding.tvReviewsAuthor.setText(mList.get(position).getAuthor());
        holder.mBinding.tvExpandableLayout.setText(mList.get(position).getReviewContent());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


     class MyViewHolder extends RecyclerView.ViewHolder {
        public ReviewRecyclerviewItemBinding mBinding;
        private int position;

        private MyViewHolder(ReviewRecyclerviewItemBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
            itemView.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    mClickListener.onReviewListItemClicked(position, mBinding);
                }
            });
        }

    }


}
