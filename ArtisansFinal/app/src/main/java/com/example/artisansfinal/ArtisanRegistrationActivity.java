package com.example.artisansfinal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ArtisanRegistrationActivity extends AppCompatActivity {

    private Button activityArtisanRegistrationButton;
    private Button activity_main_Registration_button;
    private Button activityArtisanRegistrationVerifyButton;

    private boolean emailFlag;
    private boolean pincodeFlag;
    private boolean usernameFlag;
    private boolean passwordFlag;
    private boolean contactNoFlag;
    private boolean OTPFlag;
    private boolean OTPsentFlag = false;

    private List<String> contactsList;
    private List<String> emailList;

    private String codeSent;
    private String email;
    private String pincode;
    private String username;
    private String password;
    private String ContactNo;
    private String OTP;
    private String id;
    private String wallet="0";
    private String userType;
    private String token;

    private EditText emailEdit;
    private EditText contactEdit;
    private EditText pincodeEdit;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText OTPEdit;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceVerify;
    private FirebaseAuth mAuth;
    private FirebaseAuth emailAuth;

    private static final String TAG = "MyApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        userType = intent.getStringExtra("userType");



        setContentView(R.layout.activity_artisan_registration);

        firebaseDatabase = FirebaseDatabase.getInstance();
        contactsList = new ArrayList<>();
        emailList = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference("Artisans");
        databaseReferenceVerify = FirebaseDatabase.getInstance().getReference("Artisans");
        mAuth = FirebaseAuth.getInstance();
        emailAuth = FirebaseAuth.getInstance();

        //emailEdit = (EditText) findViewById(R.id.edit_email_id);
        contactEdit = (EditText) findViewById(R.id.edit_contact_no_id);
        pincodeEdit = (EditText) findViewById(R.id.edit_address_id);
        usernameEdit = (EditText) findViewById(R.id.edit_username_id);
        //passwordEdit = (EditText) findViewById(R.id.edit_password_id);
        OTPEdit = findViewById(R.id.activity_main_EditText_OTP);

        activityArtisanRegistrationButton = findViewById(R.id.activity_main_OTP_button);
        activityArtisanRegistrationVerifyButton = findViewById(R.id.products_id);


        // email = emailEdit.getEditableText().toString().trim();
        pincode = pincodeEdit.getText().toString();
        username = usernameEdit.getText().toString();
        //password = passwordEdit.getText().toString();
        ContactNo = "+91" + contactEdit.getText().toString();
        OTP = OTPEdit.getText().toString();
        id = databaseReference.push().getKey();

        activityArtisanRegistrationButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                ContactNo = "+91" + contactEdit.getText().toString();
                if (ContactNo.length() == 13 && !contactsList.contains(ContactNo))
                    SendCode();

                else if(contactsList.contains(ContactNo))
                {
                    contactEdit.requestFocus();
                    contactEdit.setError("Contact number already exists");
                }

                else {

                    if(ContactNo.equals("+91"))
                        contactEdit.setError("Enter Contact Number");
                    else
                        contactEdit.setError("Enter a valid Contact Number");
                    contactEdit.requestFocus();
                }
            }
        });

        activityArtisanRegistrationVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OTP = OTPEdit.getText().toString();


                verify();

            }
        });

    }

    public void goToProductRegistrationPage(View view) {

        //emailEdit = (EditText) findViewById(R.id.edit_email_id);
        contactEdit = (EditText) findViewById(R.id.edit_contact_no_id);
        pincodeEdit = (EditText) findViewById(R.id.edit_address_id);
        usernameEdit = (EditText) findViewById(R.id.edit_username_id);
        //passwordEdit = (EditText) findViewById(R.id.edit_password_id);
        OTPEdit = findViewById(R.id.activity_main_EditText_OTP);


        //email = emailEdit.getEditableText().toString().trim();
        pincode = pincodeEdit.getText().toString();
        username = usernameEdit.getText().toString();
        //password = passwordEdit.getText().toString();
        ContactNo = "+91" + (contactEdit.getText().toString());
        OTP = OTPEdit.getText().toString();
        id = databaseReference.push().getKey();

//        if (password.length() == 0) {
//            if(email.length() != 0) {
//                passwordEdit.setError("Enter Password");
//                passwordEdit.requestFocus();
//                passwordFlag = false;
//            }
//            else {
//                password = " ";
//                passwordFlag = true;
//            }
//        } else
        passwordFlag = true;



        if(OTP.length() == 0)
        {
            OTPEdit.setError("Enter OTP");
            OTPEdit.requestFocus();
            OTPFlag = false;
        }
        else
            OTPFlag = true;

        if (username.length() == 0) {
            usernameEdit.setError("Enter Username");
            usernameEdit.requestFocus();
            usernameFlag = false;
        } else
            usernameFlag = true;

        if (pincode.length() == 0) {
            pincodeEdit.setError("Enter Pincode");
            pincodeEdit.requestFocus();
            pincodeFlag = false;
        } else
            pincodeFlag = true;

        if (ContactNo.equals("+91") || contactsList.contains(ContactNo)) {
            if (ContactNo.equals("+91"))
                contactEdit.setError("Enter Contact Number");
            else
                contactEdit.setError("Contact Number Already Exists");
            contactEdit.requestFocus();
            contactNoFlag = false;
        } else
            contactNoFlag = true;

//        if (emailList.contains(email) || email.length() == 0) {
//            if (email.length() == 0) {
//                email = " ";
//                emailFlag = true;
//            }
//            else {
//                emailEdit.setError("Email Already Exists");
//                emailEdit.requestFocus();
//                emailFlag = false;
//            }
//        } else
        emailFlag = true;


        if (emailFlag && pincodeFlag && usernameFlag && passwordFlag && contactNoFlag && OTPFlag) {


            ArtisanInfo artisan = new ArtisanInfo(id, email, ContactNo,wallet, pincode, username, token);

            databaseReference.child(ContactNo).setValue(artisan);



            //Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_LONG).show();
            Snackbar.make(view, "Registered", Snackbar.LENGTH_SHORT);

            Intent intent = new Intent(ArtisanRegistrationActivity.this, ArtisanHomePageActivity.class);
//                    intent.putExtra("userType", userType);
            intent.putExtra("phoneNumber", ContactNo);
            intent.putExtra("name", username);
            startActivity(intent);

            finish();

        } else {
            //Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_LONG).show();
            Snackbar.make(view, "Registration Failed", Snackbar.LENGTH_SHORT).show();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(getApplicationContext(), "Verification successful", Toast.LENGTH_LONG).show();

                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {

                            token = task.getResult().getToken();
                        }
                    });

                    OTPFlag = true;
                } else {

                    Toast.makeText(getApplicationContext(), "Verification Unsuccessful", Toast.LENGTH_LONG).show();
                    OTPFlag = false;
                }
            }
        });
    }

    private void SendCode() {

        ContactNo = "+91" + contactEdit.getText().toString();
        OTPsentFlag = true;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                ContactNo,        // Phone number to SendCode
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(getApplicationContext(), " OTP Sent", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(getApplicationContext(), "OTP sending Failed", Toast.LENGTH_LONG).show();
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(getApplicationContext(), "SMS limit reached", Toast.LENGTH_LONG).show();
                // ...
            }


        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };

    private void verify() {
        OTP = OTPEdit.getText().toString();
        //Log.d(TAG, codeSent);
//        if(OTP.equals(codeSent)) {
//            Toast.makeText(getApplicationContext(), "Verification successful", Toast.LENGTH_LONG).show();
//            OTPFlag = true;
//        }
//        else {
//            Toast.makeText(getApplicationContext(), "Verification Unsuccessful", Toast.LENGTH_LONG).show();
//            OTPFlag = false;
//        }
        if (OTPsentFlag) {
            if (OTP.length() == 0) {
                OTPEdit = findViewById(R.id.activity_main_EditText_OTP);
                OTPEdit.setError("Enter OTP");
                OTPEdit.requestFocus();
            }

            else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, OTP);
                signInWithPhoneAuthCredential(credential);
            }
        } else {
            OTPEdit = findViewById(R.id.activity_main_EditText_OTP);
            OTPEdit.setError("Ask For OTP");
            // Toast.makeText(this, "Ask for OTP", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReferenceVerify.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArtisanInfo ArtisanNo = snapshot.getValue(ArtisanInfo.class);
                    contactsList.add(ArtisanNo.getContact_no());
                    emailList.add(ArtisanNo.getEmail());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

