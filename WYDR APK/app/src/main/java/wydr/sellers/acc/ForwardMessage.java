package wydr.sellers.acc;

import java.io.Serializable;

/**
 * Created by surya on 13/10/15.
 */
public class ForwardMessage implements Serializable {
    public String isMe, msg, sub, msgId, id;

    public String getIsMe() {
        return isMe;
    }

    public void setIsMe(String isMe) {
        this.isMe = isMe;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return msg + " " + msgId + " " + sub;
    }
}
