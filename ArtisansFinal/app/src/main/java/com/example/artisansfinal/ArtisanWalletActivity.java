package com.example.artisansfinal;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//Added by Shrinidhi Anil Varna
public class ArtisanWalletActivity extends AppCompatActivity {
    private DrawerLayout artisan_wallet_page_dl;
    private ActionBarDrawerToggle abdt;
    DatabaseReference databaseUsers;
    private TextView avballance;
    private TextView phno;

    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

    FirebaseUser userX = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artisan_wallet);
        avballance=(TextView)findViewById(R.id.artisan_wallet_page_tv_display_balance);
        phno=(TextView)findViewById(R.id.artisan_wallet_page_tv_phno);



        FirebaseDatabase.getInstance().getReference("Artisans").child(userX.getPhoneNumber()).child("wallet").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        avballance.setText( dataSnapshot.getValue(String.class));
                        phno.setText("*linked to phone number: "+userX.getPhoneNumber());



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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
