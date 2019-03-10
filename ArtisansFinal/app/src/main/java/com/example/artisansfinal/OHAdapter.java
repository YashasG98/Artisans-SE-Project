package com.example.artisansfinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.VISIBLE;

public class OHAdapter extends RecyclerView.Adapter<OHAdapter.OHViewHolder> {

    private ArrayList<orderInfo> order;
    private Context context;
    private String className;
    private String userPhoneNumber;
    final long ONE_MB = 1024 * 1024;
    private StorageReference storageReference;

    public static class OHViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView image;
        TextView productPrice;
        TextView date;
        RelativeLayout layout;
        TextView reviewTextView;
        RatingBar ratingBar;
        CardView cardView;

        public OHViewHolder(@NonNull View itemView, String className) {
            super(itemView);
            productName = itemView.findViewById(R.id.orderHistory_tv_product_name);
            image = itemView.findViewById(R.id.orderHistory_iv_product_image);
            productPrice = itemView.findViewById(R.id.orderHistory_tv_product_price);
            date = itemView.findViewById(R.id.orderHistory_tv_date);
            reviewTextView = itemView.findViewById(R.id.review);
            layout = itemView.findViewById(R.id.orderHistoryRL);
            cardView = itemView.findViewById(R.id.orderHistoryCv);


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
        view = inflater.inflate(R.layout.order_history_layout, viewGroup, false);
        OHViewHolder viewHolder = new OHViewHolder(view, this.className);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final OHViewHolder viewHolder, final int i) {
        final orderInfo orderX = order.get(i);
        viewHolder.productName.setText(orderX.getName());
        viewHolder.date.setText(orderX.getDate());
        viewHolder.productPrice.setText(orderX.getPrice());

        DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userPhoneNumber = "34";

                for(DataSnapshot userSnapshot : dataSnapshot.getChildren())
                {
                    UserInfo userInfo = userSnapshot.getValue(UserInfo.class);

                    if(userInfo.userEmail.equals(email))
                    {
                        userPhoneNumber = userInfo.userPnumber;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        if (className.equals("UserCompletedOrderHistoryFragment"))
        {
            viewHolder.reviewTextView.setClickable(true);
            viewHolder.reviewTextView.setVisibility(VISIBLE);
            viewHolder.reviewTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Reviews and Ratings");
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View view = inflater.inflate(R.layout.review_ratings, null);
                    builder.setView(view);

                    final EditText ReviewInput = view.findViewById(R.id.review);
                    final RatingBar ratingBar = view.findViewById(R.id.ratingBar);


                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String review = ReviewInput.getText().toString();
                            String rating = String.valueOf(ratingBar.getRating());

//                            final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

//                            DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
//                            users.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                    userPhoneNumber = "34";
//
//                                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren())
//                                    {
//                                        UserInfo userInfo = userSnapshot.getValue(UserInfo.class);
//
//                                        if(userInfo.userEmail.equals(email))
//                                        {
//                                            userPhoneNumber = userInfo.userPnumber;
//                                            break;
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });

//                            while(userPhoneNumber == null)
//                            {
//
//                            }

                            HashMap<String, String> ReviewsAndRatings = new HashMap<>();
                            ReviewsAndRatings.put("Review", review);
                            ReviewsAndRatings.put("Ratings", rating);

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Reviews/"+orderX.getName()+"/"+userPhoneNumber);
                            databaseReference.setValue(ReviewsAndRatings);



                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                    builder.show();

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return order.size();
    }

    public void added(orderInfo orderX) {
        order.add(orderX);
        notifyItemInserted(order.indexOf(orderX));
    }

}

