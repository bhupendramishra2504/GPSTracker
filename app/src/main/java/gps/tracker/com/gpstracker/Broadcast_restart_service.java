package gps.tracker.com.gpstracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by bhupendramishra on 29/01/17.
 */

public class Broadcast_restart_service extends Service {

    private static PendingIntent pendingIntent;
    private static Intent alarmIntent,alarmIntent1;
    private static AlarmManager manager,manager2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent pIntent, int flags, int startId) {
        try {

            SharedPreferences prefs = this.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
            String channel_broadcasting = prefs.getString("broadcasting", "NA");
            if (channel_broadcasting.equalsIgnoreCase("NA")) {
                // TODO Auto-generated method stub
                alarmIntent = new Intent(this, Broadcast_Receiver.class);
                alarmIntent.putExtra("channel_id", channel_broadcasting);
                pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                int interval = 40000;

                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            }
        }catch(Exception e)
        {
            Toast.makeText(this,"Error in the service : "+e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        return super.onStartCommand(pIntent, flags, startId);
    }
}
