package wydr.sellers.network;

/**
 * Created by surya on 25/1/16.
 */
public class OrderStatus
{
    public String status;
    public  String payment_id;
    public Transactions payment_info;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public Transactions getPayment_info() {
        return payment_info;
    }

    public void setPayment_info(Transactions payment_info) {
        this.payment_info = payment_info;
    }

    public OrderStatus(String status, String payment_id, Transactions payment_info) {
        this.status = status;
        this.payment_id = payment_id;
        this.payment_info = payment_info;
    }

////    JSONObject jsonObject = new JSONObject();
////    jsonObject.put("status", "P");
////
////    JSONObject object = new JSONObject();
////    object.put("transaction_id", razorpayPaymentID);
////    jsonObject.put("payment_info", object);
////    jsonObject.put("payment_id", AppUtil.payment_id);
//
//    public String getStatus() {
//        return status;
//    }
//
//    public OrderStatus(String status) {
//        this.status = status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//
//    }
}
