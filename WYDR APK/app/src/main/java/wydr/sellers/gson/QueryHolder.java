package wydr.sellers.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by surya on 29/1/16.
 */
public class QueryHolder {
    @SerializedName("query_data")
    private List<QueryModal> query;

    public List<QueryModal> getQuery() {
        return query;
    }

    public void setQuery(List<QueryModal> query) {
        this.query = query;
    }
}
