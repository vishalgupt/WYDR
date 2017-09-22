package wydr.sellers.gson;

import com.google.gson.annotations.SerializedName;

import wydr.sellers.acc.MyTextUtils;

/**
 * Created by Deepesh_pc on 02-02-2016.
 */
public class FavoriteQueryModal {
    @SerializedName("query_id")
    public String id;
    @SerializedName("product_name")
    public String title;
    @SerializedName("order_type")
    public String type;
    @SerializedName("product_code")
    public String code;
    @SerializedName("quantity")
    public String quantity;
    @SerializedName("location")
    public String location;
    @SerializedName("needed_date")
    public String needed;
    public String postedBy;
    @SerializedName("vendor_id")
    public String vendorId;
    @SerializedName("company")
    public String company;
    @SerializedName("company_id")
    public String companyId;


    public String fav="query";
    @SerializedName("firstname")
    public String firstName;
    @SerializedName("lastname")
    public String lastName;

    public ThumbImage getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(ThumbImage thumbnails) {
        this.thumbnails = thumbnails;
    }


    public ThumbImage thumbnails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNeeded() {
        return needed;
    }

    public void setNeeded(String needed) {
        this.needed = needed;
    }

    public String getPostedBy() {
        return MyTextUtils.toTitleCase(firstName) + ", " + MyTextUtils.toTitleCase(lastName) + ", " + company.toUpperCase();


    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

       public String isFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

}
