package com.example.artisansfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import static android.view.View.VISIBLE;


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

    private String artisanContactNumber;
    private String userPhoneNumber;
    private String userName;
    private DatabaseReference users;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser userX = firebaseAuth.getCurrentUser();

    public UserProductReviewsFragment() {
        // Required empty public constructor
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0.0f) {
            view.animate().setDuration(400).rotation(180.0f);
            return true;
        }
        view.animate().setDuration(400).rotation(0.0f);
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_user_product_reviews, container, false);

        users = FirebaseDatabase.getInstance().getReference("User");
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserInfo userInfo = userSnapshot.getValue(UserInfo.class);
                    if (userInfo.userEmail.equals(userX.getEmail())) {
                        userPhoneNumber = userInfo.userPnumber;
                        userName = userInfo.userName;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        final View view = inflater.inflate(R.layout.fragment_user_product_reviews, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.user_product_review_rv);
        final TextView averageRating = view.findViewById(R.id.user_product_reviews_average_rating);
        final TextView totalRated = view.findViewById(R.id.user_product_reviews_total);
        final SeekBar fiveStar = view.findViewById(R.id.fiveStar); fiveStar.setEnabled(false);
        final SeekBar fourStar = view.findViewById(R.id.fourStar); fourStar.setEnabled(false);
        final SeekBar threeStar = view.findViewById(R.id.threeStar); threeStar.setEnabled(false);
        final SeekBar twoStar = view.findViewById(R.id.twoStar); twoStar.setEnabled(false);
        final SeekBar oneStar = view.findViewById(R.id.oneStar); oneStar.setEnabled(false);
        final FloatingActionButton fab = view.findViewById(R.id.user_product_reviews_fab);

        final CardView cardViewMine = view.findViewById(R.id.review_layout_cv_mine);
        final AppCompatRatingBar rbMine = view.findViewById(R.id.review_layout_rb_mine);
        final TextView nameMine = view.findViewById(R.id.review_layout_tv_name_mine);
        final TextView reviewMine = view.findViewById(R.id.review_layout_tv_review_mine);
        cardViewMine.setVisibility(View.GONE);

        reviewRecyclerViewAdapter = new ReviewRecyclerViewAdapter(getContext(), productReviews);
        recyclerView.setAdapter(reviewRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        Intent intent = getActivity().getIntent();
        final String productID = intent.getStringExtra("productID");
        final String productCategory = intent.getStringExtra("productCategory");
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/"+productCategory+"/"+productID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                totalRated.setText(map.get("numberOfPeopleWhoHaveRated"));
                averageRating.setText(map.get("totalRating"));
                artisanContactNumber = map.get("artisanContactNumber");
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
                            if(dataSnapshot.getKey().equalsIgnoreCase(userPhoneNumber)){
                                cardViewMine.setVisibility(View.VISIBLE);
                                rbMine.setRating(Float.parseFloat(map.get("rating")));
                                nameMine.setText(map.get("userName")+" :");
                                reviewMine.setText(map.get("review"));
                            }
                            else{
                                productReview = new ProductReview();
                                productReview.setRating(map.get("rating"));
                                productReview.setReview(map.get("review"));
                                productReview.setUserName(map.get("userName")+" :");
                                reviewRecyclerViewAdapter = new ReviewRecyclerViewAdapter(getContext(), productReviews);
                                recyclerView.setAdapter(reviewRecyclerViewAdapter);
                                reviewRecyclerViewAdapter.added(productReview);
                            }
                            int rating = Integer.valueOf(map.get("rating"));
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

        class sorting implements Comparator<ProductReview>
        {


            @Override
            public int compare(ProductReview o1, ProductReview o2) {

                if(Integer.parseInt(o1.getRating()) > Integer.parseInt(o2.getRating()))
                    return 1;
                else if(Integer.parseInt(o1.getRating()) < Integer.parseInt(o2.getRating()))
                    return -1;
                else
                    return 0;
            }
        }

        Collections.sort(productReviews, new sorting());
        reviewRecyclerViewAdapter = new ReviewRecyclerViewAdapter(getContext(), productReviews);
        recyclerView.setAdapter(reviewRecyclerViewAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleArrow(fab);
                Collections.reverse(productReviews);
                reviewRecyclerViewAdapter = new ReviewRecyclerViewAdapter(getContext(), productReviews);
                recyclerView.setAdapter(reviewRecyclerViewAdapter);
            }
        });

        cardViewMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.alertDialogTheme);
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.review_ratings, null);
                builder.setView(view);

                final EditText ReviewInput = view.findViewById(R.id.review);
                final RatingBar ratingBar = view.findViewById(R.id.ratingBar);
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                final ProgressBar progressBar = view.findViewById(R.id.review_progress_bar);

                ReviewInput.setText(reviewMine.getText());
                ratingBar.setRating(rbMine.getRating());

                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int n = Integer.parseInt(totalRated.getText().toString());
                        float oldRating =  Float.parseFloat(averageRating.getText().toString());
                        int newRating = (int)ratingBar.getRating();
                        float finalRating;

                        finalRating = (float) (n * oldRating - rbMine.getRating() + newRating) / (n);
                        String totalRating = String.format("%.1f", finalRating);

                        DatabaseReference reviewUpdateReference = FirebaseDatabase.getInstance().
                                getReference("Categories/" + productCategory + "/" + productID);
                        reviewUpdateReference.child("totalRating").setValue(String.valueOf(totalRating));
                        reviewUpdateReference.child("numberOfPeopleWhoHaveRated").setValue(String.valueOf(n));


                        ProductReview productReview = new ProductReview(userName, String.valueOf(newRating), ReviewInput.getText().toString());

                        DatabaseReference reviewsReference = FirebaseDatabase.getInstance().getReference("Reviews/" + productID + "/" + userPhoneNumber + "/");
                        reviewsReference.setValue(productReview);

                        DatabaseReference artisanProductReference = FirebaseDatabase.getInstance().getReference("ArtisanProducts/"+artisanContactNumber+"/"+productID);
                        artisanProductReference.child("totalRating").setValue(String.valueOf(totalRating));
                        artisanProductReference.child("numberOfPeopleWhoHaveRated").setValue(String.valueOf(n));

                        DatabaseReference productreference = FirebaseDatabase.getInstance().getReference("Products/" + productID + "/");
                        productreference.child("totalRating").setValue(String.valueOf(totalRating));
                        productreference.child("numberOfPeopleWhoHaveRated").setValue(String.valueOf(n));


                        rbMine.setRating(ratingBar.getRating());
                        reviewMine.setText(ReviewInput.getText());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show().getWindow().setLayout(1000, 1100);
            }
        });

        return view;
    }




}
