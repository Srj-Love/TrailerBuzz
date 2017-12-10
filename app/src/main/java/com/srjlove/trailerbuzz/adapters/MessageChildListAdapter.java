package com.srjlove.trailerbuzz.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.databinding.ChatRowBinding;
import com.srjlove.trailerbuzz.model.Message;

import java.util.ArrayList;

/**
 * Created by Suraj on 12/5/2017.
 */

public class MessageChildListAdapter extends RecyclerView.Adapter<MessageChildListAdapter.MyViewHolder> {

    private static int position; // will simply give me a position of items;
    public DatabaseReference mReference;
    private Context mContext;
    private String name;
    private ArrayList<DataSnapshot> mSnapshotList;

    public ChatRowBinding mBinding;
    private String Tag = MessageChildListAdapter.class.getSimpleName();

    public MessageChildListAdapter(Context mContext, DatabaseReference mReference, String name) {
        this.mReference = mReference;
        this.mContext = mContext;
        mReference = mReference.child(mContext.getString(R.string.msg));
        mReference.addChildEventListener(mListener); // managing offline getting data every time
        this.name = name;
        mSnapshotList = new ArrayList<>(); // trying to get all FB msg in array List
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.chat_row, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MessageChildListAdapter.position = holder.getAdapterPosition();


        DataSnapshot mSnapshot = mSnapshotList.get(holder.getAdapterPosition());// i retrieved the position from FB
        Message message = mSnapshot.getValue(Message.class); // needs object.class wich one we have passed to FB to retrieve it back
        assert message != null;
        mBinding.author.setText(message.getAuthor());
        mBinding.message.setText(message.getMsg());
        Log.d(Tag, "onBindViewHolder: message.getImgeUrl(): "+ message.getImgeUrl());
        Picasso.with(mContext).load(Uri.parse(message.getImgeUrl())).placeholder(R.drawable.ic_sign_out).fit().into(mBinding.ivPic, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(Tag,"successfully load the image");
            }

            @Override
            public void onError() {
                Log.d(Tag,"fail to load the image");
            }
        });

        boolean isItMe = message.getAuthor().equals(name);
        settingChatRowAccordingMeOrSomeoneElse(isItMe, holder);

    }

    /**
     * @param isItMe if true then my text will appear in rigth else in left
     * @param holder required for view
     */
    private void settingChatRowAccordingMeOrSomeoneElse(boolean isItMe, MyViewHolder holder) {
        // refering from : https://stackoverflow.com/questions/46882663/firebase-friendly-chat-chat-bubbles-left-and-right
        if (isItMe) {
            holder.params.gravity = Gravity.END; // left side
            mBinding.message.setTextColor(Color.BLUE);
           // holder.msg.setBackgroundResource(R.drawable.message_text); // will use 9patch image later on
        } else {
            holder.params.gravity = Gravity.START;
            mBinding.message.setTextColor(Color.BLACK);
           // mBinding.message.setBackgroundResource(R.drawable.message_text); // will use 9patch image later on
        }
        mBinding.author.setLayoutParams((holder.params));
        mBinding.message.setLayoutParams(((holder.params)));
        mBinding.ivPic.setLayoutParams((holder.params));
    }

    /**
     * care becouse of I want to store all values in mList
     */

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            mSnapshotList.add(dataSnapshot);

            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
 /*mSnapshotList.remove(dataSnapshot);
            notifyDataSetChanged();*/
            Toast.makeText(mContext, "Item  removes now size is: " + mSnapshotList.size(), Toast.LENGTH_SHORT).show();


            mSnapshotList.remove(position); // get from View

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mSnapshotList.size());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public int getItemCount() {
        return mSnapshotList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView author, msg;
        LinearLayout.LayoutParams params;

        public MyViewHolder(View view) {
            super(view);
            //  LayoutParams will give me the LayoutParams associated with this view
            params = (LinearLayout.LayoutParams)
                    mBinding.author.getLayoutParams();
        }

    }

    public void cleanUp(){
        mReference.removeEventListener(mListener);
    }

}
