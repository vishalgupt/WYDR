package wydr.sellers.gson;

import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by surya on 25/1/16.
 */
public class Products {
    String product_id, amount, var_name ;
    JsonObject product_options;


    public Products(String product_id, String amount,JsonObject var,String var_name) {
        this.product_id = product_id;
        this.amount = amount;
        this.product_options = var;
        this.var_name = var_name;
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
