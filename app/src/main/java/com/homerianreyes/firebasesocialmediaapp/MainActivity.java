package com.homerianreyes.firebasesocialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.homerianreyes.firebasesocialmediaapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            //transition to next activity.
            transitionToSocialMediaActivity();
        }
    }

    private void signUp() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up...");
        progressDialog.show();


        mAuth.createUserWithEmailAndPassword(binding.emailEditText.getText().toString(), binding.passwordEditText.getText().toString())
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("my_users")
                                                                        .child(task.getResult().getUser().getUid())
                                                                        .child("username").setValue(binding.usernameEditText.getText().toString());

                        transitionToSocialMediaActivity();
                        Toast.makeText(MainActivity.this, "sign up successful", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else {

                        Toast.makeText(MainActivity.this, "sign up failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });

    }

    private void signIn() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(binding.emailEditText.getText().toString(), binding.passwordEditText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            transitionToSocialMediaActivity();
                            Toast.makeText(MainActivity.this, "sign In successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "sign In failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void transitionToSocialMediaActivity() {

        Intent intent = new Intent(this, SocialMediaActivity.class);
        startActivity(intent);
        finish();
    }
}