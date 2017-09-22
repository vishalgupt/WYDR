package wydr.sellers.gson;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by surya on 29/1/16.
 */
public class ProductList {
    @SerializedName("filters")
    JsonElement filters;
    @SerializedName("products")
    private List<ProductModal> products;

    public JsonElement getFilters() {
        return filters;
    }

    public void setFilters(JsonElement filters) {
        this.filters = filters;
    }

    public List<ProductModal> getProducts() {
        return products;
    }

    public void setProducts(List<ProductModal> products) {
        this.products = products;
    }
}
