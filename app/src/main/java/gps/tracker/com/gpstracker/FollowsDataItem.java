package gps.tracker.com.gpstracker;

import android.graphics.Bitmap;

/**
 * Created by Vishal
 *on 2/26/2017.
 */
public class FollowsDataItem {

    /*      Data Member     */
    private String subscriber_vehicle_name, subscriber_name, vehicle_type, vehicle_category, subscriber_mobile_no, subscriber_vehicle_no, time, status, channel_id;
    private String vehicle_location;
    private int imageid;
    private Bitmap image;

    /*      Constructor     */

    public FollowsDataItem(String subscriber_vehicle_name, String subscriber_name, String vehicle_type, String vehicle_category, String subscriber_mobile_no, String subscriber_vehicle_no, String time, String status, String channel_id, String vehicle_location) {
        this.subscriber_vehicle_name = subscriber_vehicle_name;
        this.subscriber_name = subscriber_name;
        this.vehicle_type = vehicle_type;
        this.vehicle_category = vehicle_category;
        this.subscriber_mobile_no = subscriber_mobile_no;
        this.subscriber_vehicle_no = subscriber_vehicle_no;
        this.time = time;
        this.status = status;
        this.channel_id = channel_id;
        this.vehicle_location = vehicle_location;
    }

    public FollowsDataItem(String subscriber_vehicle_name, String subscriber_name, String vehicle_type, String vehicle_category, String subscriber_mobile_no, String subscriber_vehicle_no, String time, String status, String channel_id) {
        this.subscriber_vehicle_name = subscriber_vehicle_name;
        this.subscriber_name = subscriber_name;
        this.vehicle_type = vehicle_type;
        this.vehicle_category = vehicle_category;
        this.subscriber_mobile_no = subscriber_mobile_no;
        this.subscriber_vehicle_no = subscriber_vehicle_no;
        this.time = time;
        this.status = status;
        this.channel_id = channel_id;
    }

    public FollowsDataItem() {

    }

    /*      Getter      */

    public String getVehicle_location() {
        return vehicle_location;
    }

    public String getSubscriber_vehicle_name() {
        return subscriber_vehicle_name;
    }

    public String getSubscriber_name() {
        return subscriber_name;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public String getVehicle_category() {
        return vehicle_category;
    }

    public String getSubscriber_mobile_no() {
        return subscriber_mobile_no;
    }

    public String getSubscriber_vehicle_no() {
        return subscriber_vehicle_no;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getChannel_id() {
        return channel_id;
    }

    /*      Setter      */

    public void setVehicle_location(String vehicle_location) {
        this.vehicle_location = vehicle_location;
    }

    public void setSubscriber_vehicle_name(String subscriber_vehicle_name) {
        this.subscriber_vehicle_name = subscriber_vehicle_name;
    }

    public void setSubscriber_name(String subscriber_name) {
        this.subscriber_name = subscriber_name;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public void setVehicle_category(String vehicle_category) {
        this.vehicle_category = vehicle_category;
    }

    public void setSubscriber_mobile_no(String subscriber_mobile_no) {
        this.subscriber_mobile_no = subscriber_mobile_no;
    }

    public void setSubscriber_vehicle_no(String subscriber_vehicle_no) {
        this.subscriber_vehicle_no = subscriber_vehicle_no;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public Bitmap getImage(){return image;}

    public void setImage(Bitmap image){this.image=image;}

    public int getImageid(){return imageid;}

    public void setImageid(int imageid){this.imageid=imageid;}
}
