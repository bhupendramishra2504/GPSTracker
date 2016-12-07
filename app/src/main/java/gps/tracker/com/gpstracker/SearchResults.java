package gps.tracker.com.gpstracker;

import android.graphics.Bitmap;

/**
 * Created by bhupendramishra on 10/10/16.
 */

class SearchResults {
    private String name = "";
    private String vnumber = "";
    private String phone = "";
    private String vname="";
    private String channel_id="";
    private Bitmap image;

    public void setName(String name) {
        this.name = name;
    }

    public void setVname(String vname){this.vname=vname;}

    public void setChannelid(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannelid(){return channel_id;}

    public String getName() {
        return name;
    }

    public String getVname(){return vname;}

    public void setVnumber(String vnumber) {
        this.vnumber = vnumber;
    }

    public String getVnumber() {
        return vnumber;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public Bitmap getImage(){return image;}

    public void setImage(Bitmap image){this.image=image;}


}
