package wydr.sellers.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by surya on 29/1/16.
 */
public class ProductModal {
    @SerializedName("product_code")
    public String code;
    @SerializedName("product")
    public String name;
    //@SerializedName("product")
    public String category;
    @SerializedName("list_price")
    public String mrp;
    @SerializedName("base_price")
    public String sp;
    @SerializedName("amount")
    public String qty;
    @SerializedName("min_qty")
    public String minQty;

    @SerializedName("status")
    public String visibility;
    @SerializedName("max_qty")
    public String maxQty;

    @SerializedName("product_id")
    public String id;

    @SerializedName("company_id")
    public String companyId;
    @SerializedName("price_share")
    public String chatAboutProduct;
    @SerializedName("is_favourite")
    public String fav;
    @SerializedName("thumbnails")
    public ThumbImage thumbnails;

    @SerializedName("user_details")
    public List<UserDetail> user;

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscountQuote() {
        return discountQuote;
    }

    public void setDiscountQuote(String discountQuote) {
        this.discountQuote = discountQuote;
    }

    @SerializedName("discount_price")
    public String discountPrice;

    @SerializedName("discount_quote")
    public String discountQuote;

    public List<UserDetail> getUser() {
        return user;
    }

    public void setUser(List<UserDetail> user) {
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getMinQty() {
        return minQty;
    }

    public void setMinQty(String minQty) {
        this.minQty = minQty;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(String maxQty) {
        this.maxQty = maxQty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getChatAboutProduct() {
        return chatAboutProduct;
    }

    public void setChatAboutProduct(String chatAboutProduct) {
        this.chatAboutProduct = chatAboutProduct;
    }

    public String isFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

    public ThumbImage getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(ThumbImage thumbnails) {
        this.thumbnails = thumbnails;
    }

}
