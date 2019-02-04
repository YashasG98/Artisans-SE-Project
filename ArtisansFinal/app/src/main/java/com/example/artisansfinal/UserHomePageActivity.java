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

import com.example.artisansfinal.R;
import com.google.firebase.auth.FirebaseAuth;

public class UserHomePageActivity extends AppCompatActivity {
    private DrawerLayout user_home_page_dl;
    private ActionBarDrawerToggle abdt;
    private String userType;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_page_activity);
        firebaseAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        userType = intent.getStringExtra("userType");
        user_home_page_dl = (DrawerLayout) findViewById(R.id.user_home_page_dl);
        abdt = new ActionBarDrawerToggle(this, user_home_page_dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        user_home_page_dl.addDrawerListener(abdt);
        abdt.syncState();

        final NavigationView nav_view = (NavigationView) findViewById(R.id.user_home_page_navigation_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.my_profile_button) {
                    Toast.makeText(UserHomePageActivity.this, "My profile", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.logout) {
                    Toast.makeText(UserHomePageActivity.this, "Log out", Toast.LENGTH_SHORT).show();
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
        Intent i = new Intent(this, SelectedCategoryActivity.class);
        i.putExtra("category", "Bracelet");
        startActivity(i);
    }

    public void my_profile_button(MenuItem item) {
        Intent intent = getIntent();
        Intent i = new Intent(this, UserprofilePageActivity.class);
        startActivity(i);
        Toast.makeText(this, "Your profile", Toast.LENGTH_SHORT).show();
    }

    public void home_button(MenuItem item) {
        Intent i = new Intent(this, UserHomePageActivity.class);
        startActivity(i);
        Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
    }

    public void order_history_button(MenuItem item) {
        Intent i = new Intent(this, OrderHistory.class);
        startActivity(i);
        Toast.makeText(this, "Your order history", Toast.LENGTH_SHORT).show();
    }

    public void tutorial_button(MenuItem item) {
        Intent i = new Intent(this, UserHomePageActivity.class);
        startActivity(i);
        Toast.makeText(this, "Tutorial", Toast.LENGTH_SHORT).show();
    }

    public void Logout(MenuItem item) {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(UserHomePageActivity.this, LoginActivity.class));
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
    }

    public void Toys(View view) {
        Toast.makeText(this, "Toys", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, SelectedCategoryActivity.class);
        i.putExtra("category", "Toys");
        startActivity(i);
    }

    public void Saree(View view) {
        Intent i = new Intent(this, SelectedCategoryActivity.class);
        i.putExtra("category", "Saree");
        startActivity(i);
        Toast.makeText(this, "Saree", Toast.LENGTH_SHORT).show();
    }

    public void Shawl(View view) {
        Intent i = new Intent(this, SelectedCategoryActivity.class);
        i.putExtra("category", "Shawl");
        startActivity(i);
        Toast.makeText(this, "Dhoti", Toast.LENGTH_SHORT).show();
    }

    public void Garland(View view) {
        Intent i = new Intent(this, SelectedCategoryActivity.class);
        i.putExtra("category", "Garland");
        startActivity(i);
        Toast.makeText(this, "Garlands", Toast.LENGTH_SHORT).show();
    }

    public void Pottery(View view) {
        Intent i = new Intent(this, SelectedCategoryActivity.class);
        i.putExtra("category", "Pottery");
        startActivity(i);
        Toast.makeText(this, "Pottery", Toast.LENGTH_SHORT).show();
    }

    public void Glass_painting(View view) {
        Intent i = new Intent(this, SelectedCategoryActivity.class);
        i.putExtra("category", "Glass painting");
        startActivity(i);
        Toast.makeText(this, "Glass paintings", Toast.LENGTH_SHORT).show();
    }

    public void Shirt(View view) {
        Intent i = new Intent(this, SelectedCategoryActivity.class);
        i.putExtra("category", "Shirt");
        startActivity(i);
        Toast.makeText(this, "Shirts", Toast.LENGTH_SHORT).show();
    }

}