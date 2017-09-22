package wydr.sellers.modal;

/**
 * Created by Deepesh_pc on 28-08-2015.
 */
public class SyncProductModal {
    public String id;
    public String name;
    public String code;
    public Double price, sellingPrice;
    public String imgUrl;
    public String qty;
    public String minqty;
    public String status;
    public String grandparent_id;
    public String parent_id;
    public String child_id;
    public String leaf_id;
    public String desc;
    public String visibility;
    public String timestamp;
    Long updated_timestamp;
    public String url_list;
    public String cat;


    public String getLeaf_id() {
        return leaf_id;
    }

    public void setLeaf_id(String leaf_id) {
        this.leaf_id = leaf_id;
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

    public Long getUpdated_timestamp() {
        return updated_timestamp;
    }

    public void setUpdated_timestamp(Long updated_timestamp) {
        this.updated_timestamp = updated_timestamp;
    }

    public String getUrl_list() {
        return url_list;
    }

    public void setUrl_list(String url_list) {
        this.url_list = url_list;
    }



    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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


}
