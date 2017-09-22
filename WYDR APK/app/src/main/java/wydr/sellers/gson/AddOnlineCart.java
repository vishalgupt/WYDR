package wydr.sellers.gson;

import java.util.HashMap;

/**
 * Created by aksha_000 on 3/31/2016.
 */
public class AddOnlineCart
{
    String result_ids,user_id;
    HashMap<String, Products > product_data = new HashMap();

    public AddOnlineCart(String result_ids,HashMap<String, Products> product_data,String user_id)
    {
        this.result_ids = result_ids;
        this.user_id = user_id;
        this.product_data = product_data;
    }
}
