package gps.tracker.com.gpstracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by bhupendramishra on 20/01/17.
 */

public class Auto_Broadcast_Reciever extends WakefulBroadcastReceiver implements SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private long lastUpdate;
    private PowerManager.WakeLock cpuWakeLock;


    private double latitude=0.0,longitude=0.0;
    // --Commented out by Inspection (01/12/16, 10:33 PM):private final String log = "ServiceGPS";


    //OrtcFactory factory;
    //public static OrtcClient client;
    private String channel_id="";
    private String gps_time="";

    @Override
    public void onReceive(Context arg0, Intent intent) {
        context=arg0;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        cpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "gps_service");
        cpuWakeLock.setReferenceCounted(false);

        //if (!cpuWakeLock.isHeld()) {
        cpuWakeLock.acquire();
        // }
        //System.out.println("Broadcasted");
        channel_id = intent.getStringExtra("channel_id");
        auto_broadcast();
    }

    private void auto_broadcast()
    {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }


    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

        long actualTime = System.currentTimeMillis();
        Toast.makeText(context,String.valueOf(accelationSquareRoot)+" "+
                SensorManager.GRAVITY_EARTH,Toast.LENGTH_SHORT).show();

        if (accelationSquareRoot >= 2) //it will be executed if you shuffle
        {

            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;//updating lastUpdate for next shuffle
            latitude = 0.0;
            longitude = 0.0;
            offline_update();
            //get_location();
            GPSTracker gps = new GPSTracker(context);
            if (gps.canGetLocation()) {
                //Location location=gps.getLocation();
                //Global.gps_ok=true;
                latitude = 0.0;
                longitude = 0.0;

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                gps_time=gps.getTimeStamp();

                //Date date = new Date(location.getTime());
                // SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                // text = sdf.format(date);
                //Toast.makeText(context,"GPS Time Stamp for location is "+text,Toast.LENGTH_LONG).show();


                add_location_to_server();
                //gps.stopUsingGPS();
            } else {
                Toast.makeText(context, "cannot fetch the gps location in gps tracker", Toast.LENGTH_LONG).show();
                show_notification(context,"JUSTIN BROADCAST","BROADCAST_STOPPED : "+Global.date_time());

                status_update("0");
                //SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                //editor.putString("broadcasting","NA");
                //editor.commit();
            }





    }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void add_location_to_server()
    {
        if(longitude!=0.0 && latitude!=0.0 && !channel_id.equalsIgnoreCase("") && isNetworkAvailable(context)) {
            update_channel_status();

            DatabaseReference loc_long = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("locations").child("latest_location");
            //client.send(channel_id,String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            loc_long.setValue(String.valueOf(longitude+";"+latitude+";"+gps_time));
            Toast.makeText(context,"Location saved to server values are "+String.valueOf(longitude)+","+String.valueOf(latitude),Toast.LENGTH_LONG).show();
            show_notification(context,"JUSTIN BROADCAST","New Location Acquired : "+gps_time);
        }
        else
        {
            Toast.makeText(context,"cannot fetch the gps location in add location to server func",Toast.LENGTH_LONG).show();
            show_notification(context,"JUSTIN BROADCAST","BROADCAST_STOPPED : "+Global.date_time());

            status_update("0");
            SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
            editor.putString("broadcasting","NA");
            editor.apply();
        }

        //if(cpuWakeLock.isHeld()) {
        cpuWakeLock.release();
        // }



    }


    public static void show_notification(Context context,String title,String message)
    {
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_broadcast) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true); // clear notification after click

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification note = mBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;
        mNotificationManager.notify(0, note);
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
