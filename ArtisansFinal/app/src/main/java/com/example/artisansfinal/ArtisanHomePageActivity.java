package com.example.artisansfinal;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

//import e.shrinidhiav.artisanhomepageactivity.R;

public class ArtisanHomePageActivity extends AppCompatActivity {

    private DrawerLayout artisan_home_page_dl;
    private ActionBarDrawerToggle abdt;
    private String userType;
    private FirebaseAuth firebaseAuth;
    private String artisanPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artisan_home_page_activity);

        Intent intent = getIntent();
        userType = intent.getStringExtra("userType");

        artisanPhoneNumber = intent.getStringExtra("phoneNumber");
        Log.d("artisanPhoneNumber", artisanPhoneNumber);

        firebaseAuth = FirebaseAuth.getInstance();
        artisan_home_page_dl = (DrawerLayout) findViewById(R.id.artisan_home_page_dl);
        abdt = new ActionBarDrawerToggle(this, artisan_home_page_dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        artisan_home_page_dl.addDrawerListener(abdt);
        abdt.syncState();

        final NavigationView nav_view = (NavigationView) findViewById(R.id.user_home_page_navigation_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.my_profile_button) {
                    Toast.makeText(ArtisanHomePageActivity.this, "My profile", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.logout) {
                    Toast.makeText(ArtisanHomePageActivity.this, "Log out", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (abdt.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public void bracelets(View view) {
        Toast.makeText(this, "Bracelets", Toast.LENGTH_SHORT).show();

    }

    public void my_profile_button(MenuItem item) {
        Intent i = new Intent(this, ArtisanProfilePageActivity.class);
        startActivity(i);
        Toast.makeText(this, "Your profile", Toast.LENGTH_SHORT).show();
    }

    public void home_button(MenuItem item) {
        Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
    }

    public void order_history_button(MenuItem item) {
        Toast.makeText(this, "Your order history", Toast.LENGTH_SHORT).show();
    }

    public void tutorial_button(MenuItem item) {
        Toast.makeText(this, "Tutorial", Toast.LENGTH_SHORT).show();
    }

    public void Logout(MenuItem item) {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(ArtisanHomePageActivity.this, ArtisanLoginActivity.class));
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
    }


    public void Order_history(View view) {
        Toast.makeText(this, "Order history", Toast.LENGTH_SHORT).show();

    }

    public void Order_requests(View view) {
        Toast.makeText(this, "Order requests", Toast.LENGTH_SHORT).show();
    }

    public void upload_product(MenuItem item) {
        Toast.makeText(this, "Upload a product", Toast.LENGTH_SHORT).show();
        Intent intentArtisanInfo = getIntent();
        String artisanName = intentArtisanInfo.getStringExtra("name");
        String artisanContactNumber = intentArtisanInfo.getStringExtra("phoneNumber");
        Log.d("Here", artisanContactNumber+" "+artisanName);
        Intent intent = new Intent(this, ProductRegistrationActivity.class);
        intent.putExtra("phoneNumber", artisanContactNumber);
        intent.putExtra("name", artisanName);
        //Log.d("nam", artisanName);
        startActivity(intent);
    }

    public void my_products(MenuItem item){
        Intent newIntent = new Intent(this, ArtisanProductsActivity.class);
        newIntent.putExtra("phoneNumber", artisanPhoneNumber);
        startActivity(newIntent);
    }

    public void my_products(View view) {
        Intent newIntent = new Intent(this, ArtisanProductsActivity.class);
        newIntent.putExtra("phoneNumber", artisanPhoneNumber);
        startActivity(newIntent);
    }
}
