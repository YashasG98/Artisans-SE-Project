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

public class OrderHistoryTabbedActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history_tabbed);

        viewPager = findViewById(R.id.container);
        TabLayout tabLayout = findViewById(R.id.tabs);

        Toolbar toolbar = findViewById(R.id.user_order_history_toolbar);
        toolbar.setVisibility(View.GONE);

        SectionsPagerAdapter1 sectionsPagerAdapter1 = new SectionsPagerAdapter1(getSupportFragmentManager());
        sectionsPagerAdapter1.addFragments(new UserRequestedOrderHistoryFragment(), "Requested Orders");
        sectionsPagerAdapter1.addFragments(new UserPendingOrderHistoryFragment(), "Pending Orders");
        sectionsPagerAdapter1.addFragments(new UserCompletedOrderHistoryFragment(), "Completed Orders");
        viewPager.setAdapter(sectionsPagerAdapter1);

        tabLayout.setupWithViewPager(viewPager);

    }

}

class SectionsPagerAdapter1 extends FragmentPagerAdapter
{
    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();

    public SectionsPagerAdapter1(FragmentManager fm)
    {
        super(fm);
    }

    public void addFragments(Fragment fragment, String title)
    {
        fragments.add(fragment);
        titles.add(title);
    }

    @Override
    public Fragment getItem(int i)
    {

        return fragments.get(i);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return titles.get(position);
    }

    @Override
    public int getCount()
    {
        return fragments.size();
    }
}
