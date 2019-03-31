package com.example.artisansfinal;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

//Added Sayan Biswas
public class UserWalletActivity extends AppCompatActivity {
    private DrawerLayout user_wallet_page_dl;
    private ActionBarDrawerToggle abdt;
    private EditText amt_entered;
    private ImageButton add;
    private ImageButton sub;
    private String temp_id;
    private String temp_ballance;
    private float temp;
    DatabaseReference databaseUsers;
    private TextView avballance;
    private TextView phno;
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

    FirebaseUser userX = firebaseAuth.getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_wallet);
         avballance=(TextView)findViewById(R.id.user_wallet_page_tv_display_balance);
         amt_entered=(EditText)findViewById(R.id.user_wallet_page_et_amount_enter);
         add=(ImageButton)findViewById(R.id.user_wallet_page_iv_credit);
         sub=(ImageButton)findViewById(R.id.user_wallet_page_iv_debit);
         phno = (TextView)findViewById(R.id.user_wallet_page_tv_linked_phno);
        databaseUsers= FirebaseDatabase.getInstance().getReference("User");



        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    UserInfo user = userSnapshot.getValue(UserInfo.class);
                    if(user.userEmail.equals(userX.getEmail()))
                    {

                        temp_id=user.userPnumber;
                        temp_ballance=user.userWallet;
                        avballance.setText(user.userWallet);
                        phno.setText("*linked to phone number: +91"+temp_id);




                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String handle = amt_entered.getText().toString();
                if(handle.equals(""))
                    temp = 0;
                else
                    temp = Float.parseFloat(handle);
                float f=Float.parseFloat(temp_ballance);
                f=f+temp;

                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                DatabaseReference myRef=firebaseDatabase.getReference();
                myRef.child("User").child(temp_id).child("userWallet").setValue(Float.toString(f));


            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String handle = amt_entered.getText().toString();
                if(handle.equals(""))
                    temp = 0;
                else
                    temp = Float.parseFloat(amt_entered.getText().toString());
                float f=Float.parseFloat(temp_ballance);
                if(temp<=f)
                {
                    f=f-temp;

                }
                else
                {
                    Toast.makeText(UserWalletActivity.this,"Not sufficient amount to withdraw.",Toast.LENGTH_SHORT).show();

                }
                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                DatabaseReference myRef=firebaseDatabase.getReference();
                myRef.child("User").child(temp_id).child("userWallet").setValue(Float.toString(f));


            }
        });








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
