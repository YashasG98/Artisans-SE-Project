package com.example.artisansfinal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ArtisanOrderHistoryPageActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseUser userX = firebaseAuth.getCurrentUser();

    private ArrayList<orderInfo> orders = new ArrayList<orderInfo>();
    private DatabaseReference databaseReference;
    private ArtisanOrderHistoryAdapter AOHAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artisan_order_history);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.artisanOrderHistory_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AOHAdapter = new ArtisanOrderHistoryAdapter(this, orders);
        recyclerView.setAdapter(AOHAdapter);
        recyclerView.setHasFixedSize(false);
        //Log.d("HERE",userX.getPhoneNumber());
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders/Artisans/" + userX.getPhoneNumber() + "/Orders Completed");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                orderInfo order = new orderInfo();
                //Log.d("tag", order.toString() + "!" + dataSnapshot.getValue()+"!"+dataSnapshot.getKey());
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();

                //Log.d("HERE",map.toString());
                order = new orderInfo(map.get("name"), map.get("price"), map.get("date"), map.get("userUID"));
                AOHAdapter.added(order);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                //Log.d("childRemoved",dataSnapshot.toString());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}