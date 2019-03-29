package com.example.artisansfinal;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class UserWalletActivity extends AppCompatActivity {
    private DrawerLayout user_wallet_page_dl;
    private ActionBarDrawerToggle abdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_wallet);
//        user_wallet_page_dl = (DrawerLayout) findViewById(R.id.user_wallet_page_dl);
//        abdt = new ActionBarDrawerToggle(this, user_wallet_page_dl, R.string.Open, R.string.Close);
//        abdt.setDrawerIndicatorEnabled(true);
//        user_wallet_page_dl.addDrawerListener(abdt);
//        abdt.syncState();
//
//        final NavigationView nav_view = (NavigationView) findViewById(R.id.user_wallet_page_navigation_view);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                if (id == R.id.my_profile_button) {
//                    Toast.makeText(UserWalletActivity.this, "My profile", Toast.LENGTH_SHORT).show();
//                } else if (id == R.id.logout) {
//                    Toast.makeText(UserWalletActivity.this, "Log out", Toast.LENGTH_SHORT).show();
//                }
//
//                return true;
//            }
//        });

    }
}
