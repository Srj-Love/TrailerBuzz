package com.srjlove.trailerbuzz.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.adapters.Utils;
import com.srjlove.trailerbuzz.databinding.FragmentLoginBinding;

/**
 * Created by Suraj on 12/3/2017.
 */

public class LoginFragment extends Fragment {

    private static final String TAG = LoginFragment.class.getSimpleName();
    private FragmentActivity mActivity;
    private FragmentLoginBinding mBinding;

    // firebase AuthListener
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: entering in OnCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        mActivity = getActivity();
        Context mContext = getContext();
        FrameLayout mFrameLayout = mActivity.findViewById(R.id.main_container);
        mFrameLayout.removeAllViewsInLayout();
        mAuth = FirebaseAuth.getInstance();




        if (Utils.isNetworkAvailable(mActivity)) { // check if network available
            mFrameLayout.removeAllViewsInLayout();// this simply remove all views from view-group

//            mBinding.rootLoginLayout.removeAllViews();
//            mBinding.rootLoginLayout.addView(LayoutInflater.from(mContext).inflate(R.layout.offline_layout, mFrameLayout, false));
            makeNetworkRequest();
            Log.d(TAG, "onCreateView: network available ");
            Toast.makeText(mActivity, "Network Available, Pls Sign In.. ", Toast.LENGTH_SHORT).show();
        }else{
            Utils.inflateOfflineLayout(mContext, mFrameLayout);
        }


        // checking from incoming bundle
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            String email = mBundle.getString(getString(R.string.login_email));
            mBinding.loginEmail.setText(email);

        }


        return mBinding.getRoot();
    }

    private void makeNetworkRequest() {

        mBinding.loginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 77 || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mBinding.loginSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attemptLogin();
            }
        });

        // Executed when Register in button pressed
        mBinding.loginRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.main_container, new RegistrationFragment()).commit();

                // finishing FM
                mActivity.getSupportFragmentManager().beginTransaction().remove(new LoginFragment()).commit();
            }
        });
    }

    private void attemptLogin() {

        String email = mBinding.loginEmail.getText().toString();
        String password = mBinding.loginPassword.getText().toString();
        mBinding.pbLogin.setVisibility(View.VISIBLE);
        Toast.makeText(mActivity, "Login in progress...", Toast.LENGTH_SHORT).show();

        // Using FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mBinding.pbLogin.setVisibility(View.INVISIBLE);
                    Toast.makeText(mActivity, "Login Successfull", Toast.LENGTH_SHORT).show();
                    getFragmentManager().beginTransaction().replace(R.id.main_container, new MainChatFragment()).commit();
                } else showError();
            }
        });

    }

    // Creating an alert dialog to show in case Login failed
    private void showError() {
        new AlertDialog.Builder(getContext())
                .setTitle("Oops")
                .setMessage("Login failed Please Try Again ")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: entering in onStart");
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new MainChatFragment()).commit();
        }
    }

}
