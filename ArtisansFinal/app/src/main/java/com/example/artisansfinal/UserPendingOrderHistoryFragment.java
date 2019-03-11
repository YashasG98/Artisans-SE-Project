package com.example.artisansfinal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class UserPendingOrderHistoryFragment extends Fragment
{
    private ArrayList<orderInfo> orders = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_user_pending_order_history, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.user_pending_order_history_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orders.clear();
        final OHAdapter ohAdapter = new OHAdapter(getContext(), orders, "UserPendingOrderHistoryFragment");
        recyclerView.setAdapter(ohAdapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference("Orders/"+"Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Orders Accepted");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                orderInfo order;
                HashMap<String,Object> map=(HashMap<String, Object>) dataSnapshot.getValue();
                //Log.d("HERE",map.toString());
                order = new orderInfo(String.valueOf(map.get("name")),String.valueOf(map.get("price")),String.valueOf(map.get("date")), String.valueOf(map.get("userUID")),
                        String.valueOf(map.get("productCategory")), String.valueOf(map.get("productID")));
                ohAdapter.added(order);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }
}
