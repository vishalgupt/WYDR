package wydr.sellers.network;

import java.util.HashMap;

/**
 * Created by surya on 25/1/16.
 */
public class CreateOrder
{
    String b_address, b_address_2, b_city, b_state, b_firstname, b_zipcode, b_country_descr, b_phone, s_address, s_address_2, s_city, s_state, s_firstname, s_zipcode,
            s_country_descr, s_phone, user_id, payment_id;
    HashMap<String, Products> products = new HashMap();

    public CreateOrder(HashMap<String, Products> products, String payment_id, String user_id)
    {
        this.products = products;
        this.payment_id = payment_id;
        this.user_id = user_id;
    }

    public CreateOrder(String b_address, String b_address_2, String b_city, String b_state, String b_firstname, String b_zipcode, String b_country_descr, String b_phone, String s_address, String s_address_2, String s_city, String s_state, String s_firstname, String s_zipcode, String s_country_descr, String s_phone, String user_id, String payment_id, HashMap<String, Products> products) {
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
        this.s_firstname = s_firstname;
        this.s_zipcode = s_zipcode;
        this.s_country_descr = s_country_descr;
        this.s_phone = s_phone;
        this.user_id = user_id;
        this.payment_id = payment_id;
        this.products = products;
    }

    public String getB_address() {
        return b_address;
    }

    public void setB_address(String b_address) {
        this.b_address = b_address;
    }



    public String getB_address_2() {
        return b_address_2;
    }

    public void setB_address_2(String b_address_2) {
        this.b_address_2 = b_address_2;
    }

    public String getB_city() {
        return b_city;
    }

    public void setB_city(String b_city) {
        this.b_city = b_city;
    }

    public String getB_state() {
        return b_state;
    }

    public void setB_state(String b_state) {
        this.b_state = b_state;
    }

    public String getB_firstname() {
        return b_firstname;
    }

    public void setB_firstname(String b_firstname) {
        this.b_firstname = b_firstname;
    }

    public String getB_zipcode() {
        return b_zipcode;
    }

    public void setB_zipcode(String b_zipcode) {
        this.b_zipcode = b_zipcode;
    }

    public String getB_country_descr() {
        return b_country_descr;
    }

    public void setB_country_descr(String b_country_descr) {
        this.b_country_descr = b_country_descr;
    }

    public String getB_phone() {
        return b_phone;
    }

    public void setB_phone(String b_phone) {
        this.b_phone = b_phone;
    }

    public String getS_address() {
        return s_address;
    }

    public void setS_address(String s_address) {
        this.s_address = s_address;
    }

    public String getS_address_2() {
        return s_address_2;
    }

    public void setS_address_2(String s_address_2) {
        this.s_address_2 = s_address_2;
    }

    public String getS_city() {
        return s_city;
    }

    public void setS_city(String s_city) {
        this.s_city = s_city;
    }

    public String getS_state() {
        return s_state;
    }

    public void setS_state(String s_state) {
        this.s_state = s_state;
    }

    public String getS_firstname() {
        return s_firstname;
    }

    public void setS_firstname(String s_firstname) {
        this.s_firstname = s_firstname;
    }

    public String getS_zipcode() {
        return s_zipcode;
    }

    public void setS_zipcode(String s_zipcode) {
        this.s_zipcode = s_zipcode;
    }

    public String getS_country_descr() {
        return s_country_descr;
    }

    public void setS_country_descr(String s_country_descr) {
        this.s_country_descr = s_country_descr;
    }

    public String getS_phone() {
        return s_phone;
    }

    public void setS_phone(String s_phone) {
        this.s_phone = s_phone;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }
}
