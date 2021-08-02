package com.example.notificationreplyer;

import android.app.Activity;

import androidx.core.content.res.ResourcesCompat;

import www.sanju.motiontoast.MotionToast;

public class StaticMethods {

    public static void showMotionToast(Activity activity, String title, String message, String type) {
        MotionToast.Companion.createColorToast(activity,title,
                message,
                type,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(activity.getApplicationContext(),R.font.helvetica_regular));
    }

}
