package com.example.artisansfinal;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class ArtisanWalletActivity extends AppCompatActivity {
    private DrawerLayout artisan_wallet_page_dl;
    private ActionBarDrawerToggle abdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artisan_wallet);
//    //    artisan_wallet_page_dl = (DrawerLayout) findViewById(R.id.artisan_wallet_page_dl);
//      //  abdt = new ActionBarDrawerToggle(this, artisan_wallet_page_dl, R.string.Open, R.string.Close);
//        abdt.setDrawerIndicatorEnabled(true);
//        artisan_wallet_page_dl.addDrawerListener(abdt);
//        abdt.syncState();
//
//        final NavigationView nav_view = (NavigationView) findViewById(R.id.artisan_wallet_page_navigation_view);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                if (id == R.id.my_profile_button) {
//                    Toast.makeText(ArtisanWalletActivity.this, "My profile", Toast.LENGTH_SHORT).show();
//                } else if (id == R.id.logout) {
//                    Toast.makeText(ArtisanWalletActivity.this, "Log out", Toast.LENGTH_SHORT).show();
//                }
//
//                return true;
//            }
//        });

    }
}
