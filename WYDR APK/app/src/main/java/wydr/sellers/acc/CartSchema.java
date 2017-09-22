package wydr.sellers.acc;

/**
 * Created by surya on 2/12/15.
 */
public class CartSchema {
    public static final String KEY_CREATED = "created_at";
    public static final String KEY_ROWID = "_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_CODE = "product_code";
    public static final String PRODUCT_PRICE = "price";
    public static final String PRODUCT_DISCOUNT_PRICE = "discount_value";
    public static final String PRODUCT_QTY = "qty";
    public static final String PRODUCT_URL = "url";
    public static final String PRODUCT_MOQ = "moq";
    public static final String PRODUCT_SHIPPING = "shipping";
    public static final String PRODUCT_SHIPPING_CHARGES = "shipping_charges";
    public static final String PRODUCT_INVENTORY = "inventory";
    public static final String PRODUCT_STATUS = "status";
    public static final String DISCOUNT = "discount";
    public static final String PAY_METHOD = "pay_method";
    public static final String PRODUCT_WEIGHT = "weight";
    public static final String PRODUCT_CAT_ID = "catid";
    public static final String PRODUCT_VARIENT = "varients";
    public static final String CART_PROMO = "promo_cart";
    public static final String CART_PROMO_APPLIED = "promo_applied";
    public static final String PROMO_CODE = "promo_code";
    public static final String PROMO_MIN_QTY = "minqty";
    public static final String PROMO_MAX_AMT = "maxamt";
    public static final String VARIENT_STRING = "var_string";
    public static final String TABLE = "Cart";
    //   _id TEXT PRIMARY KEY,  TEXT NOT NULL, product_code TEXT NOT NULL, price TEXT, url TEXT, status INTEGER NOT NULL,created_at);");


//    public static final String SORT_ORDER_DEFAULT =
//            KEY_CREATED + " DESC";
}
