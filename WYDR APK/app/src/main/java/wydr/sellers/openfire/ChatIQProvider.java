package wydr.sellers.openfire;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;


/**
 * Created by surya on 7/12/15.
 */
public class ChatIQProvider implements IQProvider {

    public ChatIQProvider() {
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
       // Log.d("CHAT IQ PROVIDER", String.format("Received iq packet, namespace[%s], name[%s]", parser.getNamespace(), parser.getName()));
        ChatIQ iq = new ChatIQ();
        ChatIQ.Set set = new ChatIQ.Set();
        boolean done = false;

        ChatIQ.AMessage aMsg = new ChatIQ.AMessage();
        // ChatIQ.To to = new ChatIQ.To();
        String secs = "", jid = "";
        iq.setStart(parser.getAttributeValue("", "start"));
        iq.setWith(parser.getAttributeValue("", "with"));
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("from")) {

                    secs = parser.getAttributeValue("", "secs");
                    jid = parser.getAttributeValue("", "jid");

                    aMsg = new ChatIQ.AMessage();
                    aMsg.setTypes("from");
                    aMsg.setJid(jid);
                    aMsg.setSecs(secs);

                    iq.addAmessage(aMsg);

                } else if (parser.getName().equals("to")) {

                    secs = parser.getAttributeValue("", "secs");
                    jid = parser.getAttributeValue("", "jid");
                    aMsg = new ChatIQ.AMessage();
                    aMsg.setTypes("to");
                    aMsg.setJid(jid);
                    aMsg.setSecs(secs);
                    iq.addAmessage(aMsg);

                } else if (parser.getName().equals("body") && aMsg.getBody() == null) {
                    ChatIQ.Body body = new ChatIQ.Body(parser.nextText());
                    aMsg.setBody(body);
                    //  to.setBody(body);
                } else if (parser.getName().equals("first")) {
                    int index = parseInt(parser.getAttributeValue("", "index"));
                    set.setIndexAtt(index);
                    int first = parseInt(parser.nextText());
                    set.setFirst(first);
                } else if (parser.getName().equals("last")) {
                    int last = parseInt(parser.nextText());
                    set.setLast(last);
                } else if (parser.getName().equals("count")) {
                    int count = parseInt(parser.nextText());
                    set.setCount(count);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("chat")) {
                    iq.setSet(set);
                    done = true;
                }
            }
        }

        return iq;
    }

    private int parseInt(String integer) {
        return Integer.parseInt((integer != null ? integer : "0"));
    }
}
