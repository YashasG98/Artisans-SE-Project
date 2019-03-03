package com.example.artisansfinal;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.ArrayList;

public class CommonLoginActivityTabbed extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_login_tabbed);

        mSectionsPagerAdapter  = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setUpViewPager(mViewPager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    public void setUpViewPager(ViewPager viewPager)
    {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new ArtisanLoginFragment(), "Aritisan");
        sectionsPagerAdapter.addFragment(new UserLoginFragment(), "User");
        viewPager.setAdapter(sectionsPagerAdapter);
    }

}
class SectionsPagerAdapter extends FragmentPagerAdapter
{
    private final ArrayList<Fragment> fragmentList = new ArrayList<>();
    private final ArrayList<String> titleList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title)
    {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);

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
