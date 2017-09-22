package wydr.sellers.openfire;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surya on 7/12/15.
 */
public class ChatIQ extends IQ {


    private String xmlns;
    private String with;
    private String start;


    //private List<From> froms;
    //  private List<To> tos;
    private List<AMessage> iqList;
    private Set set;


    public ChatIQ() {
        //   this.froms = new ArrayList<ChatIQ.From>();
        //  this.tos = new ArrayList<To>();
        iqList = new ArrayList();
    }

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }



    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void addAmessage(AMessage aMessage) {
        iqList.add(aMessage);
    }

    public List<AMessage> getAMessages() {
        return iqList;
    }
//    public void addFrom(From from) {
//        // froms.add(from);
//        iqList.add(from);
//    }
//
//    //    public List<From> getFroms() {
////        return froms;
////    }
//    public List getContent() {
//        return iqList;
//    }
//
//    public void addTo(To to) {
//        // tos.add(to);
//        iqList.add(to);
//
//    }

//    public List<To> getTos() {
//        return tos;
//    }

    public Set getSet() {
        return set;
    }

    public void setSet(Set set) {
        this.set = set;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder("<chat xmlns=\"urn:xmpp:archive\"");
        builder.append("with=\"").append(with).append("\"");
        builder.append(" start=\"");
        builder.append(start);

        builder.append("\">");

        for (AMessage amsg : iqList) {
            //  if (type.equals("from")) {
            builder.append(amsg.toXml());
            //}

        }

        builder.append(set.toXml());
        builder.append("</chat>");
        return builder.toString();
    }

    public static class AMessage {
        private String secs;
        private String jid;
        private String type;

        private Body body;

        public String getSecs() {
            return secs;
        }

        public void setSecs(String secs) {
            this.secs = secs;
        }

        public String getTypes() {
            return type;
        }

        public void setTypes(String type) {
            this.type = type;
        }

        public String getJid() {
            return jid;
        }


        public void setJid(String jid) {
            this.jid = jid;
        }


        public Body getBody() {
            return body;
        }


        public void setBody(Body body) {
            this.body = body;
        }


        public String toXml() {
            StringBuilder builder = new StringBuilder("<message ");
            builder.append("secs=\"").append(secs).append("\" ");
            builder.append("type=\"").append(type).append("\" ");
            builder.append("jid=\"").append(jid).append("\" >");
            builder.append(body.toXml());
            builder.append("</message>");
            return builder.toString();
        }
    }

//    public static class From {
//        private String secs;
//        private String jid;
//
//        private Body body;
//
//        public String getSecs() {
//            return secs;
//        }
//
//
//        public void setSecs(String secs) {
//            this.secs = secs;
//        }
//
//
//        public String getJid() {
//            return jid;
//        }
//
//
//        public void setJid(String jid) {
//            this.jid = jid;
//        }
//
//
//        public Body getBody() {
//            return body;
//        }
//
//
//        public void setBody(Body body) {
//            this.body = body;
//        }
//
//
//        public String toXml() {
//            StringBuilder builder = new StringBuilder("<from ");
//            builder.append("secs=\"").append(secs).append("\" ");
//            builder.append("jid=\"").append(jid).append("\" >");
//            builder.append(body.toXml());
//            builder.append("</from>");
//            return builder.toString();
//        }
//    }

    public static class Body {
        private String message;

        public Body(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object toXml() {
            StringBuilder builder = new StringBuilder("<body>");
            builder.append(message);
            builder.append("</body>");
            return builder.toString();
        }
    }

    public static class Set {
        private int last;
        private int count;
        private int indexAtt;
        private int first;

        public Set() {
        }

        public int getLast() {
            return last;
        }

        public void setLast(int last) {
            this.last = last;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getIndexAtt() {
            return indexAtt;
        }

        public void setIndexAtt(int indexAtt) {
            this.indexAtt = indexAtt;
        }

        public int getFirst() {
            return first;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public String toXml() {
            StringBuilder builder = new StringBuilder("<set xmlns=\"http://jabber.org/protocol/rsm\">");
            builder.append("<first index=\"").append(indexAtt).append("\">").append(first).append("</first>");
            builder.append("<last>").append(last).append("</last>");
            builder.append("<count>").append(count).append("</count>");
            builder.append("</set>");
            return builder.toString();
        }
    }
}
