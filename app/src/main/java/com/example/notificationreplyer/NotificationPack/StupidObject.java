package com.example.notificationreplyer.NotificationPack;

import java.util.ArrayList;

public class StupidObject {
    private ArrayList<NotifAction> temp = new ArrayList<>();
    private String groupID;

    public ArrayList<NotifAction> getTemp() {
        return temp;
    }

    public void setTemp(ArrayList<NotifAction> temp) {
        this.temp = temp;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setInList(NotifAction s){
        temp.add(s);
    }
}
