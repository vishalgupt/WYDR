package wydr.sellers.modal;

/**
 * Created by Deepesh_pc on 25-08-2015.
 */
public class ProdCatModal {
    public String id, name;
    public Boolean has_child;
    public Boolean isselected;

    public Boolean getIsselected() {
        return isselected = false;
    }

    public void setIsselected(Boolean isselected) {
        this.isselected = isselected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getHas_child() {
        return has_child;
    }

    public void setHas_child(Boolean has_child) {
        this.has_child = has_child;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
