package wydr.sellers.modal;

/**
 * Created by Ishtiyaq on 5/2/2016.
 */
public class NewBroadcast {

    String broadcastName;
    String creationDate;
    String memberName;
    String memberJid;
    String keyType;
    long broadcastId;


    public String getBroadcastName() {
        return broadcastName;
    }

    public void setBroadcastName(String broadcastName) {
        this.broadcastName = broadcastName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public long getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(long broadcastId) {
        this.broadcastId = broadcastId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberJid() {
        return memberJid;
    }

    public void setMemberJid(String memberJid) {
        this.memberJid = memberJid;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

}
