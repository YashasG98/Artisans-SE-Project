package com.example.artisansfinal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class UserProductReviewsFragment extends Fragment {

    private ReviewRecyclerViewAdapter reviewRecyclerViewAdapter;
    private ArrayList<ProductReview> productReviews = new ArrayList<>();
    private DatabaseReference databaseReference;
    private int five=0;
    private int four=0;
    private int three=0;
    private int two=0;
    private int one=0;
    private int total=0;
    public UserProductReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_user_product_reviews, container, false);

        final View view = inflater.inflate(R.layout.fragment_user_product_reviews, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.user_product_review_rv);
        final TextView averageRating = view.findViewById(R.id.user_product_reviews_average_rating);
        final TextView totalRated = view.findViewById(R.id.user_product_reviews_total);
        final SeekBar fiveStar = view.findViewById(R.id.fiveStar);
        final SeekBar fourStar = view.findViewById(R.id.fourStar);
        final SeekBar threeStar = view.findViewById(R.id.threeStar);
        final SeekBar twoStar = view.findViewById(R.id.twoStar);
        final SeekBar oneStar = view.findViewById(R.id.oneStar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewRecyclerViewAdapter = new ReviewRecyclerViewAdapter(getContext(), productReviews);
        recyclerView.setAdapter(reviewRecyclerViewAdapter);

        Intent intent = getActivity().getIntent();
        String productID = intent.getStringExtra("productID");
        String productCategory = intent.getStringExtra("productCategory");
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/"+productCategory+"/"+productID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                totalRated.setText(map.get("numberOfPeopleWhoHaveRated"));
                averageRating.setText(map.get("totalRating"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Reviews/" + productID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            ProductReview productReview;
                            HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                            productReview = new ProductReview();
                            productReview.setRating(map.get("rating"));
                            productReview.setReview(map.get("review"));
                            productReview.setUserName(map.get("userName")+" :");
                            reviewRecyclerViewAdapter.added(productReview);
                            int rating = Integer.parseInt(map.get("rating"));
                            Toast.makeText(getContext(), ""+rating, Toast.LENGTH_SHORT).show();
                            if(rating==5){
                                five+=1;
                                total++;
                            }
                            else if(rating==4){
                                four+=1;
                                total++;
                            }
                            else if(rating==3){
                                three+=1;
                                total++;
                            }
                            else if(rating==2){
                                two+=1;
                                total++;
                            }
                            else{
                                one+=1;
                                total++;
                            }

                            fiveStar.setProgress(five*100/total);
                            fourStar.setProgress(four*100/total);
                            threeStar.setProgress(three*100/total);
                            twoStar.setProgress(two*100/total);
                            oneStar.setProgress(one*100/total);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}
