package cy.AlexandrosGavriel.notificationReplier;

import android.app.Application;
import android.content.Intent;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ShPref shPref = new ShPref(getApplicationContext());


        if (shPref.getRunInBackground()){
            startService(new Intent(this, BackgroundService.class));
        }
    }
}