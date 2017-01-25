package gps.tracker.com.gpstracker;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bhupendramishra on 08/10/16.
 * cd /Users/bhupendramishra/Library/Android/sdk/platform-tools
 ./adb shell dumpsys alarm > dump.txt

 */

class Global {
    private static final FirebaseDatabase firebase_database = FirebaseDatabase.getInstance();
    public static final DatabaseReference firebase_dbreference=firebase_database.getReference("JustIn");
    //public static String channel_broadcasting_name="NONE";
    //public static String channel_broadcasting_vnumber="NONE";
    public static String username="NA";
    public static String user_desc_name="NA";
    public static String search_string="NA";
    public static final String separator = System.getProperty("line.separator");
    //public static long high=0,med=0,low=0;
    //public static String rr="LOW";
    public static String channel_id="";
    // --Commented out by Inspection (01/12/16, 10:07 PM):public static boolean authenticated=false;
    public static boolean block;
    // --Commented out by Inspection (01/12/16, 10:07 PM):public static boolean gps_ok=false;
    //public static boolean broadcasting=false;
    //public static int ch_list_pos=-1;
   // public static String channel_id_bd="";
    public static String dob="",city="";
    public static int validate_string(String data)
    {
        if(data.equalsIgnoreCase("")| data.equalsIgnoreCase(null))
            return 0;
        else
            return 1;
    }

    public static String date_time()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String datetime = sdf.format(cal.getTime());
        return datetime;
    }

    public static String generate_channel_id()
    {
        channel_id="";
        Calendar cal = Calendar.getInstance();
        String num=String.valueOf(Math.abs(100000000000L-Long.parseLong(Global.username)));
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmm");
        channel_id = sdf.format(cal.getTime())+num;
        return channel_id;
    }

    public static void set_action_bar_details(AppCompatActivity context,String title,String sub_title)
    {
        ActionBar ab = context.getSupportActionBar();
        assert ab != null;
        ab.setTitle(title);
        ab.setSubtitle(sub_title);
        ab.setDisplayHomeAsUpEnabled(true);

    }

// --Commented out by Inspection START (01/12/16, 10:08 PM):
//    public static long refresh_rate()
//    {
//        long NOTIFY_INTERVAL=60000;
//        if(Global.rr.equalsIgnoreCase("low"))
//        {
//            NOTIFY_INTERVAL=Global.low;
//        }
//        else if(Global.rr.equalsIgnoreCase("high"))
//        {
//            NOTIFY_INTERVAL=Global.high;
//        }
//        else if(Global.rr.equalsIgnoreCase("medium"))
//        {
//            NOTIFY_INTERVAL=Global.med;
//        }
//        else
//        {
//            NOTIFY_INTERVAL=Global.low;
//        }
//        return NOTIFY_INTERVAL;
//    }
// --Commented out by Inspection STOP (01/12/16, 10:08 PM)

   /* public static void read_refresh_rate() {
        DatabaseReference user_ref = Global.firebase_dbreference.child("REFRESH_RATE");
        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        high=Long.parseLong(map.get("high").toString());
                        low=Long.parseLong(map.get("low").toString());
                        med=Long.parseLong(map.get("med").toString());




                }






            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

    }*/

    public static void getUserdetails()
    {
        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(Global.username);
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.getValue().toString() != null) {
                    try {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("id") != null && map.get("name") != null) {
                            Global.user_desc_name = map.get("name").toString();
                            Global.dob = map.get("dob").toString();
                            Global.city = map.get("city").toString();
                            //Global.read_refresh_rate();


                        }
                    } catch (ClassCastException ce) {
                        //Toast.makeText(MyChannels_RV.this, "Filtered few invalid Channels", Toast.LENGTH_LONG).show();
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


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void save_channel_count(Context context,int follower_channel_count)
    {

        SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
        editor.putString("follower_channel_count",String.valueOf(follower_channel_count) );
        editor.apply();

    }

    public static int get_channel_count(Context context)
    {
        int i=0;
        SharedPreferences prefs = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
        String count_string = prefs.getString("follower_channel_count", "NA");
        if(!count_string.equalsIgnoreCase("NA"))
        {
            i=Integer.parseInt(count_string);
        }
        return i;

    }

    public static void show_notification(Context context,String title,String message)
    {
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_broadcast) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message) // message for notification
                .setAutoCancel(true); // clear notification after click

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }



    public static void show_notification_dead(Context context,String title,String message)
    {
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_broadcast) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message) // message for notification
                .setAutoCancel(false)
                .setOngoing(true);// clear notification after click

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
    }



}
