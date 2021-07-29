package com.example.notificationreplyer.NotificationPack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robj.notificationhelperlibrary.utils.NotificationUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import models.Action;
import services.BaseNotificationListener;

public class NotificationService extends BaseNotificationListener {

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ArrayList<NotifAction> notifActionArrayList = new ArrayList();
    //private ArrayList<NotifAction> deletedNotifActionArrayList = new ArrayList();

    private boolean isFirebaseListener = false;

    NotificationUtils notificationUtils = new NotificationUtils();

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

       /* Timer timer = new Timer();
        TimerTask task = new Helper();

        timer.schedule(task, 2000, 6000000);*/
    }


    class Helper extends TimerTask {
        public int i = 0;
        public void run()
        {
            System.out.println("Timer ran " + ++i);
            getActiveNotification();
        }
    }


    public Notification getActiveNotification() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] barNotifications = notificationManager.getActiveNotifications();

        for(StatusBarNotification sbNotification: barNotifications) {

            // check the list and firebase to remove the rest
            // in order for that probably need for a temp list
            ArrayList<NotifAction> tempNotificationActionArrayList = new ArrayList();
            ArrayList<Integer> tempNAIds = new ArrayList();
            NotifAction notifAction = new NotifAction();

            String packageName = sbNotification.getPackageName();
            Bundle extras = sbNotification.getNotification().extras;
            String title = extras.getString("android.title");

            String notificationID = notifAction.createID(packageName, title);


            for (NotifAction nAction :
                    notifActionArrayList) {
                if (nAction.getNotificationID().equals(notificationID)){
                    //tempNotificationActionArrayList.add(nAction);
                    tempNAIds.add(notifActionArrayList.indexOf(nAction));
                }
            }


            for (int i = notifActionArrayList.size() - 1; i > -1; i--){
                if (!tempNAIds.contains(i)){
                    // remove those from list, firebase
                    //NotifAction notifAction1 = notifActionArrayList.get(i);

                    notifActionArrayList.remove(i);

                    DatabaseReference myRef;

                    if (currentUser == null){
                        if (mAuth == null){
                            mAuth = FirebaseAuth.getInstance();
                        }
                        currentUser = mAuth.getCurrentUser();
                    }

                    if (!isFirebaseListener) {
                        getUpdates();
                    }

                    myRef = database.getReference("users/" + currentUser.getUid() + "/notifications");

                    myRef.child(notificationID).removeValue();

                }
            }


        }
        return null;
    }


    @Override
    protected boolean shouldAppBeAnnounced(StatusBarNotification sbn) {
        return true;
    }



    FirebaseDatabase database = FirebaseDatabase.getInstance();

    Timestamp timestamp;

    @Override
    protected void onNotificationPosted(StatusBarNotification sbn, String dismissKey) {
        DatabaseReference myRef;

        if (currentUser == null){
            if (mAuth == null){
                mAuth = FirebaseAuth.getInstance();
            }
            currentUser = mAuth.getCurrentUser();
        }

        if (!isFirebaseListener) {
            getUpdates();
        }

        // SBN (statusBarNotification)
        String packageName = sbn.getPackageName(); // eg: org.telegram.messenger
        Notification notification = sbn.getNotification();
        //System.out.println("alex2   ---: " + packageName);
        Bundle extras = notification.extras;
        String notificationName = extras.getString("android.title");
        String notificationText = extras.getCharSequence("android.text").toString();



        if (true) { // this if is for development purposes only! On production it is needed to be gone ( the comment below )
            //notificationName.equals("MyShares: Alexandros Gavriel")
            try {

                Action action = notificationUtils.getQuickReplyAction(notification, packageName);

                if (action.isQuickReply()){

                    //return number of milliseconds since January 1, 1970, 00:00:00 GMT
                    timestamp = new Timestamp(System.currentTimeMillis());
                    System.out.println(timestamp.getTime());


                    String groupId = genGId(packageName, notificationName);
                    String uniqueId;
                    if (notificationName.contains(":")){
                        String[] splitStr = notificationName.split(":");
                        uniqueId = timestamp.getTime() + "_" + splitStr[1];
                    }else {
                        uniqueId = String.valueOf(timestamp.getTime());
                    }

                    if(!isSameWithLastTextInChat(groupId, notificationText)){

                        NotifAction notifAction = new NotifAction();

                        if (notifAction.createID(packageName, notificationName).contains("com-google-android-apps-messaging")) {

                            for (LastDeletedMessage lsDeletedMessage :
                                    lastDeletedMessageArrayList) {
                                if (lsDeletedMessage.getNotificationID().equals(notifAction.createID(packageName, notificationName))
                                        && lsDeletedMessage.getMessage().equals(notificationText)){
                                    cancelNotification(sbn.getKey());
                                    return;
                                }
                            }

                        }

                        myRef = database.getReference("users/" + currentUser.getUid() + "/notifications/" + groupId + "/" + uniqueId);
                        myRef.child("message").setValue(notificationText);

                        notifAction.setEntireObject(action, notificationName, notificationText, packageName, sbn.getKey(), timestamp.getTime());

                        notifActionArrayList.add(notifAction);
                    }



                }
            } catch (Exception e) {
                System.out.println("Alex   ---: stackTrace");
                e.printStackTrace();
            }
        }

    }


    private boolean isSameWithLastTextInChat(String groupID, String message){
        ArrayList<StupidObject> listOfList = new ArrayList<>();



        ArrayList<String> gIDs = new ArrayList<>();
        for (NotifAction stupidObject :
                notifActionArrayList) {
            if (!gIDs.contains(stupidObject.getNotificationID())){
                gIDs.add(stupidObject.getNotificationID());
            }
        }

        for (String gid :
                gIDs) {
            StupidObject test1 = new StupidObject();
            test1.setGroupID(gid);
            for (NotifAction stupidObject :
                    notifActionArrayList) {

                if (gid.equals(stupidObject.getNotificationID())){
                    test1.setInList(stupidObject);
                }
            }
            listOfList.add(test1);
        }

        for (StupidObject stObject :
                listOfList) {
            if (stObject.getGroupID().equals(groupID)) {
                int index = 0, maxIndex = -1;
                long maxTime = -1;
                for (NotifAction nfAction :
                        stObject.getTemp()) {
                    if (nfAction.getTime() > maxTime){
                        maxIndex = index;
                        maxTime = nfAction.getTime();
                    }
                    index++;
                }

                if (maxTime > -1){
                    if (stObject.getTemp().get(maxIndex).getMessage().equals(message)){
                        return true;
                    }else {
                        return false;
                    }
                }else {
                    return false;
                }
            }
        }
        return false;
    }

    ArrayList<LastDeletedMessage> lastDeletedMessageArrayList = new ArrayList<LastDeletedMessage>();
    private void getUpdates(){
        DatabaseReference myRef, myRef2;
        try {
            if (currentUser.getUid() != null){
                myRef = database.getReference("users/" + currentUser.getUid() + "/replies");

                isFirebaseListener = true;

                // Read from the database
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot d :
                                dataSnapshot.getChildren()) {

                            System.out.println("Alex   ---: d: " + d);

                            String key = d.getKey();
                            String value = (String) d.getValue();

                            if (notifActionArrayList != null || notifActionArrayList.size() > 0){

                                for (NotifAction notifAction :
                                        notifActionArrayList) {

                                    // _TODO MAY NEED SOME FIXING ( perhaps from foreach d : dataSnapshot.getChildren() )
                                    if (notifAction.getNotificationID().equals(key)) {

                                        Action action = notifAction.getAction();

                                        try {

                                            action.sendReply(context, value);

                                            DatabaseReference myRef;

                                            for (NotifAction nA :
                                                    notifActionArrayList) {
                                                if (notifAction.getNotificationID().equals(key)){

                                                    if (key.contains("com-google-android-apps-messaging")){
                                                        LastDeletedMessage lastDeletedMessage = new LastDeletedMessage();
                                                        lastDeletedMessage.setNotificationID(key);
                                                        lastDeletedMessage.setMessage(notifAction.getMessage());
                                                        lastDeletedMessageArrayList.add(lastDeletedMessage);
                                                    }

                                                    // due to deletion of an object from the list, we create a conflict.
                                                    notifActionArrayList.remove(nA);
                                                    break; // we fix that error by breaking the loop after that deletion
                                                }
                                            }

                                            myRef = database.getReference("users/" + currentUser.getUid() + "/replies");
                                            myRef.child(key).removeValue();

                                            myRef = database.getReference("users/" + currentUser.getUid() + "/notifications");
                                            myRef.child(key).removeValue();

                                            // Delete all notification messages with the same id and older than the replyable one
                                            for (int i=notifActionArrayList.size()-1; i > -1; i--){
                                                if (notifActionArrayList.get(i).getNotificationID().equals(key)
                                                && notifActionArrayList.get(i).getTime() < notifAction.getTime()){
                                                    notifActionArrayList.remove(i);
                                                }
                                            }

                                            return;

                                        } catch (PendingIntent.CanceledException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                } // end foreach

                            } // end if

                        } // end foreach


                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Failed to read value.", error.toException());
                    }
                });


                myRef2 = database.getReference("users/" + currentUser.getUid() + "/remove");

                //isFirebaseListener = true;

                // Read from the database
                myRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot d :
                                dataSnapshot.getChildren()) {

                            System.out.println("Alex   ---: d: " + d);

                            String key = d.getKey();
                            String value = (String) d.getValue();

                            if (notifActionArrayList != null || notifActionArrayList.size() > 0){

                                for (NotifAction notifAction :
                                        notifActionArrayList) {

                                    // _TODO MAY NEED SOME FIXING ( perhaps from foreach d : dataSnapshot.getChildren() )
                                    if (notifAction.getNotificationID().equals(key)) {

                                        cancelNotification(notifAction.getSbnKey());

                                        //myRef2 = database.getReference("users/" + currentUser.getUid() + "/remove");
                                        myRef2.child(key).removeValue();
                                        break;

                                    }

                                } // end foreach

                            } // end if

                        } // end foreach


                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Failed to read value.", error.toException());
                    }
                });

            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        DatabaseReference myRef;

        String notificationName = sbn.getNotification().extras.getString("android.title");
        String packageName = sbn.getPackageName();
        // String notificationText = sbn.getNotification().extras.getCharSequence("android.text").toString();

        String key;//, keyPt2;
        key = genGId(packageName, notificationName);


        if (notifActionArrayList != null && notifActionArrayList.size() > 0){
            ArrayList<NotifAction> toRemoveList = new ArrayList<>();
            for (NotifAction not :
                    notifActionArrayList) {

                if (key.equals(not.getNotificationID())) {

                    toRemoveList.add(not);


                    myRef = database.getReference("users/" + currentUser.getUid() + "/notifications");

                    myRef.child(key).removeValue();

                    System.out.println("suppose it did it");

                }
            }

            if (toRemoveList != null && toRemoveList.size() > 0) {
                notifActionArrayList.removeAll(toRemoveList);
            }
            System.out.println("alex   ---: testing for the size of the list: " + notifActionArrayList.size());

        }


        System.out.println("out of loop");

        super.onNotificationRemoved(sbn);
    }

    private String genGId(String app, String title){
        return new NotifAction().createID(app, title);
    }
}
