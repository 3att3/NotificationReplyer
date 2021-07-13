package com.example.notificationreplyer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import www.sanju.motiontoast.MotionToast;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference ref;
    TextView login;
    Button registerButton;
    TextInputLayout usernameLayout, emailLayout, passLayout, pass2Layout;
    TextInputEditText editTextUsername, editTextEmail,editTextPass,editTextPass2;
    String username,email,pass,pass2;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();
        addTextChangedListeners();
        activity = this;
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser!= null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        ref = FirebaseDatabase.getInstance().getReference("students");
        registerButton.setOnClickListener(view -> {
            username = editTextUsername.getText().toString().trim();
            email = editTextEmail.getText().toString().trim();
            pass = editTextPass.getText().toString().trim();
            pass2 = editTextPass2.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
                if (username.isEmpty()) {
                    usernameLayout.setError("You have to type your Username");
                }
                if (email.isEmpty()) {
                    emailLayout.setError("You have to type your Email Address");
                }
                if (pass.isEmpty()) {
                    passLayout.setError("You have to type your Password");
                }
                if (pass2.isEmpty()) {
                    pass2Layout.setError("You have to type your Password");
                }
            } else {
                if (!pass.equals(pass2)) {
                    pass2Layout.setError("Passwords don't match");
                    passLayout.setError("Passwords don't match");
                } else {
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) { // Αν είναι successful μεταφορά στο HomeActivity
                            String uid = FirebaseAuth.getInstance().getUid();
                            if (uid != null) {
                                // Δημιουργία νέου node στην βάση με τα στοιχεία του μαθητή
                                /*ref.child(uid).child("Username").setValue(username);
                                ref.child(uid).child("Email").setValue(email);*/

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username).build();

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                System.out.println("User profile updated.");
                                            }
                                            // Μεταφορά στο HomeActivity
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        });

                            }

                        } else {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthUserCollisionException e) { // Εμφάνιση toast για διάφορα exceptions
                                StaticMethods.showMotionToast(activity,"Oops, problem!","This email is already used by another user", MotionToast.TOAST_WARNING);
                            } catch(FirebaseAuthWeakPasswordException e) {
                                StaticMethods.showMotionToast(activity,"Oops, problem!","Your password isn't strong enough",MotionToast.TOAST_WARNING);
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                StaticMethods.showMotionToast(activity,"Oops, problem!","Invalid Email address",MotionToast.TOAST_WARNING);
                            } catch(Exception e) {
                                StaticMethods.showMotionToast(activity,"Oops, problem!","Sign up failed, try again",MotionToast.TOAST_WARNING);
                                System.out.println("alex   ---: error1: " + e);
                            }
                        }
                    }); // Έναρξη δημιουργίας του χρήστη στην firebase
                }
            }
        });

        login.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        });

    }


    private void findViews() {
        login = findViewById(R.id.tv_sign_in_click);
        registerButton = findViewById(R.id.button_register);
        editTextUsername = findViewById(R.id.register_name_edit_text);
        usernameLayout = findViewById(R.id.register_name_text_layout);
        editTextEmail = findViewById(R.id.register_email_edit_text);
        emailLayout = findViewById(R.id.register_email_text_layout);
        editTextPass = findViewById(R.id.register_password_edit_text);
        passLayout = findViewById(R.id.register_password_text_layout);
        editTextPass2 = findViewById(R.id.register_confirmpass_edit_text);
        pass2Layout = findViewById(R.id.register_confirmpass_text_layout);
    }

    private void addTextChangedListeners(){
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernameLayout.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
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
        editTextPass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pass2Layout.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            this.finishAffinity();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}