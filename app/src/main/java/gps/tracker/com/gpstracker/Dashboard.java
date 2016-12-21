package gps.tracker.com.gpstracker;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


@SuppressWarnings("ALL")
public class Dashboard extends AppCompatActivity {

    private ListView lv1;
    private final ArrayList<Suscriber_results> results = new ArrayList<Suscriber_results>();
    private Subscriber_list_view_adapter adapter;
    private String subscriber_invite,subscriber_name;
    private String status="offline";
    private static final Integer[] images = { R.drawable.green_circle,R.drawable.red_circle };
    private ProgressBar spinner;
    private String name,id;
    // --Commented out by Inspection (01/12/16, 10:05 PM):private String channel_mobile,channel_name,channel_vnumber,channel_vname,channel_invite,channel_bmp,channel_category,channel_vtype;
    private String subscriber;
    private DatabaseReference user_ref;
    private ValueEventListener subscriber_listener,subscriber_detail_listener;
    private DatabaseReference subscriber_detail,authenticate_user_ref;
    //private Value



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        adapter=null;


        //authenticate();
        if(!grant_permission()) {
            grant_all_permission();
        }

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setTitle("JUSTIN");
        /*if(Global.channel_broadcasting_name.equalsIgnoreCase("NONE"))
        {
            ab.setSubtitle("");
        }
        else
        {
            ab.setSubtitle(Global.channel_broadcasting_name+","+Global.channel_broadcasting_vnumber);
        }*/

        ab.setDisplayHomeAsUpEnabled(false);


        SharedPreferences prefs = getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
        name = prefs.getString("mobile", "not valid");
        if(!name.equalsIgnoreCase("") && !name.equalsIgnoreCase(null) && !name.equalsIgnoreCase("not valid"))
        {
            Global.username=name;
            Dashboard.Subscriber_channel_class scc=new Dashboard.Subscriber_channel_class();
            scc.execute();

        }
        else
        {
            Intent intent = new Intent(Dashboard.this, Register.class);
            startActivity(intent);
            finish();
        }
        lv1=(ListView)findViewById(R.id.subscriber_list);

        ImageView add_channel = (ImageView) findViewById(R.id.add);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
         spinner.setVisibility(View.GONE);

        add_channel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


               // searchView.setIconified(false);
              //  searchView.setFocusable(true);
             //   add_channel.setVisibility(View.GONE);
                  //searchView.clearFocus();
                if(!Global.username.equalsIgnoreCase("") |!Global.username.equalsIgnoreCase(null) | !Global.username.equalsIgnoreCase("not valid")) {
                    authenticate();
                }

                    Intent intent = new Intent(Dashboard.this, Search_activity.class);
                startActivity(intent);
                finish();

            }
        });




        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                if (isNetworkAvailable()) {

                    if (!Global.username.equalsIgnoreCase("") | !Global.username.equalsIgnoreCase(null) | !Global.username.equalsIgnoreCase("not valid")) {


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


                            if(user_ref!=null && subscriber_listener!=null) {
                                user_ref.removeEventListener(subscriber_listener);
                            }
                            if(subscriber_detail!=null && subscriber_detail_listener!=null) {
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
                        authenticate();
                    }


                } else {
                    Toast.makeText(Dashboard.this, "No Active Internet Connection Found", Toast.LENGTH_LONG).show();
                }
            }
        });


        //handleIntent(getIntent());

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
                Intent i1 = new Intent(Dashboard.this, User_setting.class);
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

        else if(id==R.id.search)
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
            builder.setMessage("Application Version"+System.getProperty("line.separator")+"Jusin-beta-1.6.3"+System.getProperty("line.separator")+"Release Date : 20/12/2016"+System.getProperty("line.separator")+"Change Logs"+System.getProperty("line.separator")+"Improved Broadcast Performance"+System.getProperty("line.separator")+"MAP performance"+System.getProperty("line.separator")+"Other Bugs fixed and improved Performance");
            builder.setPositiveButton("OK", null);
            builder.show();
            return true;
        }




        return super.onOptionsItemSelected(item);



    }







    private void GetSubscriberResults_modified_v2(){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        adapter=null;
        results.clear();
        FirebaseDatabase firebase_database = FirebaseDatabase.getInstance();
        DatabaseReference firebase_dbreference=firebase_database.getReference("JustIn");
        user_ref = firebase_dbreference.child("USERS").child(Global.username).child("Subscribers");
        user_ref.keepSynced(true);
        subscriber_listener=user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                results.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){


                        getSubscriberdetails(child);



                    }

                }

                //adapter = new Subscriber_list_view_adapter(getApplicationContext(), results);
                //lv1.setAdapter(adapter);
                //adapter.setContext(getApplicationContext());
                //spinner.setVisibility(View.GONE);




            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Dashboard.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

private void getSubscriberdetails(final DataSnapshot child)
{
    subscriber_detail = Global.firebase_dbreference.child("CHANNELS").child(child.getKey().toString()).child("status");
    subscriber_detail.keepSynced(true);
    subscriber_detail_listener=subscriber_detail.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String act;
            if(dataSnapshot!=null) {
                act = dataSnapshot.getValue().toString();


                if (child != null) {

                    try {

                        Map<String, Object> map = (Map<String, Object>) child.getValue();
                        Suscriber_results sr1 = new Suscriber_results();
                        if (map != null && map.get("name") != null && map.get("vehicle_number") != null && map.get("status") != null && map.get("vname") != null && map.get("mobile") != null) {
                            sr1.setsName("Name : " + map.get("name").toString());
                            sr1.setChannelid(child.getKey());
                            sr1.setsPhone("Mobile No. : " + map.get("mobile").toString());
                            sr1.setsVnumber("Viehicle No. : " + map.get("vehicle_number").toString());
                            sr1.setsvname("V-Name : " + map.get("vname").toString());
                            if (map.get("unblock") != null) {
                                sr1.setstatus(map.get("unblock").toString());
                            } else {
                                sr1.setstatus("1");
                            }

                            if (act.equalsIgnoreCase("1")) {
                                sr1.setImageid(images[0]);
                            } else {
                                sr1.setImageid(images[1]);
                            }

                            if (map.get("image") != null) {
                                sr1.setImage(download_image_to_firebase1(map.get("image").toString()));
                            } else {
                                sr1.setImage(download_image_to_firebase1("default"));
                            }
                            if (map.get("vtype") != null) {
                                sr1.setvtype("Type : " + map.get("vtype").toString());
                            } else {
                                sr1.setvtype("Type : " + "NA");
                            }
                            if (map.get("category") != null) {
                                sr1.setcategory("category : " + map.get("category").toString());
                            } else {
                                sr1.setcategory("category : " + "NA");
                            }


                            results.add(sr1);
                            map.clear();
                            sr1 = null;
                        }


                    }
                    catch (ClassCastException ce) {
                        Toast.makeText(Dashboard.this, "Filtered few invalid Channels", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else
            {
                Toast.makeText(Dashboard.this,"Invalid Subscriber Details",Toast.LENGTH_LONG).show();
            }



            adapter = new Subscriber_list_view_adapter(getApplicationContext(), results);
            if(adapter!=null) {
                lv1.setAdapter(adapter);
                //adapter.setContext(getApplicationContext());
            }
            spinner.setVisibility(View.GONE);




        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Toast.makeText(Dashboard.this, error.toException().toString(), Toast.LENGTH_LONG).show();

        }
    });
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

            all_permission_granted = !(ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED);
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
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK};
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


    /*@Override
    public void onBackPressed() {

        //user_ref.removeEventListener();

        System.gc();

          finish();

    }*/


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
