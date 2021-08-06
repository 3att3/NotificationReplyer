package cy.AlexandrosGavriel.notificationReplier.NotificationPack;

public class GID_LMessage_ReplyM {

    private String groupID, lastMessage, reply;

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    // custom //

    public void setGID_LMessage(String groupID, String lastMessage){
        this.groupID = groupID;
        this.lastMessage = lastMessage;
    }
}
