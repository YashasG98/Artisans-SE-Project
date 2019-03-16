package com.example.artisansfinal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArtisanPendingOrderAdapter extends RecyclerView.Adapter<ArtisanPendingOrderAdapter.ArtisanPendingOrderViewHolder> {

    private ArrayList<orderInfo> order;
    private Context context;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser userX = firebaseAuth.getCurrentUser();
    final DatabaseReference dba = FirebaseDatabase.getInstance().getReference("Orders/Artisans/" + userX.getPhoneNumber() + "/Orders Pending");

    public static class ArtisanPendingOrderViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView image;
        TextView productPrice;
        TextView dueDate;
        TextView userUID;
        RelativeLayout layout;
        CardView card;


        public ArtisanPendingOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.artisan_order_request_tv_product_name);
            image = itemView.findViewById(R.id.artisan_order_request_iv_product_image);
            productPrice = itemView.findViewById(R.id.artisan_order_request_tv_product_price);
            dueDate = itemView.findViewById(R.id.artisan_order_request_tv_date);
            userUID = itemView.findViewById(R.id.artisan_order_request_tv_user_uid);
            layout = itemView.findViewById(R.id.artisan_order_request_rl);
            card = itemView.findViewById(R.id.artisan_order_request_cv);
        }
    }

    public ArtisanPendingOrderAdapter(Context context, ArrayList<orderInfo> order) {
        this.order = order;
        this.context = context;
    }

    @NonNull
    @Override
    public ArtisanPendingOrderAdapter.ArtisanPendingOrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.artisan_order_request_layout, viewGroup, false);
        ArtisanPendingOrderAdapter.ArtisanPendingOrderViewHolder viewHolder = new ArtisanPendingOrderAdapter.ArtisanPendingOrderViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ArtisanPendingOrderAdapter.ArtisanPendingOrderViewHolder viewHolder, final int i) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://artisansfinal.firebaseapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Api api = retrofit.create(Api.class);

        final orderInfo orderX = order.get(i);

        viewHolder.productName.setText(orderX.getName());
        viewHolder.dueDate.setText(orderX.getDate());
        viewHolder.productPrice.setText(orderX.getPrice());
        viewHolder.userUID.setText(orderX.getUserUID());
        viewHolder.userUID.setVisibility(View.GONE);
        Log.d("HEY", orderX.toString() + "!" + i);
//        viewHolder.card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Log.d("HEY",orderX.getPrice());
//
//            }
//        });

        if (orderX.getC().equals("g")) {
            viewHolder.card.setCardBackgroundColor(Color.parseColor("#76FF03"));
        }
        else {
            viewHolder.card.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }


        viewHolder.card.setOnClickListener(new View.OnClickListener() {

            private String artisanKey, userKey;


            @Override
            public void onClick(View v) {
                if(orderX.getC().equals("d")) {
                    final DatabaseReference dbu = FirebaseDatabase.getInstance().getReference("Orders/Users/" + viewHolder.userUID.getText().toString() + "/Orders Accepted");
                    Log.d("onClick", orderX.toString());

                    dba.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Log.d("HEY",dataSnapshot.getChildren().toString());
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                //Log.d("HEY",child.child("userUID").getValue().toString());
                                //Log.d("HERE",viewHolder.userUID.getText().toString()+" "+child.getKey());
                                if (child.child("userUID").getValue().toString().equals(viewHolder.userUID.getText().toString()) && child.child("date").getValue().toString().equals(viewHolder.dueDate.getText().toString()) && child.child("name").getValue().toString().equals(viewHolder.productName.getText().toString())) {
                                    //Log.d("HERE",child.getKey());
                                    artisanKey = child.getKey();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    dbu.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Log.d("HEY",dataSnapshot.getChildren().toString());
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                //Log.d("HEY",child.child("userUID").getValue().toString());
                                //Log.d("HERE",viewHolder.userUID.getText().toString()+" "+child.getKey());
                                if (child.child("userUID").getValue().toString().equals(viewHolder.userUID.getText().toString()) && child.child("date").getValue().toString().equals(viewHolder.dueDate.getText().toString()) && child.child("name").getValue().toString().equals(viewHolder.productName.getText().toString())) {
                                    userKey = child.getKey();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Order Completion");
                    builder.setMessage("Have you completed this order?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            orderX.setC("g");
                            Log.d("HERE", "Val" + i);
                            viewHolder.card.setCardBackgroundColor(Color.parseColor("#76FF03"));
                            DatabaseReference dbam = FirebaseDatabase.getInstance().getReference("Orders/Artisans/" + userX.getPhoneNumber() + "/Orders Completed");
                            String x = dbam.push().getKey();
                            move(dba.child(artisanKey), dbam.child(x), artisanKey);
                            //dba.child(artisanKey).setValue(null);
                            //Log.d("HERE2", dba.child(artisanKey).toString());
                            DatabaseReference dbum = FirebaseDatabase.getInstance().getReference("Orders/Users/" + viewHolder.userUID.getText().toString() + "/Orders Received");
                            x = dbum.push().getKey();
                            //dbum.child(x).setValue("Hai");
                            move(dbu.child(userKey), dbum.child(x), userKey);
                            //Log.d("HEY",orderX.fcmToken);
                            Call<ResponseBody> call=api.sendNotification(orderX.fcmToken,"Order Completed!","Your order for "+viewHolder.productName.getText().toString()+" has been completed by the artisan and will be delivered shortly");
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                            //Log.d("HERE", dbu.child(userKey).toString());
                            //dbu.child(userKey).setValue(null);
                            //Log.d("HERE2",artisanKey);
                            //Log.d("HERE2",userKey);
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

            }});
    }

    @Override
    public int getItemCount() {
        return order.size();
    }

    public void added(orderInfo orderX) {
        order.add(orderX);
        notifyItemInserted(order.indexOf(orderX));
    }

    private void move(final DatabaseReference fromPath, final DatabaseReference toPath, final String key) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");
                            //Log.d("HERE",key);
                            //Log.d("HERE2",fromPath.getParent().child(key).toString());
                            fromPath.getParent().child(key).setValue(null);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
