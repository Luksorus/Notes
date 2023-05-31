package com.example.last.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.last.Util;
import com.example.last.databinding.ActivityRegisterBinding;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding bin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bin = ActivityRegisterBinding.inflate(getLayoutInflater());
        bin.progressBar.setIndeterminateDrawable(new Circle());
        setContentView(bin.getRoot());
        setListeners();

    }
    private void setListeners(){
        bin.create.setOnClickListener(v-> createAccount());
        bin.signIn.setOnClickListener(v-> finish());
    }

    void createAccount() {
        String email = bin.editTextEmail.getText().toString();
        String password = bin.editTextPas.getText().toString();
        String confrimPassword = bin.editTextRepPas.getText().toString();


        boolean isValidated = validateData(email, password, confrimPassword);
        if (!isValidated) {
            return;
        }

        createAccountInFirebase(email, password);
    }

    void createAccountInFirebase( String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()) {
                            Util.showToast(RegisterActivity.this,"Succesfully create account, Check email to verify and don't forget check spam");

                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        } else {
                            Util.showToast(RegisterActivity.this,task.getException().getLocalizedMessage());

                        }
                    }
                }
        );

    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            bin.progressBar.setVisibility(View.VISIBLE);
            bin.create.setVisibility(View.INVISIBLE);
        } else {
            bin.progressBar.setVisibility(View.GONE);
            bin.create.setVisibility(View.VISIBLE);
        }

    }

    boolean validateData(String email, String password, String confrimPassword) {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            bin.editTextEmail.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            bin.editTextPas.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confrimPassword)) {
            bin.editTextRepPas.setError("Password not matched");
            return false;
        }
        return true;
    }

}