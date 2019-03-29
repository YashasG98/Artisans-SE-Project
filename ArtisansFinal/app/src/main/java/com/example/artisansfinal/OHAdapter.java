package com.example.artisansfinal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.view.View.VISIBLE;

public class OHAdapter extends RecyclerView.Adapter<OHAdapter.OHViewHolder> {

    private ArrayList<orderInfo> order;
    private Context context;
    private String className;
    private String userPhoneNumber;
    private String userName;
    private String userUID;
    private String artisanContactNumber;
    private String noOfPeopleWhoHaveRated;
    private String totalRating;
    final long ONE_MB = 1024 * 1024;
    private StorageReference storageReference;
    private String OKtext;
    private String key;
    private int ratingBefore;


    public static class OHViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView image;
        TextView productPrice;
        TextView date;
        RelativeLayout layout;
        TextView reviewTextView;
        RatingBar ratingBar;
        CardView cardView;
        View divider;

        public OHViewHolder(@NonNull View itemView, String className) {
            super(itemView);
            productName = itemView.findViewById(R.id.orderHistory_tv_product_name);
            image = itemView.findViewById(R.id.orderHistory_iv_product_image);
            productPrice = itemView.findViewById(R.id.orderHistory_tv_product_price);
            date = itemView.findViewById(R.id.orderHistory_tv_date);
            reviewTextView = itemView.findViewById(R.id.review);
            layout = itemView.findViewById(R.id.orderHistoryRL);
            cardView = itemView.findViewById(R.id.orderHistoryCv);
            divider = itemView.findViewById(R.id.divider_line);


        }
    }

    public OHAdapter(Context context, ArrayList<orderInfo> order, String className) {
        this.order = order;
        this.context = context;
        this.className = className;
    }

    @NonNull
    @Override
    public OHViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.order_history_layout_im, viewGroup, false);
        OHViewHolder viewHolder = new OHViewHolder(view, this.className);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final OHViewHolder viewHolder, final int i) {

        final orderInfo orderX = order.get(i);
        viewHolder.productName.setText(orderX.getName());
        viewHolder.date.setText(orderX.getDate());
        viewHolder.productPrice.setText(orderX.getPrice());
        viewHolder.image.setClipToOutline(true);
        storageReference = FirebaseStorage.getInstance().getReference("ProductImages/LowRes/" + orderX.getProductID());
        Glide.with(context).asGif().load(R.mipmap.loading3).into(viewHolder.image);

        RequestOptions requestOptions = new RequestOptions().error(R.mipmap.not_found);
        Glide.with(context).load(storageReference).apply(requestOptions).diskCacheStrategy(DiskCacheStrategy.DATA).into(viewHolder.image);


        DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserInfo userInfo = userSnapshot.getValue(UserInfo.class);

                    if (userInfo.userEmail.equals(email)) {
                        userPhoneNumber = userInfo.userPnumber;
                        userName = userInfo.userName;
                        userUID = userInfo.UID;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (className.equals("UserCompletedOrderHistoryFragment")) {

            OKtext = "Send";



            viewHolder.reviewTextView.setClickable(true);
            String s = orderX.getReviewExists();
            if (orderX.getReviewExists().equals("true")) {
                viewHolder.reviewTextView.setText("Edit Review");
                OKtext = new String("Edit");
            }
            viewHolder.reviewTextView.setVisibility(VISIBLE);
            viewHolder.divider.setVisibility(VISIBLE);
            viewHolder.reviewTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.alertDialogTheme);
                    //builder.setTitle("Reviews and Ratings");
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View view = inflater.inflate(R.layout.review_ratings, null);
                    builder.setView(view);

                    final EditText ReviewInput = view.findViewById(R.id.review);
                    final RatingBar ratingBar = view.findViewById(R.id.ratingBar);
                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    final ProgressBar progressBar = view.findViewById(R.id.review_progress_bar);

//                    progressDialog.setMessage("Loading reviews");
//                    progressDialog.show();

                    progressBar.setVisibility(VISIBLE);




                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Reviews").child(orderX.getProductID()).child(userPhoneNumber);

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ProductReview productReview;

                            if (dataSnapshot.exists()) {

                                productReview = dataSnapshot.getValue(ProductReview.class);
                                ReviewInput.setText(productReview.getReview());
                                ReviewInput.setSelection(productReview.getReview().length());
                                Log.d("key", productReview.getReview());
                                ratingBar.setRating(Float.parseFloat(productReview.getRating()));
                                ratingBefore = Integer.parseInt(productReview.getRating());
                            }

                            progressBar.setVisibility(View.GONE);

//                            new CountDownTimer(2000, 1000) {
//                                public void onTick(long millisUntilFinished) {
//                                    // You don't need anything here
//                                }
//
//                                public void onFinish() {
//                                    progressDialog.dismiss();
//                                }
//                            }.start();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    DatabaseReference productUpdateReference = FirebaseDatabase.getInstance().getReference("Categories").child(orderX.getProductCategory()).child(orderX.getProductID());

                    productUpdateReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                ProductInfo productInfo = dataSnapshot.getValue(ProductInfo.class);
                                artisanContactNumber = productInfo.getArtisanContactNumber();
                                noOfPeopleWhoHaveRated = productInfo.getNumberOfPeopleWhoHaveRated();
                                totalRating = productInfo.getTotalRating();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    builder.setPositiveButton(OKtext, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {



                            final DatabaseReference reviewExistsReference = FirebaseDatabase.getInstance().getReference("Orders/Users/" + orderX.getUserUID()).child("Orders Received");
                            reviewExistsReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    orderInfo order;

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        order = snapshot.getValue(orderInfo.class);
                                        if (order.getProductID().equals(orderX.getProductID())) {
                                            key = snapshot.getKey();
                                            reviewExistsReference.child(key).child("reviewExists").setValue("true");
                                            break;
                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            String review = ReviewInput.getText().toString();
                            String rating = String.valueOf((int) ratingBar.getRating());


                            int n = Integer.parseInt(noOfPeopleWhoHaveRated);
                            float oldRating =  Float.parseFloat(totalRating);
                            int newRating = Integer.parseInt(rating);
                            float finalRating;

                            if (orderX.getReviewExists().equals("false")) {
                                finalRating = (float) (n * oldRating + newRating) / (n + 1);
                                n = n + 1;
                            } else
                                finalRating = (float) (n * oldRating - ratingBefore + newRating) / (n);


                            totalRating = String.format("%.1f", finalRating);

                            DatabaseReference reviewUpdateReference = FirebaseDatabase.getInstance().
                                    getReference("Categories/" + orderX.getProductCategory() + "/" + orderX.getProductID());
                            reviewUpdateReference.child("totalRating").setValue(String.valueOf(totalRating));
                            reviewUpdateReference.child("numberOfPeopleWhoHaveRated").setValue(String.valueOf(n));


                            ProductReview productReview = new ProductReview(userName, rating, review);

                            DatabaseReference reviewsReference = FirebaseDatabase.getInstance().getReference("Reviews/" + orderX.getProductID() + "/" + userPhoneNumber + "/");
                            reviewsReference.setValue(productReview);

                            DatabaseReference artisanProductReference = FirebaseDatabase.getInstance().getReference("ArtisanProducts/"+artisanContactNumber+"/"+orderX.getProductID());
                            artisanProductReference.child("totalRating").setValue(String.valueOf(totalRating));
                            artisanProductReference.child("numberOfPeopleWhoHaveRated").setValue(String.valueOf(n));


                        }
                    });

                    builder.setNegativeButton("Later!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show().getWindow().setLayout(1000, 1100);


                }
            });
        }


    }
    //

    @Override
    public int getItemCount() {
        return order.size();
    }

    public void added(orderInfo orderX) {
        order.add(orderX);
        notifyItemInserted(order.indexOf(orderX));
    }

}

