package gps.tracker.com.gpstracker;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mapzen.tangram.HttpHandler;
import com.mapzen.tangram.LngLat;
import com.mapzen.tangram.MapController;
import com.mapzen.tangram.MapData;
import com.mapzen.tangram.MapView;
import com.mapzen.tangram.Marker;
import com.mapzen.tangram.TouchInput;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static gps.tracker.com.gpstracker.Global.isNetworkAvailable;

//import ibt.ortc.extensibility.OrtcClient;

public class Map_activity extends AppCompatActivity implements MapView.OnMapReadyCallback {
    private MapController map;

    // MapView is the View used to display the map.
    private MapView mapview;
    private double latitude; // latitude
    private double longitude;
    //private  double my_latitude; // latitude
    //private  double my_longitude;
    private MapData points;
    private DatabaseReference fetch_loc_ref, channel_status;
    private ValueEventListener fetch_listener, channel_status_listener;
    private TextView map_style;
    private float scale_map = 13f;

    private String s_phone;
    private String time_stamp = "NA";
    private final Map<String, String> props = new HashMap<>();
    private ImageButton center;
    private GPSTracker gps;
    private int sel_loc = 1;
    private static WeakReference<Map_activity> activity;
    private ActionBar ab;
    private OkHttpClient okClient;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout ma;
    private Date date1, date2;
    private Marker ownLocation;
    Typeface robotoLight;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        mapview = (MapView) findViewById(R.id.map);
        activity = new WeakReference<>(this);
        map_style = (TextView) findViewById(R.id.maps);
        ImageButton zoomplus = (ImageButton) findViewById(R.id.zoomplus);
        ImageButton zoomminus = (ImageButton) findViewById(R.id.zoomminus);
        center = (ImageButton) findViewById(R.id.center);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        ma = (RelativeLayout) findViewById(R.id
                .ma);

        Intent i = getIntent();
        okClient = new OkHttpClient();

