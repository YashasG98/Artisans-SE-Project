package com.example.artisansfinal;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static android.os.Build.VERSION_CODES.P;

public class ProductRegistrationActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ImageView imageView;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri mainImageURI;

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

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(ProductRegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(ProductRegistrationActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(ProductRegistrationActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

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

                    if (mainImageURI != null) {
                        final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                        progressDialog.setMessage("Uploading....");
                        progressDialog.show();

                        StorageReference ref = storageReference.child("ProductImages").child("HighRes/"+productID);
                        ref.putFile(mainImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
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

                        StorageReference ref2 = storageReference.child("ProductImages/").child("LowRes/"+productID);
                        try{

                            InputStream imageStream = getContentResolver().openInputStream(mainImageURI);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            int quality = 10000/selectedImage.getWidth();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            selectedImage.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                            byte[] compress = baos.toByteArray();
                            ref2.putBytes(compress);
                            try{
                                imageStream.close();
                            }catch (IOException e) {
                                e.printStackTrace();
                            }

                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }

                    }

                    Intent intent1 = new Intent(ProductRegistrationActivity.this, ArtisanHomePageActivity.class);
                    intent1.putExtra("param","");
                    startActivity(intent1);
                }

            }
        });
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ProductRegistrationActivity.this);

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                imageView.setImageURI(mainImageURI);
                try {
                    InputStream imageStream = getContentResolver().openInputStream(mainImageURI);
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
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}