package wydr.sellers.modal;

/**
 * Created by surya on 5/8/15.
 */
public class AttachModal {

    public String id, name, code, price, sellingPrice, imgUrl, moq;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMoq() {
        return moq;
    }

    public void setMoq(String moq) {
        this.moq = moq;
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
        return String.format("%.2f", Double.parseDouble(price));
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellingPrice() {
        return String.format("%.2f", Double.parseDouble(sellingPrice));

    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
