package wydr.sellers.holder;

import org.json.JSONObject;

import java.util.ArrayList;

import wydr.sellers.modal.OrderCancelBean;

/**
 * Created by Manoj on 5/19/2016.
 */
public class DataHolder {

    private static DataHolder instance;

    private ArrayList<JSONObject> list;
    private ArrayList<OrderCancelBean> orderCancelBeanList;

    private DataHolder() {

    }

    public static DataHolder getInstance() {
        if (instance == null)
            instance = new DataHolder();
        return instance;
    }

    public ArrayList<OrderCancelBean> getOrderCancelBeanList() {
        return orderCancelBeanList;
    }

    public void setOrderCancelBeanList(ArrayList<OrderCancelBean> orderCancelBeanList) {
        this.orderCancelBeanList = orderCancelBeanList;
    }

    public ArrayList<JSONObject> getList() {
        return list;
    }

    public void setList(ArrayList<JSONObject> list) {
        this.list = list;
    }

}
