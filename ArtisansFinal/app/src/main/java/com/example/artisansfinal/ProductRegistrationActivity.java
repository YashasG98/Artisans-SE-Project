package com.example.artisansfinal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ProgressBar;
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

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

import static android.os.Build.VERSION_CODES.P;

public class ProductRegistrationActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ImageView imageView;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri mainImageURI,firebaseUri;
    private ProductInfo product;
    private static final String TAG = "productRegistration";
    private static boolean runInOnePage = false;
    private static String artisanName = null;
    private static String artisanContactNumber = null;
    //private double resizeFactorForHighRes[] = {1,0.8,0.7,0.6,0.5};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_registration);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        final FloatingActionButton browse = findViewById(R.id.product_page_fab_insert_image);
        imageView = findViewById(R.id.product_page_iv_product_image);
        Button register = findViewById(R.id.product_registration_button_register);

        Intent intent = getIntent();
        artisanName = intent.getStringExtra("name");
        artisanContactNumber = intent.getStringExtra("phoneNumber");
        Log.d(TAG, "onClick: "+artisanName+" "+artisanContactNumber);

        if(!runInOnePage){
            Tutorial tutorial = new Tutorial(this);
            tutorial.checkIfFirstRun();
            tutorial.requestFocusForView(browse, "Click here to browse for image","");
            tutorial.finishedTutorial();
            runInOnePage=false;
        }

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(ProductRegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

                boolean productNameflag = true, productPriceflag = true;

                if (productName.length() == 0) {
                    name_of_product.setError("Enter Product Name");
                    name_of_product.requestFocus();
                    productNameflag = false;
                }

                if (productPrice.length() == 0) {
                    price_of_product.setError("Enter Product Price");
                    price_of_product.requestFocus();
                    productPriceflag = false;
                }

                if (productPriceflag && productNameflag) {

                    product = new ProductInfo(productID, productName, productDescription, productCategory, productPrice, artisanName, artisanContactNumber);
                    product.setTotalRating("0");
                    product.setNumberOfPeopleWhoHaveRated("0");
                    databaseReference.child("Categories").child(productCategory).child(productID).setValue(product);
                    databaseReference.child("ArtisanProducts").child(artisanContactNumber).child(productID).setValue(product);

//                    databaseReference.child("Products").child(productName).setValue(product);
                    if (mainImageURI != null) {
                        Log.d("IMAGEURI", mainImageURI.toString());
                        uploadImage(mainImageURI);
                    }

                    Toast.makeText(getApplicationContext(), "Product Registered", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(ProductRegistrationActivity.this, ArtisanHomePageActivity.class);
                    intent1.putExtra("phoneNumber", artisanContactNumber);
                    intent1.putExtra("name", artisanName);
                    startActivity(intent1);
                    finish();
                }
            }
        });
    }

    private void uploadImage(Uri ImageURI) {
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(ImageURI);
    }

    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]>{

        Bitmap bitmap;

        public BackgroundImageResize(Bitmap bitmap) {
            if(bitmap!=null)
                this.bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            if(bitmap==null){
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uris[0]);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            Log.d("IMAGE COMPRESSION", "originalImageSize: "+bitmap.getByteCount()/(1024 )+"kB");
            byte[] bytes = null;
            if(bitmap.getByteCount()/(1024*1024) < 15)
                bytes = getBytesFromBitmap(bitmap,100);
            else
                bytes = getBytesFromBitmap(bitmap,83);
            Log.d("IMAGE COMPRESSION", "resizedImagesize: "+bytes.length/(1024)+"kB");
            return  bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            StorageReference ref1 = storageReference.child("ProductImages/").child("HighRes/" + product.getProductID());
            executeUploadTask(ref1,bytes);

            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap,250,250);
            byte[] thumbnailImage = getBytesFromBitmap(thumbnail,100);
            Log.d("IMAGE COMPRESSION", "Thumbnail Image size: "+thumbnailImage.length/1024+"kB");

            StorageReference ref2 = storageReference.child("ProductImages/").child("LowRes/" + product.getProductID());
            executeUploadTask(ref2,thumbnailImage);
        }
    }

    private void executeUploadTask(StorageReference ref, byte[] image) {

        final ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setMessage("Uploading");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setIndeterminate(true);

        ref.putBytes(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.dismiss();
                Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double currentProgress = (double) (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressBar.setProgress((int) currentProgress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image Upload failed",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality,stream);
        return stream.toByteArray();
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ProductRegistrationActivity.this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                imageView.setImageURI(result.getUri());
                Log.d("IMAGEURI", "onActivityResult: "+mainImageURI.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
