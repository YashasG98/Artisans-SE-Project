package com.example.artisansfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;

public class UserTutorialActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();

        addSlide(SlideFragment.newInstance(R.layout.fragment_user_tutorial_slide_1));
        addSlide(SlideFragment.newInstance(R.layout.fragment_user_tutorial_slide_2));
        addSlide(SlideFragment.newInstance(R.layout.fragment_user_tutorial_slide_3));
        setFadeAnimation();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        Intent intent = new Intent(this,UserHomePageActivity.class);
        startActivity(intent);
        finish();
        super.onDonePressed(currentFragment);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        Intent intent = new Intent(this,UserHomePageActivity.class);
        startActivity(intent);
        finish();
        super.onSkipPressed(currentFragment);
    }
}
