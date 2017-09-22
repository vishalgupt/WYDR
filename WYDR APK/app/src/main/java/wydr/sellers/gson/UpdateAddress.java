package wydr.sellers.gson;

import java.util.HashMap;

/**
 * Created by surya on 25/1/16.
 */
public class UpdateAddress
{
    String s_address, s_address_2, s_city, s_state, s_firstname, s_zipcode,
            s_country_descr, s_phone, user_id, payment_id,s_lastname,status,s_landmark;
    String promotion_ids;
    HashMap<String, Products> products = new HashMap();

    public UpdateAddress(HashMap<String, Products> products, String payment_id, String user_id)
    {
        this.products = products;
        this.payment_id = payment_id;
        this.user_id = user_id;
    }


    public UpdateAddress(String s_firstname, String s_zipcode, String s_address, String s_address_2, String lm, String s_city, String s_state, String s_phone) {

        this.s_firstname = s_firstname;
        this.s_zipcode = s_zipcode;
        this.s_address = s_address;
        this.s_address_2 = s_address_2;
        this.s_landmark = lm;
        this.s_city = s_city;
        this.s_state = s_state;
        this.s_phone = s_phone;


    }



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}
