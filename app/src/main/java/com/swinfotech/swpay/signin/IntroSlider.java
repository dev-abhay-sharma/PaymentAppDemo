package com.swinfotech.swpay.signin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.adapter.ViewPagerAdapter;
import com.rd.PageIndicatorView;

public class IntroSlider extends AppCompatActivity {

    ViewPager mSLideViewPager;
    LinearLayout mDotLayout;
    Button backbtn, nextbtn, skipbtn, getStarted;

    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    IntroSlider activity;

    PageIndicatorView pageIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);

        activity = this;

//        backbtn = findViewById(R.id.backbtn);
//        nextbtn = findViewById(R.id.nextbtn);
        skipbtn = findViewById(R.id.skipButton);
        getStarted = findViewById(R.id.getStarted);


//        backbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (getitem(0) > 0){
//
//                    mSLideViewPager.setCurrentItem(getitem(-1),true);
//
//                }
//
//            }
//        });

//        nextbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (getitem(0) < 2)
//                    mSLideViewPager.setCurrentItem(getitem(1),true);
//                else {
//
//                    Intent i = new Intent(activity, Login.class);
//                    startActivity(i);
//                    finish();
//
//                }
//
//            }
//        });

        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(activity, Login.class);
                startActivity(i);
                finish();

            }
        });

        getStarted.setOnClickListener(v -> {
            Intent i = new Intent(activity, Login.class);
            startActivity(i);
            finish();
        });

        mSLideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
//        mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        viewPagerAdapter = new ViewPagerAdapter(this);

        mSLideViewPager.setAdapter(viewPagerAdapter);

//        setUpindicator(0);
        mSLideViewPager.addOnPageChangeListener(viewListener);


        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(mSLideViewPager);
        pageIndicatorView.setCount(3); // specify total count of indicators
//        pageIndicatorView.setSelection(2);

    }


//    public void setUpindicator(int position){
//
//        dots = new TextView[3];
//        mDotLayout.removeAllViews();
//
//        for (int i = 0 ; i < dots.length ; i++){
//
//            dots[i] = new TextView(this);
//            dots[i].setText(Html.fromHtml("&#8226"));
//            dots[i].setTextSize(35);
//            dots[i].setWidth(35);
//            dots[i].setBackground(getResources().getDrawable(R.drawable.onboarding_dotunselected));
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                dots[i].setTextColor(getResources().getColor(R.color.inactive,getApplicationContext().getTheme()));
////            }
//            mDotLayout.addView(dots[i]);
//
//        }
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            dots[position].setTextColor(getResources().getColor(R.color.active,getApplicationContext().getTheme()));
////        }
//        dots[position].setBackground(getResources().getDrawable(R.drawable.onboarding_dot_selected));
//
//
//    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

//            setUpindicator(position);
            pageIndicatorView.setSelection(position);

            if (position == 2){

//                backbtn.setVisibility(View.VISIBLE);
                getStarted.setVisibility(View.VISIBLE);

            }else {

//                backbtn.setVisibility(View.INVISIBLE);
                getStarted.setVisibility(View.GONE);

            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getitem(int i){

        return mSLideViewPager.getCurrentItem() + i;
    }

}