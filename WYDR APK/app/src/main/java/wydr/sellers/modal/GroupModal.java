package wydr.sellers.modal;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by surya on 25/8/15.
 */
public class GroupModal {
    public String title, id;
    ArrayList<HashMap<String, String>> itemModals;

  /*  public ArrayList<OrderItemModal> getItemModals() {
        return itemModals;
    }

    public void setItemModals(ArrayList<OrderItemModal> itemModals) {
        this.itemModals = itemModals;
    }*/

    public ArrayList<HashMap<String, String>> getItemModals() {
        return itemModals;
    }

    public void setItemModals(ArrayList<HashMap<String, String>> itemModals) {
        this.itemModals = itemModals;
    }

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
}
