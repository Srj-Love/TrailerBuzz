package com.srjlove.trailerbuzz.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.adapters.MessageChildListAdapter;
import com.srjlove.trailerbuzz.databinding.FragmentMainChatBinding;
import com.srjlove.trailerbuzz.model.Message;

/**
 * Created by Suraj on 12/4/2017.
 */

public class MainChatFragment extends android.support.v4.app.Fragment {

    private FragmentMainChatBinding mBinding;
    public static final String TAG = LoginFragment.class.getSimpleName();
    private FragmentActivity mActivity;
    private Context mContext;
    private static String USERNAME;
    private DatabaseReference mReference;
    private String IMAGE_PATH;
    private String DOWNLOAD_URI;
    private MessageChildListAdapter mAdapter;
    private FirebaseRecyclerAdapter<Message, MyViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_chat, container, false);
        //    mCBinding = DataBindingUtil.inflate(inflater, R.layout.chat_row, container, false);
        mActivity = getActivity();
        mContext = getContext();
        setHasOptionsMenu(true); // tell FM that I have TB the call to setHasOptionsMenu(true) is required for the onCreateOptionsMenu method being called in the fragment


        mReference = FirebaseDatabase.getInstance().getReference();
        setupDisplayName();   //Set up the display name and get the Firebase reference


        // mBinding.recyclerView.setAdapter(mAdapter);
        adapter = new FirebaseRecyclerAdapter<Message, MyViewHolder>(Message.class, R.layout.chat_row, MyViewHolder.class, mReference.child(mContext.getString(R.string.msg))) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Message model, int position) {
                viewHolder.author.setText(model.getAuthor());
                viewHolder.msg.setText(model.getMsg());
                Log.d(TAG, "populateViewHolder: message.getImgeUrl(): " + model.getImgeUrl());
                Picasso.with(mContext).load(Uri.parse(model.getImgeUrl())).placeholder(R.drawable.ic_sign_out).fit().into(viewHolder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "successfully load the image");
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "fail to load the image");
                    }
                });
                boolean isItMe = model.getAuthor().equals(setupDisplayName());
                settingChatRowAccordingMeOrSomeoneElse(isItMe, viewHolder);
            }

        };

        mBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        return mBinding.getRoot();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sign_out, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Add an OnClickListener to the sendButton to send a message
     */
    private void sendMessage() {
        Log.d(TAG, "sendMessage: I'm sending Something");
        // Grab the text the user typed in and push the message to Firebase
        String getTextFromUser = mBinding.messageInput.getText().toString();


        if (!getTextFromUser.equals("")) {

            Message message = new Message(USERNAME, getTextFromUser, setupDisplayImage());
            mReference.child(getString(R.string.msg)).push().setValue(message);
            mBinding.messageInput.setText("");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mBinding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setStackFromEnd(true);
        mBinding.recyclerView.setLayoutManager(manager);
        //mAdapter = new MessageChildListAdapter(mActivity, mReference, setupDisplayName());


        mBinding.recyclerView.setAdapter(adapter);

        //mBinding.recyclerView.invalidate();

    }

    /**
     * get the name to chatfrom user from SP
     */
    public String setupDisplayName() {
        SharedPreferences mPreferences = mActivity.getSharedPreferences(RegistrationFragment.CHAT_PREFS, Context.MODE_PRIVATE);
        Log.d(TAG, "setupDisplayName: default Name " + mPreferences.getString(RegistrationFragment.DISPLAY_NAME_KEY, ""));
        String defName = "Anonymous";
        return USERNAME = mPreferences.getString(RegistrationFragment.DISPLAY_NAME_KEY, defName);
    }


    public String setupDisplayImage() {
        SharedPreferences mPreferences = mActivity.getSharedPreferences(RegistrationFragment.CHAT_PREFS, Context.MODE_PRIVATE);
        Log.d(TAG, "setupDisplayName: default Name " + mPreferences.getString(RegistrationFragment.DISPLAY_NAME_KEY, ""));
        return mPreferences.getString(getString(R.string.download_uri), "");
    }


    /**
     * @param isItMe if true then my text will appear in rigth else in left
     * @param holder required for view
     */
    private void settingChatRowAccordingMeOrSomeoneElse(boolean isItMe, MyViewHolder holder) {
        // refering from : https://stackoverflow.com/questions/46882663/firebase-friendly-chat-chat-bubbles-left-and-right
        if (isItMe) {
            holder.params.gravity = Gravity.END; // left side
            holder.msg.setTextColor(Color.BLUE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                holder.mLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            // holder.msg.setBackgroundResource(R.drawable.message_text); // will use 9patch image later on
        } else {
            holder.params.gravity = Gravity.START;
            holder.msg.setTextColor(Color.BLACK);
            // mBinding.message.setBackgroundResource(R.drawable.message_text); // will use 9patch image later on
        }
//        holder.author.setLayoutParams((holder.params));
//        holder.msg.setLayoutParams(((holder.params)));
//        holder.imageView.setLayoutParams((holder.params));

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView author, msg;
        ImageView imageView;
        LinearLayout mLayout;
        LinearLayout.LayoutParams params;

        public MyViewHolder(View view) {
            super(view);
            author = view.findViewById(R.id.author);
            msg = view.findViewById(R.id.message);
            imageView = view.findViewById(R.id.iv_pic);
            mLayout = view.findViewById(R.id.singleMessageContainer);

            //  LayoutParams will give me the LayoutParams associated with this view
            params = (LinearLayout.LayoutParams) author.getLayoutParams();
        }

    }


}
