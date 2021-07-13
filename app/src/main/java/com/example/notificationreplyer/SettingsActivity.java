package com.example.notificationreplyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    Button settingsBtnDeleteAccount, settingsBtnLogOut, settingsBtnChNotAccess;
    Switch settingsSwitchRunInBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        settingsBtnDeleteAccount = findViewById(R.id.settingsBtnDeleteAccount);
        settingsBtnLogOut = findViewById(R.id.settingsBtnLogOut);
        settingsBtnChNotAccess = findViewById(R.id.settingsBtnChNotAccess);

        settingsSwitchRunInBackground = findViewById(R.id.settingsSwitchRunInBackground);



        // On click

        settingsBtnChNotAccess.setOnClickListener(v -> {
            // maybe say, if you going to disable then the app wont work properly

            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);

        });

        settingsBtnLogOut.setOnClickListener(v -> {

            showWarningDialog("Log Out",
                    "Are you sure you want to Log Out?");

        });

        settingsBtnDeleteAccount.setOnClickListener(v -> {
            // definitely say for are you sure and maybe/definitely need to ask for password

            showInteractiveWarningDialog(
                    "Delete Account",
                    "Sorry to see you go :`( \n" +
                            "For verification please fill your credentials below."
            );

        });
    }




    private void showWarningDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(SettingsActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText(title); //or  .getResources().getString(R.id....)
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.buttonYes)).setText("yes");
        ((Button) view.findViewById(R.id.buttonNo)).setText("no");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_warning_24);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);

        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }


    AlertDialog interactiveAlertDialog;
    private void showInteractiveWarningDialog(String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(SettingsActivity.this).inflate(
                R.layout.layout_interactive_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText(title); //or  .getResources().getString(R.id....)
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.buttonYes)).setText("Delete Account");
        ((Button) view.findViewById(R.id.buttonNo)).setText("Cancel");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_warning_24);

        interactiveAlertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            //interactiveAlertDialog.dismiss();

            deleteUser(
                    ((EditText) view.findViewById(R.id.edit_Text_Email_Address)).getText().toString(),
                    ((EditText) view.findViewById(R.id.edit_Text_Password)).getText().toString()
            );
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            interactiveAlertDialog.dismiss();
        });

        if (interactiveAlertDialog.getWindow() != null) {
            interactiveAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        interactiveAlertDialog.show();
    }



    private void deleteUser(String email, String password) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        // Prompt the user to re-provide their sign-in credentials
        if (user != null) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // need check if re-authentication was successful
                            if (task.isSuccessful()){
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    System.out.println("alex   ---: User account deleted.");

                                                    startActivity(new Intent(SettingsActivity.this, RegisterActivity.class));
                                                    Toast.makeText(SettingsActivity.this, "Your account is deleted", Toast.LENGTH_LONG).show();
                                                    interactiveAlertDialog.dismiss();
                                                }
                                                else {

                                                    Toast.makeText(SettingsActivity.this, "Something went wrong, please try again!", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });
                            }
                            else {

                                Toast.makeText(SettingsActivity.this, "Wrong Email or Password, please try again!", Toast.LENGTH_LONG).show();

                            }

                        }
                    });
        }


    }

}