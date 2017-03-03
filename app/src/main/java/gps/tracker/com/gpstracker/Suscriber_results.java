package gps.tracker.com.gpstracker;

import android.graphics.Bitmap;

/**
 * Created by bhupendramishra on 12/10/16.
 */

class Suscriber_results {
    private String name = "";
    private String phone = "";
    private String vnumber="";
    private String vname="";
    private int imageid;
    private String channel_id="";
    private Bitmap image;
    private String status="";
    private String vtype="";
    private String category="";





    public void setsName(String name) {
        this.name = name;
    }

    public String getsName() {
        return name;
    }

    public void setChannelid(String channel_id) {
        this.channel_id = channel_id;
    }
    public String getChannelid() {
        return channel_id;
    }





    public void setsPhone(String phone) {
        this.phone = phone;
    }

    public String getsPhone() {
        return phone;
    }

    public void setsVnumber(String vnumber){this.vnumber=vnumber;}
    public String getsVnumber(){return vnumber;}

    public void setsvname(String vname){this.vname=vname;}
    public String getsvname(){return vname;}

    public void setstatus(String status){this.status=status;}
    public String getstatus(){return status;}

    public void setvtype(String vtype){this.vtype=vtype;}
    public String getvtype(){return vtype;}

    public void setcategory(String category){this.category=category;}
    public String getcategory(){return category;}

    public Bitmap getImage(){return image;}

    public void setImage(Bitmap image){this.image=image;}

    public int getImageid(){return imageid;}

    public void setImageid(int imageid){this.imageid=imageid;}


}
