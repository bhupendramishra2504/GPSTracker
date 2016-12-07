package gps.tracker.com.gpstracker;

/**
 * Created by bhupendramishra on 31/10/16.
 */

public class Channel_search {

    private String name = "";
    private String phone = "";
    private String vnumber="";
    private String vname="";
    private String channel_id="";
    private String city="";
    private String vcategory="";
    private String vtype="";
    private String follower_setting="";

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }

    public void setChannelid(String channel_id) {
        this.channel_id = channel_id;
    }
    public String getChannelid() {
        return channel_id;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setVnumber(String vnumber){this.vnumber=vnumber;}
    public String getVnumber(){return vnumber;}

    public void setvname(String vname){this.vname=vname;}
    public String getsvname(){return vname;}

    public void setvtype(String vtype){this.vtype=vtype;}
    public String getvtype(){return vtype;}

    public void setvcategory(String vcategory){this.vcategory=vcategory;}
    public String getvcategory(){return vcategory;}

    public void setfollower(String follower_setting){this.follower_setting=follower_setting;}
    public String getfollower(){return follower_setting;}


}
