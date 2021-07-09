package com.example.notificationreplyer.NotificationPack;

import models.Action;

public class NotifAction {
    private Action action;
    private String title, message, notificationID, app;
    private long time;

    public String getApp() { return app; }

    // public void setApp(String app) { this.app = app; }

    public String getNotificationID() { return notificationID; }

    // public void setNotificationID(String notificationID) { this.notificationID = notificationID; }

    public Action getAction() {
        return action;
    }

    // public void setAction(Action action) { this.action = action; }

    public String getTitle() {
        return title;
    }

    // public void setTitle(String title) { this.title = title; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) { this.message = message; }

    public long getTime() {
        return time;
    }

    // public void setTime(long time) { this.time = time; }

    public void setEntireObject(Action action, String title, String message, String app, long time) { //action, message,
        this.action = action;
        this.title = title;
        this.message = message;
        this.time = time;
        this.notificationID = createID(app, title);
        this.app = app;
    }

    public String createID(String app, String value2){
        String gKey;
        System.out.println("alex33   ---: value2: " + value2);
        if (value2.contains(":")){
            String[] splitStr = value2.split(":");
            gKey = app + "_" + splitStr[0];
        }else {
            gKey = app + "_" + value2;
        }

        return gKey.replace(".", "-");
    }
}
