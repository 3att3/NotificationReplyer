package cy.AlexandrosGavriel.notificationReplier.NotificationPack;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import cy.AlexandrosGavriel.notificationReplier.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.hawk.Hawk;
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
    private ArrayList<NotifAction> notifActionArrayList = new ArrayList<>();
    private final String HAWK_NOTIF_ACTION_ARRAY_LIST_KEY = "hawkNotifActionArrayList";

    private boolean isFirebaseListener = false;

    NotificationUtils notificationUtils = new NotificationUtils();

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        Hawk.init(context).build();

        if (notifActionArrayList.isEmpty()){
            notifActionArrayList = getListFromHawk();
        }


        if (currentUser == null){
            if (mAuth == null){
                mAuth = FirebaseAuth.getInstance();
            }
            currentUser = mAuth.getCurrentUser();
        }

        if (!isFirebaseListener) {
            getUpdates();
        }

        Timer timer = new Timer();
        TimerTask task = new Helper();

        timer.schedule(task, 2000, 60000); // 1 min
    }



    // Periodically "ask" for check for active notifications and remove the rest from notifActionArrayList
    class Helper extends TimerTask {
        public int i = 0;
        public void run()
        {
            removeNoNActiveNotifications();
        }
    }

    // Check for active notifications in the status bar and remove the rest from the notifActionArrayList and from the firebase
    public void removeNoNActiveNotifications() {
        try {
            // it throws an error when user doesn't give notification access to the app.
            StatusBarNotification[] activeNos =  getActiveNotifications();

            if (currentUser == null){
                if (mAuth == null){
                    mAuth = FirebaseAuth.getInstance();
                }
                currentUser = mAuth.getCurrentUser();
            }

            if (!isFirebaseListener) {
                getUpdates();
            }

            try {
                DatabaseReference myRef = database.getReference("users/" + currentUser.getUid());
                ArrayList<Integer> indexesMatchingGroupIDs = new ArrayList<>();

                // get all the indexes of the active notifications in the notifActionArrayList
                for (StatusBarNotification sbNotification : activeNos) {
                    try {

                        String packageName = sbNotification.getPackageName();

                        Bundle extras = sbNotification.getNotification().extras;
                        String title = extras.getString("android.title");

                        for (NotifAction na :
                                notifActionArrayList) {
                            if (na.getNotificationID().equals(genGId(packageName, title))) {
                                indexesMatchingGroupIDs.add(notifActionArrayList.indexOf(na));
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                // Removes not Active Notifications //

                // if common groups fount then delete those that are not in the Status Bar
                if (indexesMatchingGroupIDs.size() > 0){

                    ArrayList<String> removedGroupIDs = new ArrayList<>();
                    for (int index = notifActionArrayList.size() -1; index > -1; index--){

                        if (!indexesMatchingGroupIDs.contains(index)){

                            // first remove it from firebase ( the entire Group )
                            if (!removedGroupIDs.contains(notifActionArrayList.get(index).getNotificationID())){

                                myRef.child("notifications").child(notifActionArrayList.get(index).getNotificationID()).removeValue();
                                removedGroupIDs.add(notifActionArrayList.get(index).getNotificationID());

                            }

                            // then remove it from the list. Is done multiple times because there are (sometimes) multiple entries with the same groupID.
                            notifActionArrayList.remove(index);
                        }
                    }
                }
                else {
                    notifActionArrayList.clear();
                    myRef.child("notifications").removeValue();
                    myRef.child("remove").removeValue();
                    myRef.child("reply").removeValue();
                }

                // Update hawk: notifActionArrayList backup.
                addListToHawk(notifActionArrayList);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    // a library need this, but all the checks are done in onNotificationPosted and removeNoNActiveNotifications because they have some small differences
    @Override
    protected boolean shouldAppBeAnnounced(StatusBarNotification sbn) {
        return true;
    }





    Timestamp timestamp;
    // When a new notification is posted, check if it is replyable and add it in notifActionArrayList and the firebase to be shown in the windows app
    @Override
    protected void onNotificationPosted(StatusBarNotification sbn, String dismissKey) { // SBN (statusBarNotification)
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

        Bundle extras = notification.extras;
        String notificationName = extras.getString("android.title");
        String notificationText = extras.getCharSequence("android.text").toString();


        try {

            Action action = notificationUtils.getQuickReplyAction(notification, packageName);

            if (action.isQuickReply()){

                //return number of milliseconds since January 1, 1970, 00:00:00 GMT
                timestamp = new Timestamp(System.currentTimeMillis());


                // Create unique ID for each notification (each time something is been sent)
                String groupId = genGId(packageName, notificationName);
                String uniqueId;
                if (notificationName.contains(":")){
                    String[] splitStr = notificationName.split(":");
                    uniqueId = timestamp.getTime() + "_" + splitStr[1];
                }else {
                    uniqueId = String.valueOf(timestamp.getTime());
                }

                // check if for some reason the same notification triggered the listener twice
                if(!isSameWithLastTextInChat(groupId, notificationText)){

                    NotifAction notifAction = new NotifAction();

                    // "com-google-android-apps-messaging" resends the notification after replying. In notification bar you can see the notification with the users reply
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

                    // add to firebase
                    myRef = database.getReference("users/" + currentUser.getUid() + "/notifications/" + groupId + "/" + uniqueId);
                    myRef.child("message").setValue(notificationText);

                    // add to notifActionArrayList
                    notifAction.setEntireObject(action, notificationName, notificationText, packageName, sbn.getKey(), timestamp.getTime());
                    notifActionArrayList.add(notifAction);

                    addListToHawk(notifActionArrayList);
                }



            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Check if the notifications message is the same with the last entry from the same person/group that we have
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

    
    // enable listeners from the realtimeBD firebase
    ArrayList<LastDeletedMessage> lastDeletedMessageArrayList = new ArrayList<>();
    private void getUpdates(){

        DatabaseReference myRefReplies, myRefRemove;
        try {

            if (currentUser.getUid() != null){
                myRefReplies = database.getReference("users/" + currentUser.getUid() + "/replies");
                myRefRemove = database.getReference("users/" + currentUser.getUid() + "/remove");

                isFirebaseListener = true;

                // Read from the database for replies
                myRefReplies.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot d :
                                dataSnapshot.getChildren()) {

                            String key = d.getKey();
                            String value = (String) d.getValue();

                            if (notifActionArrayList != null || notifActionArrayList.size() > 0){

                                for (NotifAction notifAction :
                                        notifActionArrayList) {

                                    if (notifAction.getNotificationID().equals(key)) {


                                        try {

                                            Action action = notifAction.getAction();
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
                                                    // we fix that error by breaking the loop after deletion
                                                    notifActionArrayList.remove(nA);
                                                    addListToHawk(notifActionArrayList);
                                                    break;
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
                                                    addListToHawk(notifActionArrayList);
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
                        Log.w(getResources().getString(R.string.failed_to_read_value), error.toException());
                    }
                });




                // Read from the database for remove
                myRefRemove.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot d :
                                dataSnapshot.getChildren()) {

                            DatabaseReference myRef2 = database.getReference("users/" + currentUser.getUid() + "/remove");

                            String key = d.getKey();
                            if (notifActionArrayList != null && notifActionArrayList.size() > 0){

                                for (NotifAction notifAction :
                                        notifActionArrayList) {

                                    if (notifAction.getNotificationID().equals(key)) {

                                        cancelNotification(notifAction.getSbnKey());

                                        myRef2.child(key).removeValue();
                                        break;

                                    }

                                } // end foreach

                            } // end if
                            else {
                                myRef2.child(key).removeValue();
                            }

                        } // end foreach


                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w(getResources().getString(R.string.failed_to_read_value), error.toException());
                    }
                });

            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    // when a notification from the status bar is removed, is getting removed from both notifActionArrayList and firebase
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        DatabaseReference myRef;

        String notificationName = sbn.getNotification().extras.getString("android.title");
        String packageName = sbn.getPackageName();

        String key = genGId(packageName, notificationName);
        if (notifActionArrayList != null && notifActionArrayList.size() > 0){
            ArrayList<NotifAction> toRemoveList = new ArrayList<>();
            for (NotifAction not :
                    notifActionArrayList) {

                if (key.equals(not.getNotificationID())) {

                    toRemoveList.add(not);

                    myRef = database.getReference("users/" + currentUser.getUid() + "/notifications");
                    myRef.child(key).removeValue();

                }
            }

            if (toRemoveList != null && toRemoveList.size() > 0) {
                notifActionArrayList.removeAll(toRemoveList);
                addListToHawk(notifActionArrayList);
            }

        }

        super.onNotificationRemoved(sbn);
    }

    // returns the groupID that is a combination of the title and the app
    // the actual generation of the groupID is happening in the NotifAction class
    private String genGId(String app, String title){
        return new NotifAction().createID(app, title);
    }

    // Hawk is a more advance Shared Preferences
    // Adds the NotifActionArrayList to the Hawk.
    private void addListToHawk(ArrayList<NotifAction> arrayList){
        Hawk.put(HAWK_NOTIF_ACTION_ARRAY_LIST_KEY, arrayList);
    }

    // Get the NotifActionArrayList from the Hawk, if it doesn't exists it returns an empty ArrayList
    private ArrayList<NotifAction> getListFromHawk(){

        if (Hawk.contains(HAWK_NOTIF_ACTION_ARRAY_LIST_KEY)){

            return Hawk.get(HAWK_NOTIF_ACTION_ARRAY_LIST_KEY);
        }
        else {
            return new ArrayList<NotifAction>();
        }
    }
}
