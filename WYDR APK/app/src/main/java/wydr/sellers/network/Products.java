package wydr.sellers.network;

/**
 * Created by surya on 25/1/16.
 */
public class Products {
    String product_id, amount;


    public Products(String product_id, String amount) {
        this.product_id = product_id;
        this.amount = amount;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


}
