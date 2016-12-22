package gps.tracker.com.gpstracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.mapzen.tangram.HttpHandler;
import com.mapzen.tangram.LngLat;
import com.mapzen.tangram.MapController;
import com.mapzen.tangram.MapData;
import com.mapzen.tangram.MapView;
import com.mapzen.tangram.TouchInput;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static gps.tracker.com.gpstracker.Global.isNetworkAvailable;

//import ibt.ortc.extensibility.OrtcClient;

public class Map_activity extends AppCompatActivity implements MapView.OnMapReadyCallback{
    private  MapController  map;

    // MapView is the View used to display the map.
    private  MapView mapview;
    private  double latitude; // latitude
    private  double longitude;
    private  double my_latitude; // latitude
    private  double my_longitude;
    private  MapData points;
    private DatabaseReference fetch_loc_ref,channel_status;
    private ValueEventListener fetch_listener,channel_status_listener;
   // private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR;

    // --Commented out by Inspection (01/12/16, 10:08 PM):public static MapData marker;
    // --Commented out by Inspection (01/12/16, 10:08 PM):Map<String,String> desc;

    // --Commented out by Inspection (01/12/16, 10:09 PM):int sel=0;
    //private int first_child=1;
    private TextView map_style;
    private  float scale_map=10f;

    private String s_phone;
    // --Commented out by Inspection (01/12/16, 10:09 PM):OrtcFactory factory;
    //private static OrtcClient client;
    private boolean map_is_ready=false;
    // --Commented out by Inspection (01/12/16, 10:09 PM):private Activity activity;
    //private long count;
    //private String status;
    private String time_stamp="NA";
    private final Map<String, String> props = new HashMap<>();
    private ImageButton center;
    private GPSTracker gps;
    // --Commented out by Inspection (01/12/16, 10:09 PM):private String details;
    private int sel_loc =1;
    Thread location_update;
    private static WeakReference<Map_activity> activity;
    private ActionBar ab;
    private OkHttpClient okClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        //getSupportActionBar().hide();
        mapview=(MapView)findViewById(R.id.map);

        //Handler handler = new Handler();
        //activity=this;
        activity = new WeakReference<>(this);
        //count=0;
        map_style=(TextView)findViewById(R.id.maps);
        ImageButton zoomplus = (ImageButton) findViewById(R.id.zoomplus);
        ImageButton zoomminus = (ImageButton) findViewById(R.id.zoomminus);
        center=(ImageButton)findViewById(R.id.center);
        Intent i = getIntent();
        okClient = new OkHttpClient();
        okClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okClient.setReadTimeout(72, TimeUnit.HOURS);
        s_phone= i.getStringExtra("subscriber");
        //status= i.getStringExtra("status");
        String name=i.getStringExtra("name");
        String vnumber=i.getStringExtra("vnumber");
       // details=name.split(":")[1].trim()+" : "+vnumber.split(":")[1].trim();
        //Global.set_action_bar_details(Map_activity.this,name.split(":")[1].trim(),vnumber.split(":")[1].trim());
        ab = getSupportActionBar();
        ab.setTitle(name.split(":")[1].trim()+" "+vnumber.split(":")[1].trim());
        //ab.setSubtitle(sub_title);
        ab.setDisplayHomeAsUpEnabled(true);
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

                map.applySceneUpdates();
                map.requestRender();
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

        //initial_realtime_service();

