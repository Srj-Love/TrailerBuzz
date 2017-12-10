package com.srjlove.trailerbuzz.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.roughike.bottombar.OnTabSelectListener;
import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.adapters.Utils;
import com.srjlove.trailerbuzz.databinding.ActivityMainBinding;
import com.srjlove.trailerbuzz.fragments.FavoriteFragment;
import com.srjlove.trailerbuzz.fragments.LoginFragment;
import com.srjlove.trailerbuzz.fragments.PopularFragment;
import com.srjlove.trailerbuzz.fragments.TopRatedFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static int targetY;
    private ActivityMainBinding mBinding;
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Log.d(TAG, "onCreate: entering in onCreate");

        setClickListenerNavigationBottom(); // when user choose any tab BTN
        setImageHeightValue();

        Utils.ConstructURLs(this);
        mBinding.toolbar.inflateMenu(R.menu.menu_sign_out);
        item = mBinding.toolbar.getMenu().findItem(R.id.action_sign_out);
        mBinding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_sign_out) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        signOut();
                    } else {
                        Toast.makeText(MainActivity.this, "Sign In ", Toast.LENGTH_SHORT).show();
                        navigateFragment(new LoginFragment(), getResources().getString(R.string.comment), getResources().getColor(R.color.bg_blue));
                        mBinding.bottomBar.selectTabAtPosition(3);
                    }

                }
                return true;
            }
        });
    }

    /**
     * User want to sign out
     */
    private void signOut() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sign_out)
                .setMessage(R.string.r_u_sure_sign_out)
                .setIcon(R.drawable.ic_sign_out)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this, "Sign Out Successfully ", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new PopularFragment()).commit();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Sign Out Failed ", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }


    // just testing for understanding
    private void setImageHeightValue() {
        DisplayMetrics mMetrics = new DisplayMetrics();    //simply use DisplayMetrics to get Display info of user device
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        int hgInPixel = mMetrics.heightPixels;
        targetY = 2 * (hgInPixel / 5);//and set display height into targetY; //
    }

    /**
     * when use choose any tab BNT
     * when user clicks any of tab the position will be saved into SP
     */
    private void setClickListenerNavigationBottom() {
        SharedPreferences mPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE);
        final SharedPreferences.Editor mEditor = mPreferences.edit();


        mBinding.bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Log.d(TAG, "onTabSelected: " + tabId);

                switch (tabId) {
                    case R.id.chat:

                        mEditor.putInt(getString(R.string.last_selected), 4); //user clacked CF save position 4 to SP
                        //mFragment = new LoginFragment();
                        //mBinding.toolbar.setTitle(getResources().getString(R.string.comment));
                        Log.d(TAG, "onTabSelected: chat " + tabId);
                        navigateFragment(new LoginFragment(), getResources().getString(R.string.comment), getResources().getColor(R.color.bg_light_orange));
                        break;
                    case R.id.favorites:
                        mEditor.putInt(getString(R.string.last_selected), 3); //user clacked FF save position 3 to SP
//                        mFragment = new FavoriteFragment();
//                        mBinding.toolbar.setTitle(getResources().getString(R.string.fav));
                        Log.d(TAG, "onTabSelected: fav " + tabId);
                        navigateFragment(new FavoriteFragment(), getResources().getString(R.string.fav), getResources().getColor(R.color.bg_yellow));
                        break;
                    case R.id.top_rated:
                        mEditor.putInt(getString(R.string.last_selected), 2); //user clacked TRF save position 2 to SP
//                        mFragment = new TopRatedFragment();
//                        mBinding.toolbar.setTitle(getResources().getString(R.string.top_rated));
                        navigateFragment(new TopRatedFragment(), getResources().getString(R.string.top_rated), getResources().getColor(R.color.bg_orrange));
                        Log.d(TAG, "onTabSelected: tp " + tabId);
                        break;
                    case R.id.popular:
                        mEditor.putInt(getString(R.string.last_selected), 1); //user clacked FF save position 1 to SP
//                        mFragment = new PopularFragment();
//                        mBinding.toolbar.setTitle(getResources().getString(R.string.Poppular));
                        navigateFragment(new PopularFragment(), getResources().getString(R.string.Poppular), getResources().getColor(R.color.bg_blue));
                        Log.d(TAG, "onTabSelected: pop " + tabId);
                        break;
                }

                mEditor.apply();

            }
        });

    }

    private void navigateFragment(Fragment mFragment, String string, int color) {
        mBinding.toolbar.setTitle(string);
        //mBinding.mainContainer.setBackgroundColor(color);  // playing with FL background colors
        //getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mFragment).addToBackStack(null).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        item.setVisible(true);
    }


    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    // user pressed Back Button Show dialog to exit
    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
