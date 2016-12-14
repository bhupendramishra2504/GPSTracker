package gps.tracker.com.gpstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

//import ibt.ortc.api.Ortc;
//import ibt.ortc.extensibility.OrtcClient;
//import ibt.ortc.extensibility.OrtcFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bhupendramishra on 09/12/16.
 */

public class Broadcast_Receiver extends BroadcastReceiver {

    private Context context;



    private double latitude=0.0,longitude=0.0;
    // --Commented out by Inspection (01/12/16, 10:33 PM):private final String log = "ServiceGPS";


    //OrtcFactory factory;
    //public static OrtcClient client;
    private String channel_id="";

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
        Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();

        channel_id=arg1.getStringExtra("channel_id");
        context=arg0;
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

        //offline_update();
        GPSTracker gps = new GPSTracker(context);
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
            status_update("0");
            //SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
            //editor.putString("broadcasting","NA");
            //editor.commit();
        }


    }

    private void add_location_to_server()
    {
        if(longitude!=0.0 && latitude!=0.0 && !channel_id.equalsIgnoreCase("") && isNetworkAvailable(context)) {
            update_channel_status();
            DatabaseReference loc_long = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("locations").child("latest_location");
            //client.send(channel_id,String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            loc_long.setValue(String.valueOf(longitude+";"+latitude+";"+Global.date_time()));
            Toast.makeText(context,"Location saved to server values are "+String.valueOf(longitude)+","+String.valueOf(latitude),Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(context,"cannot fetch the gps location in add location to server func",Toast.LENGTH_LONG).show();
            status_update("0");
            SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
            editor.putString("broadcasting","NA");
            editor.apply();
        }

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