        //update_loc_realtime();
        channel_status_check();
        fetch_loc_fb();

    }


    private void fetch_loc_fb()
    {
        fetch_loc_ref = Global.firebase_dbreference.child("CHANNELS").child(s_phone).child("locations").child("latest_location");
        fetch_loc_ref.keepSynced(true);
        fetch_listener=fetch_loc_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot != null) {
                    String data[] = dataSnapshot.getValue().toString().split(";");
                    if (data.length == 3 && map_is_ready) {

                        longitude = Double.parseDouble(data[0]);
                        latitude = Double.parseDouble(data[1]);
                        time_stamp = data[2];
                        if (longitude != 0.0 && latitude != 0.0) {
                            //System.out.println("map data : " + data[0]);
                            // System.out.println("map data : " + data[1]);
                            //goToLandmark_mod();
                            //count++;
                            //  System.out.println("map data : " + String.valueOf(count));
                            update_tf_realtime();
                        } else {
                            get_last_location_offline_mod();
                            // goToLandmark();
                            update_tf_offline();
                        }
                        //Map_activity.this.map_style.setText("Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude));
                    } else {
                        Map_activity.this.map_style.setText("Channel not active or map not initialized");

                    }
                    //Toast.makeText(Channel_settings.this,"message recieved "+message,Toast.LENGTH_LONG ).show();
                }
            }





            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(activity.get(), error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });
    }


    private void channel_status_check()
    {
        channel_status = Global.firebase_dbreference.child("CHANNELS").child(s_phone).child("status");
        channel_status.keepSynced(true);
        channel_status_listener=channel_status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot != null) {
                    if(dataSnapshot.getValue().toString().equalsIgnoreCase("1"))
                    {
                        ab.setSubtitle("ONLINE");
                    }
                    else if(dataSnapshot.getValue().toString().equalsIgnoreCase("0"))
                    {
                        ab.setSubtitle("OFFLINE");
                    }
                    else
                    {
                        ab.setSubtitle("STATUS UNKNOWN");
                    }

                }
            }





            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(activity.get(), error.toException().toString(), Toast.LENGTH_LONG).show();

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
            Toast.makeText(activity.get(),"Map is still not initialized :)",Toast.LENGTH_LONG).show();

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
            Toast.makeText(activity.get(),"Map is still not initialized :)",Toast.LENGTH_LONG).show();

            return;
        }

        int duration = 1; // Milliseconds

        // We use the position, zoom, tilt, and rotation of the Landmark to move the camera over time.
        // Different types of "easing" are available to make the transition smoother or sharper.
        if(longitude!=0.0 && latitude!=0.0) {

            try {
                if (map != null) {
                    try {
                        if (points != null) {
                            points.clear();
                        }
                    } catch (NullPointerException ignored) {

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
            } catch (NullPointerException ignored) {

            }
        }
        // Toast.makeText(Map_activity.this,String.valueOf(latitude)+" , "+String.valueOf(longitude),Toast.LENGTH_LONG).show();







    }



    private void goToLandmark1() {
       // scale_map=12f;

        if (map == null) {
            Toast.makeText(activity.get(),"Map is null :)",Toast.LENGTH_LONG).show();

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
                //mapview.onDestroy();
                Intent intent = new Intent(activity.get(), Dashboard.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.refresh:
                // app icon in action bar clicked; go home
                //mapview.onDestroy();
                map.applySceneUpdates();
                map.requestRender();
                Toast.makeText(activity.get(),"Map Refreshed",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(MapController mapController) {
        // We receive a MapController object in this callback when the map is ready for use.
        map = mapController;
        Toast.makeText(Map_activity.this,"Map is ready and cache will be saved at "+Environment.getExternalStorageDirectory().getAbsolutePath() + "/gpstracker/tile_cache",Toast.LENGTH_LONG).show();

        //map.requestRender();

        map.useCachedGlState(true);
        map.setHttpHandler(getHttpHandler());
       // map.setRenderMode(1);
        map_is_ready=true;

        map.setViewCompleteListener(new MapController.ViewCompleteListener() {
            public void onViewComplete() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        map.setHttpHandler(getHttpHandler());
                        //setMbTiles_map();
                       //Toast.makeText(activity.get(),"Map Loaded",Toast.LENGTH_SHORT).show();
                    }
                });
            }});




      /* if(map!=null) {
           if (status.equalsIgnoreCase("online")) {
               blink();
               initial_realtime_service();
               update_loc_realtime();
           } else {
               get_last_location_offline();

           }*/


           initialize_map();
      // }

        map.setScaleResponder(new TouchInput.ScaleResponder() {
            @Override public boolean onScale(float x, float y, float scale, float velocity) {


                return true;
            }
        });

    }


    HttpHandler getHttpHandler() {
        File cacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gpstracker/cache");
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {

            }
        }
        HttpHandler handler = new HttpHandler();

        if (cacheDir != null && cacheDir.exists()) {
            handler.setCache(new File(cacheDir, "tile_cache"), 100 * 1024 * 1024);
            //Toast.makeText(Map_activity.this,"cache saved at "+Environment.getExternalStorageDirectory().getAbsolutePath() + "/gpstracker/tile_cache",Toast.LENGTH_LONG).show();

        }

        return handler;
    }



    private void initialize_map()
    {
        /*gps = new GPSTracker(getApplicationContext());
        if(gps.canGetLocation()){
            my_latitude = gps.getLatitude();
            my_longitude = gps.getLongitude();
            //Toast.makeText(Map_activity.this,"Your location in red point",Toast.LENGTH_LONG).show();
           // goToLandmark1();
        }
        else{
            gps.showSettingsAlert1();
        }*/

        get_last_location_offline_mod3();


        /*if(my_latitude!=0.0 && my_longitude!=0.0) {
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
            Toast.makeText(activity.get(),"your location could not be determined",Toast.LENGTH_LONG).show();
        }*/




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


        location_update = new Thread() {

            @Override
            public void run() {
                //yourOperation
                Map_activity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        map.applySceneUpdates();
                        map.requestRender();
                        map.useCachedGlState(true);

                        goToLandmark_mod();
                        map_style.setText( time_stamp + Global.separator);
                        //blink();
                        //Toast.makeText(activity,"Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude)+Global.separator+"Total Location Recieved : "+String.valueOf(count),Toast.LENGTH_LONG).show();


                    }
                });
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



  /*  private void blink(){
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
    }*/

    public void setMbTiles_map()
    {
        File storageDir = Environment.getExternalStorageDirectory();
        File mbtilesFile = new File(storageDir, "tangram-geojson-cache.mbtiles");
        map.queueSceneUpdate("sources.osm.mbtiles",mbtilesFile.toString());
        map.applySceneUpdates();
        map.requestRender();
    }





    private void update_tf_offline()
    {


                        if(latitude!=0.0 | longitude!=0.0) {
                            map_style.setText(time_stamp + Global.separator);
                            //Toast.makeText(activity,"Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude)+Global.separator+"Total Location Recieved : "+String.valueOf(count),Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            map_style.setText("No Last Location record found");

                        }


    }

