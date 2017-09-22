package wydr.sellers.gson;

import java.util.HashMap;

/**
 * Created by surya on 3/2/16.
 */
public class MakeOrder
{
    HashMap<String, Products> products = new HashMap();
    public String user_id, payment_id,book_now;

    public MakeOrder(HashMap<String, Products> products, String user_id, String payment_id, String book_now) {
        this.products = products;
        this.user_id = user_id;
        this.payment_id = payment_id;
        this.book_now = book_now;
    }

    public HashMap<String, Products> getProducts() {
        return products;
    }
    public void setProducts(HashMap<String, Products> products) {
        this.products = products;
    }
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
}
