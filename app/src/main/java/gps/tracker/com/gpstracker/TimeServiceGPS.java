package gps.tracker.com.gpstracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by bhupendramishra on 14/10/16.
 */

public class TimeServiceGPS extends Service {
    // constant
    public static long NOTIFY_INTERVAL;
    public static final long NOTIFY_INTERVAL1=10000;
    // 15 mins
    GPSTracker gps;

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private double latitude=0.0,longitude=0.0;
    private final String log = "ServiceGPS";
    public static FirebaseDatabase firebase_database1;
    public static DatabaseReference firebase_dbreference1;
    boolean status=false;
    public String channel_id="",gps_speed="0 km/h",gps_time="";
    Context context;
    String rr_interval="10 secs";

    //String channel=Global.username;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {



        context=this;
        /*final long nt=NOTIFY_INTERVAL;
        latitude = 0.0;
        longitude = 0.0;
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
            Log.d("GPS Service","Timer Started");
        }

        //SharedPreferences prefs = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
        //channel_id = prefs.getString("broadcasting_sticky", "NA");
        //Intent i=getApplicationContext().getIntent();

        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, 10000);*/




    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        //channel_id = intent.getStringExtra("channel_id");
        //rr_interval=intent.getStringExtra("refresh_rate");

        SharedPreferences prefs = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
        channel_id = prefs.getString("broadcasting", "NA");
        rr_interval=prefs.getString("refresh_rate","10 secs");
        if(rr_interval.equalsIgnoreCase("10 secs"))
        {
            NOTIFY_INTERVAL=10000;
        }
        else if(rr_interval.equalsIgnoreCase("30 secs"))
        {
            NOTIFY_INTERVAL=30000;
        }
        else if(rr_interval.equalsIgnoreCase("1 min"))
        {
            NOTIFY_INTERVAL=10000*60;
        }
        else if(rr_interval.equalsIgnoreCase("15 mins"))
        {
            NOTIFY_INTERVAL=10000*60*15;
        }
        else if(rr_interval.equalsIgnoreCase("30 mins"))
        {
            NOTIFY_INTERVAL=10000*60*30;
        }

        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
            Log.d("GPS Service","Timer Started");
        }

        //SharedPreferences prefs = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
        //channel_id = prefs.getString("broadcasting_sticky", "NA");
        //Intent i=getApplicationContext().getIntent();

        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "service onDestroy", Toast.LENGTH_LONG).show();
        status=false;
        mHandler.removeCallbacksAndMessages(null);
        mTimer.cancel();
        //status_update("0");
        this.stopSelf();

    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    try
                    {
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
                        gps_time=gps.getTimeStamp();
                        gps_speed=gps.getSpeed();

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
                }catch(Exception e)
                {
                    Toast.makeText(context,"Fatal error at Alarm Manager : "+e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }

            });
        }






    }

    private void add_location_to_server()
    {
        if(longitude!=0.0 && latitude!=0.0 && !channel_id.equalsIgnoreCase("") && isNetworkAvailable(context)) {
            update_channel_status();

            DatabaseReference loc_long = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("locations").child("latest_location");
            //client.send(channel_id,String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            loc_long.setValue(String.valueOf(longitude+";"+latitude+";"+gps_time+";"+gps_speed));
            Toast.makeText(context,"Location saved to server values are "+String.valueOf(longitude)+","+String.valueOf(latitude),Toast.LENGTH_LONG).show();
            //show_notification(context,"JUSTIN BROADCAST","New Location Acquired : "+gps_time);
        }
        else
        {
            Toast.makeText(context,"cannot fetch the gps location in add location to server func",Toast.LENGTH_LONG).show();
            //show_notification(context,"JUSTIN BROADCAST","BROADCAST_STOPPED : "+Global.date_time());

            status_update("0");
            SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
            editor.putString("broadcasting","NA");
            editor.apply();
        }

        //if(cpuWakeLock.isHeld()) {
        //cpuWakeLock.release();
        // }



    }

    private void status_update(final String update)
    {
        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
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




    private void offline_update()
    {

        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
        ref2.onDisconnect().setValue("0");
        //offline_network_issue();
        ref2.keepSynced(true);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);
    }


    private void update_channel_status()
    {
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
        user_ref.keepSynced(true);
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //results.clear();
                if(dataSnapshot!=null)
                {
                    if(dataSnapshot.getValue().toString().equalsIgnoreCase("0"))
                    {
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