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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class UserProductDetailsFragment extends Fragment {

    private DatabaseReference databaseReference;
    private DatabaseReference users;
    private StorageReference storageReference;
    final long ONE_MB = 1024 * 1024;
    private DatabaseReference ordHis;
    private String userPhoneNumber;
    private String artisanContactNumber;
    Calendar calendar;
    private String formattedDate;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseUser userX = firebaseAuth.getCurrentUser();

    public UserProductDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_user_product_details, container, false);

        final TextView pname = view.findViewById(R.id.user_product_details_tv_product_name);
        final TextView aname = view.findViewById(R.id.user_product_details_tv_artisan_name);
        final Button price = view.findViewById(R.id.user_product_details_button_product_price);
        final TextView desc = view.findViewById(R.id.user_product_details_tv_product_description);
        final ImageView image = view.findViewById((R.id.user_product_details_iv_product_image));

        final Intent intent = getActivity().getIntent();
        final String productCategory = intent.getStringExtra("productCategory");
        final String productName = intent.getStringExtra("productName");
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
                artisanContactNumber = map.get("artisanContactNumber");
                Log.d("STORAGE", storageReference.child(map.get("productID")).toString());
//                storageReference.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Glide.with(getContext())
//                                .load(bytes)
//                                .listener(new RequestListener<Drawable>() {
//                                    @Override
//                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                        Toast.makeText(getContext(), "Glide load failed", Toast.LENGTH_SHORT).show();
//                                        return false;
//                                    }
//
//                                    @Override
//                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                        return false;
//                                    }
//                                })
//                                .into(image);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
////                        Toast.makeText(getApplicationContext(), "Image Load Failed", Toast.LENGTH_SHORT).show();
//                        Glide.with(getContext())
//                                .load(R.mipmap.image_not_provided)
//                                .into(image);
//                    }
//                });
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

        price.setOnClickListener(new View.OnClickListener() {
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


                                String opname = pname.getText().toString();
                                //Log.d("HERE",opname);
                                String oprice = price.getText().toString();
                                orderInfo order = new orderInfo(opname, oprice, formattedDate, userX.getUid(), productCategory, productID);
                                String orderID = ordHis.push().getKey();
                                //ordHis.child(userX.getEmail().substring(0,userX.getEmail().indexOf('@'))).child(orderID).setValue(order);
                                ordHis.child("Users").child(userX.getUid()).child("Orders Requested").child(orderID).setValue(order);
                                orderID = ordHis.push().getKey();
                                ordHis.child("Artisans").child(artisanContactNumber).child("Order Requests").child(orderID).setValue(order);


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
