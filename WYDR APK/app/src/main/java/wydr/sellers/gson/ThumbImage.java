package wydr.sellers.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by surya on 27/1/16.
 */
public class ThumbImage {
    @SerializedName("image_path")
    public String url = "";

    public String getUrl() {
        return "" + url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ThumbImage(String url) {
        this.url = url;
    }
}
