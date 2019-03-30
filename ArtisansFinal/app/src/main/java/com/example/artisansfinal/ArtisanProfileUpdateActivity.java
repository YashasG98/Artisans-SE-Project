package com.example.artisansfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
        setContentView(R.layout.activity_edit_artisan_info);
        Intent i=getIntent();
        String name=i.getStringExtra("Name");
        String pc=i.getStringExtra("PC");

        databaseArtisans= FirebaseDatabase.getInstance().getReference("Artisans/"+ userX.getPhoneNumber());
        final DatabaseReference dbrefArtisanProducts = FirebaseDatabase.getInstance().getReference("ArtisanProducts/"+userX.getPhoneNumber());

        updateArtisanName = (EditText) findViewById(R.id.updateArtisanName);
        updateArtisanPin = (EditText) findViewById(R.id.updateArtisanPin);
        buttonUpdateArtisan = (Button) findViewById(R.id.buttonUpdateArtisan);
        updateArtisanName.setText(name);
        updateArtisanPin.setText(pc);

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

                    dbrefArtisanProducts.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                DatabaseReference tempref= FirebaseDatabase.getInstance().getReference("ArtisanProducts/"+userX.getPhoneNumber()+"/"+data.getKey());
                                tempref.child("artisanName").setValue(newName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(final DataSnapshot data: dataSnapshot.getChildren()){
                                FirebaseDatabase.getInstance().getReference("Categories/"+data.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                        for(DataSnapshot data2:dataSnapshot2.getChildren()){
                                            if(data2.child("artisanContactNumber").getValue().toString().equals(userX.getPhoneNumber())){
                                                FirebaseDatabase.getInstance().getReference("Categories/"+data.getKey()+"/"+data2.getKey()).child("artisanName").setValue(newName);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    databaseArtisans.child("username").setValue(newName);
                    databaseArtisans.child("postal_address").setValue(newPin);
                    Toast.makeText(ArtisanProfileUpdateActivity.this,"Profile Updated",Toast.LENGTH_LONG).show();
                    Intent i= new Intent(getApplicationContext(), ArtisanProfilePageActivity.class);
                    startActivity(i);
                    finish();
                }
            }

        });
    }
}
