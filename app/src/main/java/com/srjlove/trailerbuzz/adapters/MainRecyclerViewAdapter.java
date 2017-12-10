package com.srjlove.trailerbuzz.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.activities.MainActivity;
import com.srjlove.trailerbuzz.databinding.RecyclerviewItemBinding;
import com.srjlove.trailerbuzz.model.MovieModel;

import java.util.ArrayList;

/**
 * Created by Suraj on 11/13/2017.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = MainRecyclerViewAdapter.class.getSimpleName();
    private MainListItemClickListener mClicked;
    private Context mContext;
    private ArrayList<MovieModel> mList;

    // interface
     public interface MainListItemClickListener{
        void onListItemClicked(int position);
    }

    public MainRecyclerViewAdapter(MainListItemClickListener mClicked, Context mContext, ArrayList<MovieModel> mList) {
        this.mClicked = mClicked;
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerviewItemBinding mViee = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.recyclerview_item, parent, false);
        return new ViewHolder(mViee);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mBinding.rvImageItem.setMinimumHeight(MainActivity.targetY); // just testing
        Picasso.with(mContext).load(mList.get(position).getMovie_poster()).placeholder(R.drawable.trending).error(R.drawable.fav).fit().into(holder.mBinding.rvImageItem);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private RecyclerviewItemBinding mBinding;

        ViewHolder(RecyclerviewItemBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
            mBinding.getRoot().setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: attempt to click items");
            int position = getAdapterPosition();
            mClicked.onListItemClicked(position);
            Toast.makeText(mContext, "Clicked at position :" + position, Toast.LENGTH_SHORT).show();
        }
    }
}
