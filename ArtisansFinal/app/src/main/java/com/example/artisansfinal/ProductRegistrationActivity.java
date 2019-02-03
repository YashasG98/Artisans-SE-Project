package com.example.artisansfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static android.os.Build.VERSION_CODES.P;

public class ProductRegistrationActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private ImageView imageView;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_registration);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        final Button browse = findViewById(R.id.product_registration_button_browse_image);
        imageView = findViewById(R.id.product_registration_iv_product_image);
        Button register = findViewById(R.id.product_registration_button_register);

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText name_of_product = findViewById(R.id.product_registration_et_product_name);
                EditText description_of_product = findViewById(R.id.product_registration_et_product_description);
                EditText price_of_product = findViewById(R.id.product_registration_et_product_price);
                Spinner category = findViewById(R.id.product_registration_spinner_categories);

                String productName = name_of_product.getText().toString();
                String productDescription = description_of_product.getText().toString();
                String productCategory = category.getSelectedItem().toString();
                String productPrice = price_of_product.getText().toString();
                String productID = databaseReference.push().getKey();

                Intent intent = getIntent();
                String artisanName = intent.getStringExtra("name");
                String artisanContactNumber = intent.getStringExtra("phoneNumber");

                boolean productNameflag=true, productPriceflag=true;

                if(productName.length()==0){
                    name_of_product.setError("Enter Product Name");
                    name_of_product.requestFocus();
                    productNameflag = false;
                }

                if(productPrice.length()==0){
                    price_of_product.setError("Enter Product Price");
                    price_of_product.requestFocus();
                    productPriceflag=false;
                }

                if(productPriceflag && productNameflag){
                    ProductInfo product = new ProductInfo(productID, productName, productDescription, productCategory, productPrice, artisanName, artisanContactNumber);
                    databaseReference.child("Categories").child(productCategory).child(productName).setValue(product);
                    databaseReference.child("Products").child(productName).setValue(product);
                    Toast.makeText(getApplicationContext(),"Product Registered",Toast.LENGTH_SHORT).show();

                    if (filePath != null) {
                        final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                        progressDialog.setMessage("Uploading....");
                        progressDialog.show();

                        StorageReference ref = storageReference.child("ProductImages/" + productID);
                        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                //Toast.makeText(ProductRegistrationActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ProductRegistrationActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
                    }

                    Intent intent1 = new Intent(ProductRegistrationActivity.this, UserHomePageActivity.class);
                    intent1.putExtra("param","");
                    startActivity(intent1);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(filePath);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                try {
                    imageStream.close();
                    imageView.setImageBitmap(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}