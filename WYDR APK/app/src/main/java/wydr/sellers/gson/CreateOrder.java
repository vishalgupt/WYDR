package wydr.sellers.gson;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * Created by surya on 25/1/16.
 */
public class CreateOrder
{
    String b_address, b_address_2, b_city, b_state, b_firstname, b_lastname, b_zipcode, b_country_descr, b_phone,
            s_address, s_address_2, s_city, s_state, s_firstname, s_zipcode,
            s_country_descr, s_phone, user_id, payment_id,s_lastname,status,shipping_cost;
    String lattitude,longitude,modal_name,os_name,brand_name,android_appversion_name,imei, current_user_id;
    HashMap<String, Products> products = new HashMap();
    JSONArray promotions_cods = new JSONArray();
    String use_discount;
    String points_in_use;



    public CreateOrder(HashMap<String, Products> products, String payment_id, String user_id)
    {
        this.products = products;
        this.payment_id = payment_id;
        this.user_id = user_id;
    }

    public CreateOrder(String lattitude,
                       String longitude, String modal_name, String os_name, String brand_name, String appversion,String imei,String user)
    {
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.modal_name = modal_name;
        this.os_name = os_name;
        this.brand_name = brand_name;
        this.android_appversion_name = appversion;
        this.imei = imei;
        this.current_user_id = user;
    }

    public CreateOrder(String b_address, String b_address_2, String b_city, String b_state, String b_firstname,
                       String b_zipcode, String b_country_descr, String b_phone, String s_address, String s_address_2,
                       String s_city, String s_state, String s_firstname, String s_zipcode, String s_country_descr,
                       String s_phone, String user_id, String payment_id, String status,
                       String shipping_cost,  String lattitude,
                       String longitude, String modal_name, String os_name, String brand_name, String android_appversion_name
                       ,String imei,JSONArray promotions_cods,String use_discount, String points_in_use)
    {
        this.b_address = b_address;
        this.b_address_2 = b_address_2;
        this.b_city = b_city;
        this.b_state = b_state;
        this.b_firstname = b_firstname;
        this.b_zipcode = b_zipcode;
        this.b_country_descr = b_country_descr;
        this.b_phone = b_phone;
        this.s_address = s_address;
        this.s_address_2 = s_address_2;
        this.s_city = s_city;
        this.s_state = s_state;
        //this.promotion_id= promo;
        this.s_firstname = s_firstname;
        this.s_zipcode = s_zipcode;
        this.s_country_descr = s_country_descr;
        this.s_phone = s_phone;
        this.user_id = user_id;
        this.payment_id = payment_id;
        this.products = products;
        this.b_lastname= "";
        this.s_lastname= "";
        this.status = status;
        //this.promocode = promocode;
        this.shipping_cost = shipping_cost;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.modal_name = modal_name;
        this.os_name = os_name;
        this.brand_name = brand_name;
        this.android_appversion_name = android_appversion_name;
        this.imei = imei;
        this.promotions_cods = promotions_cods;
        this.use_discount = use_discount;
        this.points_in_use = points_in_use;
    }



    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }


}