// --Commented out by Inspection START (14/12/16, 10:20 PM):
//    private String[] parse_location_data(String data)
//    {
//        return data.split(";");
//    }
// --Commented out by Inspection STOP (14/12/16, 10:20 PM)

   /* private void initial_realtime_service()
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

    }*/

   /* private void update_loc_realtime()
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
                                    Map_activity.this.map_style.setText("Channel not active or map not initialized");

                                }
                                //Toast.makeText(Channel_settings.this,"message recieved "+message,Toast.LENGTH_LONG ).show();
                            }
                        });

            }
        };

        client.onReconnected = new OnReconnected() {

            public void run(final OrtcClient sender) {
                runOnUiThread(new Runnable() {

                    public void run() {
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
                                            Map_activity.this.map_style.setText("Channel not active or map not initialized");

                                        }
                                        //Toast.makeText(Channel_settings.this,"message recieved "+message,Toast.LENGTH_LONG ).show();
                                    }
                                });

                    }
                });
            }
        };


        client.onException = new OnException() {

            public void run(OrtcClient send, Exception ex) {
                final Exception exception = ex;
                runOnUiThread(new Runnable() {

                    public void run() {
                        Toast.makeText(activity.get(),exception.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        };


        


    }*/


   /* private void get_last_location_offline()
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

                            //first_child++;
                            //Map_activity.this.map_style.setText("Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude));
                        }
                        else
                        {
                            Toast.makeText(activity.get(),"No Last location found",Toast.LENGTH_LONG).show();
                            //Map_activity.this.map_style.setText("Channel not active or map not initialized");

                        }

                    }

                }




            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(activity.get(), error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }*/


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
                                map_style.setText(time_stamp + Global.separator);

                                // map_style.setText("showing channel location");
                            }

                            // Toast.makeText(Map_activity.this,String.valueOf(latitude)+" , "+String.valueOf(longitude),Toast.LENGTH_LONG).show();

                        }


                        //Map_activity.this.map_style.setText("Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude));
                    } else {
                        Toast.makeText(activity.get(), "No Last location found", Toast.LENGTH_LONG).show();
                        //Map_activity.this.map_style.setText("Channel not active or map not initialized");

                    }


                }
                else
                {
                    Toast.makeText(activity.get(), "No Last location found", Toast.LENGTH_LONG).show();

                }
            }




            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(activity.get(), error.toException().toString(), Toast.LENGTH_LONG).show();

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
                //scale_map=13f;
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
                                map_style.setText(time_stamp + Global.separator);

                                // map_style.setText("showing channel location");
                            }

                            // Toast.makeText(Map_activity.this,String.valueOf(latitude)+" , "+String.valueOf(longitude),Toast.LENGTH_LONG).show();

                        }


                        //Map_activity.this.map_style.setText("Data Recieved : "+String.valueOf(longitude)+" , "+String.valueOf(latitude));
                    } else {
                        Toast.makeText(activity.get(), "No Last location found", Toast.LENGTH_LONG).show();
                        //Map_activity.this.map_style.setText("Channel not active or map not initialized");

                    }


                }
                else
                {
                    Toast.makeText(activity.get(), "No Last location found", Toast.LENGTH_LONG).show();

                }
            }




            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(activity.get(), error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        fetch_loc_ref.removeEventListener(fetch_listener);
        channel_status.removeEventListener(channel_status_listener);
        if(location_update!=null) {
            location_update.interrupt();
        }
        if(gps!=null)
        {
            gps=null;
        }
        //client.disconnect();
        mapview.onDestroy();

        //map=null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }

    @Override
    public void onBackPressed() {

        mapview.onDestroy();
        fetch_loc_ref.removeEventListener(fetch_listener);
        channel_status.removeEventListener(channel_status_listener);
        Intent intent = new Intent(Map_activity.this, Dashboard.class);
        startActivity(intent);
        finish();
    }


    private void my_location_gps()
    {
        gps = new GPSTracker(getApplicationContext());
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
                Toast.makeText(activity.get(),"channel location could not be determined",Toast.LENGTH_LONG).show();
                map_style.setText("channel location could not be determined");
            }
           // Toast.makeText(Map_activity.this,"Your location in red point",Toast.LENGTH_LONG).show();

        }
        else{
            gps.showSettingsAlert1();
        }


    }




}
