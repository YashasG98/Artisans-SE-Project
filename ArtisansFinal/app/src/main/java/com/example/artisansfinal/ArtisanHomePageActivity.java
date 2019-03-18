package com.example.artisansfinal;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import e.shrinidhiav.artisanhomepageactivity.R;

public class ArtisanHomePageActivity extends AppCompatActivity {

    private DrawerLayout artisan_home_page_dl;
    private ActionBarDrawerToggle abdt;
    private String userType;
    private FirebaseAuth firebaseAuth;
    private String artisanPhoneNumber;
    private String name;
    int counter;
    //private String name2; // added by shrinidhi
    private static final String TAG = "artisanHomePageActivity";

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        if(artisanPhoneNumber!=null && !artisanPhoneNumber.isEmpty()){
//            savedInstanceState.putString("phoneNumber",artisanPhoneNumber);
//        }
//        if(name!=null && !name.isEmpty()){
//            savedInstanceState.putString("name",name);
//        }
//        Log.d(TAG, "onSaveInstanceState: "+name+" "+artisanPhoneNumber);
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState){
//        artisanPhoneNumber = savedInstanceState.getString("phoneNumber");
//        name = savedInstanceState.getString("name");
//        Log.d(TAG, "onRestoreInstanceState: "+name+" "+artisanPhoneNumber);
//        super.onRestoreInstanceState(savedInstanceState);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artisan_home_page_activity);
        //DrawerLayout drawerLayout = findViewById(R.id.artisan_home_page_dl);

        Intent intent = getIntent();
        //userType = intent.getStringExtra("userType");
//        artisanPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        //Log.d("artisanPhoneNumber", artisanPhoneNumber);
        artisanPhoneNumber = intent.getStringExtra("phoneNumber");
        name = intent.getStringExtra("name");

        Log.d(TAG, "onCreate: "+name+" "+artisanPhoneNumber);
        //Added by Dhanasekhar

        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference("Artisans/" + artisanPhoneNumber + "/username");
        final ProgressDialog progressDialog = new ProgressDialog(this);

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        name = "dummy";


        //artisanPhoneNumber = intent.getStringExtra("phoneNumber");
        //Log.d("artisanPhoneNumber", artisanPhoneNumber);


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
    protected void onResume() {
        super.onResume();
        counter++;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (abdt.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

//    public void bracelets(View view) {
//        Toast.makeText(this, "Bracelets", Toast.LENGTH_SHORT).show();
//
//    }

    public void my_profile_button(MenuItem item) {
        Intent i = new Intent(this, ArtisanProfilePageActivity.class);
        startActivity(i);
        Toast.makeText(this, "Your profile", Toast.LENGTH_SHORT).show();
    }

    public void home_button(MenuItem item) {
        Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
    }

    public void order_history_button(MenuItem item) {
        Intent newIntent = new Intent(this, ArtisanOrderHistoryPageActivity.class);
        startActivity(newIntent);
    }

    public void tutorial_button(MenuItem item) {
        Toast.makeText(this, "Tutorial", Toast.LENGTH_SHORT).show();
    }

    public void Logout(MenuItem item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout Confirmation");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(ArtisanHomePageActivity.this, CommonLoginActivityTabbed.class));
                Toast.makeText(ArtisanHomePageActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = builder.create();
        builder.show();





    }


    public void Order_history(View view) {
        Intent newIntent = new Intent(this, ArtisanOrderHistoryPageActivity.class);
        startActivity(newIntent);
    }

    public void Order_requests(View view) {
        Intent newIntent = new Intent(this, ArtisanOrderRequestPageActivity.class);
        startActivity(newIntent);
    }

    public void upload_product(MenuItem item) {
        Toast.makeText(this, "Upload a product", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ProductRegistrationActivity.class);
        intent.putExtra("phoneNumber", artisanPhoneNumber);
        intent.putExtra("name", name);
        //Log.d("nam", artisanName);
        startActivity(intent);
    }

    public void my_products(MenuItem item){
        Intent newIntent = new Intent(this, ArtisanProductsActivity.class);
        newIntent.putExtra("phoneNumber", artisanPhoneNumber);
        startActivity(newIntent);
    }

    public void pending_orders(MenuItem item){
        Intent newIntent = new Intent(this, ArtisanPendingOrderActivity.class);
        startActivity(newIntent);
    }

    public void my_products(View view) {
        Intent newIntent = new Intent(this, ArtisanProductsActivity.class);
        newIntent.putExtra("phoneNumber", artisanPhoneNumber);
        startActivity(newIntent);
    }

    public void Pending_orders(View view) {
        Intent newIntent = new Intent(this, ArtisanPendingOrderActivity.class);
        startActivity(newIntent);
    }

    public void My_products(View view) {
        Intent newIntent = new Intent(this, ArtisanProductsActivity.class);
        newIntent.putExtra("phoneNumber", artisanPhoneNumber);
        startActivity(newIntent);
    }
}
