package gps.tracker.com.gpstracker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mapzen.tangram.LngLat;
import com.mapzen.tangram.MapController;
import com.mapzen.tangram.MapData;
import com.mapzen.tangram.MapView;
import com.mapzen.tangram.TouchInput;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ibt.ortc.api.Ortc;
import ibt.ortc.extensibility.OnConnected;
import ibt.ortc.extensibility.OnMessage;
import ibt.ortc.extensibility.OnRegistrationId;
import ibt.ortc.extensibility.OrtcClient;
import ibt.ortc.extensibility.OrtcFactory;

public class Map_activity extends AppCompatActivity implements MapView.OnMapReadyCallback{
    private MapController map;

    // MapView is the View used to display the map.
    private static MapView mapview;
    private static double latitude; // latitude
    private static double longitude;
    private static double my_latitude; // latitude
    private static double my_longitude;
    private static MapData points;
    // --Commented out by Inspection (01/12/16, 10:08 PM):public static MapData marker;
    // --Commented out by Inspection (01/12/16, 10:08 PM):Map<String,String> desc;

    // --Commented out by Inspection (01/12/16, 10:09 PM):int sel=0;
    private int first_child=1;
    private TextView map_style;
    private static float scale_map=10f;

    private String s_phone;
    // --Commented out by Inspection (01/12/16, 10:09 PM):OrtcFactory factory;
    private static OrtcClient client;
    private boolean map_is_ready=false;
    private Thread location_update;
    // --Commented out by Inspection (01/12/16, 10:09 PM):private Activity activity;
    private long count;
    private String status;
    private String time_stamp="NA";
    private final Map<String, String> props = new HashMap<>();
    private ImageButton zoomminus;
    private ImageButton zoomplus;
    private ImageButton center;
    private GPSTracker gps;
    // --Commented out by Inspection (01/12/16, 10:09 PM):private String details;
    private int sel_loc =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        //getSupportActionBar().hide();
        mapview=(MapView)findViewById(R.id.map);
        Handler handler = new Handler();
        //activity=this;
        count=0;
        map_style=(TextView)findViewById(R.id.maps);
        zoomplus=(ImageButton)findViewById(R.id.zoomplus);
        zoomminus=(ImageButton)findViewById(R.id.zoomminus);
        center=(ImageButton)findViewById(R.id.center);
        Intent i = getIntent();
        s_phone= i.getStringExtra("subscriber");
        status= i.getStringExtra("status");
        String name=i.getStringExtra("name");
        String vnumber=i.getStringExtra("vnumber");
       // details=name.split(":")[1].trim()+" : "+vnumber.split(":")[1].trim();
        Global.set_action_bar_details(Map_activity.this,name.split(":")[1].trim(),vnumber.split(":")[1].trim());

        mapview.onCreate(savedInstanceState);
       // mapview.getMapAsync(this, "scene/scene.yaml");
        //mapview.getMapAsync(this, "cinnabar/cinnabar-style.yaml");
        //mapview.getMapAsync(this, "tron/tron.yaml");
         mapview.getMapAsync(this, "bubble-wrap1/bubble-wrap.yaml");
        //mapview.getMapAsync(this, "walkabout/walkabout-style.yaml");
        scale_map=13f;

        zoomplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(scale_map>=10f && scale_map<17)
               {
                   scale_map++;
                   map.setZoomEased(scale_map, 1, MapController.EaseType.LINEAR);

               }
                else if(scale_map>=17f)
               {
                   scale_map=17f;
                   map.setZoomEased(scale_map, 1, MapController.EaseType.LINEAR);

               }
                //Toast.makeText(Map_activity.this,"Zoom Value : "+String.valueOf(scale_map)+" ,Sel : "+String.valueOf(sel),Toast.LENGTH_SHORT).show();
            }
        });

        zoomminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scale_map>10f)
                {
                    scale_map--;
                    map.setZoomEased(scale_map, 1, MapController.EaseType.LINEAR);
                }
                else if(scale_map<=10f)
                {
                    scale_map=10f;
                    map.setZoomEased(scale_map, 1, MapController.EaseType.LINEAR);
                }
                      }
        });

        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sel_loc==1) {
                    my_location_gps();
                    sel_loc = 2;
                    center.setImageResource(R.drawable.my_loc);
                }
                else if(sel_loc==2)
                {
                    if (longitude != 0.0 && latitude != 0.0 ){
                        goToLandmark();
                    }
                    else
                    {
                        get_last_location_offline_mod();
                        update_tf_offline();
                    }

                    sel_loc=1;
                    center.setImageResource(R.drawable.cha_loc);
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.ab_map, menu);

        // Associate searchable configuration with the SearchView

        return true;
    }


    private void goToLandmark() {
       // scale_map=15f;

        if (map == null) {
            Toast.makeText(Map_activity.this,"Map is still not initialized :)",Toast.LENGTH_LONG).show();

            return;
        }
        if(points!=null ){
            points.clear();
        }

        int duration = 1; // Milliseconds

        // We use the position, zoom, tilt, and rotation of the Landmark to move the camera over time.
        // Different types of "easing" are available to make the transition smoother or sharper.
        if(longitude!=0.0 && latitude!=0.0) {

            map.setPositionEased(new LngLat(longitude, latitude), duration, MapController.EaseType.CUBIC);
            map.setZoomEased(scale_map, duration, MapController.EaseType.QUINT);
            points = map.addDataLayer("mz_default_point");
            props.put("type", "point");
            props.put("color", "#000000");
            props.put("text","hiii");
            points.addPoint(new LngLat(longitude,latitude),props);
            map_style.setText("Last updated on :"+ time_stamp + Global.separator);
           // map_style.setText("showing channel location");
        }

       // Toast.makeText(Map_activity.this,String.valueOf(latitude)+" , "+String.valueOf(longitude),Toast.LENGTH_LONG).show();







    }


    private void goToLandmark_mod() {
      //  scale_map=12f;

        if (map == null) {
            Toast.makeText(Map_activity.this,"Map is still not initialized :)",Toast.LENGTH_LONG).show();

            return;
        }

        int duration = 1; // Milliseconds

        // We use the position, zoom, tilt, and rotation of the Landmark to move the camera over time.
        // Different types of "easing" are available to make the transition smoother or sharper.
        if(longitude!=0.0 && latitude!=0.0) {

           if(map!=null) {
               if(points!=null ){
                   points.clear();
              }

               map.setPositionEased(new LngLat(longitude, latitude), duration, MapController.EaseType.CUBIC);
               map.setZoomEased(scale_map, duration, MapController.EaseType.QUINT);
               points = map.addDataLayer("mz_default_point");
               props.put("type", "point");
               props.put("color", "#000000");
               points.addPoint(new LngLat(longitude, latitude), props);
           }
            //map_style.setText("Last updated on :"+ time_stamp + Global.separator);
            // map_style.setText("showing channel location");
        }

        // Toast.makeText(Map_activity.this,String.valueOf(latitude)+" , "+String.valueOf(longitude),Toast.LENGTH_LONG).show();







    }



    private void goToLandmark1() {
       // scale_map=12f;

        if (map == null) {
            Toast.makeText(Map_activity.this,"Map is null :)",Toast.LENGTH_LONG).show();

            return;
        }

        int duration = 1; // Milliseconds
        if(points!=null ) {
            points.clear();
        }
        // We use the position, zoom, tilt, and rotation of the Landmark to move the camera over time.
        // Different types of "easing" are available to make the transition smoother or sharper.
        if(my_latitude!=0.0 && my_longitude!=0.0) {
            map.setPositionEased(new LngLat(my_longitude, my_latitude), duration, MapController.EaseType.CUBIC);
            //map.setZoomEased(scale_map, duration, MapController.EaseType.QUINT);
            points = map.addDataLayer("mz_dropped_pin");
            props.put("type", "point");
            props.put("color", "#ff0000");
            points.addPoint(new LngLat(my_longitude, my_latitude), props);
            //Toast.makeText(Map_activity.this, String.valueOf(my_latitude) + " , " + String.valueOf(my_longitude), Toast.LENGTH_LONG).show();
        }
    }


   /* private void initialize_map_data()
    {
        points = map.addDataLayer("mz_default_point");
        props.put("type", "point");
        props.put("color", "#000000");
        props.put("text","hiii");
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                mapview.onDestroy();
                Intent intent = new Intent(this, Dashboard.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(MapController mapController) {
        // We receive a MapController object in this callback when the map is ready for use.
        map = mapController;
        map.requestRender();
        map.useCachedGlState(true);
        map_is_ready=true;

       if(map!=null) {
           if (status.equalsIgnoreCase("online")) {
               blink();
               initial_realtime_service();
               update_loc_realtime();
           } else {
               get_last_location_offline();

           }


           initialize_map();
       }

        map.setScaleResponder(new TouchInput.ScaleResponder() {
            @Override public boolean onScale(float x, float y, float scale, float velocity) {


                return true;
            }
        });

    }



    private void initialize_map()
    {
        gps = new GPSTracker(Map_activity.this);
        if(gps.canGetLocation()){
            my_latitude = gps.getLatitude();
            my_longitude = gps.getLongitude();
            //Toast.makeText(Map_activity.this,"Your location in red point",Toast.LENGTH_LONG).show();
           // goToLandmark1();
        }
        else{
            gps.showSettingsAlert1();
        }

        get_last_location_offline_mod3();


        if(my_latitude!=0.0 && my_longitude!=0.0) {
           // scale_map=13f;
            map.setPositionEased(new LngLat(my_longitude,my_latitude), 1, MapController.EaseType.CUBIC);
            map.setZoomEased(scale_map, 1, MapController.EaseType.QUINT);
            points = map.addDataLayer("mz_dropped_pin");
            props.put("type", "point");
            props.put("color", "#ff0000");
            points.addPoint(new LngLat(my_longitude, my_latitude), props);
        }
        else
        {
            Toast.makeText(Map_activity.this,"your location could not be determined",Toast.LENGTH_LONG).show();
        }




    }

   /* @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Map_activity.this, Sel_Map_style.class);
        startActivity(intent);
        finish();
    }*/



    @Override
    public void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapview.onPause();
    }


    private void update_tf_realtime()
    {


        location_update = new Thread()
        {

            @Override
            public void run() {
                //yourOperation
                Map_activity.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {

                        goToLandmark_mod();
                        map_style.setText("Time Stamp : "+time_stamp+Global.separator);
                        //blink();
                        //Toast.makeText(activity,"Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude)+Global.separator+"Total Location Recieved : "+String.valueOf(count),Toast.LENGTH_LONG).show();


                    }});
                super.run();
            }
        };
        location_update.start();
    }


