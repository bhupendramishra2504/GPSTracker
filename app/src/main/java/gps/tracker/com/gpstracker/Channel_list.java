package gps.tracker.com.gpstracker;

import android.graphics.Bitmap;

/**
 * Created by bhupendramishra on 12/10/16.
 */

class Channel_list {
    private String name = "";
    private String vnumber="";
    private String vtype="";
    private String vcategary="";
    private String channel_id="";
    private String vname="";
    private String rr="";
    private int imageid;
    private int aimageid;
    private int visibleimageid;
    private Bitmap image;
    private Boolean state;



    public void setsName(String name) {
        this.name = name;
    }

    public String getsName() {
        return name;
    }


    public String getsActive() {
        String active = "";
        return active;
    }

    public void setChannelid(String channel_id) {
        this.channel_id = channel_id;
    }
    public String getChannelid() {
        return channel_id;
    }


    public String getsPhone() {
        String phone = "";
        return phone;
    }

    public void setsVnumber(String vnumber){this.vnumber=vnumber;}
    public String getsVnumber(){return vnumber;}

    public void setsvtype(String vtype){this.vtype=vtype;}
    public String getsvtype(){return vtype;}

    public void setvname(String vname){this.vname=vname;}
    public String getvname(){return vname;}

    public void setscategary(String vcategary){this.vcategary=vcategary;}
    public String getsvcategary(){return vcategary;}

    public int getImageid(){return imageid;}
    public void setImageid(int imageid){this.imageid=imageid;}

    public int agetImageid(){return aimageid;}
    public void asetImageid(int aimageid){this.aimageid=aimageid;}

    public int getvisibleimageid(){return visibleimageid;}
    public void setvisibleimageid(int visibleimageid){this.visibleimageid=visibleimageid;}

    public boolean getstate(){return state;}
    public void setstate(boolean state){this.state=state;}

    public Bitmap getImage(){return image;}
    public void setImage(Bitmap image){this.image=image;}

    public void setrr(String rr){this.rr=rr;}
    public String getrr(){return rr;}


}
