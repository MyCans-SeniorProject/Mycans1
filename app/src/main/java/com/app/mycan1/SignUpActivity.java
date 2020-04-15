package com.app.mycan1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    Context ctx;
    ProgressDialog dialog;
    public EditText name_et, phone_et, email_et, password_et, familyID_et;
    CheckBox checkbox;
    Button signup_btn;
    TextView signin_tv;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ctx = this;
        dialog = new ProgressDialog(ctx);
        dialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        name_et = findViewById(R.id.name_et);
        phone_et = findViewById(R.id.phone_et);
        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        checkbox = findViewById(R.id.checkbox);
        familyID_et = findViewById(R.id.familyID_et);
        signup_btn = findViewById(R.id.signup_btn);
        signin_tv = findViewById(R.id.signin_tv);

        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(ctx , MainActivity.class));
            finish();
        }

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    familyID_et.setEnabled(true);
                }
                else{
                    familyID_et.setEnabled(false);
                }
            }
        });
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = name_et.getText().toString();
                String phone = phone_et.getText().toString();
                String familyID = familyID_et.getText().toString();
                String email = email_et.getText().toString();
                String password = password_et.getText().toString();

                if (name.isEmpty()) {
                    name_et.setError("Enter name!");
                    name_et.requestFocus();
                    return;
                }
                else if (phone.isEmpty()) {
                    phone_et.setError("Enter phone number!");
                    phone_et.requestFocus();
                    return;
                }
                else if (email.isEmpty()) {
                    email_et.setError("Enter email address!");
                    email_et.requestFocus();
                    return;
                }
                else if (password.isEmpty()) {
                    password_et.setError("Enter password!");
                    password_et.requestFocus();
                    return;
                }

                if(!familyID.isEmpty()){
                    familyID = familyID_et.getText().toString().trim();
                }

                else{
                    familyID = String.valueOf(new Random().nextInt(500000 - 100000) + 100000);
                }

                final  Map<String,Object> map = new HashMap<>();
                map.put("name",name);
                map.put("phone",phone);
                map.put("familyID", familyID);

                final String finalFamilyID = familyID;

                dialog.setMessage("Signing up...");
                dialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (!task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(ctx, "SignUp unsuccessful: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String userID = firebaseAuth.getCurrentUser().getUid();

                            mDatabase.getReference("users").child(userID).updateChildren(map, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError != null){
                                        dialog.dismiss();
                                        Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show();

                                    }
                                    else{
                                        dialog.dismiss();
                                        SharedPrefManager.setFamilyId(ctx, finalFamilyID);
                                        startActivity(new Intent(ctx,  MainActivity.class));
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });


        signin_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
