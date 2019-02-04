package com.example.artisansfinal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ArtisanProfileUpdateActivity extends AppCompatActivity {

    EditText updateArtisanName;
    EditText updateArtisanPin;
    Button buttonUpdateArtisan;
    DatabaseReference databaseArtisans;
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

    FirebaseUser userX = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_artisan_info);

        databaseArtisans= FirebaseDatabase.getInstance().getReference("Artisans");
        updateArtisanName = (EditText) findViewById(R.id.updateArtisanName);
        updateArtisanPin = (EditText) findViewById(R.id.updateArtisanPin);
        buttonUpdateArtisan = (Button) findViewById(R.id.buttonUpdateArtisan);

        buttonUpdateArtisan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String newName = updateArtisanName.getText().toString().trim();
                final String newPin = updateArtisanPin.getText().toString().trim();

                boolean nf=true,pf=true;
                if (TextUtils.isEmpty(newName)) {
                    updateArtisanName.setError("Enter name");
                    updateArtisanName.requestFocus();
                    nf=false;
                }
                if(TextUtils.isEmpty(newPin)){
                    updateArtisanPin.setError("Enter Pincode");
                    updateArtisanPin.requestFocus();
                    pf=false;
                }
                if(nf && pf) {
                    databaseArtisans.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if (data.child("email").getValue().toString().equals(userX.getEmail())) {
                                    String uid = data.getKey();
                                    databaseArtisans.child(uid).child("userName").setValue(newName);
                                    databaseArtisans.child(uid).child("userPC").setValue(newPin);
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
