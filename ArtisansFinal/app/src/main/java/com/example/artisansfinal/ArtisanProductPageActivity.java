package com.example.artisansfinal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ArtisanProductPageActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference dr;
    private StorageReference storageReference;
    final long ONE_MB = 1024*1024;

    private RelativeLayout displayLayout;
    private RelativeLayout editLayout;
    private FloatingActionButton fab;

    String updatePrice;
    String updateDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artisan_product_page);
        Toast.makeText(this, "REACHED!", Toast.LENGTH_LONG).show();

        displayLayout = findViewById(R.id.artisan_product_page_display_rl);
        editLayout = findViewById(R.id.artisan_product_page_edit_rl);
        fab = findViewById(R.id.artisan_product_page_fab);
        displayLayout.setVisibility(View.VISIBLE);
        editLayout.setVisibility(View.GONE);

        NestedScrollView layout = findViewById(R.id.artisan_product_page_display_nsv);
        final TextView displayName = layout.findViewById(R.id.artisan_product_page_display_tv_product_name);
        final Button displayPrice = layout.findViewById(R.id.artisan_product_page_display_button_product_price);
        final TextView displayDescription = layout.findViewById(R.id.artisan_product_page_display_tv_product_description);
        final ImageView displayImage = layout.findViewById((R.id.artisan_product_page_display_iv_product_image));

        final TextView editName = findViewById(R.id.artisan_product_page_edit_tv_product_name);
        final TextInputEditText editPrice = findViewById(R.id.artisan_product_page_edit_et_product_price);
        final TextInputEditText editDescription = findViewById(R.id.artisan_product_page_edit_et_product_description);
        final ImageView editImage = findViewById(R.id.artisan_product_page_edit_iv_product_image);

        final Intent intent = getIntent();
//        String productCategory = intent.getStringExtra("productCategory");
        final String artisanPhoneNumber = intent.getStringExtra("artisanPhoneNumber");
        final String productName = intent.getStringExtra("productName");
        final String productID = intent.getStringExtra("productID");
        final String productCategory = intent.getStringExtra("productCategory");

        databaseReference = FirebaseDatabase.getInstance().getReference("ArtisanProducts/"+artisanPhoneNumber+"/"+productName);
        storageReference = FirebaseStorage.getInstance().getReference("ProductImages/HighRes/" + productID);

        final String key = databaseReference.getKey();
        Log.d("KEY", key);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("DATA", dataSnapshot.toString());
                Log.d("DATA", dataSnapshot.getKey());
                Log.d("DATA", dataSnapshot.getValue().toString());

                ProductInfo productInfo = (ProductInfo)intent.getParcelableExtra("productInfo");

                try{
                    String productName = productInfo.getProductName();
                    Log.d("pINFO: ", "displayName: "+productName);
                }
                catch(Exception e){
                    Log.d("EXCEPT" ,e.toString());
                }

                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                displayName.setText(map.get("productName"));
                displayPrice.setText(map.get("productPrice"));
                displayDescription.setText(map.get("productDescription"));
                editName.setText(map.get("productName"));
                editPrice.setText(map.get("productPrice"));
                editDescription.setText(map.get("productDescription"));
                Log.d("STORAGE", storageReference.child(map.get("productID")).toString());
//                storageReference.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Glide.with(getApplicationContext())
//                                .load(bytes)
//                                .listener(new RequestListener<Drawable>() {
//                                    @Override
//                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                        Toast.makeText(getApplicationContext(), "Glide load failed", Toast.LENGTH_SHORT).show();
//                                        return false;
//                                    }
//
//                                    @Override
//                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                        return false;
//                                    }
//                                })
//                                .into(displayImage);
//                        Glide.with(getApplicationContext())
//                                .load(bytes)
//                                .into(editImage);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
////                        Toast.makeText(getApplicationContext(), "Image Load Failed", Toast.LENGTH_SHORT).show();
//                        Glide.with(getApplicationContext())
//                                .load(R.mipmap.image_not_provided)
//                                .into(displayImage);
//                        Glide.with(getApplicationContext())
//                                .load(R.mipmap.image_not_provided)
//                                .into(editImage);
//                    }
//                });
                RequestOptions options = new RequestOptions().error(R.mipmap.image_not_provided);
                GlideApp.with(getApplicationContext())
                        .load(storageReference)
                        .apply(options)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(displayImage);
                GlideApp.with(getApplicationContext())
                        .load(storageReference)
                        .apply(options)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(editImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Tutorial tutorial = new Tutorial(this);
        tutorial.checkIfFirstRun();
        tutorial.requestFocusForView(fab,"Click here to edit details", "");
        tutorial.finishedTutorial();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setSelected(!fab.isSelected());
                fab.setImageResource(fab.isSelected() ? R.drawable.avd_anim : R.drawable.avd_anim_reverse);
                Drawable drawable = fab.getDrawable();
                if(drawable instanceof Animatable){
                    ((Animatable) drawable).start();
                }
                

                if(displayLayout.getVisibility()==View.VISIBLE) {
                    editLayout.setVisibility(View.VISIBLE);
                    displayLayout.setVisibility(View.GONE);
                }
                else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Save the changes?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateDescription = editDescription.getText().toString();
                            updatePrice = editPrice.getText().toString();
                            Log.d("UPDATE", updateDescription);
                            Log.d("UPDATE", editDescription.toString());
                            databaseReference.child("productDescription").setValue(updateDescription);
                            databaseReference.child("productPrice").setValue(updatePrice);
                            dr = FirebaseDatabase.getInstance().getReference("Categories/"+productCategory+"/"+productName);
                            dr.child("productDescription").setValue(updateDescription);
                            dr.child("productPrice").setValue(updatePrice);
                            Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    Dialog dialog = builder.create();
                    builder.show();

                    if(!dialog.isShowing()) {
                        editLayout.setVisibility(View.GONE);
                        displayLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
