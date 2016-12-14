package gps.tracker.com.gpstracker;

/**
 * Created by bhupendramishra on 12/10/16.
 */

class Follower_results {
    private String name = "";
    private String phone = "";
    private int imageid;
    private String status="";


    public void setfName(String name) {
        this.name = name;
    }

    public String getfName() {
        return name;
    }

    public void setfPhone(String phone) {
        this.phone = phone;
    }

    public String getfPhone() {
        return phone;
    }

    public int getImageid(){return imageid;}

    public void setImageid(int imageid){this.imageid=imageid;}


    public void setstatus(String status) {
        this.status = status;
    }

    public String getstatus() {
        return status;
    }

    // --Commented out by Inspection (01/12/16, 10:07 PM):public String setStatus(){return status;}


}
