package gps.tracker.com.gpstracker;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mapzen.tangram.MapController;

public class Data_usage_activity extends AppCompatActivity {
    TextView data_usage_txt;
    Button busage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_usage_activity);
        Global.set_action_bar_details(Data_usage_activity.this,"Data Usage","");
        data_usage_txt=(TextView)findViewById(R.id.usage);
        busage=(Button)findViewById(R.id.busage);
        data_usage_txt.setText("Loading Data Usage Stats..."+System.getProperty("line.separator")+"Please Wait...");
        network_stats();




        busage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageName = "gps.tracker.com.gpstracker";

                try {
                    //Open the specific App Info page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);

                } catch ( ActivityNotFoundException e ) {
                    //e.printStackTrace();

                    //Open the generic Apps page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);

                }
            }
        });

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
            public void run() {
                String unitrx = "Bytes", unittx = "Bytes";

                if (TrafficStats.getUidRxBytes(Global.Uid) == TrafficStats.UNSUPPORTED || TrafficStats.getUidTxBytes(Global.Uid) == TrafficStats.UNSUPPORTED) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(Data_usage_activity.this);
                    alert.setTitle("Data Usage Disabled");
                    alert.setMessage("Data Usage is Disabled.... Kindly enable it from settings");
                    alert.show();
                } else {
                    //do something
                    long rxBytes = (TrafficStats.getUidRxBytes(Global.Uid) - Global.mStartRX);
                    long txBytes = (TrafficStats.getUidTxBytes(Global.Uid) - Global.mStartTX);
                    if (rxBytes > 1024 && rxBytes<1024*1024) {
                        rxBytes = rxBytes / 1024;
                        unitrx = "KB";
                    } else if (rxBytes > 1024 * 1024) {
                        rxBytes = rxBytes / (1024 * 1024);
                        unitrx = "MB";
                    }
                    if (txBytes > 1024 && txBytes<1024*1024) {
                        txBytes = txBytes / 1024;
                        unittx = "KB";
                    } else if (txBytes > 1024 * 1024) {
                        txBytes = txBytes / (1024 * 1024);
                        unittx = "MB";
                    }


                    data_usage_txt.setText("Recieved Bytes : " + String.valueOf(rxBytes) + " " + unitrx + System.getProperty("line.separator") + "Transmitted Bytes : " + String.valueOf(txBytes) + " " + unittx);
                    h.postDelayed(this, delay);


                }
            }
        }, delay);


    }

}
