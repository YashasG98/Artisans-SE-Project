package com.example.artisansfinal;

import android.Manifest;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jsibbold.zoomage.ZoomageView;

import org.w3c.dom.Text;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProductDetails1Fragment extends Fragment {

    private DatabaseReference databaseReference;
    private DatabaseReference users;
    private String temp_productprice;
    private String temp_user_wallet;
    private DatabaseReference ordHis;
    private StorageReference storageReference;
    private String artisanContactNumber;
    private String artisanPin;
    private String userPhoneNumber;
    Calendar calendar;
    private String formattedDate;
    private UserProductPageTabbedActivity act;
    private String token;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String artisanToken;
    private ArtisanInfo artisanInfo;
    private String pincode;
    private boolean confirmationFlag = false;

    //Lcation based done by shrinidhi anil varna
    AddressResultReceiver mResultReceiver, mResultReceiver2;
    double latid = 0,longit = 0;
    EditText latitudeEdit, longitudeEdit, addressEdit;
    ProgressBar progressBar;
    TextView infoText;
    TextView locText;
    ProgressBar locProg;
    //ProgressBar calProg;
    TextView current_location;
    CheckBox checkBox;
    TextView pprice, dprice, tprice;
    int ch,pp;
    public String name;
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

    FirebaseUser userX = firebaseAuth.getCurrentUser();
    String TAG ="userProductDetails";

    public UserProductDetails1Fragment(){}

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0.0f) {
            view.animate().setDuration(400).rotation(180.0f);
            return true;
        }
        view.animate().setDuration(400).rotation(0.0f);
        return false;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_user_product_details1, container, false);
        act = (UserProductPageTabbedActivity) view.getContext();
        final TextView pname = view.findViewById(R.id.user_product_details1_tv_product_name);
        final TextView aname = view.findViewById(R.id.user_product_details1_tv_artisan_name);
        final TextView price = view.findViewById(R.id.user_product_details1_tv_product_price);
        final TextView desc = view.findViewById(R.id.user_product_details1_tv_product_description);
        final ZoomageView image = view.findViewById((R.id.user_product_details1_iv_product_image));
        GlideApp.with(getContext()).asGif().load(R.mipmap.loading1).into(image);
        final FloatingActionButton fab = view.findViewById(R.id.user_product_details1_fab);
        final AppCompatRatingBar ratingBar = view.findViewById(R.id.user_product_details1_rb_rating);
        final TextView numberRated = view.findViewById(R.id.user_product_details1_tv_number_of_ratings);

        // added by Shrinidhi Anil Varna
        final ImageButton toggleDescription = view.findViewById(R.id.user_product_details1_bt_toggle_description);
        final ImageButton toggleReviewTab = view.findViewById(R.id.user_product_details1_bt_tab_reviews);
        final LinearLayout expandDescription = view.findViewById(R.id.user_product_details1_ll_expand_description);

        final ImageButton toggleLocation = view.findViewById(R.id.buttonaddress);
        final LinearLayout expandLocation = view.findViewById(R.id.user_product_details1_ll_expand_description_location);
        locText = (TextView) view.findViewById(R.id.LocText);
        locProg = (ProgressBar) view.findViewById(R.id.locProg);
        //calProg = (ProgressBar) CalendarView.findViewById(R.id.CalProg);



        //addressEdit = (EditText) view.findViewById(R.id.addressEdit);
        //progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        //infoText = (TextView) view.findViewById(R.id.infoText2);
        checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        //final Button buttonaddress2 = view.findViewById(R.id.buttonaddress2);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mResultReceiver = new AddressResultReceiver(null);
        if (isServicesOK()) {
            fetchLocation();
        }
