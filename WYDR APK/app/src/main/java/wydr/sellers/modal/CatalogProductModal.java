package wydr.sellers.modal;

import java.util.List;

/**
 * Created by Deepesh_pc on 28-08-2015.
 */
public class CatalogProductModal {
    public String id, name, code, price, sellingPrice, imgUrl, qty, minqty, cat, moq, userid,
            company_id, status, user_net_id, desc, visibility, server_flag, grandparent_id, parent_id,
            child_id, timestamp, updated_timestamp, url_list, class_name, row_id,chat_price;
    public String[] categories;

    public String getChat_price() {
        return chat_price;
    }

    public void setChat_price(String chat_price) {
        this.chat_price = chat_price;
    }

    public Boolean isfav = false;
    List<String> imageURLs;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getServer_flag() {
        return server_flag;
    }

    public void setServer_flag(String server_flag) {
        this.server_flag = server_flag;
    }

    public String getGrandparent_id() {
        return grandparent_id;
    }

    public void setGrandparent_id(String grandparent_id) {
        this.grandparent_id = grandparent_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUpdated_timestamp() {
        return updated_timestamp;
    }

    public void setUpdated_timestamp(String updated_timestamp) {
        this.updated_timestamp = updated_timestamp;
    }

    public String getUrl_list() {
        return url_list;
    }

    public void setUrl_list(String url_list) {
        this.url_list = url_list;
    }

    public String getRow_id() {
        return row_id;
    }

    public void setRow_id(String row_id) {
        this.row_id = row_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_net_id() {
        return user_net_id;
    }

    public void setUser_net_id(String user_net_id) {
        this.user_net_id = user_net_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public Boolean getIsfav() {
        return isfav;
    }

    public void setIsfav(Boolean isfav) {
        this.isfav = isfav;
    }

    public List<String> getImageURLs() {
        return imageURLs;
    }

    public void setImageURLs(List<String> imageURLs) {
        this.imageURLs = imageURLs;
    }

    public String getMoq() {
        return moq;
    }

    public void setMoq(String moq) {
        this.moq = moq;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }


    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getMinqty() {
        return minqty;
    }

    public void setMinqty(String minqty) {
        this.minqty = minqty;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getImgUrl() {
        return imgUrl;
    }


    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrice() {
        if (!price.equalsIgnoreCase("") && price != null)
            return String.format("%.2f", Double.parseDouble(price));
        else
            return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellingPrice() {
        if (!sellingPrice.equalsIgnoreCase("") && sellingPrice != null)
            return String.format("%.2f", Double.parseDouble(sellingPrice));
        else
            return sellingPrice;


    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
