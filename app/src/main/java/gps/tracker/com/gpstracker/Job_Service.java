package gps.tracker.com.gpstracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Created by bhupendramishra on 25/02/17.
 */

public class Job_Service extends JobService {

    Context context;
    private double latitude = 0.0, longitude = 0.0;
    // --Commented out by Inspection (01/12/16, 10:33 PM):private final String log = "ServiceGPS";
    private PowerManager.WakeLock cpuWakeLock;


    //OrtcFactory factory;
    //public static OrtcClient client;
    private String channel_id = "";
    private String gps_time = "";
    private String gps_speed = "0 km/h";

    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            PersistableBundle pb = params.getExtras();
            channel_id = pb.getString("channel_id");
            context = getApplicationContext();

            latitude = 0.0;
            longitude = 0.0;
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            cpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "gps_service");
            cpuWakeLock.setReferenceCounted(false);

            //if (!cpuWakeLock.isHeld()) {
            cpuWakeLock.acquire();
            // }
            //System.out.println("Broadcasted");
            //channel_id = arg1.getStringExtra("channel_id");


            SharedPreferences prefs = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
            channel_id = prefs.getString("broadcasting_sticky", "NA");
       /* if(client==null) {

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

        client.setGoogleProjectId("joinin-440f7");*/

            offline_update();
            //get_location();
            GPSTracker gps = new GPSTracker(context);
            if (gps.canGetLocation() && !channel_id.equalsIgnoreCase("NA")) {
                //Location location=gps.getLocation();
                //Global.gps_ok=true;
                latitude = 0.0;
                longitude = 0.0;

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                gps_time = gps.getTimeStamp();
                gps_speed = gps.getSpeed();

                //Date date = new Date(location.getTime());
                // SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                // text = sdf.format(date);
                //Toast.makeText(context,"GPS Time Stamp for location is "+text,Toast.LENGTH_LONG).show();


                add_location_to_server();
                //gps.stopUsingGPS();
            } else {
                Toast.makeText(context, "cannot fetch the gps location in gps tracker", Toast.LENGTH_LONG).show();
                //show_notification(context,"JUSTIN BROADCAST","BROADCAST_STOPPED : "+Global.date_time());

                status_update("0");
                //SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                //editor.putString("broadcasting","NA");
                //editor.commit();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Fatal error at Alarm Manager : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


        Log.i(TAG, "on start job: " + channel_id);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }


    private void add_location_to_server() {
        if (longitude != 0.0 && latitude != 0.0 && !channel_id.equalsIgnoreCase("") && isNetworkAvailable(context)) {
            update_channel_status();

            DatabaseReference loc_long = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("locations").child("latest_location");
            //client.send(channel_id,String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            loc_long.setValue(String.valueOf(longitude + ";" + latitude + ";" + gps_time + ";" + gps_speed));
            Toast.makeText(context, "Location saved to server values are " + String.valueOf(longitude) + "," + String.valueOf(latitude), Toast.LENGTH_LONG).show();
            //show_notification(context,"JUSTIN BROADCAST","New Location Acquired : "+gps_time);
        } else {
            Toast.makeText(context, "cannot fetch the gps location in add location to server func", Toast.LENGTH_LONG).show();
            //show_notification(context,"JUSTIN BROADCAST","BROADCAST_STOPPED : "+Global.date_time());

            status_update("0");
            SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
            editor.putString("broadcasting", "NA");
            editor.apply();
        }

        //if(cpuWakeLock.isHeld()) {
        cpuWakeLock.release();
        // }


    }


    private void status_update(final String update) {
        DatabaseReference ref2 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
        ref2.setValue(update);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);
    }


// --Commented out by Inspection START (14/12/16, 10:13 PM):
//    private void status_update_kill()
//    {
//        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
//        ref2.setValue("0");
//        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);
//    }
// --Commented out by Inspection STOP (14/12/16, 10:13 PM)

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void offline_update() {

        DatabaseReference ref2 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
        ref2.onDisconnect().setValue("0");
        //offline_network_issue();
        ref2.keepSynced(true);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);
    }


    private void update_channel_status() {
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
        user_ref.keepSynced(true);
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //results.clear();
                if (dataSnapshot != null) {
                    if (dataSnapshot.getValue().toString().equalsIgnoreCase("0")) {
                        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
                        user_ref.setValue("1");

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
