package wydr.sellers.gson;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Deepesh_pc on 09-02-2016.
 */
public class OrdersHolder {
    @SerializedName("orders")
    private JsonElement orders;

    public JsonElement getQuery() {
        return orders;
    }

    public void setQuery(JsonElement query) {
        this.orders = query;
    }
    @SerializedName("params")
    private JsonElement params;

    public JsonElement getParams() {
        return params;
    }

    public void setParams(JsonElement params) {
        this.params = params;
    }
}