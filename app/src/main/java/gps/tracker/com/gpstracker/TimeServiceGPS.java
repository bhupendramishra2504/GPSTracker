package gps.tracker.com.gpstracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
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

import ibt.ortc.api.Ortc;
import ibt.ortc.extensibility.OnConnected;
import ibt.ortc.extensibility.OnMessage;
import ibt.ortc.extensibility.OnRegistrationId;
import ibt.ortc.extensibility.OrtcClient;
import ibt.ortc.extensibility.OrtcFactory;

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
    OrtcFactory factory;
    OrtcClient client;
    String channel=Global.username;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {


        status=true;
        Log.d("GPS Service","GPS Service Started");
        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
            Log.d("GPS Service","Timer Started");
        }
        if(Global.rr.equalsIgnoreCase("low"))
        {
            NOTIFY_INTERVAL=Global.low;
        }
        else if(Global.rr.equalsIgnoreCase("high"))
        {
            NOTIFY_INTERVAL=Global.high;
        }
        else if(Global.rr.equalsIgnoreCase("medium"))
        {
            NOTIFY_INTERVAL=Global.med;
        }
        else
        {
            NOTIFY_INTERVAL=Global.low;
        }

        final long nt=NOTIFY_INTERVAL;
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL1);


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
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        // Use this method if you have implemented a backend to store your user's GCM registration ids
        //RegistrationIdRemoteStore.getRegistrationIdFromBackend(getApplicationContext(), client);

        OrtcClient.setOnRegistrationId(new OnRegistrationId() {
            @Override
            public void run(String registrationId) {
                Log.i("REG", "GCM Registration ID: " + registrationId);

                // Use this method if you have implemented a backend to store your user's GCM registration ids
                //RegistrationIdRemoteStore.setRegistrationIdToBackend(getApplicationContext(), registrationId);
            }
        });

        client.setGoogleProjectId("joinin-440f7");

        /*client.onConnected = new OnConnected() {
            @Override
            public void run(final OrtcClient sender) {
                // Messaging client connected

                // Now subscribe the channel
                client.subscribe(Global.username, true,
                        new OnMessage() {
                            // This function is the message handler
                            // It will be invoked for each message received in myChannel

                            public void run(OrtcClient sender, String channel, String message) {
                                // Received a message
                                System.out.println(message);
                                //Toast.makeText(Channel_settings.this,"message recieved "+message,Toast.LENGTH_LONG ).show();
                            }
                        });
            }
        };*/




    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "service onDestroy", Toast.LENGTH_LONG).show();
        status=false;
        mHandler.removeCallbacksAndMessages(null);
        mTimer.cancel();
        status_update("0");
        this.stopSelf();

    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    gps = new GPSTracker(TimeServiceGPS.this);
                    if(gps.canGetLocation()){
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        Toast.makeText(TimeServiceGPS.this,"Location saved to server"+String.valueOf(longitude)+","+String.valueOf(latitude),Toast.LENGTH_LONG).show();



                        AsyncGPSWSCall task= new AsyncGPSWSCall();
                        task.execute();
                        //common.showToast("Your Location is - \nLat: " + latitude + "\nLong: " + longitude + "\nDate: " + dba.getDateTime()+ "\nUsername: " + user.get(UserSessionManager.KEY_USERNAME)+ "\nIMEI: " + user.get(UserSessionManager.KEY_IMEI));
                    }
                    else{
                        gps.showSettingsAlert();
                    }

                }

            });
        }

        //Async Class to send Credentials
        private class AsyncGPSWSCall extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                Log.d("GPS Service","Background service Started");
                Log.d("GPS Service","Location collected");


                if(status) {
                    add_location_to_server();
                }

                return null;
              }
            @Override
            protected void onPostExecute(Void result) {
                Log.i(log, "onPostExecute");
                //Toast.makeText(TimeServiceGPS.this,"Location saved to server"+String.valueOf(longitude)+","+String.valueOf(latitude),Toast.LENGTH_LONG).show();

            }

            @Override
            protected void onPreExecute() {
                Log.i(log, "onPreExecute");
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                Log.i(log, "onProgressUpdate");
            }
        }



    }

    public void add_location_to_server()
    {
        if(longitude!=0.0 && latitude!=0.0) {
            //DatabaseReference loc_long = Global.firebase_dbreference.child("CHANNELS").child(Global.username).child("locations").push();
            client.send(Global.channel_id,String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            //loc_long.setValue(String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            //Toast.makeText(Channel_settings.this,"Location saved to server",Toast.LENGTH_LONG).show();
        }
        else
        {
            //Toast.makeText(Channel_settings.this,"Location not valid, check GPS Settings",Toast.LENGTH_LONG).show();
        }
    }

    private void status_update(final String update)
    {
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("followers");
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);

        if(user_ref!=null) {

            user_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        if (child != null) {

                            DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(child.getKey().toString()).child("Subscribers").child(Global.username).child("status");
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

}