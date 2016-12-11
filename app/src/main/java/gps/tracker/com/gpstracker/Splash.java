package gps.tracker.com.gpstracker;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class Splash extends AppCompatActivity {
    // --Commented out by Inspection (01/12/16, 10:29 PM):private Context context;
    // --Commented out by Inspection (01/12/16, 10:29 PM):boolean isresumed=false;
    private String name;
    private String id;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        System.out.println("splash activity started");

        //context=Splash.this;

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        SharedPreferences prefs = getSharedPreferences("GPSTRACKER", MODE_PRIVATE);

        name = prefs.getString("mobile", "not valid");
        if(!name.equalsIgnoreCase("") && !name.equalsIgnoreCase(null) && !name.equalsIgnoreCase("not valid"))
        {
            authenticate();
        }
        else
        {
            Intent intent = new Intent(Splash.this, Register.class);
            startActivity(intent);
            finish();
        }
        //id =prefs.getString("id", "not valid");
                    //Signup_user signup=new Signup_user();
                   // signup.execute();

        //authenticate();


    }







   /*public static boolean location_network_status()
    {
        boolean enabled=false;
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(gps_enabled && network_enabled)
        {
            enabled=true;
        }
        else
        {
            enabled=false;
        }

        return enabled;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void location_enable() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }*/


    private void authenticate()
    {

        SharedPreferences prefs = getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
        name = prefs.getString("mobile", "not valid");
        id =prefs.getString("id", "not valid");
//        Toast.makeText(Splash.this,name+id,Toast.LENGTH_LONG).show();
        if(!name.equalsIgnoreCase("not valid")) {
            DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(name);
            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    if (dataSnapshot.getValue().toString() != null) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        String rx_id = map.get("id").toString();
                        String user_desc=map.get("name").toString();


                        Toast.makeText(Splash.this, rx_id, Toast.LENGTH_LONG).show();
                        spinner.setVisibility(View.INVISIBLE);
                        if (id.equals(rx_id)) {
                            Global.username = name;
                            Global.user_desc_name=user_desc;
                            Global.dob=map.get("dob").toString();
                            //Global.gender=map.get("gender").toString();
                            Global.city=map.get("city").toString();
                            //Global.country=map.get("country").toString();
                           // Global.read_refresh_rate();
                            Intent intent = new Intent(Splash.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(Splash.this, Register.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(Splash.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });

        }
        else
        {
            Intent intent = new Intent(Splash.this, Register.class);
            startActivity(intent);
            //finish();
        }

    }




   /* private class Signup_user extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {

            if (isNetworkAvailable() && location_network_status()) {
                authenticate();
                resp="ok";
            }
            else
            {
                resp="notok";
            }

            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            if(resp.equalsIgnoreCase("notok"))
            {
                    Toast.makeText(Splash.this,"Location/Network service is not enabled", Toast.LENGTH_LONG).show();

            }

        }


        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }


        @Override
        protected void onProgressUpdate(String... text) {

            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }*/





  /* private void grant_permission()
    {
        int android_Version = Build.VERSION.SDK_INT;
        if (android_Version > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE}, 101);

        }
        //location_enable();
    }*/

}


