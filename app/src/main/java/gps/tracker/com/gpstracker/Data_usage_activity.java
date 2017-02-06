package gps.tracker.com.gpstracker;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class Data_usage_activity extends AppCompatActivity {
    TextView data_usage_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_usage_activity);
        Global.set_action_bar_details(Data_usage_activity.this,"Data Usage","");
        data_usage_txt=(TextView)findViewById(R.id.usage);
        data_usage_txt.setText("Loading Data Usage Stats..."+System.getProperty("line.separator")+"Please Wait...");
        network_stats();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if(id==android.R.id.home) {
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);



    }

    public void network_stats()
    {

        final Handler h = new Handler();
        final int delay = 1000; //milliseconds


        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                long rxBytes = (TrafficStats.getUidRxBytes(Global.Uid)- Global.mStartRX)/1024;
                long txBytes = (TrafficStats.getUidTxBytes(Global.Uid)- Global.mStartTX)/1024;
                data_usage_txt.setText("Recieved Bytes : "+String.valueOf(rxBytes)+System.getProperty("line.separator")+"Transmitted Bytes : "+String.valueOf(txBytes));
                h.postDelayed(this, delay);


            }
        }, delay);


    }

}
