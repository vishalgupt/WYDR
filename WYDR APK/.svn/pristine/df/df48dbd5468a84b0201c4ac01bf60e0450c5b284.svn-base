package wydr.sellers.modal;

import com.google.gson.annotations.SerializedName;


public class GridRow {

    @SerializedName("category")
    public String title;
    @SerializedName("category_id")
    public String id;
    @SerializedName("http_image_path")
    public String url="";
    // , id, url;
    public boolean subCategories = false;
    @SerializedName("product_count")
    int count;

    public GridRow() {
        super();
        // TODO Auto-generated constructor stub
    }

    public GridRow(String url, String title, String id) {
        // TODO Auto-generated constructor stub
        this.url = url;
        this.title = title;
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean hasSubCategories() {
        return subCategories;
    }
}
