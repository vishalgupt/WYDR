package wydr.sellers.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Deepesh_pc on 01-02-2016.
 */
public class FavoriteHolder {
    @SerializedName("favourite_data")
    private List<FavoriteQueryModal> query;

    public List<FavoriteQueryModal> getQuery() {
        return query;
    }

    public void setQuery(List<FavoriteQueryModal> query) {
        this.query = query;
    }
}
