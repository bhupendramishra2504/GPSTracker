package gps.tracker.com.gpstracker;


import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.leakcanary.LeakCanary;


/**
 * Created by bhupendramishra on 18/10/16.
 */

public class Justin_app extends MultiDexApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //MultiDex.install(this);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);


    }

   @Override

    protected void attachBaseContext(Context base) {
       super.attachBaseContext(base);

       MultiDex.install(this);

   }

}
