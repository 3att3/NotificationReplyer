package com.example.notificationreplyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private TextView textViewLoggedOrNot, tvMainWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        textViewLoggedOrNot = findViewById(R.id.textViewLoggedOrNot);
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