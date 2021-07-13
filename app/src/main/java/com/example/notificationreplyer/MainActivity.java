package com.example.notificationreplyer;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private TextView textViewLoggedOrNot, tvMainWelcome, tvMdlMat1;
    private Button btnMdlMatProceed, btnMdlMatBack, btnBottomMatOpenWeb;
    private Space spaceMdlMatHorizontal1;
    private ImageView ivOpenWebArea;

    MaterialCardView matCVMidl, matCVBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        textViewLoggedOrNot = findViewById(R.id.textViewLoggedOrNot);
        tvMdlMat1 = findViewById(R.id.tvMdlMat1);

        matCVMidl = findViewById(R.id.matCVMidl);
        matCVBottom = findViewById(R.id.matCVBottom);

        btnMdlMatProceed = findViewById(R.id.btnMdlMatProceed);
        btnMdlMatBack = findViewById(R.id.btnMdlMatBack);
        btnBottomMatOpenWeb = findViewById(R.id.btnBottomMatOpenWeb);

        spaceMdlMatHorizontal1 = findViewById(R.id.spaceMdlMatHorizontal1);

        ivOpenWebArea = findViewById(R.id.ivOpenWebArea);



        ShPref shPref = new ShPref(this);

        Boolean isOpenFirstTime = shPref.getOpenFirstTime();
        if (!isOpenFirstTime){
            // show user first welcome message with extra data
            matCVMidl.dispatchWindowVisibilityChanged(View.VISIBLE);
            tvMdlMat1.setText(R.string.enableNotificationAccess);
            btnMdlMatBack.setVisibility(View.GONE);
            spaceMdlMatHorizontal1.setVisibility(View.GONE);
            btnMdlMatProceed.setText("Proceed");
        }
        else{
            matCVMidl.setVisibility(View.INVISIBLE);
        }


        // On click listeners

        btnMdlMatProceed.setOnClickListener(v -> {

            if (btnMdlMatProceed.getText().toString().equals("Open Settings")){
                // open settings instead from the three dots
                shPref.setOpenFirstTime(true);
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
                matCVMidl.setVisibility(View.INVISIBLE);
            }else {
                tvMdlMat1.setText(R.string.enableNotificationAccessInstractions);
                btnMdlMatBack.setVisibility(View.VISIBLE);
                spaceMdlMatHorizontal1.setVisibility(View.VISIBLE);
                btnMdlMatProceed.setText("Open Settings");
            }

        });

        btnMdlMatBack.setOnClickListener(v -> {
            matCVMidl.dispatchWindowVisibilityChanged(View.VISIBLE);
            tvMdlMat1.setText(R.string.enableNotificationAccess);
            btnMdlMatBack.setVisibility(View.GONE);
            spaceMdlMatHorizontal1.setVisibility(View.GONE);
            btnMdlMatProceed.setText("Proceed");
        });

        btnBottomMatOpenWeb.setOnClickListener(v -> {
            String url = "http://www.amazon.com"; // maybe add a website
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });

        ivOpenWebArea.setOnClickListener(v -> {

            showInfoDialog("Windows App",
                    "In order for the replyable notifications to show in your windows computer, a new application is needed. \n" +
                            "You can find our Windows application at our website by pressing the button \"Open Website\"");

        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // it has to be since there is a dedicated login and register activity
        // and it only let it here if login/register is successful

        tvMainWelcome = findViewById(R.id.tvMainWelcome);

        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }



    private void updateUI(FirebaseUser firebaseUser){
        if (firebaseUser != null){
            textViewLoggedOrNot.setText("User is logged/signed in");
            //getUpdates();
            tvMainWelcome.setText("Welcome " + firebaseUser.getDisplayName() + "!");
        }
        else {
            textViewLoggedOrNot.setText("User is NOT logged/signed in");
        }
    }



    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu) ; //Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // show alerts

    private void showSuccessDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.layout_success_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Title Success"); //or  .getResources().getString(R.id....)
        ((TextView) view.findViewById(R.id.textMessage)).setText("Message success");
        ((Button) view.findViewById(R.id.buttonAction)).setText("okay");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_done_24);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();

    }

    private void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Title Success"); //or  .getResources().getString(R.id....)
        ((TextView) view.findViewById(R.id.textMessage)).setText("Message success");
        ((Button) view.findViewById(R.id.buttonYes)).setText("yes");
        ((Button) view.findViewById(R.id.buttonNo)).setText("no");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_warning_24);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            Toast.makeText(this, "Button Yes was pressed", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            alertDialog.dismiss();
            Toast.makeText(this, "Button NO was pressed", Toast.LENGTH_SHORT).show();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void showInfoDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.layout_info_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText(title); //or  .getResources().getString(R.id....)
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.buttonAction)).setText("Ok");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_info_24);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

}