package com.example.notificationreplyer;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private TextView textViewLoggedOrNot, tvMainWelcome, tvMdlMat1;
    private Button btnMdlMatProceed, btnMdlMatBack;
    private Space spaceMdlMatHorizontal1;

    MaterialCardView matCVMidl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        textViewLoggedOrNot = findViewById(R.id.textViewLoggedOrNot);
        tvMdlMat1 = findViewById(R.id.tvMdlMat1);
        matCVMidl = findViewById(R.id.matCVMidl);
        btnMdlMatProceed = findViewById(R.id.btnMdlMatProceed);
        btnMdlMatBack = findViewById(R.id.btnMdlMatBack);

        spaceMdlMatHorizontal1 = findViewById(R.id.spaceMdlMatHorizontal1);



        ShPref shPref = new ShPref(this);

        Boolean isOpenFirstTime = shPref.getOpenFirstTime();
        System.out.println("alex99   ---: isOpenFirstTime: " + isOpenFirstTime);
        if (!isOpenFirstTime){
            // show user first welcome message with extra data
            matCVMidl.dispatchWindowVisibilityChanged(View.VISIBLE);
            tvMdlMat1.setText(R.string.enableNotificationAccess);
            btnMdlMatBack.setVisibility(View.GONE);
            spaceMdlMatHorizontal1.setVisibility(View.GONE);
            btnMdlMatProceed.setText("Proceed");
        }
        else{
            matCVMidl.dispatchWindowVisibilityChanged(View.INVISIBLE);
        }


        // Buttons on click

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
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}