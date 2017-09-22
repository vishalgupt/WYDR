package wydr.sellers.gson;

/**
 * Created by surya on 2/3/16.
 */
public class RequestChat {
    public  String ownerJid,mode;

    public RequestChat(String ownerJid, String mode) {
        this.ownerJid = ownerJid;
        this.mode = mode;
    }

    public String getOwnerJid() {
        return ownerJid;
    }

    public void setOwnerJid(String ownerJid) {
        this.ownerJid = ownerJid;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
