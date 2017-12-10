package com.srjlove.trailerbuzz.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.databinding.TrailerRecyclerviewItemBinding;
import com.srjlove.trailerbuzz.interfaces.TrailerListItemClickListener;
import com.srjlove.trailerbuzz.model.Trailer;

import java.util.ArrayList;

/**
 * Created by Suraj on 11/24/2017.
 */

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.MyViewHolder> {

    final private TrailerListItemClickListener mClickListener;
    private Context mContext;
    private ArrayList<Trailer> mList;

    public TrailerRecyclerViewAdapter(TrailerListItemClickListener mClickListener, Context mContext, ArrayList<Trailer> mList) {
        this.mClickListener = mClickListener;
        this.mContext = mContext;
        this.mList = mList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TrailerRecyclerviewItemBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.trailer_recyclerview_item,parent,false);
        return new MyViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.mBinding.rvTextViewItem.setText(mList.get(position).getVideoURL());
        Picasso.with(mContext).load(mList.get(position).getVideoThumbURL()).placeholder(R.drawable.trending).error(R.drawable.popular).fit().into(holder.mBinding.rvImage);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

     class MyViewHolder extends RecyclerView.ViewHolder{
        TrailerRecyclerviewItemBinding mBinding;
        private int position;
        private MyViewHolder(TrailerRecyclerviewItemBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;

            itemView.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    mClickListener.onTrailerListItemClicked(position, mBinding);
                }
            });
        }
    }
}
