package com.srjlove.trailerbuzz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.srjlove.trailerbuzz.R;

public class TrailerBuzzSplashActivity extends AppCompatActivity {

    private static final String TAG = "TrailerBuzzSplashActivi";
    KenBurnsView myKenBurnsView;
    private Button enter;
    private TextView tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_trailer_buzz_splash);

        myKenBurnsView = findViewById(R.id.ken_burns_images);
        ImageView splash_image = findViewById(R.id.iv_splash);
        enter = findViewById(R.id.btn_enter);
        tb = findViewById(R.id.tv_tb);
        enter.setVisibility(View.GONE);
        tb.setVisibility(View.GONE);

        myKenBurnsView.setImageDrawable(getResources().getDrawable(R.drawable.trailer));
        RandomTransitionGenerator generator = new RandomTransitionGenerator(20000, new AnticipateOvershootInterpolator());
        myKenBurnsView.setTransitionGenerator(generator);
        myKenBurnsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        final Animation mAnimation =  AnimationUtils.loadAnimation(this, R.anim.animate_splash_activity);
        Animation tbAnimation =  AnimationUtils.loadAnimation(this, R.anim.trailer_buzz);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "onAnimationEnd: navigating to MainActivaty");
                enter.setVisibility(View.VISIBLE);
                if (animation == mAnimation)
                    tb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splash_image.startAnimation(mAnimation);
        tb.startAnimation(tbAnimation);


    }

    public void enterToMainActivity(View view) {

        startActivity(new Intent(TrailerBuzzSplashActivity.this, MainActivity.class));
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Animating our splash");

    }

}
