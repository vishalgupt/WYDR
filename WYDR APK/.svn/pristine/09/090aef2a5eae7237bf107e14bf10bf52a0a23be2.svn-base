package wydr.sellers.gson;

import com.google.gson.annotations.SerializedName;

import wydr.sellers.activities.AppUtil;

/**
 * Created by Deepesh_pc on 03-02-2016.
 */
public class SellerModal {

    @SerializedName("firstname")
    public String firstName;
    @SerializedName("lastname")
    public String lastName;
    @SerializedName("company_name")
    public String company;
    @SerializedName("company_id")
    public String companyId;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("user_login")
    public String networkId;

    @SerializedName("is_openfire")
    public String isOpenFire;
    @SerializedName("state")
    public String state;
    @SerializedName("city")
    public String city;

    @SerializedName("main_pair")
    public ImageUrl imageUrl;

    public ImageUrl getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(ImageUrl imageUrl) {
        this.imageUrl = imageUrl;
    }

    public class Icon {

        @SerializedName("image_path")
        public String imageUrl;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public class ImageUrl {
        @SerializedName("icon")
        public Icon icon;

        public Icon getIcon() {
            return icon;
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNetworkId() {
        return networkId + "@" + AppUtil.SERVER_NAME;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }


    public String getIsOpenFire() {
        return isOpenFire;
    }

    public void setIsOpenFire(String isOpenFire) {
        this.isOpenFire = isOpenFire;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {

        return city;

    }

    public void setCity(String city) {
        this.city = city;
    }


}
