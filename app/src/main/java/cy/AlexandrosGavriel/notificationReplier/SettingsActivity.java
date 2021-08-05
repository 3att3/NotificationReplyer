package cy.AlexandrosGavriel.notificationReplier;

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
import android.widget.TextView;
import android.widget.Toast;

import cy.AlexandrosGavriel.notificationReplier.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    Button settingsBtnDeleteAccount, settingsBtnLogOut, settingsBtnChNotAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        settingsBtnDeleteAccount = findViewById(R.id.settingsBtnDeleteAccount);
        settingsBtnLogOut = findViewById(R.id.settingsBtnLogOut);
        settingsBtnChNotAccess = findViewById(R.id.settingsBtnChNotAccess);

        // On click //

        settingsBtnChNotAccess.setOnClickListener(v -> {

            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);

        });

        settingsBtnLogOut.setOnClickListener(v -> {

            showWarningDialog(getResources().getString(R.string.LogOut),
                    getResources().getString(R.string.Are_you_sure_you_want_toLogOut__));

        });

        // Ask for credentials before deleting the account.
        settingsBtnDeleteAccount.setOnClickListener(v -> {

            showInteractiveWarningDialog(
                    getResources().getString(R.string.DeleteAccount),
                    getResources().getString(R.string.Sorry_to_see_you_go) + " :`( \n" +
                            getResources().getString(R.string.For_verification_please_fill_your_credentials_below__)
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
        ((TextView) view.findViewById(R.id.textTitle)).setText(title); //or  .getResources().getString(R.string.
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((Button) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));
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
        ((TextView) view.findViewById(R.id.textTitle)).setText(title); //or  .getResources().getString(R.string.
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.DeleteAccount));
        ((Button) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.Cancel));
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_warning_24);

        interactiveAlertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
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

        // Get auth credentials from the user for re-authentication.
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        if (user != null) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()){
                            user.delete()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            startActivity(new Intent(SettingsActivity.this, RegisterActivity.class));
                                            Toast.makeText(SettingsActivity.this, getResources().getString(R.string.Your_account_is_deleted), Toast.LENGTH_LONG).show();
                                            interactiveAlertDialog.dismiss();
                                        }
                                        else {

                                            Toast.makeText(SettingsActivity.this, getResources().getString(R.string.Something_went_wrong___please_try_again__), Toast.LENGTH_LONG).show();

                                        }
                                    });
                        }
                        else {

                            Toast.makeText(SettingsActivity.this, getResources().getString(R.string.Wrong_Email_or_Password___please_try_again__), Toast.LENGTH_LONG).show();

                        }

                    });
        }


    }

}