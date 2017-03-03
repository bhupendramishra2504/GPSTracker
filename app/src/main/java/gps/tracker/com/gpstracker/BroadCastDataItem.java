package gps.tracker.com.gpstracker;

import android.graphics.Bitmap;

/**
 * Created by Vishal on 2/26/2017.
 */

public class BroadCastDataItem {

    /*      Data Member     */
    private String channel_vehicle_name, channel_name, vehicle_type, vehicle_category, channel_mobile_no, channel_vehicle_no, channel_id;
    private String vehicle_location;
    private Boolean status;

    private int imageid;
    private Bitmap image;

    /*      Constructor     */

    public BroadCastDataItem(String channel_vehicle_name, String channel_name, String vehicle_type, String vehicle_category, String channel_mobile_no, String channel_vehicle_no, Boolean status, String channel_id) {
        this.channel_vehicle_name = channel_vehicle_name;
        this.channel_name = channel_name;
        this.vehicle_type = vehicle_type;
        this.vehicle_category = vehicle_category;
        this.channel_mobile_no = channel_mobile_no;
        this.channel_vehicle_no = channel_vehicle_no;
        this.status = status;
        this.channel_id = channel_id;
    }

    public BroadCastDataItem(String channel_vehicle_name, String channel_name, String vehicle_type, String vehicle_category, String channel_mobile_no, String channel_vehicle_no, Boolean status, String channel_id, String vehicle_location) {
        this.channel_vehicle_name = channel_vehicle_name;
        this.channel_name = channel_name;
        this.vehicle_type = vehicle_type;
        this.vehicle_category = vehicle_category;
        this.channel_mobile_no = channel_mobile_no;
        this.channel_vehicle_no = channel_vehicle_no;
        this.status = status;
        this.channel_id = channel_id;
        this.vehicle_location = vehicle_location;
    }

    public BroadCastDataItem() {
    }

    /*      Getter      */

    public String getVehicle_location() {
        return vehicle_location;
    }

    public String getChannel_vehicle_name() {
        return channel_vehicle_name;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public String getVehicle_category() {
        return vehicle_category;
    }

    public String getChannel_mobile_no() {
        return channel_mobile_no;
    }

    public String getChannel_vehicle_no() {
        return channel_vehicle_no;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getChannel_id() {
        return channel_id;
    }

    /*      Setter      */

    public void setVehicle_location(String vehicle_location) {
        this.vehicle_location = vehicle_location;
    }

    public void setChannel_vehicle_name(String channel_vehicle_name) {
        this.channel_vehicle_name = channel_vehicle_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public void setVehicle_category(String vehicle_category) {
        this.vehicle_category = vehicle_category;
    }

    public void setChannel_mobile_no(String channel_mobile_no) {
        this.channel_mobile_no = channel_mobile_no;
    }

    public void setChannel_vehicle_no(String channel_vehicle_no) {
        this.channel_vehicle_no = channel_vehicle_no;
    }

    public int getImageid(){return imageid;}
    public void setImageid(int imageid){this.imageid=imageid;}

    public Bitmap getImage(){return image;}
    public void setImage(Bitmap image){this.image=image;}

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }
}
