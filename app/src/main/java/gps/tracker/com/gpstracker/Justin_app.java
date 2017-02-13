package gps.tracker.com.gpstracker;


import android.app.AlertDialog;
import android.app.Application;
import android.net.TrafficStats;
//import android.support.multidex.MultiDex;
//import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;
//import com.squareup.leakcanary.LeakCanary;


/**
 * Created by bhupendramishra on 18/10/16.
 */

public class Justin_app extends Application{
//extends MultiDexApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Global.setAppUid(this);

        Global.mStartRX = TrafficStats.getUidRxBytes(Global.Uid);
        Global.mStartTX = TrafficStats.getUidTxBytes(Global.Uid);
        if (Global.mStartRX == TrafficStats.UNSUPPORTED || Global.mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Data Usage Disabled");
            alert.setMessage("Data Usage is Disabled.... Kindly enable it from settings");
            alert.show();
        }
        //MultiDex.install(this);
        //if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
           // return;
        //}
        //LeakCanary.install(this);


    }

  /* @Override

    protected void attachBaseContext(Context base) {
       super.attachBaseContext(base);

       MultiDex.install(this);

   }*/

}
