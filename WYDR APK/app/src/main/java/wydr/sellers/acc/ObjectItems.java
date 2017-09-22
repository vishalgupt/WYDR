package wydr.sellers.acc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surya on 15/9/15.
 */
public class ObjectItems {
    public String title; // use getters and setters instead
    public String id;
    public int count,level;
    public boolean is_expanded = false;
    public List<ObjectItems> children; // same as above

    public ObjectItems() {
        children = new ArrayList<ObjectItems>();
    }

}
//}
