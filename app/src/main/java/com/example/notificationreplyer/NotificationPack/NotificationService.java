package com.example.notificationreplyer.NotificationPack;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.notificationreplyer.R;
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

import models.Action;
import services.BaseNotificationListener;

public class NotificationService extends BaseNotificationListener {

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ArrayList<NotifAction> notifActionArrayList = new ArrayList();
    //private ArrayList<NotifAction> deletedNotifActionArrayList = new ArrayList();

    private boolean isFirebaseListener = false;


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

    }


    @Override
    protected boolean shouldAppBeAnnounced(StatusBarNotification sbn) {
        return true;
    }

    NotificationUtils notificationUtils = new NotificationUtils();

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

        String packageName = sbn.getPackageName(); // eg: org.telegram.messenger
        Notification notification = sbn.getNotification();
        //System.out.println("alex2   ---: " + packageName);
        String notificationName = notification.extras.getString("android.title");
        String notificationText = notification.extras.getCharSequence("android.text").toString();


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

                        myRef = database.getReference("users/" + currentUser.getUid() + "/notifications/" + groupId + "/" + uniqueId);

                        myRef.child("message").setValue(notificationText);

                        NotifAction notifAction = new NotifAction();
                        notifAction.setEntireObject(action, notificationName, notificationText, packageName, timestamp.getTime());

                        // notificationText="";

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

    private void getUpdates(){
        DatabaseReference myRef;
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

                                    // TODO MAY NEED SOME FIXING ( perhaps from foreach d : dataSnapshot.getChildren() )
                                    if (notifAction.getNotificationID().equals(key)) {

                                        Action action = notifAction.getAction();

                                        try {
                                            DatabaseReference myRef;

                                            myRef = database.getReference("users/" + currentUser.getUid() + "/replies");
                                            myRef.child(key).removeValue();

                                            myRef = database.getReference("users/" + currentUser.getUid() + "/notifications");
                                            myRef.child(key).removeValue();


                                            // deletedNotifActionArrayList.add(notifAction);
                                            // TODO  I thing this need to change (line below) I thing it removes only one but more exists
                                            // TODO it definitely dose that

                                            notifActionArrayList.remove(notifAction);
                                            System.out.println("haha");
                                            System.out.println("houhou");

                                            for (NotifAction nA :
                                                    notifActionArrayList) {
                                                if (notifAction.getNotificationID().equals(key)){
                                                    notifActionArrayList.remove(nA);
                                                }
                                            }
                                            action.sendReply(context, value);


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