// --Commented out by Inspection START (01/12/16, 10:09 PM):
//    private void mbtile_mapzen_map()
//    {
//        File storageDir = Environment.getExternalStorageDirectory();
//        File mbtilesFile = new File(storageDir, "tangram-geojson-cache.mbtiles");
//        //map.setMBTiles("osm", mbtilesFile);
//    }
// --Commented out by Inspection STOP (01/12/16, 10:09 PM)



    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if(map_style.getVisibility() == View.VISIBLE){
                            map_style.setVisibility(View.INVISIBLE);
                        }else{
                            map_style.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }

    private void update_tf_offline()
    {


                        if(latitude!=0.0 | longitude!=0.0) {
                            map_style.setText("Last updated on :"+ time_stamp + Global.separator);
                            //Toast.makeText(activity,"Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude)+Global.separator+"Total Location Recieved : "+String.valueOf(count),Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            map_style.setText("No Last Location record found");

                        }


    }

    private String[] parse_location_data(String data)
    {
        return data.split(";");
    }

    private void initial_realtime_service()
    {
        try {
            Ortc ortc = new Ortc();

            OrtcFactory factory;


            factory = ortc.loadOrtcFactory("IbtRealtimeSJ");


            client = factory.createClient();


            client.setClusterUrl("http://ortc-developers.realtime.co/server/2.1");
            client.connect("Cmo9Y1", "testToken");
            client.setApplicationContext(getApplicationContext());
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        OrtcClient.setOnRegistrationId(new OnRegistrationId() {
            @Override
            public void run(String registrationId) {
                Log.i("REG", "GCM Registration ID: " + registrationId);

                // Use this method if you have implemented a backend to store your user's GCM registration ids
                //RegistrationIdRemoteStore.setRegistrationIdToBackend(getApplicationContext(), registrationId);
            }
        });
    }

    private void update_loc_realtime()
    {


        client.setGoogleProjectId("joinin-440f7");
        client.onConnected = new OnConnected() {
            @Override
            public void run(final OrtcClient sender) {
                // Messaging client connected

                // Now subscribe the channel
                client.subscribe(s_phone, true,
                        new OnMessage() {
                            // This function is the message handler
                            // It will be invoked for each message received in myChannel

                            public void run(OrtcClient sender, String channel, String message) {
                                // Received a message
                                //System.out.println("map data : "+message);
                                String[] data=parse_location_data(message);
                                if(data.length==3 && map_is_ready)
                                {

                                    longitude=Double.parseDouble(data[0]);
                                    latitude=Double.parseDouble(data[1]);
                                    time_stamp=data[2];
                                    if(longitude!=0.0 && latitude!=0.0) {
                                        //System.out.println("map data : " + data[0]);
                                       // System.out.println("map data : " + data[1]);
                                        //goToLandmark_mod();
                                        count++;
                                      //  System.out.println("map data : " + String.valueOf(count));
                                        update_tf_realtime();
                                    }
                                    else
                                    {
                                        get_last_location_offline_mod();
                                       // goToLandmark();
                                        update_tf_offline();
                                    }
                                    //Map_activity.this.map_style.setText("Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude));
                                }
                                else
                                {
                                    //Map_activity.this.map_style.setText("Channel not active or map not initialized");

                                }
                                //Toast.makeText(Channel_settings.this,"message recieved "+message,Toast.LENGTH_LONG ).show();
                            }
                        });
            }
        };

        


    }


    private void get_last_location_offline()
    {

        System.out.println("reached offline location reading facility by firebase");
        DatabaseReference ref = Global.firebase_dbreference.child("CHANNELS").child(s_phone).child("locations").child("latest_location");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {


                        String data[]=child.getValue().toString().split(";");
                        if(data.length==3)
                        {

                            longitude=Double.parseDouble(data[0]);
                            latitude=Double.parseDouble(data[1]);
                            time_stamp=data[2];
                            System.out.println("map data : "+data[0]);
                            System.out.println("map data : "+data[1]);



                            count++;
                            System.out.println("map data : "+String.valueOf(count));

                            first_child++;
                            //Map_activity.this.map_style.setText("Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude));
                        }
                        else
                        {
                            Toast.makeText(Map_activity.this,"No Last location found",Toast.LENGTH_LONG).show();
                            //Map_activity.this.map_style.setText("Channel not active or map not initialized");

                        }

                    }

                }




            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Map_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }


    private void get_last_location_offline_mod2()
    {

        System.out.println("reached offline location reading facility by firebase");
        DatabaseReference ref = Global.firebase_dbreference.child("CHANNELS").child(s_phone).child("locations").child("latest_location");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {


                    String data[]=child.getValue().toString().split(";");
                    if(data.length==3)
                    {

                        longitude=Double.parseDouble(data[0]);
                        latitude=Double.parseDouble(data[1]);
                        time_stamp=data[2];
                        System.out.println("map data : "+data[0]);
                        System.out.println("map data : "+data[1]);



                        count++;
                        System.out.println("map data : "+String.valueOf(count));

                        first_child++;
                        //Map_activity.this.map_style.setText("Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude));
                    }


                }

            }




            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Map_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }



    private void get_last_location_offline_mod()
    {

        System.out.println("reached offline location reading facility by firebase");
        DatabaseReference ref = Global.firebase_dbreference.child("CHANNELS").child(s_phone).child("locations").child("latest_location");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String data[] = dataSnapshot.getValue().toString().split(";");
                    if (data.length == 3) {

                        longitude = Double.parseDouble(data[0]);
                        latitude = Double.parseDouble(data[1]);
                        time_stamp = data[2];
                        System.out.println("map data : " + data[0]);
                        System.out.println("map data : " + data[1]);
                        if (longitude != 0.0 && latitude != 0.0) {
                            if (points != null) {
                                points.clear();
                            }

                            int duration = 1; // Milliseconds

                            // We use the position, zoom, tilt, and rotation of the Landmark to move the camera over time.
                            // Different types of "easing" are available to make the transition smoother or sharper.
                            if (longitude != 0.0 && latitude != 0.0) {
                                map.setPositionEased(new LngLat(longitude, latitude), duration, MapController.EaseType.CUBIC);
                                map.setZoomEased(scale_map, duration, MapController.EaseType.QUINT);
                                points = map.addDataLayer("mz_default_point");
                                props.put("type", "point");
                                props.put("color", "#000000");
                                props.put("text", "hiii");
                                points.addPoint(new LngLat(longitude, latitude), props);
                                map_style.setText("Last updated on :" + time_stamp + Global.separator);

                                // map_style.setText("showing channel location");
                            }

                            // Toast.makeText(Map_activity.this,String.valueOf(latitude)+" , "+String.valueOf(longitude),Toast.LENGTH_LONG).show();

                        }


                        //Map_activity.this.map_style.setText("Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude));
                    } else {
                        Toast.makeText(Map_activity.this, "No Last location found", Toast.LENGTH_LONG).show();
                        //Map_activity.this.map_style.setText("Channel not active or map not initialized");

                    }


                }
                else
                {
                    Toast.makeText(Map_activity.this, "No Last location found", Toast.LENGTH_LONG).show();

                }
            }




            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Map_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }


    private void get_last_location_offline_mod3()
    {

        System.out.println("reached offline location reading facility by firebase");
        DatabaseReference ref = Global.firebase_dbreference.child("CHANNELS").child(s_phone).child("locations").child("latest_location");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String data[] = dataSnapshot.getValue().toString().split(";");
                    if (data.length == 3) {

                        longitude = Double.parseDouble(data[0]);
                        latitude = Double.parseDouble(data[1]);
                        time_stamp = data[2];
                        System.out.println("map data : " + data[0]);
                        System.out.println("map data : " + data[1]);
                        if (longitude != 0.0 && latitude != 0.0) {
                            if (points != null) {
                                points.clear();
                            }

                            int duration = 1; // Milliseconds

                            // We use the position, zoom, tilt, and rotation of the Landmark to move the camera over time.
                            // Different types of "easing" are available to make the transition smoother or sharper.
                            if (longitude != 0.0 && latitude != 0.0) {
                                map.setPositionEased(new LngLat(longitude, latitude), duration, MapController.EaseType.CUBIC);
                               // map.setZoomEased(scale_map, duration, MapController.EaseType.QUINT);
                                points = map.addDataLayer("mz_default_point");
                                props.put("type", "point");
                                props.put("color", "#000000");
                                props.put("text", "hiii");
                                points.addPoint(new LngLat(longitude, latitude), props);
                                map_style.setText("Last updated on :" + time_stamp + Global.separator);

                                // map_style.setText("showing channel location");
                            }

                            // Toast.makeText(Map_activity.this,String.valueOf(latitude)+" , "+String.valueOf(longitude),Toast.LENGTH_LONG).show();

                        }


                        //Map_activity.this.map_style.setText("Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude));
                    } else {
                        Toast.makeText(Map_activity.this, "No Last location found", Toast.LENGTH_LONG).show();
                        //Map_activity.this.map_style.setText("Channel not active or map not initialized");

                    }


                }
                else
                {
                    Toast.makeText(Map_activity.this, "No Last location found", Toast.LENGTH_LONG).show();

                }
            }




            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Map_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }

    @Override
    public void onBackPressed() {

        mapview.onDestroy();
        Intent intent = new Intent(Map_activity.this, Dashboard.class);
        startActivity(intent);
        finish();
    }


    private void my_location_gps()
    {
        gps = new GPSTracker(Map_activity.this);
        if(gps.canGetLocation()){
            my_latitude=0.0;
            my_longitude=0.0;
            my_latitude = gps.getLatitude();
            my_longitude = gps.getLongitude();
            if(my_longitude!=0.0 && my_latitude!=0.0) {
                goToLandmark1();
                map_style.setText("Showing my location");
            }
            else
            {
                Toast.makeText(Map_activity.this,"channel location could not be determined",Toast.LENGTH_LONG).show();
                map_style.setText("channel location could not be determined");
            }
           // Toast.makeText(Map_activity.this,"Your location in red point",Toast.LENGTH_LONG).show();

        }
        else{
            gps.showSettingsAlert1();
        }


    }




}
