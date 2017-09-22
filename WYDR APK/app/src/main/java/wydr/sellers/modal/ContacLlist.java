package wydr.sellers.modal;

import java.io.Serializable;

/**
 * Created by Deepesh_pc on 07-07-2015.
 */
public class ContacLlist implements Serializable {
    public String url;
    private String name, number, status, compname, network_userid, disp_name, user_id, comp_id, email, location;
    private Integer id;
    private boolean isSelected;
    public ContacLlist() {
        this.name = "";
        this.number = "";
        this.id = 0;
        this.status = "";
        this.compname = "";
        this.network_userid = "";
        this.disp_name = "";
        this.user_id = "";
        this.comp_id = "";

        this.email = "";


    }
    public ContacLlist(int id, String name, String _phone_number, String _status, boolean isSelected, String compname, String network_userid, String disp_name, String user_def_id, String comp_id, String email) {
        this.name = name;
        this.number = _phone_number;
        this.id = id;
        this.status = _status;
        this.isSelected = isSelected;
        this.compname = compname;
        this.network_userid = network_userid;
        this.disp_name = disp_name;
        this.user_id = user_def_id;
        this.comp_id = comp_id;

        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_def_id) {
        this.user_id = user_def_id;
    }

    public String getComp_id() {
        return comp_id;
    }

    public void setComp_id(String comp_id) {
        this.comp_id = comp_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisp_name() {
        return disp_name;
    }

    public void setDisp_name(String disp_name) {
        this.disp_name = disp_name;
    }

    public String getNetwork_userid() {
        return network_userid;
    }

    public void setNetwork_userid(String network_userid) {
        this.network_userid = network_userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String name) {
        this.status = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getCompname() {
        return compname;
    }

    public void setCompname(String name) {
        this.compname = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
