package com.example.artisansfinal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ArtisanOrderRequestPageActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseUser userX = firebaseAuth.getCurrentUser();

    private ArrayList<orderInfo> orders = new ArrayList<orderInfo>();
    private DatabaseReference databaseReference;
    private ArtisanOrderRequestAdapter AORAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artisan_order_requests);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Loading order requests...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.artisanOrderRequest_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AORAdapter = new ArtisanOrderRequestAdapter(this, orders);
        recyclerView.setAdapter(AORAdapter);
        recyclerView.setHasFixedSize(false);
        //Log.d("HERE",userX.getPhoneNumber());
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders/Artisans/" + userX.getPhoneNumber() + "/Order Requests");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(findViewById(R.id.noMatchArtisanOrderRequest1).getVisibility()== View.VISIBLE){
                    findViewById(R.id.noMatchArtisanOrderRequest1).setVisibility(View.GONE);
                    findViewById(R.id.noMatchArtisanOrderRequest2).setVisibility(View.GONE);
                }
                //Log.d("HERE2",dataSnapshot.getValue().toString());
                orderInfo order = new orderInfo();
                Log.d("tag", order.toString() + "!" + dataSnapshot.getValue()+"!"+dataSnapshot.getKey());
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();

                order = new orderInfo(map.get("name"), map.get("price"), map.get("date"), map.get("userUID"),map.get("productCategory"),map.get("productID"),map.get("userEmail"),map.get("fcmToken"));
                order.setQuantity(map.get("quantity"));
                AORAdapter.added(order);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.d("childRemoved",dataSnapshot.toString());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        progress.dismiss();
    }
}