        robotoLight = Typeface.createFromAsset(activity.get().getAssets(), "fonts/roboto_light.ttf");
        map_style.setTypeface(robotoLight);
        okClient.setConnectTimeout(10, TimeUnit.HOURS);
        okClient.setReadTimeout(72, TimeUnit.HOURS);
        try {
            s_phone = i.getStringExtra("subscriber");
            String name = i.getStringExtra("name");
            String vnumber = i.getStringExtra("vnumber");
            ab = getSupportActionBar();
            assert ab != null;
            ab.setTitle(name);
            ab.setDisplayHomeAsUpEnabled(true);

            mapview.onCreate(savedInstanceState);
            mapview.getMapAsync(this, "bubble-wrap1/bubble-wrap.yaml");
            scale_map = 13f;

            //fetch_loc_fb();


            assert zoomplus != null;
            zoomplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (scale_map >= 10f && scale_map < 16) {
                        scale_map++;
                        map.setZoomEased(scale_map, 1, MapController.EaseType.LINEAR);

                    } else if (scale_map >= 16f) {
                        scale_map = 16f;
                        map.setZoomEased(scale_map, 1, MapController.EaseType.LINEAR);

                    }
                    //Toast.makeText(Map_activity.this,"Zoom Value : "+String.valueOf(scale_map)+" ,Sel : "+String.valueOf(sel),Toast.LENGTH_SHORT).show();
                }
            });

            assert zoomminus != null;
            zoomminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (scale_map > 10f) {
                        scale_map--;
                        map.setZoomEased(scale_map, 1, MapController.EaseType.LINEAR);
                    } else if (scale_map <= 10f) {
                        scale_map = 10f;
                        map.setZoomEased(scale_map, 1, MapController.EaseType.LINEAR);
                    }
                }
            });

            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    map.applySceneUpdates();
                    map.requestRender();
                    if (sel_loc == 1) {
                        my_location_gps();
                        sel_loc = 2;
                        center.setImageResource(R.drawable.my_loc1);
                    } else if (sel_loc == 2) {
                        if (longitude != 0.0 && latitude != 0.0) {
                            //goToLandmark();
                            if (map == null) {
                                Toast.makeText(activity.get(), "Map is still not initialized :)", Toast.LENGTH_LONG).show();


                                return;
                            }
                            if (points != null) {
                                points.clear();
                            }

                            int duration = 100;


                            //ownLocation.setStyling("{ style: 'points', size: [27px, 27px], order: 2000, collide: false , color: white}");
                            //ownLocation.setVisible(true);

                            map.setPositionEased(new LngLat(longitude, latitude), duration, MapController.EaseType.CUBIC);
                            map.setZoomEased(scale_map, duration, MapController.EaseType.QUINT);
                            points = map.addDataLayer("mz_default_point");
                            props.put("type", "point");
                            props.put("color", "#000000");
                            props.put("text", "hiii");
                            points.addPoint(new LngLat(longitude, latitude), props);
                            map_style.setText("Last updated on :" + time_stamp + Global.separator);
                            //Snackbar snackbar = Snackbar.make(ma, "Last updated on :"+ time_stamp, Snackbar.LENGTH_INDEFINITE);
                            //snackbar.show();
                        } else {
                            fetch_loc_fb();
                        }

                        sel_loc = 1;
                        center.setImageResource(R.drawable.cha_loc1);
                    }

                }
            });


            channel_status_check();
        } catch (Exception e) {
            Toast.makeText(activity.get(), "Fatal error in fetching channel details", Toast.LENGTH_LONG).show();
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void fetch_loc_fb() {
        try {
            fetch_loc_ref = Global.firebase_dbreference.child("CHANNELS").child(s_phone).child("locations").child("latest_location");
            fetch_loc_ref.keepSynced(true);
            fetch_listener = fetch_loc_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    longitude = 0.0;
                    latitude = 0.0;
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        String data[] = dataSnapshot.getValue().toString().split(";");
                        if (data.length == 3) {

                            longitude = Double.parseDouble(data[0]);
                            latitude = Double.parseDouble(data[1]);
                            time_stamp = data[2];
                            if (longitude != 0.0 && latitude != 0.0) {
                                if (map == null) {
                                    Toast.makeText(activity.get(), "Map is still not initialized :)", Toast.LENGTH_LONG).show();

                                    return;
                                }

                                int duration = 100;
                                if (points != null) {
                                    points.clear();
                                }


                                //ownLocation = map.addMarker();
                                //ownLocation.setDrawable(R.drawable.cha_loc1);

                             /*  if(longitude!=0.0 && latitude!=0.0 && ownLocation!=null) {
                                   //map.setZoomEased(scale_map, duration, MapController.EaseType.QUINT);

                                   ownLocation.setStyling("{ style: 'points', size: [27px, 27px], order: 2000, collide: false , color: white}");
                                    ownLocation.setVisible(true);
                                    ownLocation.setPointEased(
                                            new LngLat(longitude, latitude),
                                            duration, MapController.EaseType.CUBIC);

                                    //map.setPositionEased(new LngLat(longitude, latitude), duration, MapController.EaseType.CUBIC);
                                      }*/
                                map.setPositionEased(new LngLat(longitude, latitude), duration, MapController.EaseType.CUBIC);
                                map.setZoomEased(scale_map, duration, MapController.EaseType.QUINT);

                                points = map.addDataLayer("mz_default_point");
                                props.put("type", "point");
                                props.put("color", "#000000");
                                points.addPoint(new LngLat(longitude, latitude), props);


                                SimpleDateFormat simpleDateFormat =
                                        new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                                //goToLandmark_mod();
                                try {
                                    date1 = simpleDateFormat.parse(time_stamp);
                                    date2 = simpleDateFormat.parse(Global.date_time_mod());
                                    map_style.setText(time_stamp + " (" + printDifference(date1, date2) + " )");

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Toast.makeText(activity.get(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                //Snackbar snackbar = Snackbar.make(coordinatorLayout, time_stamp, Snackbar.LENGTH_INDEFINITE);
                                //snackbar.show();
                            } else {
                                map_style.setText("Waiting for data...");
                                //Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for data...", Snackbar.LENGTH_INDEFINITE);
                                //snackbar.show();
                            }
                        } else {
                            map_style.setText("Channel not active or map not initialized");
                            //Snackbar snackbar = Snackbar.make(coordinatorLayout, "Channel not active or map not initialized", Snackbar.LENGTH_INDEFINITE);
                            // snackbar.show();

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
        } catch (Exception e) {
            Toast.makeText(activity.get(), "FATAL ERROR AT MAP ACTIVITY", Toast.LENGTH_LONG).show();
        }
    }


    private void channel_status_check() {
        try {
            channel_status = Global.firebase_dbreference.child("CHANNELS").child(s_phone).child("status");
            channel_status.keepSynced(true);
            channel_status_listener = channel_status.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (dataSnapshot != null) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue().toString().equalsIgnoreCase("1")) {
                                ab.setSubtitle("ONLINE");
                            } else if (dataSnapshot.getValue().toString().equalsIgnoreCase("0")) {
                                ab.setSubtitle("OFFLINE");
                            } else {
                                ab.setSubtitle("STATUS UNKNOWN");
                            }
                        } else {
                            Intent intent = new Intent(activity.get(), Dashboard.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(activity.get(), error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });
        } catch (Exception e) {
            Toast.makeText(activity.get(), "Fatal Error on loading Channel Details", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.ab_map, menu);

        // Associate searchable configuration with the SearchView

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(activity.get(), Dashboard.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.refresh:

                map.applySceneUpdates();
                map.requestRender();
                Toast.makeText(activity.get(), "Map Refreshed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(MapController mapController) {
        // We receive a MapController object in this callback when the map is ready for use.
        map = mapController;
        //Toast.makeText(Map_activity.this,"Map is ready and cache will be saved at "+Environment.getExternalStorageDirectory().getAbsolutePath() + "/gpstracker/tile_cache",Toast.LENGTH_LONG).show();

        //ownLocation = map.addMarker();
        //ownLocation.setDrawable(R.drawable.cha_loc1);
        map.useCachedGlState(true);
        map.setHttpHandler(getHttpHandler());
        fetch_loc_fb();

        map.setViewCompleteListener(new MapController.ViewCompleteListener() {
            public void onViewComplete() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        map.setHttpHandler(getHttpHandler());

                        //Toast.makeText(activity.get(),"Map Loaded",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        map.setScaleResponder(new TouchInput.ScaleResponder() {
            @Override
            public boolean onScale(float x, float y, float scale, float velocity) {


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
            handler.setCache(new File(cacheDir, "tile_cache"), 500 * 1024 * 1024);
            long SIZE_OF_CACHE = 500 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gpstracker/cache", "http"), SIZE_OF_CACHE);
            OkHttpClient client = new OkHttpClient();
            //client.cache(cache);
            client.networkInterceptors().add(new CachingControlInterceptor());
            //Toast.makeText(Map_activity.this,"cache saved at "+Environment.getExternalStorageDirectory().getAbsolutePath() + "/gpstracker/tile_cache",Toast.LENGTH_LONG).show();

        }

        return handler;
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(fetch_loc_ref!=null && fetch_listener!=null) {
            fetch_loc_ref.removeEventListener(fetch_listener);
        }
        if(channel_status!=null && channel_status_listener!=null) {
            channel_status.removeEventListener(channel_status_listener);
        }

        if (gps != null) {
            gps = null;
        }

        // mapview.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }

    @Override
    public void onBackPressed() {

        // mapview.onDestroy();
        // fetch_loc_ref.removeEventListener(fetch_listener);
        // channel_status.removeEventListener(channel_status_listener);
        Intent intent = new Intent(Map_activity.this, Dashboard.class);
        startActivity(intent);
        finish();
    }


    private void my_location_gps() {
        double my_latitude = 0.0;
        double my_longitude = 0.0;
        gps = new GPSTracker(getApplicationContext());
        if (gps.canGetLocation()) {

            my_latitude = gps.getLatitude();
            my_longitude = gps.getLongitude();
            if (my_longitude != 0.0 && my_latitude != 0.0) {
                if (map == null) {
                    Toast.makeText(activity.get(), "Map is null :)", Toast.LENGTH_LONG).show();

                    return;
                }

                int duration = 100; // Milliseconds
                map.setPositionEased(new LngLat(my_longitude, my_latitude), duration, MapController.EaseType.CUBIC);
                points = map.addDataLayer("mz_dropped_pin");
                props.put("type", "point");
                props.put("color", "#ff0000");
                points.addPoint(new LngLat(my_longitude, my_latitude), props);
                map_style.setText("Showing my location");
                //Snackbar snackbar = Snackbar.make(coordinatorLayout, "Showing my location", Snackbar.LENGTH_INDEFINITE);
                //snackbar.show();

            } else {
                Toast.makeText(activity.get(), "channel location could not be determined", Toast.LENGTH_LONG).show();
                map_style.setText("channel location could not be determined");
                //Snackbar snackbar = Snackbar.make(coordinatorLayout, "channel location could not be determined", Snackbar.LENGTH_INDEFINITE);
                //snackbar.show();
            }

        } else {
            map_style.setText("Your location could not be determined, GPS is not enabled, Enable Location services ");
            //gps.showSettingsAlert1();
        }


    }


    public String printDifference(Date startDate, Date endDate) {

        //milliseconds
        String diff = "";
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedDays > 0) {
            diff = diff + " " + String.valueOf(elapsedDays) + " days";
        }
        else if (elapsedHours > 0) {
            diff = diff + " " + String.valueOf(elapsedHours) + " hrs";
        }
        else if (elapsedMinutes > 0) {
            diff = diff + " " + String.valueOf(elapsedMinutes) + " mins";
        }

        if (diff.equalsIgnoreCase("")) {
            diff = "right now";
        } else {
            diff = diff + " ago";
        }
        return diff;

    }



    public class CachingControlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            // Add Cache Control only for GET methods
            if (request.method().equals("GET")) {

                // 1 day
                request = request.newBuilder()
                        .header("Cache-Control", "only-if-cached")
                        .build();
            } else {
                // 4 weeks stale
                request = request.newBuilder()
                        .header("Cache-Control", "public, max-stale=2419200")
                        .build();
            }


            Response originalResponse = chain.proceed(request);
            return originalResponse.newBuilder()
                    .header("Cache-Control", "max-age=2500")
                    .build();
        }
    }


}
