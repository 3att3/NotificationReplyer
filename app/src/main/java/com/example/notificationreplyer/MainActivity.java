package com.example.notificationreplyer;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private TextView tvMainWelcome;
    private TextView tvMdlMat1;
    private Button btnMdlMatProceed;
    private Button btnMdlMatBack;
    private Space spaceMdlMatHorizontal1;

    MaterialCardView matCVMidl, matCVBottom;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        tvMdlMat1 = findViewById(R.id.tvMdlMat1);
        TextView tvBtMat1 = findViewById(R.id.tvBtMat1);

        matCVMidl = findViewById(R.id.matCVMidl);
        matCVBottom = findViewById(R.id.matCVBottom);

        btnMdlMatProceed = findViewById(R.id.btnMdlMatProceed);
        btnMdlMatBack = findViewById(R.id.btnMdlMatBack);
        Button btnBottomMatOpenWeb = findViewById(R.id.btnBottomMatOpenWeb);

        spaceMdlMatHorizontal1 = findViewById(R.id.spaceMdlMatHorizontal1);

        ImageView ivOpenWebArea = findViewById(R.id.ivOpenWebArea);



        // Dummy initiations //
        tvBtMat1.setText(getResources().getString(R.string.installWinApplicationPt1) +
                System.getProperty("line.separator") +
                getResources().getString(R.string.installWinApplicationPt2));

        // ---------------- //

        ShPref shPref = new ShPref(this);

        boolean isOpenFirstTime = shPref.getOpenFirstTime();
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

        ivOpenWebArea.setOnClickListener(v -> showInfoDialog("Windows App",
                "In order for the replyable notifications to show in your windows computer, a new application is needed. \n" +
                        "You can find our Windows application at our website by pressing the button \"Open Website\""));

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // just a small precaution

        tvMainWelcome = findViewById(R.id.tvMainWelcome);

        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    @SuppressLint("SetTextI18n")
    private void updateUI(FirebaseUser firebaseUser){
        if (firebaseUser != null){
            //getUpdates();
            tvMainWelcome.setText(getResources().getString(R.string.welcome_) + firebaseUser.getDisplayName() + "!");
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

    // show alert/s

    private void showInfoDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(
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