package com.example.artisansfinal;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class UserProductPageTabbedActivity extends AppCompatActivity {

    private UserProductPageSectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_page_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.common_login_toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new UserProductPageSectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.user_product_page_container);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.user_product_page_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        toolbar.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

    }

    public void setUpViewPager(ViewPager viewPager) {
        UserProductPageSectionsPagerAdapter sectionsPagerAdapter = new UserProductPageSectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new UserProductDetails1Fragment(), "Details");
        sectionsPagerAdapter.addFragment(new UserProductReviewsFragment(), "Reviews");
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    public void toggleTab(){
        mViewPager.setCurrentItem(2);
    }
}

class UserProductPageSectionsPagerAdapter extends FragmentPagerAdapter {

    public UserProductPageSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private final ArrayList<Fragment> fragmentList = new ArrayList<>();
    private final ArrayList<String> titleList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title)
    {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}

