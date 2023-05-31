package com.example.last.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.example.last.Util;
import com.example.last.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding bin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bin = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bin.getRoot());
        setListeners();
    }

    private void setListeners(){
        bin.signbtn.setOnClickListener((v)-> loginUser() );

        bin.createANew.setOnClickListener((v) ->startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            bin.progressBar.setVisibility(View.VISIBLE);
            bin.signbtn.setVisibility(View.INVISIBLE);
        } else {
            bin.progressBar.setVisibility(View.GONE);
            bin.signbtn.setVisibility(View.VISIBLE);
        }

    }
    void loginUser(){
        String email = bin.editTextEmail.getText().toString();
        String password = bin.editTextPas.getText().toString();


        boolean isValidated = validateData(email, password );
        if (!isValidated) {
            return;
        }

        loginAccountInFirebase( email, password);
    }

    void loginAccountInFirebase(String email, String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            } else {
                            Util.showToast(LoginActivity.this, "Email not verified, Please verify you Email.");

                            }
                        }else {
                            Util.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                }
        );

    }

    boolean validateData(String email, String password) {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            bin.editTextEmail.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            bin.editTextPas.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}
