package wydr.sellers.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Deepesh_pc on 15-02-2016.
 */
public class FavProdHolder {

    @SerializedName("favourite_data")
    private List<FavProdModal> products;

    public List<FavProdModal> getQuery() {
        return products;
    }

    public void setQuery(List<FavProdModal> products) {
        this.products = products;
    }
}
