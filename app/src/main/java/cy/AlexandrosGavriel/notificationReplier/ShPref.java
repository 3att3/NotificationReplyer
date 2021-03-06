package cy.AlexandrosGavriel.notificationReplier;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class ShPref extends AppCompatActivity{

    private final String SHARED_PREF_NAME = "thisIsASharedPreferenceName",
            OPEN_FIRST_TIME_KEY = "openFirstTimeKey",
            RUN_IN_BACKGROUND_KEY = "runInBackgroundKey";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Context context;

    @SuppressLint("CommitPrefEdits")
    ShPref(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, 0);
        editor = sharedPreferences.edit();
    }

    public void setOpenFirstTime(boolean openFirstTime){
        editor.putBoolean(OPEN_FIRST_TIME_KEY, openFirstTime);
        editor.commit();
    }

    public boolean getOpenFirstTime(){
        return sharedPreferences.getBoolean(OPEN_FIRST_TIME_KEY, false);
    }

    public void setRunInBackground(boolean runInBackground){
        editor.putBoolean(RUN_IN_BACKGROUND_KEY, runInBackground);
        editor.commit();
    }

    public boolean getRunInBackground(){
        return sharedPreferences.getBoolean(RUN_IN_BACKGROUND_KEY, true);
    }

}