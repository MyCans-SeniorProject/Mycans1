package com.app.mycan1;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mycan1.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    Context ctx;
    ProgressDialog dialog;
    TextView nameTV, phoneTV, familyIDTV;

    FirebaseDatabase mDatabse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ctx = this;
        dialog = new ProgressDialog(ctx);
        dialog.setCancelable(false);

        mDatabse = FirebaseDatabase.getInstance();

        nameTV = findViewById(R.id.nameTV);
        phoneTV = findViewById(R.id.phoneTV);
        familyIDTV = findViewById(R.id.familyIDTV);

        dialog.setMessage("Loading...");
        dialog.show();
        getProfileInfo();

    }

    void getProfileInfo(){

        String userId = FirebaseAuth.getInstance().getUid();
        mDatabse.getReference("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();
                User user = dataSnapshot.getValue(User.class);
                nameTV.setText(user.getName());
                phoneTV.setText(user.getPhone());
                familyIDTV.setText(user.getFamilyID());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(ctx, "Error getting profile info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
