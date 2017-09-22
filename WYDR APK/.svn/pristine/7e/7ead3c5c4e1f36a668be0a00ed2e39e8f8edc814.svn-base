package wydr.sellers.openfire;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.MessageEventManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by surya on 13/1/16.
 */
public class JSONMessage {

    public void sendHiFi(Chat chat, String agreeCode, String user_name, String selectedCode, String agreePrice, String quantity, String request_for, String msg_id) {
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "HiFi");
            data.put("name", user_name);
            data.put("product_id", selectedCode);
            data.put("code", agreeCode);
            data.put("price", agreePrice);
            data.put("qty", quantity);
            data.put("request_for", request_for);
            data.put("packet_id", msg_id);
            final Calendar c2 = Calendar.getInstance();
            Message newMessage2 = new Message();
            newMessage2.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage2,
                    true, true, true, true);
            newMessage2.setPacketID(msg_id);

            chat.sendMessage(newMessage2);
        } catch (XMPPException | JSONException e) {
            e.printStackTrace();
        }
    }

    public String sendProduct(Chat chat, String name, String code, String price, String mrp, String url, String moq) {
        String messageId = null;
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "product");
            data.put("name", name);
            data.put("code", code);
            data.put("price", price);
            data.put("mrp", mrp);
            data.put("url", url);
            data.put("moq", moq);
            Message newMessage = new Message();
            messageId = newMessage.getPacketID();
            data.put("packet_id", messageId);
            newMessage.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage,
                    true, true, true, true);


            chat.sendMessage(newMessage);
        } catch (XMPPException | JSONException e) {
            e.printStackTrace();
        }
        return messageId;
    }

    public void resendProduct(Chat chat, String name, String code, String price, String mrp, String url, String moq, String msgID) {
        //    String messageId = null;
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "product");
            data.put("name", name);
            data.put("code", code);
            data.put("price", price);
            data.put("mrp", mrp);
            data.put("url", url);
            data.put("moq", moq);
            Message newMessage = new Message();
            data.put("packet_id", msgID);
            newMessage.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage,
                    true, true, true, true);
            newMessage.setPacketID(msgID);
            chat.sendMessage(newMessage);
        } catch (XMPPException | JSONException e) {
            e.printStackTrace();
        }

    }

    public String sendText(Chat chat, String text) {
        String messageId = null;
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "text");
            data.put("text", text);
            Message newMessage = new Message();
            messageId = newMessage.getPacketID();
            data.put("packet_id", messageId);
            newMessage.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage,
                    true, true, true, true);

            chat.sendMessage(newMessage);
        } catch (JSONException | XMPPException e) {
            e.printStackTrace();
        }
        return messageId;
    }

    public String resendText(Chat chat, String text, int broadcast) {
        String messageId = null;
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "text");
            data.put("text", text);
            data.put("broadcast", broadcast);

            Message newMessage = new Message();
            messageId = newMessage.getPacketID();
            data.put("packet_id", messageId);
            newMessage.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage,
                    true, true, true, true);

            chat.sendMessage(newMessage);
        } catch (JSONException | XMPPException e) {
            e.printStackTrace();
        }
        return messageId;
    }

    public String sendAction(Chat chat, String action, String msgId) {
        String messageId = null;
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "action");
            data.put("msg_id", msgId);
            data.put("action", action);
            //     data.put("packet_id", msgId);
            Message newMessage = new Message();
            newMessage.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage,
                    true, true, true, true);
            if (msgId == null) {
                messageId = msgId;
                newMessage.setPacketID(messageId);
            } else {
                messageId = newMessage.getPacketID();
            }
            chat.sendMessage(newMessage);

        } catch (JSONException | XMPPException e) {
            e.printStackTrace();
        }
        return messageId;
    }

    public String sendQuery(Chat chat, String query, String url) {
        String messageId = null;
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "query");
            data.put("query", query);
            data.put("url", url);
            Message newMessage = new Message();
            messageId = newMessage.getPacketID();
            data.put("packet_id", messageId);
            newMessage.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage,
                    true, true, true, true);


            chat.sendMessage(newMessage);
        } catch (XMPPException | JSONException e) {
            e.printStackTrace();
        }
        return messageId;
    }

    public String resendQuery(Chat chat, String query, String url, String msgId) {
        String messageId = null;
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "query");
            data.put("query", query);
            data.put("url", url);
            Message newMessage = new Message();
            data.put("packet_id", msgId);
            newMessage.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage,
                    true, true, true, true);
            newMessage.setPacketID(msgId);

            chat.sendMessage(newMessage);
        } catch (XMPPException | JSONException e) {
            e.printStackTrace();
        }
        return msgId;
    }

    public String sendImage(Chat chat, String stringUrl, String msgId) {
        String messageId = null;
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "img");
            data.put("url", stringUrl);
            data.put("packet_id", msgId);
            Message newMessage = new Message();
            newMessage.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage,
                    true, true, true, true);
            newMessage.setPacketID(msgId);

            chat.sendMessage(newMessage);
        } catch (XMPPException | JSONException e) {
            e.printStackTrace();
        }
        return messageId;
    }

    public Message sendTextBroadcast(String text) {
        Message newMessage = null;
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "text");
            data.put("text", text);
            data.put("broadcast", 1);

            newMessage = new Message();
            data.put("packet_id", newMessage.getPacketID());
            newMessage.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage,
                    true, true, true, true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newMessage;
    }

    public Message sendProductBroadcast(String name, String code, String price, String mrp, String url, String moq) {
        Message newMessage = null;
        JSONObject data = new JSONObject();
        try {
            data.put("subject", "product");
            data.put("name", name);
            data.put("code", code);
            data.put("price", price);
            data.put("mrp", mrp);
            data.put("url", url);
            data.put("moq", moq);
            newMessage = new Message();
            data.put("packet_id", newMessage.getPacketID());
            newMessage.setBody(data.toString());
            MessageEventManager.addNotificationsRequests(newMessage,
                    true, true, true, true);
//            messageId = newMessage.getPacketID();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newMessage;
    }
}
