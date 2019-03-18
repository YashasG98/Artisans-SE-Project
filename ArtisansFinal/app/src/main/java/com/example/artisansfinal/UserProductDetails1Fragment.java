package com.example.artisansfinal;

import android.Manifest;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class UserProductDetails1Fragment extends Fragment {

    private DatabaseReference databaseReference;
    private DatabaseReference users;
    private DatabaseReference ordHis;
    private StorageReference storageReference;
    private String artisanContactNumber;
    private String userPhoneNumber;
    Calendar calendar;
    private String formattedDate;
    private UserProductPageTabbedActivity act;
    private String token;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseUser userX = firebaseAuth.getCurrentUser();

    public UserProductDetails1Fragment(){}

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

        final View view = inflater.inflate(R.layout.fragment_user_product_details1, container, false);
        act = (UserProductPageTabbedActivity) view.getContext();
        final TextView pname = view.findViewById(R.id.user_product_details1_tv_product_name);
        final TextView aname = view.findViewById(R.id.user_product_details1_tv_artisan_name);
        final TextView price = view.findViewById(R.id.user_product_details1_tv_product_price);
        final TextView desc = view.findViewById(R.id.user_product_details1_tv_product_description);
        final ImageView image = view.findViewById((R.id.user_product_details1_iv_product_image));
        final FloatingActionButton fab = view.findViewById(R.id.user_product_details1_fab);
        final AppCompatRatingBar ratingBar = view.findViewById(R.id.user_product_details1_rb_rating);
        final TextView numberRated = view.findViewById(R.id.user_product_details1_tv_number_of_ratings);

        final ImageButton toggleDescription = view.findViewById(R.id.user_product_details1_bt_toggle_description);
        final ImageButton toggleReviewTab = view.findViewById(R.id.user_product_details1_bt_tab_reviews);
        final LinearLayout expandDescription = view.findViewById(R.id.user_product_details1_ll_expand_description);

        final Button buttonaddress = view.findViewById(R.id.buttonaddress);
        toggleDescription.setOnClickListener(new OnClickListener() {
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

        toggleReviewTab.setOnClickListener(new OnClickListener() {
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
        users = FirebaseDatabase.getInstance().getReference("User");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                pname.setText(map.get("productName"));
                aname.setText(map.get("artisanName"));
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

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserInfo userInfo = userSnapshot.getValue(UserInfo.class);
                    if (userInfo.userEmail.equals(userX.getEmail())) {
                        userPhoneNumber = userInfo.userPnumber;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ordHis = FirebaseDatabase.getInstance().getReference("Orders");

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Choose a delivery date");

                //Added by dhanasekhar
                LayoutInflater layoutInflater = inflater.from(v.getContext());
                final View userConfirmationView = layoutInflater.inflate(R.layout.user_confirmation, null);
                builder.setView(userConfirmationView);
                final CalendarView calendarView = userConfirmationView.findViewById(R.id.calendarView);
                calendarView.setMinDate(Calendar.getInstance().getTimeInMillis());

                final String months[] = { "Jan", "Feb", "Mar", "Apr",
                        "May", "Jun", "Jul", "Aug",
                        "Sep", "Oct", "Nov", "Dec" };

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth)
                    {
                        calendar = new GregorianCalendar(year, month, dayOfMonth);
                        formattedDate = calendar.get(Calendar.DATE) + "-" +months[calendar.get(Calendar.MONTH)] + "-" + calendar.get(Calendar.YEAR);
                    }
                });

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String opname = pname.getText().toString();
                        //Log.d("HERE",opname);
                        final String oprice = price.getText().toString();
                        final DatabaseReference database= FirebaseDatabase.getInstance().getReference("User/");
                        database.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot data: dataSnapshot.getChildren()){
                                    if(data.child("userEmail").getValue().toString().equals(userX.getEmail())){
                                        token=data.child("FCMToken").getValue().toString();

                                        orderInfo order = new orderInfo(opname, oprice, formattedDate, userX.getUid(), productCategory, productID,userX.getEmail(),token);
                                        String orderID = ordHis.push().getKey();
                                        //ordHis.child(userX.getEmail().substring(0,userX.getEmail().indexOf('@'))).child(orderID).setValue(order);
                                        ordHis.child("Users").child(userX.getUid()).child("Orders Requested").child(orderID).setValue(order);
                                        orderID = ordHis.push().getKey();
                                        ordHis.child("Artisans").child(artisanContactNumber).child("Order Requests").child(orderID).setValue(order);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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
        });

        return view;
    }

}