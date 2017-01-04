package gps.tracker.com.gpstracker;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Dashboard extends BaseClass  {

    private ListView lv1;
    private final ArrayList<Suscriber_results> results = new ArrayList<Suscriber_results>();
    private Subscriber_list_view_adapter adapter;
    private String subscriber_invite, subscriber_name;
    private String status = "offline";
    private static final Integer[] images = {R.drawable.green_circle, R.drawable.red_circle};
    //private ProgressBar spinner;
    private String name, id,username;
    // --Commented out by Inspection (01/12/16, 10:05 PM):private String channel_mobile,channel_name,channel_vnumber,channel_vname,channel_invite,channel_bmp,channel_category,channel_vtype;
    private String subscriber;
    private DatabaseReference user_ref;
    private ValueEventListener subscriber_listener,subscriber_detail_listener;
    private DatabaseReference subscriber_detail, authenticate_user_ref;
    Toolbar toolbar;
    FloatingActionButton fab;

    Activity activity;

    Typeface robotoBold;
    ActionBar actionBar;
    Constant constant;
    private int channel_count=0;

    //private Value


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_new);

        adapter = null;
        activity = Dashboard.this;
        constant = new Constant(activity);

        //authenticate();
        if (!grant_permission()) {
            grant_all_permission();
        }

        robotoBold = Typeface.createFromAsset(activity.getAssets(), "fonts/roboto_bold.ttf");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        constant.setUpActionBar("JustIn", actionBar, false);
        //ActionBar ab = getSupportActionBar();
        //assert ab != null;
        //ab.setTitle("JUSTIN");
        /*if(Global.channel_broadcasting_name.equalsIgnoreCase("NONE"))
        {
            ab.setSubtitle("");
        }
        else
        {
            ab.setSubtitle(Global.channel_broadcasting_name+","+Global.channel_broadcasting_vnumber);
        }*/

       // ab.setDisplayHomeAsUpEnabled(false);


        SharedPreferences prefs = getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
        name = prefs.getString("mobile", "not valid");
        username=prefs.getString("username", "NA");
        if (!name.equalsIgnoreCase("") && !name.equalsIgnoreCase(null) && !name.equalsIgnoreCase("not valid")) {
            Global.username = name;
            Dashboard.Subscriber_channel_class scc = new Dashboard.Subscriber_channel_class();
            scc.execute();

        } else {
            Intent intent = new Intent(Dashboard.this, Register.class);
            startActivity(intent);
            finish();
        }
        lv1 = (ListView) findViewById(R.id.subscriber_list);





        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Dashboard.this, Search_activity.class);
                startActivity(intent);
                finish();
            }
        });




        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                if (isNetworkAvailable() | !isNetworkAvailable()) {

                    if (!Global.username.equalsIgnoreCase("") && !Global.username.equalsIgnoreCase(null) && !Global.username.equalsIgnoreCase("not valid") && !username.equalsIgnoreCase(null) && !username.equalsIgnoreCase("NA")) {


                        Object o = lv1.getItemAtPosition(position);
                        final Suscriber_results fullObject = (Suscriber_results) o;
                        // Toast.makeText(Dashboard.this, "You have chosen: " + " " + fullObject.getsName()+Global.separator+fullObject.getsPhone(), Toast.LENGTH_LONG).show();
                        subscriber_invite = fullObject.getChannelid();
                        subscriber = subscriber_invite;
                        String block_unblock = fullObject.getstatus();
                        subscriber_name = fullObject.getsName();
                        if (fullObject.getImageid() == images[0]) {
                            status = "online";
                        } else {
                            status = "offline";
                        }
                        if (block_unblock.equalsIgnoreCase("1")) {


                            if (user_ref != null && subscriber_listener != null) {
                                user_ref.removeEventListener(subscriber_listener);
                            }
                            if (subscriber_detail != null && subscriber_detail_listener != null) {
                                subscriber_detail.removeEventListener(subscriber_detail_listener);
                            }
                            Intent i1 = new Intent(Dashboard.this, Map_activity.class);
                            i1.putExtra("subscriber", subscriber);
                            i1.putExtra("status", status);
                            i1.putExtra("name", subscriber_name);
                            i1.putExtra("vnumber", fullObject.getsVnumber());
                            startActivity(i1);
                            //System.gc();
                            finish();


                        } else {
                            Toast.makeText(Dashboard.this, "This Channel has blocked you, you cannot view its details", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Dashboard.this,"Authetication failed ....",Toast.LENGTH_SHORT).show();
                        authenticate();
                    }


                } else {
                    Toast.makeText(Dashboard.this, "No Active Internet Connection Found", Toast.LENGTH_LONG).show();
                }
            }
        });

    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.ab_main_screen, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if(id==R.id.user_setting) {
            if(grant_permission() && isNetworkAvailable()) {
                Intent i1 = new Intent(Dashboard.this,Settings.class);
                startActivity(i1);
                finish();
            }
            else
            {
                Toast.makeText(Dashboard.this,"Give full permission to app, without it can not work properly , restart your app to get permission request page, if you are not seeing it then go to settings app permissions then give all permissions",Toast.LENGTH_LONG).show();

            }
            return true;
        }
        else if (id==R.id.broadcast) {
            if (grant_permission() && isNetworkAvailable()) {
                Intent i2 = new Intent(Dashboard.this, MyChannels_RV.class);
                startActivity(i2);
                finish();
            }
        }

        else if(id==R.id.action_search)
        {
            if(!Global.username.equalsIgnoreCase("") |!Global.username.equalsIgnoreCase(null) | !Global.username.equalsIgnoreCase("not valid")) {
                authenticate();
            }

            Intent intent = new Intent(Dashboard.this, Search_activity.class);
            startActivity(intent);
            finish();
            return true;

        }
        else if(id==R.id.about)
        {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);
            builder.setTitle("JustIn");
            builder.setMessage("Application Version"+System.getProperty("line.separator")+"Justin-beta-1.8.1"+System.getProperty("line.separator")+"Release Date : 03/01/2017"+System.getProperty("line.separator")+System.getProperty("line.separator")+"Change Logs"+System.getProperty("line.separator")+"bug fixes and perf improvements"+System.getProperty("line.separator")+"Broadcasting performance enhanced"+System.getProperty("line.separator")+"old gps on map issue solved"+System.getProperty("line.separator")+"real time from location is fed into the server in place of current time");
            builder.setPositiveButton("OK", null);
            builder.show();
            return true;
        }




        return super.onOptionsItemSelected(item);



    }







    private void GetSubscriberResults_modified_v2(){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        adapter=null;
        //results.clear();
        FirebaseDatabase firebase_database = FirebaseDatabase.getInstance();
        DatabaseReference firebase_dbreference=firebase_database.getReference("JustIn");
        user_ref = firebase_dbreference.child("USERS").child(Global.username).child("Subscribers");
        user_ref.keepSynced(true);
        subscriber_listener=user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                results.clear();
                channel_count=0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){


                       // getSubscriberdetails(child);

                        try {

                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            Suscriber_results sr1 = new Suscriber_results();
                            if (map != null && map.get("name") != null && map.get("vehicle_number") != null && map.get("status") != null && map.get("vname") != null && map.get("mobile") != null) {
                                sr1.setsName("N: " + map.get("name").toString());
                                sr1.setChannelid(child.getKey());
                                sr1.setsPhone("M: " + map.get("mobile").toString());
                                sr1.setsVnumber("VNo: " + map.get("vehicle_number").toString());
                                sr1.setsvname("VN: " + map.get("vname").toString());
                                if (map.get("unblock") != null) {
                                    sr1.setstatus(map.get("unblock").toString());
                                } else {
                                    sr1.setstatus("1");
                                }

                               // if (act.equalsIgnoreCase("1")) {
                                   // sr1.setImageid(images[0]);
                              /*  } else {
                                    sr1.setImageid(images[1]);
                                }*/

                                if (map.get("image") != null) {
                                    sr1.setImage(download_image_to_firebase1(map.get("image").toString()));
                                } else {
                                    sr1.setImage(download_image_to_firebase1("default"));
                                }
                                if (map.get("vtype") != null) {
                                    sr1.setvtype("T: " + map.get("vtype").toString());
                                } else {
                                    sr1.setvtype("T: " + "NA");
                                }
                                if (map.get("category") != null) {
                                    sr1.setcategory("c: " + map.get("category").toString());
                                } else {
                                    sr1.setcategory("c: " + "NA");
                                }


                                results.add(sr1);
                                channel_count++;
                                map.clear();
                                sr1 = null;
                            }


                        }
                        catch (ClassCastException ce) {
                            Toast.makeText(Dashboard.this, "Filtered few invalid Channels", Toast.LENGTH_LONG).show();
                        }



                    }

                }


                //adapter = new Subscriber_list_view_adapter(getApplicationContext(), results);
                //lv1.setAdapter(adapter);
                //adapter.setContext(getApplicationContext());
                //spinner.setVisibility(View.GONE);

                adapter = new Subscriber_list_view_adapter(getApplicationContext(), results);
                if(adapter!=null) {
                    lv1.setAdapter(adapter);
                    Global.save_channel_count(activity,channel_count);
                    Toast.makeText(activity,"Channel Count is"+String.valueOf(Global.get_channel_count(activity)),Toast.LENGTH_LONG).show();
                    //adapter.setContext(getApplicationContext());
                }

                    getStatus();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Dashboard.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }



    private void getStatus()
    {

        for (int i = 0; i < lv1.getCount(); i++) {
            Object o = lv1.getItemAtPosition(i);
            final Suscriber_results fullObject = (Suscriber_results) o;
            String Channel_id=fullObject.getChannelid();
            subscriber_detail = Global.firebase_dbreference.child("CHANNELS").child(Channel_id).child("status");
            subscriber_detail.keepSynced(true);
            subscriber_detail_listener= subscriber_detail.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String act;
                    //results.clear();
                    if(dataSnapshot!=null) {
                        if (dataSnapshot.getValue() != null) {
                            act = dataSnapshot.getValue().toString();
                            if (act.equalsIgnoreCase("1")) {
                                fullObject.setImageid(images[0]);
                            } else {
                                fullObject.setImageid(images[1]);
                            }


                        }
                    }

                    else
                    {
                        Toast.makeText(Dashboard.this,"Invalid Subscriber Details",Toast.LENGTH_LONG).show();
                    }
                    if(adapter!=null) {
                        adapter.notifyDataSetChanged();
                        lv1.invalidate();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(Dashboard.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });


        }

    }

    @Override
    public void onPause()
    {
        super.onPause();

    }





    private Bitmap download_image_to_firebase1(String data_string)
    {
        Bitmap bitmap;


        if(data_string!=null && !data_string.equalsIgnoreCase("default"))
        {
            byte[] data = Base64.decode(data_string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        }
        else
        {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_photo);

        }

        return bitmap;
    }


    private class Subscriber_channel_class extends AsyncTask<String, String, String> {

        private String resp;
        //private boolean res;

        @Override
        protected String doInBackground(String... params) {
            if(!Global.username.equalsIgnoreCase("") |!Global.username.equalsIgnoreCase(null) | !Global.username.equalsIgnoreCase("not valid")) {

                GetSubscriberResults_modified_v2();
            }
            return resp;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation





        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {

            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
            //channel_info.setText("please wait while we loading data from server ....");
        }
    }





    private void authenticate()
    {


        SharedPreferences prefs = getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
        name = prefs.getString("mobile", "not valid");
        id = prefs.getString("id", "not valid");
//        Toast.makeText(Splash.this,name+id,Toast.LENGTH_LONG).show();
        if (!name.equalsIgnoreCase("not valid")) {
            DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(name);
            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    if (dataSnapshot.getValue().toString() != null) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("id") != null && map.get("name") != null) {
                            String rx_id = map.get("id").toString();
                            String user_desc = map.get("name").toString();


                            // Toast.makeText(Dashboard.this, rx_id, Toast.LENGTH_LONG).show();

                            if (id.equals(rx_id)) {
                                Global.username = name;
                                Global.user_desc_name = user_desc;
                                Global.dob = map.get("dob").toString();
                                //Global.gender = map.get("gender").toString();
                                Global.city = map.get("city").toString();

                                SharedPreferences.Editor editor = getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                                editor.putString("username",user_desc );
                                editor.putString("dob", map.get("dob").toString());
                                editor.apply();
                                //Global.country = map.get("country").toString();
                                //Global.read_refresh_rate();

                            } else {
                                Intent intent = new Intent(Dashboard.this, Register.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else
                        {
                            Intent intent = new Intent(Dashboard.this, Register.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(Dashboard.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });

        } else {
            Intent intent = new Intent(Dashboard.this, Register.class);
            startActivity(intent);
            finish();
        }


    }








    private boolean grant_permission()
    {
        boolean all_permission_granted=true;
        int android_Version = Build.VERSION.SDK_INT;
        if (android_Version > Build.VERSION_CODES.LOLLIPOP_MR1) {

            all_permission_granted = !(ContextCompat.checkSelfPermission(Dashboard.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Dashboard.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Dashboard.this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Dashboard.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Dashboard.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED);
        }
        return all_permission_granted;

        //location_enable();
    }

    private boolean has_permissions(String[] permissions) {
        boolean all_permission_granted = true;
        int android_Version = Build.VERSION.SDK_INT;
        if (android_Version > Build.VERSION_CODES.LOLLIPOP_MR1) {

            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(Dashboard.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void grant_all_permission()
    {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.INTERNET, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};
        if(!has_permissions(PERMISSIONS)){
            ActivityCompat.requestPermissions(Dashboard.this, PERMISSIONS, PERMISSION_ALL);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(user_ref!=null && subscriber_listener!=null) {
            user_ref.removeEventListener(subscriber_listener);
        }
        if(subscriber_detail!=null && subscriber_detail_listener!=null) {
            subscriber_detail.removeEventListener(subscriber_detail_listener);
        }

        if(results!=null) {
            results.clear();
        }
        if(adapter!=null)
        {
            adapter=null;
        }


    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
