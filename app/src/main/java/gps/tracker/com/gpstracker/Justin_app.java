package gps.tracker.com.gpstracker;


import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by bhupendramishra on 18/10/16.
 */

public class Justin_app extends MultiDexApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        //MultiDex.install(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

   @Override

    protected void attachBaseContext(Context base) {
       super.attachBaseContext(base);

       MultiDex.install(this);

   }

}
