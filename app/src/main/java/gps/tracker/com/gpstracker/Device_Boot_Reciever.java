package gps.tracker.com.gpstracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bhupendramishra on 31/01/17.
 */

public class Device_Boot_Reciever extends BroadcastReceiver {
    Intent alarmIntent;
    private static PendingIntent pendingIntent;
    private static AlarmManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            try {


                SharedPreferences prefs = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
                String channel_id = prefs.getString("broadcasting_sticky", "NA");
                if(!channel_id.equalsIgnoreCase("NA")) {
            /* Setting the alarm here */
                    //alarmIntent.putExtra("channel_id", channellist.get(position).getChannelid());
                    alarmIntent = new Intent(context, Broadcast_Receiver.class);
                    pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    int interval = 40000;

                    manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
                }
            }catch(Exception e)
            {
                Toast.makeText(context,"Error on resuming broadcast", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
