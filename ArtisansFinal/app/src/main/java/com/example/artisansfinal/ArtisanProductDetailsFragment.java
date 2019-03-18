package com.example.artisansfinal;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class ArtisanProductDetailsFragment extends Fragment {

    private DatabaseReference databaseReference;
    private DatabaseReference users;
    private DatabaseReference ordHis;
    private StorageReference storageReference;
    private String artisanContactNumber;
    private ArtisanProductPageTabbedActivity act;
    private static boolean runInOnePage=false;

    public ArtisanProductDetailsFragment(){}

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0.0f) {
            view.animate().setDuration(400).rotation(180.0f);
            return true;
        }
        view.animate().setDuration(400).rotation(0.0f);
        return false;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_artisan_product_details, container, false);
        act = (ArtisanProductPageTabbedActivity) view.getContext();
        final TextView pname = view.findViewById(R.id.artisan_product_details_tv_product_name);
        final TextView price = view.findViewById(R.id.artisan_product_details_tv_product_price);
        final TextView desc = view.findViewById(R.id.artisan_product_details_tv_product_description);
        final ImageView image = view.findViewById((R.id.artisan_product_details_iv_product_image));
        final FloatingActionButton fab = view.findViewById(R.id.artisan_product_details_fab);
        final AppCompatRatingBar ratingBar = view.findViewById(R.id.artisan_product_details_rb_rating);
        final TextView numberRated = view.findViewById(R.id.artisan_product_details_tv_number_of_ratings);

        final ImageButton toggleDescription = view.findViewById(R.id.artisan_product_details_bt_toggle_description);
        final ImageButton toggleReviewTab = view.findViewById(R.id.artisan_product_details_bt_tab_reviews);
        final LinearLayout expandDescription = view.findViewById(R.id.artisan_product_details_ll_expand_description);

        HashMap<View,String> title= new HashMap<>();
        title.put(fab,"Click here to edit product information");
        title.put(toggleDescription,"Click here to view description of product");
        title.put(toggleReviewTab,"Click here to view reviews of the product");

        ArrayList<View> views = new ArrayList<>();
        views.add(fab);
        views.add(toggleDescription);
        views.add(toggleReviewTab);

        if(!runInOnePage){
            Tutorial tutorial = new Tutorial(getActivity(),views);
            tutorial.checkIfFirstRun();
            tutorial.requestFocusForViews(title);
            tutorial.finishedTutorial();
            runInOnePage=true;
        }

        toggleDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleArrow(toggleDescription);
                if(expandDescription.getVisibility()==View.GONE){
                    expandDescription.setVisibility(View.VISIBLE);
                }
                else{
                    expandDescription.setVisibility(View.GONE);
                }
            }
        });

        toggleReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.toggleTab();
            }
        });

        final Intent intent = getActivity().getIntent();
        final String productCategory = intent.getStringExtra("productCategory");
        final String productID = intent.getStringExtra("productID");

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/" + productCategory + "/" + productID);
        storageReference = FirebaseStorage.getInstance().getReference("ProductImages/HighRes/" + productID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                pname.setText(map.get("productName"));
                price.setText(map.get("productPrice"));
                desc.setText(map.get("productDescription"));
                ratingBar.setRating(Float.parseFloat(map.get("totalRating")));
                numberRated.setText(map.get("numberOfPeopleWhoHaveRated"));
                artisanContactNumber = map.get("artisanContactNumber");
                Log.d("STORAGE", storageReference.child(map.get("productID")).toString());

                RequestOptions options = new RequestOptions().error(R.mipmap.image_not_provided);
                GlideApp.with(getActivity().getApplicationContext())
                        .load(storageReference)
                        .apply(options)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getContext(), ArtisanEditProductInfoActivity.class);
                newIntent.putExtra("productID", productID);
                newIntent.putExtra("productCategory", productCategory);
                newIntent.putExtra("artisanContactNumber", artisanContactNumber);
                newIntent.putExtra("productName", pname.getText());
                newIntent.putExtra("productPrice", price.getText());
                newIntent.putExtra("productDescription", desc.getText());
                getContext().startActivity(newIntent);
            }
        });

        return view;
    }
}