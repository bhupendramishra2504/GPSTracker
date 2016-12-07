package gps.tracker.com.gpstracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
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
import java.util.TimerTask;

import ibt.ortc.api.Ortc;
import ibt.ortc.extensibility.OrtcClient;
import ibt.ortc.extensibility.OrtcFactory;

/**
 * Created by bhupendramishra on 14/10/16.
 */

public class TimeServiceGPS extends Service {
    // constant
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
    private OrtcClient client;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //offline_update();
        offline_network_issue();

        //Toast.makeText(TimeServiceGPS.this,"Refesh Rate is : "+String.valueOf(NOTIFY_INTERVAL/1000)+" seconds",Toast.LENGTH_LONG).show();
        status=true;
        //Log.d("GPS Service","GPS Service Started");
        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
            //Log.d("GPS Service","Timer Started");
        }


        //final long nt=NOTIFY_INTERVAL;
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);


        try {
            Ortc ortc = new Ortc();
            OrtcFactory factory;
            factory = ortc.loadOrtcFactory("IbtRealtimeSJ");
            client = factory.createClient();
            client.setClusterUrl("http://ortc-developers.realtime.co/server/2.1");
            client.connect("Cmo9Y1", "testToken");
            client.setApplicationContext(getApplicationContext());
        }
        catch (InstantiationException e) {
            //e.printStackTrace();
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }

        client.setGoogleProjectId("joinin-440f7");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        status_update_kill();
        //Toast.makeText(this, "service onDestroy", Toast.LENGTH_LONG).show();
        status=false;
        mHandler.removeCallbacksAndMessages(null);
        mTimer.cancel();



    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    //offline_update();
                    offline_network_issue();
                    gps = new GPSTracker(TimeServiceGPS.this);
                    if(gps.canGetLocation()){
                        //Global.gps_ok=true;
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        //Toast.makeText(TimeServiceGPS.this,"Location saved to server"+String.valueOf(longitude)+","+String.valueOf(latitude),Toast.LENGTH_LONG).show();

                        testNotification();

                        AsyncGPSWSCall task= new AsyncGPSWSCall();
                        task.execute();
                        //common.showToast("Your Location is - \nLat: " + latitude + "\nLong: " + longitude + "\nDate: " + dba.getDateTime()+ "\nUsername: " + user.get(UserSessionManager.KEY_USERNAME)+ "\nIMEI: " + user.get(UserSessionManager.KEY_IMEI));
                    }
                    else{
                       // gps.showSettingsAlert1();
                       // Global.gps_ok=false;
                        Toast.makeText(TimeServiceGPS.this,"Location service is off,Enable your location services",Toast.LENGTH_LONG).show();
                    }


                }

            });
        }

        //Async Class to send Credentials
        private class AsyncGPSWSCall extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                //Log.d("GPS Service","Background service Started");
                //Log.d("GPS Service","Location collected");


                if(status) {
                    add_location_to_server();
                }

                return null;
              }
            @Override
            protected void onPostExecute(Void result) {
                //Log.i(log, "onPostExecute");
                //Toast.makeText(TimeServiceGPS.this,"Location saved to server"+String.valueOf(longitude)+","+String.valueOf(latitude),Toast.LENGTH_LONG).show();

            }

            @Override
            protected void onPreExecute() {
                //Log.i(log, "onPreExecute");
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                //Log.i(log, "onProgressUpdate");
            }
        }



    }

    private void add_location_to_server()
    {
        if(longitude!=0.0 && latitude!=0.0 && isNetworkAvailable()) {
            DatabaseReference loc_long = Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("locations").child("latest_location");
            client.send(Global.channel_id,String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            loc_long.setValue(String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            //Toast.makeText(Channel_settings.this,"Location saved to server",Toast.LENGTH_LONG).show();
        }

    }




    private void status_update(final String update)
    {

        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(Global.channel_id).child("followers");
        DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(Global.channel_id).child("status");
        ref1.setValue(update);
        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("status");
        ref2.setValue(update);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);

        if(user_ref!=null) {

            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        if (child != null) {

                            DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(child.getKey()).child("Subscribers").child(Global.channel_id).child("status");
                            ref.setValue(update);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }


    private void status_update_kill()
    {

        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(Global.channel_id).child("followers");
        DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(Global.channel_id).child("status");
        ref1.setValue("0");
        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("status");
        ref2.setValue("0");
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);

        if(user_ref!=null) {

            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        if (child != null) {

                            DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(child.getKey()).child("Subscribers").child(Global.channel_id).child("status");
                            ref.setValue("0");

                        }
                    }
                    TimeServiceGPS.this.stopSelf();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }




    private void offline_update()
    {

        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(Global.channel_id).child("followers");
        DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(Global.channel_id).child("status");
        ref1.onDisconnect().setValue("0");
        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("status");
        ref2.onDisconnect().setValue("0");
        //offline_network_issue();

        ref1.keepSynced(true);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);

        if(user_ref!=null) {

            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        if (child != null) {

                                DatabaseReference ref = Global.firebase_dbreference.child("USERS").child(child.getKey()).child("Subscribers").child(Global.channel_id).child("status");
                                ref.onDisconnect().setValue("0");
                                ref.keepSynced(true);


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    private void testNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setOngoing(true);
        builder.setLights(0xff00ff00, 300, 100);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showNotification() {


        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(yourmilliseconds);
        //System.out.println(sdf.format(resultdate));

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(Global.channel_broadcasting_name+" : Broadcasting")
                .setWhen(System.currentTimeMillis())
                .setContentText(String.valueOf(sdf.format(resultdate)))
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setOngoing(true).build();
        startForeground(1956, notification);

    }


    private void offline_network_issue()
    {

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    offline_update();
                    if(Global.broadcasting && Global.channel_id_bd.equalsIgnoreCase(Global.channel_id)) {
                        status_update("1");
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

    }


    public class MyBinder extends Binder {
        public TimeServiceGPS getService() {
            return TimeServiceGPS.this;
        }
    }
}