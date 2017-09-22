package wydr.sellers.modal;

/**
 * Created by Deepesh_pc on 11-08-2015.
 */
public class GridParent {

    private String title;
    private Integer counter;
    private boolean isSelected;

    public GridParent() {
        this.title = "";
        this.counter = 0;
        this.isSelected = false;


    }

    public GridParent(int id, String title, boolean isSelected) {
        this.counter = id;
        this.title = title;
        this.isSelected = isSelected;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
