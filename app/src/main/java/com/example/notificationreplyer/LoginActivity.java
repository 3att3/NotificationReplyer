package com.example.notificationreplyer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import www.sanju.motiontoast.MotionToast;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    TextView register;
    Button loginButton;
    TextInputLayout emailLayout, passLayout;
    TextInputEditText editTextEmail, editTextPass;
    String email, pass;
    ImageView ivLoginActInfo;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;
        //startService(new Intent(this,MyService.class));
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser!= null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        findViews();
        addOtherListeners();
        addTextChangedListeners();

        register.setOnClickListener(view -> {
            finish();
        });

        loginButton.setOnClickListener(view -> {
            email = editTextEmail.getText().toString().trim();
            pass = editTextPass.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty()) {
                if (email.isEmpty()) {
                    emailLayout.setError("Please type your Email");
                }
                if (pass.isEmpty()) {
                    passLayout.setError("Please type your Password");
                }
            } else {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthInvalidUserException e) { // Εμφάνιση toast για διάφορα exceptions
                                    StaticMethods.showMotionToast(activity,"Oops, problem!","Connection Issues.", MotionToast.TOAST_WARNING);
                                    System.out.println("alex   ---: error1: " + e);
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    StaticMethods.showMotionToast(activity, "Oops, problem!","Wrong Email/Password.",MotionToast.TOAST_WARNING);
                                    System.out.println("alex   ---: error1: " + e);
                                } catch (Exception e) {
                                    StaticMethods.showMotionToast(activity,"Oops, problem!","Connection Issues.",MotionToast.TOAST_WARNING);
                                    System.out.println("alex   ---: error1: " + e);
                                }
                            }
                        });
            }
        });

    }

    private void findViews() {
        register = findViewById(R.id.tv_sign_up_click);
        loginButton = findViewById(R.id.button_login);
        editTextEmail = findViewById(R.id.login_email_edit_text);
        editTextPass = findViewById(R.id.login_password_edit_text);
        emailLayout = findViewById(R.id.login_email_text_layout);
        passLayout = findViewById(R.id.login_password_text_layout);
        ivLoginActInfo = findViewById(R.id.ivLoginActInfo);
    }

    private void addTextChangedListeners() {
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailLayout.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        editTextPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passLayout.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    private void addOtherListeners(){
        ivLoginActInfo.setOnClickListener(v -> showInfoDialog(
                "Why LogIn?",
                "We need to send your notifications from your phone to your computer.\n" +
                        "The only way for that, is to connect both of them with the same account."
        ));
    }


    private void showInfoDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(LoginActivity.this).inflate(
                R.layout.layout_info_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText(title); //or  .getResources().getString(R.string....)
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.ok));
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_info_24);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }


}