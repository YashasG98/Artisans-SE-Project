package com.example.artisansfinal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileUpdateActivity extends AppCompatActivity {

    String inputId = "1234";
    EditText updateUserName;
    EditText updateUserPin;
    Button buttonUpdateUser;
    DatabaseReference databaseUsers;
    private int i=0;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseUser userX = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        databaseUsers = FirebaseDatabase.getInstance().getReference("User");
        updateUserName = (EditText) findViewById(R.id.updateUserName);
        updateUserPin = (EditText) findViewById(R.id.updateUserPin);
        buttonUpdateUser = (Button) findViewById(R.id.buttonUpdateUser);

        buttonUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String newName = updateUserName.getText().toString().trim();
                final String newPin = updateUserPin.getText().toString().trim();
                boolean nf = true, pf = true;
                if (TextUtils.isEmpty(newName)) {
                    updateUserName.setError("Enter name");
                    updateUserName.requestFocus();
                    nf = false;
                }
                if (TextUtils.isEmpty(newPin)) {
                    updateUserPin.setError("Enter Pincode");
                    updateUserPin.requestFocus();
                    pf = false;
                }
                if (nf && pf) {
                    databaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if (data.child("userEmail").getValue().toString().equals(userX.getEmail())&&i==0) {
                                    String uid = data.getKey();
                                    databaseUsers.child(uid).child("userName").setValue(newName);
                                    databaseUsers.child(uid).child("userPcode").setValue(newPin);
                                    i=1;
                                    Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