//        buttonaddress2.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                artisanfetch(v);
//
//            }
//        });
        toggleDescription.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleArrow(toggleDescription);
                if(expandDescription.getVisibility()==View.GONE){
                    expandDescription.setVisibility(View.VISIBLE);
                }
                else{
                    expandDescription.setVisibility(View.GONE);
                }
            }
        });

        toggleReviewTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                act.toggleTab();
            }
        });

        // added by Shrinidhi Anil Varna
        toggleLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleArrow(toggleLocation);
                if(expandLocation.getVisibility()==View.GONE){
                    expandLocation.setVisibility(View.VISIBLE);
                    artisanfetch(v);
                    //locText.setText("Rs."+ch);
                }
                else{
                    expandLocation.setVisibility(View.GONE);
                }
            }
        });


        final Intent intent = getActivity().getIntent();
        final String productCategory = intent.getStringExtra("productCategory");
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
                temp_productprice=map.get("productPrice");
                price.setText(map.get("productPrice"));
                pp = Integer.parseInt(map.get("productPrice"));
                desc.setText(map.get("productDescription"));
                //locText.setText("Rs."+(int)(0.1*Float.parseFloat(map.get("productPrice"))));
                ratingBar.setRating(Float.parseFloat(map.get("totalRating")));

               numberRated.setText(map.get("numberOfPeopleWhoHaveRated")); 

                artisanContactNumber = map.get("artisanContactNumber");
                artisanPin = map.get("pincode");
                try {
                    FirebaseDatabase.getInstance().getReference("Artisans").child(artisanContactNumber).child("FCMToken")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    artisanToken = dataSnapshot.getValue(String.class);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                    Log.d("STORAGE", storageReference.child(map.get("productID")).toString());
                    FirebaseDatabase.getInstance().getReference("Artisans").child(artisanContactNumber).child("postal_address").
                            addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    pincode = dataSnapshot.getValue(String.class);
                                    name = dataSnapshot.getValue(String.class);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                catch (Exception e)
                {

                }

                RequestOptions options = new RequestOptions().error(R.mipmap.image_not_provided);
                try {
                    GlideApp.with(getContext())
                            .load(storageReference)
                            .apply(options)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(image);
                }
                catch (Exception e)
                {

                }
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
                        temp_user_wallet=userInfo.userWallet;

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ordHis = FirebaseDatabase.getInstance().getReference("Orders");

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Choose a delivery date");

                if(ch == 0)
                {
                    int i;
                    for(i=0;i<99;i++);
                    ch = (int)(0.1*pp);
                }
                //Added by dhanasekhar
                LayoutInflater layoutInflater = inflater.from(v.getContext());
                final View userConfirmationView = layoutInflater.inflate(R.layout.user_confirmation, null);
                builder.setView(userConfirmationView);
                pprice = (TextView) userConfirmationView.findViewById(R.id.user_confirmation_product_price);
                dprice = (TextView) userConfirmationView.findViewById(R.id.user_confirmation_delivery_charge);
                tprice = (TextView) userConfirmationView.findViewById(R.id.user_confirmation_total_charge);
                //calProg = (ProgressBar) userConfirmationView.findViewById(R.id.CalProg);
                final CalendarView calendarView = userConfirmationView.findViewById(R.id.calendarView);
                calendarView.setMinDate(Calendar.getInstance().getTimeInMillis());

                final String months[] = { "Jan", "Feb", "Mar", "Apr",
                        "May", "Jun", "Jul", "Aug",
                        "Sep", "Oct", "Nov", "Dec" };

                pprice.setText("Product price: Rs."+pp);
                dprice.setText("Shipping price: Rs."+ch);
                tprice.setText("Total amount: Rs."+(pp+ch));

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
                        //added by Sayan Biswas
                        float w = Float.parseFloat(temp_user_wallet);
                        float pp = Float.parseFloat(temp_productprice);
                        if (w >= pp) {


                            final String opname = pname.getText().toString();
                            confirmationFlag = true;
                            //Log.d("HERE",opname);
                            Log.d("token", artisanToken);
                            final String oprice = (tprice.getText().toString()).substring(17);
                            final DatabaseReference database = FirebaseDatabase.getInstance().getReference("User/");
                            database.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        if (data.child("userEmail").getValue().toString().equals(userX.getEmail())) {
                                            token = data.child("FCMToken").getValue().toString();

                                            orderInfo order = new orderInfo(opname, oprice, formattedDate, userX.getUid(), productCategory, productID, userX.getEmail(), token);
                                            String orderID = ordHis.push().getKey();
                                            //ordHis.child(userX.getEmail().substring(0,userX.getEmail().indexOf('@'))).child(orderID).setValue(order);
                                            ordHis.child("Users").child(userX.getUid()).child("Orders Requested").child(orderID).setValue(order);
                                            orderID = ordHis.push().getKey();
                                            ordHis.child("Artisans").child(artisanContactNumber).child("Order Requests").child(orderID).setValue(order);

                                            Retrofit retrofit = new Retrofit.Builder()
                                                    .baseUrl("https://artisansfinal.firebaseapp.com/api/")
                                                    .addConverterFactory(GsonConverterFactory.create())
                                                    .build();

                                            final Api api = retrofit.create(Api.class);
                                            Call<ResponseBody> call = api.sendNotification(artisanToken, "Order Request!", "You have request for " + opname);
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                }
                                            });


                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                        else
                        {
                            Toast.makeText(getContext(),"Not sufficient ballance to purchase.Please add money to your wallet to continue",Toast.LENGTH_SHORT).show();
                        }
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

        if(confirmationFlag)
            Snackbar.make(view.getRootView(), "Product order confirmed", Snackbar.LENGTH_SHORT);

        return view;
    }

    public boolean isServicesOK() {

        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        user_location.setText("Longitude: " + longitude + "\n" + "Latitude: " + latitude);
    }

    private void fetchLocation() {
// Here, thisActivity is the current activity

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.app.AlertDialog.Builder(getContext()).setTitle("Permission needed!").setMessage("This permission is needed to enable delivery services").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity() ,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, STORAGE_PERMISSION_CODE);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        STORAGE_PERMISSION_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
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
                Toast.makeText(getContext(), "Permission granted!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
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

//        addressEdit.setEnabled(true);
//        addressEdit.requestFocus();
        FirebaseDatabase.getInstance().getReference("Artisans").child(artisanContactNumber).child("postal_address").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        pincode = dataSnapshot.getValue(String.class);
                        name = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        final Intent intent = new Intent(getActivity(), GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
        if (fetchType == Constants.USE_ADDRESS_NAME) {
//          if (addressEdit.getText().length() == 0) {
//              Toast.makeText(this, "Please enter an address name", Toast.LENGTH_LONG).show();
//              return;
//            }
            //FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            //name = "Bangalore";
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
                Toast.makeText(getContext(),
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
//        locText.setVisibility(View.INVISIBLE);
      //progressBar.setVisibility(View.VISIBLE);
//        locProg.setVisibility(View.VISIBLE);
        Log.e(TAG, "Starting Service");
        getContext().startService(intent);
    }
    public void artisanfetch2(View view) {
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

//        addressEdit.setEnabled(true);
//        addressEdit.requestFocus();
        FirebaseDatabase.getInstance().getReference("Artisans").child(artisanContactNumber).child("postal_address").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        pincode = dataSnapshot.getValue(String.class);
                        name = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        final Intent intent = new Intent(getActivity(), GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
        if (fetchType == Constants.USE_ADDRESS_NAME) {
//          if (addressEdit.getText().length() == 0) {
//              Toast.makeText(this, "Please enter an address name", Toast.LENGTH_LONG).show();
//              return;
//            }
            //FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            //name = "Bangalore";
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
                Toast.makeText(getContext(),
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
//        locText.setVisibility(View.INVISIBLE);
        //progressBar.setVisibility(View.VISIBLE);
//        locProg.setVisibility(View.VISIBLE);
        Log.e(TAG, "Starting Service");
        getContext().startService(intent);
    }
    class AddressResultReceiver extends ResultReceiver
    {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            Log.d(TAG,"Rec result");
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        double R = 6371; // Radius of the earth in km
                        double charges = Math.sqrt((latid-address.getLatitude())*(latid-address.getLatitude()) + (longit-address.getLongitude())*(longit-address.getLongitude()));
                        charges = charges*R*0.01;
                        if(charges > 100)
                            charges = 0.1*pp;
                        else
                            charges = 10;
                         ch = (int)charges;
                        FirebaseDatabase.getInstance().getReference("Artisans").child(artisanContactNumber).child("postal_address").
                                addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        pincode = dataSnapshot.getValue(String.class);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        //progressBar.setVisibility(View.GONE);
                        //calProg.setVisibility(View.GONE);
                        locProg.setVisibility(View.GONE);

                        //infoText.setVisibility(View.VISIBLE);
                        locText.setVisibility(View.VISIBLE);
                        //dprice.setVisibility(View.VISIBLE);
                        //infoText.setText("Rs. " + ch);
                        locText.setText("Rs. "+ch);
                        //dprice.setText("Shipping price: Rs."+ch);
                        //tprice.setText("Total amount: Rs."+(pp+ch));
//                        databaseReference.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
//                                locText.setText("Rs."+ch);
//                                try {
//                                    FirebaseDatabase.getInstance().getReference("Artisans").child(artisanContactNumber).child("FCMToken")
//                                            .addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                    artisanToken = dataSnapshot.getValue(String.class);
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                    Log.d("STORAGE", storageReference.child(map.get("productID")).toString());
//                                    FirebaseDatabase.getInstance().getReference("Artisans").child(artisanContactNumber).child("postal_address").
//                                            addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                                    pincode = dataSnapshot.getValue(String.class);
//                                                    name = dataSnapshot.getValue(String.class);
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                }
//                                catch (Exception e)
//                                {
//
//                                }
//
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                            }
//                        });

                        //                    locText.setText(ch);
                    }
                });
            }
            else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //progressBar.setVisibility(View.GONE);
                        locProg.setVisibility(View.GONE);
                        //dprice.setVisibility(CalendarView.GONE);
                        //infoText.setVisibility(View.VISIBLE);
                        locText.setVisibility(View.VISIBLE);
                        //dprice.setVisibility(View.VISIBLE);
                        //infoText.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                        locText.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                        //dprice.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            }
        }

    }

}
