package com.example.notificationreplyer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import www.sanju.motiontoast.MotionToast;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    TextView login;
    Button registerButton;
    TextInputLayout usernameLayout, emailLayout, passLayout, pass2Layout;
    TextInputEditText editTextUsername, editTextEmail,editTextPass,editTextPass2;
    String username,email,pass,pass2;
    ImageView ivRegisterActInfo;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();
        addTextChangedListeners();
        addOtherListeners();
        activity = this;
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser!= null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        registerButton.setOnClickListener(view -> {
            username = editTextUsername.getText().toString().trim();
            email = editTextEmail.getText().toString().trim();
            pass = editTextPass.getText().toString().trim();
            pass2 = editTextPass2.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {

                if (username.isEmpty()) {
                    usernameLayout.setError(getResources().getText(R.string.You_have_to_type_yourUsername));
                }
                if (email.isEmpty()) {
                    emailLayout.setError(getResources().getText(R.string.You_have_to_type_yourEmailAddress));
                }
                if (pass.isEmpty()) {
                    passLayout.setError(getResources().getText(R.string.You_have_to_type_yourPassword));
                }
                if (pass2.isEmpty()) {
                    pass2Layout.setError(getResources().getText(R.string.You_have_to_retype_yourPassword));
                }

            } else {

                if (!pass.equals(pass2)) {

                    pass2Layout.setError(getResources().getText(R.string.Passwords_don__t_match));
                    passLayout.setError(getResources().getText(R.string.Passwords_don__t_match));

                } else {

                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) { // if it is successful go to HomeActivity
                            String uid = FirebaseAuth.getInstance().getUid();
                            if (uid != null) {

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username).build();

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                System.out.println(getResources().getText(R.string.User_profile_updated));
                                            }
                                            // Transfer to HomeActivity
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        });

                            }

                        } else {

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthUserCollisionException e) { // Show toast for the different exceptions
                                StaticMethods.showMotionToast(activity, getResources().getString(R.string.Oops__problem),getResources().getString(R.string.This_email_is_already_used_by_another_user), MotionToast.TOAST_WARNING);
                            } catch(FirebaseAuthWeakPasswordException e) {
                                StaticMethods.showMotionToast(activity,getResources().getString(R.string.Oops__problem),getResources().getString(R.string.Your_password_isn__t_strong_enough),MotionToast.TOAST_WARNING);
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                StaticMethods.showMotionToast(activity,getResources().getString(R.string.Oops__problem),getResources().getString(R.string.InvalidEmail_address),MotionToast.TOAST_WARNING);
                            } catch(Exception e) {
                                StaticMethods.showMotionToast(activity,getResources().getString(R.string.Oops__problem),getResources().getString(R.string.Sign_up_failed___try_again),MotionToast.TOAST_WARNING);
                                e.printStackTrace();
                            }

                        }

                    });

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
        ivRegisterActInfo = findViewById(R.id.ivRegisterActInfo);
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

    private void addOtherListeners(){
        ivRegisterActInfo.setOnClickListener(v -> showInfoDialog(
                getResources().getString(R.string.Why_register__),
                getResources().getString(R.string.We_need_to_send_your_notifications_from_your_phone_to_your_computer) + "\n" +
                        getResources().getString(R.string.The_only_way_for_that___is_to_connect_both_of_them_with_the_same_account)
        ));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            this.finishAffinity();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void showInfoDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(RegisterActivity.this).inflate(
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