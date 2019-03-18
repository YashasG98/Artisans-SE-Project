package com.example.artisansfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

public class ArtisanTutorialActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(SlideFragment.newInstance(R.layout.fragment_artisan_tutorial_slide_1));
        addSlide(SlideFragment.newInstance(R.layout.fragment_artisan_tutorial_slide_2));
        addSlide(SlideFragment.newInstance(R.layout.fragment_artisan_tutorial_slide_3));
        setFadeAnimation();
        getSupportActionBar().hide();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        Intent intent = new Intent(this,ArtisanHomePageActivity.class);
        startActivity(intent);
        finish();
        super.onDonePressed(currentFragment);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        Intent intent = new Intent(this,ArtisanHomePageActivity.class);
        startActivity(intent);
        finish();
        super.onSkipPressed(currentFragment);
    }
}
