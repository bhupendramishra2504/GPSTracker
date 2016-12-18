package gps.tracker.com.gpstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by bhupendramishra on 18/12/16.
 */

public class Br_rx extends BroadcastReceiver {
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
        Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();
        //System.out.println("Broadcasted");


    }

}
