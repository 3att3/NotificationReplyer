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
import androidx.core.content.ContextCompat;

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

            btnMdlMatProceed.setBackground(ContextCompat.getDrawable(this, R.drawable.button_neutral_background));
            btnMdlMatProceed.setText(getResources().getString(R.string.Proceed));
        }
        else{
            //matCVMidl.setVisibility(View.INVISIBLE);
            setDataOfMdlLayout();
        }


        // On click listeners //

        // Proceed -> first
        // Open Settings -> second
        // Show me how -> last
        btnMdlMatProceed.setOnClickListener(v -> {

            if (btnMdlMatProceed.getText().toString().equals(getResources().getString(R.string.OpenSettings))){

                shPref.setOpenFirstTime(true);
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);

                //matCVMidl.setVisibility(View.INVISIBLE);
                setDataOfMdlLayout();
            }
            else if (btnMdlMatProceed.getText().toString().equals(getResources().getString(R.string.Show_me_how))){

                String url = "https://dontkillmyapp.com/";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

            }
            else {

                tvMdlMat1.setText(R.string.enableNotificationAccessInstractions);
                btnMdlMatBack.setVisibility(View.VISIBLE);
                spaceMdlMatHorizontal1.setVisibility(View.VISIBLE);

                btnMdlMatProceed.setText(getResources().getString(R.string.OpenSettings));
                btnMdlMatProceed.setBackground(ContextCompat.getDrawable(this, R.drawable.button_neutral_right_corners_background));

            }

        });

        btnMdlMatBack.setOnClickListener(v -> {

            matCVMidl.dispatchWindowVisibilityChanged(View.VISIBLE);
            tvMdlMat1.setText(R.string.enableNotificationAccess);
            btnMdlMatBack.setVisibility(View.GONE);
            spaceMdlMatHorizontal1.setVisibility(View.GONE);
            btnMdlMatProceed.setText(getResources().getString(R.string.Proceed));
            btnMdlMatProceed.setBackground(ContextCompat.getDrawable(this, R.drawable.button_neutral_background));

        });

        btnBottomMatOpenWeb.setOnClickListener(v -> {

            String url = "https://filedn.com/l3kyrUktX1XpySaldrw7b28/";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

        });

        ivOpenWebArea.setOnClickListener(v -> showInfoDialog(getResources().getString(R.string.Windows_App),
                getResources().getString(R.string.In_order_for_the_replyable_notifications_to_show_in_your_windows_computer___a_new_application_is_needed) + "\n" +
                        getResources().getString(R.string.You_can_find_our_Windows_application_at_our_website_by_pressing_the_button) + " \""+
                        getResources().getString(R.string.OpenWebsite) +"\""));

    }


    @SuppressLint("SetTextI18n")
    private void setDataOfMdlLayout(){

        tvMdlMat1.setText(getResources().getString(R.string.Some_companies_stop_app_for_running_in_the_background_after_a_period_of_time___eg___Samsung____) +
                "\n\n"
                +getResources().getString(R.string.You_can_prevent_this_for_apps_you_choose_by_following_some_instructions_from_this_website__)
                +"\n\n"
                +getResources().getString(R.string.https______dontkillmyapp__com__)
                +"\n ");

        spaceMdlMatHorizontal1.setVisibility(View.GONE);

        btnMdlMatBack.setVisibility(View.GONE);

        btnMdlMatProceed.setText(getResources().getString(R.string.Show_me_how));
        btnMdlMatProceed.setBackground(ContextCompat.getDrawable(this, R.drawable.button_neutral_background));

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
            tvMainWelcome.setText(getResources().getString(R.string.welcome_) + " " + firebaseUser.getDisplayName() + "!");
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
    

    // show alert/s //

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