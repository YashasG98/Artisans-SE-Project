package com.example.artisansfinal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.module.AppGlideModule;
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
import java.util.HashMap;
import android.Manifest;

import android.app.Dialog;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class ProductPageActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference users;
    private StorageReference storageReference;
    final long ONE_MB = 1024*1024;
    private DatabaseReference ordHis;
    private String userPhoneNumber;
    private String artisanContactNumber;
    private String productID2;
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

    FirebaseUser userX = firebaseAuth.getCurrentUser();
    AddressResultReceiver mResultReceiver;
    double latid = 0,longit = 0;
    EditText latitudeEdit, longitudeEdit, addressEdit;
    ProgressBar progressBar;
    TextView infoText;
    TextView current_location;
    CheckBox checkBox;
    //private static final String TAG = "MainActivity";
    private int STORAGE_PERMISSION_CODE = 1;
    //private LocationManager locationManager;
    protected LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;

    Button fetch;
    TextView user_location;
    private static final int ERROR_DIALOG_REQUEST = 9001;


    boolean fetchAddress;
    int fetchType = Constants.USE_ADDRESS_LOCATION;

    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        Log.d("HERE",userX.getPhoneNumber());
        Toast.makeText(this, "REACHED!", Toast.LENGTH_LONG).show();

        NestedScrollView layout = findViewById(R.id.product_page_nsv);
        final TextView pname = layout.findViewById(R.id.product_page_tv_product_name);
        final TextView aname = layout.findViewById(R.id.product_page_tv_artisan_name);
        final Button price = layout.findViewById(R.id.product_page_button_product_price);
        final TextView desc = layout.findViewById(R.id.product_page_tv_product_description);
        final ImageView image = layout.findViewById((R.id.product_page_iv_product_image));

        final Intent intent = getIntent();
        final String productCategory = intent.getStringExtra("productCategory");
        final String productName = intent.getStringExtra("productName");
        final String productID = intent.getStringExtra("productID");

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/"+productCategory+"/"+productName);
        storageReference = FirebaseStorage.getInstance().getReference("ProductImages/HighRes/" + productID);
        users = FirebaseDatabase.getInstance().getReference("User");

        final String key = databaseReference.getKey();
        Log.d("KEY", key);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("DATA", dataSnapshot.getKey());
                Log.d("DATA", dataSnapshot.getValue().toString());

                ProductInfo productInfo = (ProductInfo)intent.getParcelableExtra("productInfo");

                try{
                    String productName = productInfo.getProductName();
                    Log.d("pINFO: ", "pNAME: "+productName);
                }
                catch(Exception e){
                    Log.d("EXCEPT" ,e.toString());
                }

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
//                        Glide.with(getApplicationContext())
//                        .load(bytes)
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
//                        .into(image);
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
////                        Toast.makeText(getApplicationContext(), "Image Load Failed", Toast.LENGTH_SHORT).show();
//                        Glide.with(getApplicationContext())
//                                .load(R.mipmap.image_not_provided)
//                                .into(image);
//                    }
//                });
                RequestOptions options = new RequestOptions().error(R.mipmap.image_not_provided);
                GlideApp.with(getApplicationContext())
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

                for(DataSnapshot userSnapshot : dataSnapshot.getChildren())
                {
                    UserInfo userInfo = userSnapshot.getValue(UserInfo.class);

                    if(userInfo.userEmail.equals(userX.getEmail()))
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





        ordHis = FirebaseDatabase.getInstance().getReference("Orders/");


        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Choose");
                builder.setMessage("Want to buy this?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        String opname=pname.getText().toString();
                        //Log.d("HERE",opname);
                        String oprice=price.getText().toString();
                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c);

                        orderInfo order=new orderInfo(opname,oprice,formattedDate,userX.getUid());
                        DatabaseReference UserOrderHistory = FirebaseDatabase.getInstance().getReference("Orders/Users/"+userX.getUid()+"/Orders Requested");
                        String orderID = UserOrderHistory.push().getKey();
                        //ordHis.child(userX.getEmail().substring(0,userX.getEmail().indexOf('@'))).child(orderID).setValue(order);


                        String productCategory2 = "abd";
                        productID2 = "ab";

                        orderInfo orderUser = new orderInfo(opname, oprice, formattedDate, userX.getUid(), "ss", "saa","xyz","hai");
                        UserOrderHistory.child(orderID).setValue(orderUser);
                        //ordHis.child("Users").child(userX.getUid()).child("Orders Requested").child(orderID).setValue(orderUser);
//                        String orderID2 = ordHis.push().getKey();
//                        ordHis.child("Artisans").child(artisanContactNumber).child("Order Requests").child(orderID2).setValue(order);


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
        // longitudeEdit = (EditText) findViewById(R.id.longitudeEdit);
        //latitudeEdit = (EditText) findViewById(R.id.latitudeEdit);
        //addressEdit = (EditText) findViewById(R.id.addressEdit);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //infoText = (TextView) findViewById(R.id.infoText2);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        //fetch = findViewById(R.id.fetch_location); //main
        //user_location = findViewById((R.id.user_location)); //main
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mResultReceiver = new AddressResultReceiver(null);
//        fetch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fetchLocation();
//            }
//        });
        if (isServicesOK()) {
            fetchLocation();
        }
    }
    public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        user_location.setText("Longitude: " + longitude + "\n" + "Latitude: " + latitude);
    }

    private void fetchLocation() {
// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.app.AlertDialog.Builder(this).setTitle("Permission needed!").setMessage("This permission is needed to enable delivery services").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(com.example.artisansfinal.ProductPageActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, STORAGE_PERMISSION_CODE);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(com.example.artisansfinal.ProductPageActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        STORAGE_PERMISSION_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        // user_location.setText("Latitude = " + latitude + "\n" + "Longitude = " + longitude);
                        latid = latitude;
                        longit = longitude;
                        //if (ContextCompat.checkSelfPermission(MainActivity.this,
                        //      Manifest.permission.ACCESS_COARSE_LOCATION)
                        //    == PackageManager.PERMISSION_GRANTED){
                        //location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        //onLocationChanged(location);
                    }
                }

            });
        }
    }
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isServicesOK() {

        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(com.example.artisansfinal.ProductPageActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(com.example.artisansfinal.ProductPageActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    /*public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioAddress:
                if (checked) {
                    fetchAddress = false;
                    fetchType = Constants.USE_ADDRESS_NAME;
                    longitudeEdit.setEnabled(false);
                    latitudeEdit.setEnabled(false);
                    addressEdit.setEnabled(true);
                    addressEdit.requestFocus();
                }
                break;
            case R.id.radioLocation:
                if (checked) {
                    fetchAddress = true;
                    fetchType = Constants.USE_ADDRESS_LOCATION;
                    latitudeEdit.setEnabled(true);
                    latitudeEdit.requestFocus();
                    longitudeEdit.setEnabled(true);
                    addressEdit.setEnabled(false);
                }
                break;
        }
    }*/

    public void onButtonClicked(View view) {
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
        if (fetchType == Constants.USE_ADDRESS_NAME) {
            if (addressEdit.getText().length() == 0) {
                Toast.makeText(this, "Please enter an address name", Toast.LENGTH_LONG).show();
                return;
            }
            intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, addressEdit.getText().toString());
        } else {
            if (latitudeEdit.getText().length() == 0 || longitudeEdit.getText().length() == 0) {
                Toast.makeText(this,
                        "Please enter both latitude and longitude",
                        Toast.LENGTH_LONG).show();
                return;
            }
            intent.putExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA,
                    Double.parseDouble(latitudeEdit.getText().toString()));
            intent.putExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA,
                    Double.parseDouble(longitudeEdit.getText().toString()));
        }
        //infoText.setVisibility(View.INVISIBLE);
        //progressBar.setVisibility(View.VISIBLE);
        Log.e(TAG, "Starting Service");
        startService(intent);
    }

    public void artisanfetch(View view) {
        fetchAddress = false;
        fetchType = Constants.USE_ADDRESS_NAME;
        //longitude.setEnabled(false);
        //latitude.setEnabled(false);
//        fetch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fetchLocation();
//            }
//        });
        if (isServicesOK()) {
            fetchLocation();
        }
        addressEdit.setEnabled(true);
        addressEdit.requestFocus();
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
        if (fetchType == Constants.USE_ADDRESS_NAME) {
//          if (addressEdit.getText().length() == 0) {
//              Toast.makeText(this, "Please enter an address name", Toast.LENGTH_LONG).show();
//              return;
//            }
            String name = "Delhi";
            //intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA2, name);
            intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, name);
        } else {
            fetchAddress = true;
            fetchType = Constants.USE_ADDRESS_LOCATION;
            //latitudeEdit = latid;
            //longitudeEdit = longit;
            latitudeEdit.setEnabled(true);
            latitudeEdit.requestFocus();
            longitudeEdit.setEnabled(true);
            addressEdit.setEnabled(false);

            if (latitudeEdit.getText().length() == 0 || longitudeEdit.getText().length() == 0) {
                Toast.makeText(this,
                        "Please enter both latitude and longitude",
                        Toast.LENGTH_LONG).show();
                return;
            }
            intent.putExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA,
                    (latid));
            intent.putExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA,
                    longit);
        }
        //infoText.setVisibility(View.INVISIBLE);
        //progressBar.setVisibility(View.VISIBLE);
        Log.e(TAG, "Starting Service");
        startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        double R = 6371; // Radius of the earth in km
                        double charges = Math.sqrt((latid-address.getLatitude())*(latid-address.getLatitude()) + (longit-address.getLongitude())*(longit-address.getLongitude()));
                        charges = charges*R*0.05;
                        //progressBar.setVisibility(View.GONE);
                        //infoText.setVisibility(View.VISIBLE);
                        //infoText.setText("Delivery Charges: " + charges);
                    }
                });
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //progressBar.setVisibility(View.GONE);
                        //infoText.setVisibility(View.VISIBLE);
                        //infoText.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            }
        }

    }
    public class MainActivity extends AppCompatActivity {
        private static final String TAG = "MainActivity";
        private int STORAGE_PERMISSION_CODE = 1;
        //private LocationManager locationManager;
        protected LocationManager locationManager;
        private FusedLocationProviderClient mFusedLocationClient;

        Button fetch;
        TextView user_location;
        private static final int ERROR_DIALOG_REQUEST = 9001;

        Button btnShowCoord;
        EditText edtAddress;
        TextView txtCoord;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_user_product_details1);
            //fetch = findViewById(R.id.fetch_location);
            //user_location = findViewById((R.id.user_location));
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            //btnShowCoord = (Button) findViewById(R.id.btnShowCoordinates);
            //edtAddress = (EditText) findViewById(R.id.edtAddress);
            //txtCoord = (TextView) findViewById(R.id.textCoordinates);
        /*btnShowCoord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new GetCoordinates().execute(edtAddress.getText().toString().replace(" ", "+"));
            }
        });*/
            // if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //  return;
            //}
            fetch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fetchLocation();
                }
            });
            if (isServicesOK()) {
                fetchLocation();
            }
        }


        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            user_location.setText("Longitude: " + longitude + "\n" + "Latitude: " + latitude);

        }

        private void fetchLocation() {
// Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    new android.app.AlertDialog.Builder(this).setTitle("Permission needed!").setMessage("This permission is needed to enable delivery services").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, STORAGE_PERMISSION_CODE);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            STORAGE_PERMISSION_CODE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            user_location.setText("Latitude = " + latitude + "\n" + "Longitude = " + longitude);
                            //if (ContextCompat.checkSelfPermission(MainActivity.this,
                            //      Manifest.permission.ACCESS_COARSE_LOCATION)
                            //    == PackageManager.PERMISSION_GRANTED){
                            //location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                            //onLocationChanged(location);
                        }
                    }

                });
            }
        }

        public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == STORAGE_PERMISSION_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public boolean isServicesOK() {

            Log.d(TAG, "isServicesOK: checking google services version");
            int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
            if (available == ConnectionResult.SUCCESS) {
                Log.d(TAG, "isServicesOK: Google Play services is working");
                return true;
            } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
                Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
                dialog.show();
            } else {
                Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

  /*  private class GetCoordinates extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog  = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Please wait until....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            try{
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s",address);
                String response = http.getHTTPData(url);
                return response;

            } catch (IOException ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            try{
                JSONObject jsonObject = new JSONObject(s);
                String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
                String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
                txtCoord.setText(String.format("Latitude = %s\nLongitude = %s", lat, lng));
                if(dialog.isShowing())
                    dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
*/
}
