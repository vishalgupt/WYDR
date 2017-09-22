package wydr.sellers.gson;

import java.util.HashMap;

/**
 * Created by surya on 25/1/16.
 */
public class OrderInDeal {
    String user_id, payment_id, deal_ref_id;
    boolean deal_order = true;

    public boolean isDeal_order() {
        return deal_order;
    }

    public void setDeal_order(boolean deal_order) {
        this.deal_order = deal_order;
    }
//    public OrderInDeal(String user_id, String payment_id, String deal_ref_id, HashMap<String, ProductInDeal> products) {
//        this.user_id = user_id;
//        this.payment_id = payment_id;
//        this.deal_ref_id = deal_ref_id;
//        this.products = products;
//    }

    public OrderInDeal(String user_id, String payment_id, String deal_ref_id, boolean deal_order, HashMap<String, ProductInDeal> products) {
        this.user_id = user_id;
        this.payment_id = payment_id;
        this.deal_ref_id = deal_ref_id;
        this.deal_order = deal_order;
        this.products = products;
    }

    HashMap<String, ProductInDeal> products = new HashMap();

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getDeal_ref_id() {
        return deal_ref_id;
    }

    public void setDeal_ref_id(String deal_ref_id) {
        this.deal_ref_id = deal_ref_id;
    }

    public HashMap<String, ProductInDeal> getProducts() {
        return products;
    }

    public void setProducts(HashMap<String, ProductInDeal> products) {
        this.products = products;
    }
}
