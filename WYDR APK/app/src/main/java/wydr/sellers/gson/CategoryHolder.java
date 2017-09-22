package wydr.sellers.gson;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Deepesh_pc on 26-02-2016.
 */
public class CategoryHolder {

    @SerializedName("categories")
    private JsonElement categories;

    public JsonElement getCategories() {
        return categories;
    }

    public void setCategories(JsonElement categories) {
        this.categories = categories;
    }
}