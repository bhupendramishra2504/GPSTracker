package gps.tracker.com.gpstracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import ibt.ortc.api.Ortc;
import ibt.ortc.extensibility.OrtcClient;
import ibt.ortc.extensibility.OrtcFactory;

/**
 * Created by bhupendramishra on 09/12/16.
 */

public class Broadcast_Receiver extends BroadcastReceiver {

    Context context;
    private static final long  NOTIFY_INTERVAL=20000;
    //Global.refresh_rate();
    //public static final long NOTIFY_INTERVAL1=10000;
    // 15 mins
    private GPSTracker gps;

    // run on another Thread to avoid crash
    private final Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private double latitude=0.0,longitude=0.0;
    // --Commented out by Inspection (01/12/16, 10:33 PM):private final String log = "ServiceGPS";

    private boolean status=false;
    //OrtcFactory factory;
    public static OrtcClient client;
    private String channel_id="";

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
        Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();

        channel_id=arg1.getStringExtra("channel_id");
        context=arg0;
        if(client==null) {

            try {
                Ortc ortc = new Ortc();
                OrtcFactory factory;
                factory = ortc.loadOrtcFactory("IbtRealtimeSJ");
                client = factory.createClient();
                client.setClusterUrl("http://ortc-developers.realtime.co/server/2.1");
                client.connect("Cmo9Y1", "testToken");
                client.setApplicationContext(context);
            } catch (InstantiationException e) {
                //e.printStackTrace();
            } catch (IllegalAccessException e) {
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
            }
        }

        client.setGoogleProjectId("joinin-440f7");

        offline_update();
        gps = new GPSTracker(context);
        if(gps.canGetLocation()) {
            //Global.gps_ok=true;
            latitude=0.0;
            longitude=0.0;
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            add_location_to_server();
        }
        else
        {
            Toast.makeText(context,"cannot fetch the gps location in gps tracker",Toast.LENGTH_LONG).show();
        }


    }

    private void add_location_to_server()
    {
        if(longitude!=0.0 && latitude!=0.0 && !channel_id.equalsIgnoreCase("")) {
            DatabaseReference loc_long = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("locations").child("latest_location");
            client.send(channel_id,String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            loc_long.setValue(String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            Toast.makeText(context,"Location saved to server values are "+String.valueOf(longitude)+","+String.valueOf(latitude),Toast.LENGTH_LONG).show();
        }
        {
            Toast.makeText(context,"cannot fetch the gps location in add location to server func",Toast.LENGTH_LONG).show();
        }

    }




    private void status_update(final String update)
    {
        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
        ref2.setValue(update);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);
    }


    private void status_update_kill()
    {
        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
        ref2.setValue("0");
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);
    }




    private void offline_update()
    {

        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
        ref2.onDisconnect().setValue("0");
        //offline_network_issue();
        ref2.keepSynced(true);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);
    }









}
