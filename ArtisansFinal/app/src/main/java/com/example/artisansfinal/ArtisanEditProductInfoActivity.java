package com.example.artisansfinal;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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

import java.io.IOException;

import static com.example.artisansfinal.ProductRegistrationActivity.getBytesFromBitmap;

public class ArtisanEditProductInfoActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private Uri mainImageURI;
    private ImageView editImage;
    private String updateDescription;
    private String updatePrice;
    private String updateName;
    private String productID;
    private String productCategory;
    private String artisanContactNumber;
    private static boolean runInOnePage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artisan_edit_product_info);

        Intent intent = getIntent();
        productID = intent.getStringExtra("productID");
        productCategory = intent.getStringExtra("productCategory");
        artisanContactNumber = intent.getStringExtra("artisanContactNumber");


        //Added by shashwatha
        storageReference = FirebaseStorage.getInstance().getReference();

        final FloatingActionButton saveFab = findViewById(R.id.artisan_edit_product_info_fab_save);
        final TextInputEditText editName = findViewById(R.id.artisan_edit_product_info_et_product_name);
        final TextInputEditText editPrice = findViewById(R.id.artisan_edit_product_info_et_product_price);
        final TextInputEditText editDescription = findViewById(R.id.artisan_edit_product_info_et_product_description);
        editImage = findViewById(R.id.artisan_edit_product_info_iv_product_image);
        final FloatingActionButton imageFab = findViewById(R.id.artisan_edit_product_info_fab_image);

        if(!runInOnePage){
            Tutorial tutorial = new Tutorial(this);
            tutorial.checkIfFirstRun();
            tutorial.requestFocusForView(saveFab,"Click here to edit product","");
            tutorial.finishedTutorial();
            runInOnePage=true;
        }
        StorageReference sr = storageReference.child("ProductImages/HighRes/" + productID);

        editName.setText(intent.getStringExtra("productName"));
        editDescription.setText(intent.getStringExtra("productDescription"));
        editPrice.setText(intent.getStringExtra("productPrice"));
        RequestOptions options = new RequestOptions().error(R.mipmap.image_not_provided);
        GlideApp.with(getApplicationContext())
                .load(sr)
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(editImage);

        imageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(ArtisanEditProductInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(ArtisanEditProductInfoActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(ArtisanEditProductInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }
                } else {
                    BringImagePicker();
                }
            }
        });

        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Save the changes?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean flag = true;
                        updateDescription = editDescription.getText().toString();
                        updatePrice = editPrice.getText().toString();
                        updateName = editName.getText().toString();
                        if (updatePrice.length() == 0) {
                            editPrice.setError("Enter Product Price");
                            editPrice.requestFocus();
                            flag = false;
                        }
                        if(updateName.length() == 0){
                            editName.setError("Enter Product Name");
                            editName.requestFocus();
                            flag = false;
                        }
                        if(flag) {
                            DatabaseReference db= FirebaseDatabase.getInstance().getReference("Categories/"+productCategory+"/"+productID);
                            db.child("productDescription").setValue(updateDescription);
                            db.child("productPrice").setValue(updatePrice);
                            db.child("productName").setValue(updateName);

                            DatabaseReference db1= FirebaseDatabase.getInstance().getReference("ArtisanProducts/"+artisanContactNumber+"/"+productID);
                            db1.child("productDescription").setValue(updateDescription);
                            db1.child("productPrice").setValue(updatePrice);
                            db1.child("productName").setValue(updateName);

                            DatabaseReference db2= FirebaseDatabase.getInstance().getReference("Products/"+productID);
                            db2.child("productDescription").setValue(updateDescription);
                            db2.child("productPrice").setValue(updatePrice);
                            db2.child("productName").setValue(updateName);

                            // update image here
                            if(mainImageURI!=null)
                                uploadImage(mainImageURI);
                            finish();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                Dialog dialog = builder.create();
                builder.show();
            }
        });
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ArtisanEditProductInfoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                editImage.setImageURI(result.getUri());
                Log.d("IMAGEURI", "onActivityResult: "+mainImageURI.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage(Uri ImageURI) {
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(ImageURI);
    }

    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {

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
            StorageReference ref1 = storageReference.child("ProductImages/").child("HighRes/" + productID);
            executeUploadTask(ref1,bytes);

            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap,250,250);
            byte[] thumbnailImage = getBytesFromBitmap(thumbnail,100);
            Log.d("IMAGE COMPRESSION", "Thumbnail Image size: "+thumbnailImage.length/1024+"kB");

            StorageReference ref2 = storageReference.child("ProductImages/").child("LowRes/" + productID);
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


}
