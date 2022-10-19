package com.swinfotech.swpay;

import static com.swinfotech.swpay.Session.IS_LOGIN;
import static com.swinfotech.swpay.Session.LAST_VISIT_TIME;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.swinfotech.swpay.activities.LockScreenActivity;
import com.swinfotech.swpay.databinding.ActivitySplashBinding;
import com.swinfotech.swpay.signin.IntroSlider;
import com.swinfotech.swpay.signin.Login;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2000;

    ActivitySplashBinding binding;
    SplashActivity activity;

    // Variables
    Animation topAnim, bottomAnim;

    Session session;
    boolean isGotoMainActivity = true;

    FirstTimeLauncher firstTimeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        firstTimeLauncher = new FirstTimeLauncher(activity);

        // Animation
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        binding.splashLogo.setAnimation(topAnim);
        binding.splashIntro.setAnimation(bottomAnim);
        binding.splashDec.setAnimation(bottomAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, Login.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(binding.splashLogo, "logo_image");
                pairs[1] = new Pair<View, String>(binding.splashIntro, "logo_text");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, pairs);
                    startActivity(intent, options.toBundle());
                }

            }
        }, SPLASH_SCREEN);


        session = new Session(SplashActivity.this);
        try {
            if (!saveDateAndTime()) {
                // if user not access the app in past 8 hours
                isGotoMainActivity = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Handler mhandler = new Handler(Looper.getMainLooper());
        mhandler.postDelayed(() -> {
            Intent intent;

            if (firstTimeLauncher.isFirstTime()) {
                firstTimeLauncher.setFirstLaunch(false);
                startActivity(new Intent(activity, IntroSlider.class));
            } else {

//            } here old code

                if (session.getBoolean(Session.IS_LOCK_SCREEN_ACTIVE)) {
                    intent = new Intent(SplashActivity.this, LockScreenActivity.class);
                    if (isGotoMainActivity) {
                        intent.putExtra("activity_name", "MainActivity");
                    } else {
                        intent.putExtra("activity_name", "Login");
                    }
                } else {
                    if (isGotoMainActivity) {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, Login.class);
                    }
                }
                startActivity(intent);
                finish();

            }
        }, 2000);

    }


    public boolean saveDateAndTime() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // get the current date and time
        String currentDate = simpleDateFormat.format(Calendar.getInstance().getTime());
        // get the store and date
        String time2 = session.getString(LAST_VISIT_TIME);
        //check if store time and date is not empty
        if (!time2.equalsIgnoreCase("")) {
            Date date1 = simpleDateFormat.parse(currentDate);
            Date date2 = simpleDateFormat.parse(time2);

            long difference = date2.getTime() - date1.getTime();
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            hours = (hours < 0 ? -hours : hours);
            Log.i("======= Hours", " :: " + hours);

            // set the current time and date
            session.setString(LAST_VISIT_TIME, currentDate);
            if (hours > 8) {
                return false;
            }
            // check if user login or not
            return session.getBoolean(IS_LOGIN);
        } else {
            session.setString(LAST_VISIT_TIME, currentDate);
            return false;
        }
    }

}