package com.example.artisansfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ArtisanLoginActivity extends AppCompatActivity {
    private EditText contactNoEdit;
    private EditText OTPEdit;
    private Button Login;
    private Button userRegistration;
    private Button sendOTP;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReferenceVerify;
    private ProgressDialog progressDialog;
    private String ContactNo;
    private String OTP;
    private String codeSent;
    private boolean OTPFlag;
    private List<String> contactsList;
    private boolean OTPsent = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_artisan_login);

        contactNoEdit = (EditText) findViewById(R.id.edit_artisan_login_activity_Contact_No);
        OTPEdit = (EditText) findViewById(R.id.edit_artisan_login_activity_OTP);
        Login = (Button) findViewById(R.id.btnLogin);
        userRegistration = (Button) findViewById(R.id.Register);
        sendOTP = findViewById(R.id.send_otp_button);

        contactsList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        databaseReferenceVerify = FirebaseDatabase.getInstance().getReference("Artisans");

        progressDialog = new ProgressDialog(this);
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            finish();

            startActivity(new Intent(ArtisanLoginActivity.this, ArtisanHomePageActivity.class));
        }
        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArtisanLoginActivity.this, ArtisanRegistrationActivity.class));
            }
        });

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contactNoEdit = findViewById(R.id.edit_artisan_login_activity_Contact_No);
                ContactNo = contactNoEdit.getText().toString();
                if(ContactNo.length() !=0 && contactsList.contains(ContactNo))
                    SendCode();
                else {
                    if(ContactNo.length() == 0) {

                        contactNoEdit.setError("Enter Contact Number");
                        contactNoEdit.requestFocus();
                    }
                    else
                        Toast.makeText(ArtisanLoginActivity.this, "Contact Number not registered. Tap on Sign Up to register", Toast.LENGTH_LONG).show();
                }
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OTPEdit = (EditText) findViewById(R.id.edit_artisan_login_activity_OTP);
                OTP = OTPEdit.getText().toString();
//                if(OTP.length() != 0 && OTPFlag)
                    verify();
//                else
//                {
//                    OTPEdit.setError("Enter OTP");
//                    OTPEdit.requestFocus();
//                }
            }
        });


    }

//    private void validate(String userName,String userPassword){
//        progressDialog.setMessage("Please wait while we log U in");
//        progressDialog.show();
//        firebaseAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful())
//                {
//                    progressDialog.dismiss();
//                    Toast.makeText(artisan_login.this,"Login Successful",Toast.LENGTH_LONG).show();
//                    startActivity(new Intent(artisan_login.this,product_details.class));
//                }
//                else
//                {
//                    progressDialog.dismiss();
//                    Toast.makeText(artisan_login.this,"Login Failed",Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }

    private void verify() {

        OTPEdit = findViewById(R.id.edit_artisan_login_activity_OTP);
        OTP = OTPEdit.getText().toString();
        if(!OTPsent){
            OTPEdit.setError("Request OTP first");
        }
        else if(OTP.length()==0){
            OTPEdit.setError("Enter OTP");
            OTPEdit.requestFocus();
        }
        else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, OTP);
            signInWithPhoneAuthCredential(credential);
        }


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(getApplicationContext(), "Verification successful", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(ArtisanLoginActivity.this,ArtisanHomePageActivity.class));

                    OTPFlag = true;
                } else {

                    Log.d("Here", "Fail");

                    Toast.makeText(ArtisanLoginActivity.this, "Verification Unsuccessful", Toast.LENGTH_LONG).show();
                    OTPFlag = false;
                }
            }
        });
    }

    private void SendCode() {

        contactNoEdit =  findViewById(R.id.edit_artisan_login_activity_Contact_No);
        ContactNo = contactNoEdit.getText().toString();

        OTPsent = true;

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
            OTPFlag = true;
            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(getApplicationContext(), "Login UnSuccessful", Toast.LENGTH_LONG).show();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(getApplicationContext(), "Login UnSuccessful", Toast.LENGTH_LONG).show();
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(getApplicationContext(), "sms", Toast.LENGTH_LONG).show();
                // ...
            }


        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            //super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }


    };

    @Override
    protected void onStart() {
        super.onStart();

        databaseReferenceVerify.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArtisanInfo ArtisanNo = snapshot.getValue(ArtisanInfo.class);
                    contactsList.add(ArtisanNo.getContact_no());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}