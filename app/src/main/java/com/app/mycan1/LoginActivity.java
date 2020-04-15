package com.app.mycan1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mycan1.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    FirebaseDatabase mDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    Context ctx;
    ProgressDialog dialog;
    EditText email_et, password_et;
    Button login_btn;
    TextView signup_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ctx = this;
        dialog = new ProgressDialog(ctx);
        dialog.setCancelable(false);

        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);
        signup_tv = findViewById(R.id.signup_tv);

        mDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Intent i = new Intent(ctx, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ctx, SignUpActivity.class);
                startActivity(I);
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = email_et.getText().toString();
                String password = password_et.getText().toString();

                if (email.isEmpty()) {
                    email_et.setError("Enter Email!");
                    email_et.requestFocus();
                    return;
                }
                else if (password.isEmpty()) {
                    password_et.setError("Enter Password!");
                    password_et.requestFocus();
                    return;
                }

                dialog.setMessage("Signing in...");
                dialog.show();

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (!task.isSuccessful()) {

                            dialog.dismiss();
                            Toast.makeText(ctx, "Error logging in", Toast.LENGTH_SHORT).show();
                        } else {

                            getFamilyId();
                        }
                    }
                });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }


    void getFamilyId(){

        String userId = firebaseAuth.getCurrentUser().getUid();
        mDatabase.getReference("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dialog.dismiss();
                User user = dataSnapshot.getValue(User.class);

                SharedPrefManager.setFamilyId(ctx, user.getFamilyID());
                startActivity(new Intent(ctx,  MainActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(ctx, "Error getting user info", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
