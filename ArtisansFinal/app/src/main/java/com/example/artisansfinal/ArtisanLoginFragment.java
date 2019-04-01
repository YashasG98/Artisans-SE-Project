package com.example.artisansfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ArtisanLoginFragment extends Fragment {

    private EditText contactNoEdit;
    private EditText OTPEdit;
    private Button Login;
    private TextView userRegistration;
    private Button sendOTP;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReferenceVerify;
    private ProgressDialog progressDialog;
    private String ContactNo;
    private String OTP;
    private String codeSent;
    private String name;
    private boolean OTPFlag;
    private List<String> contactsList;
    private List<String> usernameList;
    private boolean OTPsent = false;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.activity_artisan_login, container, false);
        FirebaseUser userx = FirebaseAuth.getInstance().getCurrentUser();
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
//        {
//            if(FirebaseAuth.getInstance().getCurrentUser().getEmail()!=null)
//            {
//                Intent intent = new Intent(getContext(), ArtisanHomePageActivity.class);
//                intent.putExtra("userType", "a");
//                intent.putExtra("phoneNumber", "123");
//                startActivity(intent);
//
//            }
//            else
//                startActivity(new Intent(getContext(),UserHomePageActivity.class));
//
//
//        }

        contactNoEdit = (EditText) view.findViewById(R.id.edit_artisan_login_activity_Contact_No);
        OTPEdit = (EditText) view.findViewById(R.id.edit_artisan_login_activity_OTP);
        Login = (Button) view.findViewById(R.id.btnLogin);
        userRegistration = view.findViewById(R.id.Register);
        sendOTP = view.findViewById(R.id.send_otp_button);

        userRegistration.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        contactsList = new ArrayList<>();
        usernameList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        databaseReferenceVerify = FirebaseDatabase.getInstance().getReference("Artisans");

        progressDialog = new ProgressDialog(getContext());
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            progressDialog.setMessage("wait");
            progressDialog.show();

            //view.setVisibility(view.INVISIBLE);

            String check = user.getPhoneNumber();

            if(check !=null && !check.isEmpty()) {
                final String contactNo = mAuth.getCurrentUser().getPhoneNumber();
                Intent intent = new Intent(getContext(), ArtisanHomePageActivity.class);

                intent.putExtra("phoneNumber", contactNo);
                startActivity(intent);
                progressDialog.dismiss();
                getActivity().finish();
//            final String nameTry = mAuth.getCurrentUser().get
//                DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference("Artisans/" + contactNo + "/username");
//                nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        name = dataSnapshot.getValue(String.class);
//                        Intent intent = new Intent(getContext(), ArtisanHomePageActivity.class);
//                        intent.putExtra("name", name);
//                        intent.putExtra("phoneNumber", contactNo);
//                        intent.putExtra("userType", "a");
//                        Log.d("no", contactNo);
//                        startActivity(intent);
//                        Log.d("name", name);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
            }
            else
            {
                progressDialog.dismiss();
            }
//            else
//            {
//                Intent intent1 = new Intent(new Intent(getContext(), UserHomePageActivity.class));
//                startActivity(intent1);
//                getActivity().finish();
//            }


            //finish();
//            Intent intent = new Intent(ArtisanLoginActivity.this, ArtisanHomePageActivity.class);
//            intent.putExtra("name", name);
//            intent.putExtra("phoneNumber", contactNo);
//            Log.d("no", contactNo);
//            startActivity(intent);
        }
        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ArtisanRegistrationActivity.class));
                getActivity().finish();
            }
        });

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //contactNoEdit = view.findViewById(R.id.edit_artisan_login_activity_Contact_No);

                ContactNo = "+91" + contactNoEdit.getText().toString();

//                databaseReferenceVerify.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            ArtisanInfo ArtisanNo = snapshot.getValue(ArtisanInfo.class);
//                            contactsList.add(ArtisanNo.getContact_no());
//                            usernameList.add(ArtisanNo.getUsername());
//
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

                if(ContactNo.length() !=0 && contactsList.contains(ContactNo)) {
                    progressDialog.setMessage("Sending OTP..");
                    progressDialog.show();
                    SendCode(view);
                }
                else {
                    if(ContactNo.equals("+91")) {

                        contactNoEdit.setError("Enter Contact Number");
                        contactNoEdit.requestFocus();
                    }
                    else
                        Toast.makeText(getContext(), "Contact Number not registered. Tap on Sign Up to register", Toast.LENGTH_LONG).show();
                }
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //OTPEdit = (EditText) view.findViewById(R.id.edit_artisan_login_activity_OTP);
                OTP = OTPEdit.getText().toString();
//                if(OTP.length() != 0 && OTPFlag)

                if(contactNoEdit.getText().toString().equals(""))
                {
                    contactNoEdit.setError("Enter Contact number");
                    contactNoEdit.requestFocus();
                }

                if(OTP.equals(""))
                {
                    OTPEdit.setError("Enter OTP");
                    OTPEdit.requestFocus();
                }
                else
                    verify(view);
//                else
//                {
//                    OTPEdit.setError("Enter OTP");
//                    OTPEdit.requestFocus();
//                }
            }
        });


        return view;
    }

    private void verify(View view) {

        OTPEdit = view.findViewById(R.id.edit_artisan_login_activity_OTP);
        OTP = OTPEdit.getText().toString();
        if(!OTPsent){
            OTPEdit.setError("Request OTP first");
        }
        else if(OTP.length()==0){
            OTPEdit.setError("Enter OTP");
            OTPEdit.requestFocus();
        }
        else {
            progressDialog.setMessage("Please wait while we log you in");
            progressDialog.show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, OTP);
            signInWithPhoneAuthCredential(credential);
        }


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            final String token = task.getResult().getToken();
                            FirebaseDatabase.getInstance().getReference("Artisans")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("FCMToken").setValue(token);


                        }
                    });
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),   "Verification successful", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getContext(),ArtisanHomePageActivity.class);
                    intent.putExtra("phoneNumber", ContactNo);
                    String username = usernameList.get(contactsList.indexOf(ContactNo));
                    intent.putExtra("name", username);
                    intent.putExtra("userType", "a");
                    Log.d("Here", username+" "+ContactNo);
                    startActivity(intent);
                    OTPFlag = true;

                    getActivity().finish();
                } else {

                    Log.d("Here", "Fail");

                    Toast.makeText(getContext(), "Verification Unsuccessful", Toast.LENGTH_LONG).show();
                    OTPFlag = false;
                }
            }
        });
        return ;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);


    }

    private void SendCode(View view) {

        contactNoEdit =  view.findViewById(R.id.edit_artisan_login_activity_Contact_No);
        ContactNo = "+91" + contactNoEdit.getText().toString();

        OTPsent = true;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                ContactNo,        // Phone number to SendCode
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            OTPFlag = true;
            progressDialog.dismiss();
            Toast.makeText(getContext(), "OTP Sent", Toast.LENGTH_LONG).show();


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(getContext(), "Login Unsuccessful", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(getContext(), "Login Unsuccessful", Toast.LENGTH_LONG).show();
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(getContext(), "sms", Toast.LENGTH_LONG).show();
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
    public void onStart() {
        super.onStart();

        databaseReferenceVerify.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArtisanInfo ArtisanNo = snapshot.getValue(ArtisanInfo.class);
                    contactsList.add(ArtisanNo.getContact_no());
                    usernameList.add(ArtisanNo.getUsername());


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
