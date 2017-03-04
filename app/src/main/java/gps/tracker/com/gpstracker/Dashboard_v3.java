package gps.tracker.com.gpstracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapzen.tangram.LngLat;
import com.mapzen.tangram.MapController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Dashboard_v3 extends AppCompatActivity {
    private int broadcast_index=0,follows_index=0;
    List<BroadCastDataItem> results = new ArrayList();
    List<FollowsDataItem> follows_results = new ArrayList();
    String name,username;
    RecyclerView rcv_broadcast,rcv_follows;
    private final Integer[] images = { R.drawable.ic_broadcast,R.drawable.broadcast_off };
    private static final Integer[] status_images = {R.drawable.ic_tick, R.drawable.ic_busy};

    private DatabaseReference user_ref;
    private ValueEventListener subscriber_listener,subscriber_detail_listener,subscriber_listener1,subscriber_detail_listener1;
    private DatabaseReference subscriber_detail, authenticate_user_ref,subscriber_detail1;
    private DatabaseReference fetch_loc_ref, channel_status;
    private ValueEventListener fetch_listener, channel_status_listener;
    private int channel_count=0;
    FollowsDataAdapter followsDataAdapter;
    String time_stamp="";
    private Date date1, date2;
    private ImageView follow_delete,follow_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_v3);
        follow_delete=(ImageView)findViewById(R.id.activity_main_iv_follows_delete);
        follow_add=(ImageView)findViewById(R.id.activity_main_iv_follows_add);
        SharedPreferences prefs = getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
        name = prefs.getString("mobile", "not valid");
        username = prefs.getString("username", "NA");
        initViews();
    }

    private void initViews() {

        LinearLayoutManager llm_follows_list_rcv
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rcv_follows = (RecyclerView) findViewById(R.id.activity_main_rcv_follows);



        rcv_follows.setHasFixedSize(true);
        rcv_follows.setLayoutManager(llm_follows_list_rcv);
        rcv_follows.setNestedScrollingEnabled(false);

        get_follows_data();
        //final FollowsDataAdapter followsDataAdapter = new FollowsDataAdapter(this, getFollowsData());

        //rcv_follows.setAdapter(followsDataAdapter);


        LinearLayoutManager llm_broadcast_list_rcv
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rcv_broadcast = (RecyclerView) findViewById(R.id.activity_main_rcv_broadcast);
        rcv_broadcast.setHasFixedSize(true);
        rcv_broadcast.setLayoutManager(llm_broadcast_list_rcv);
        rcv_broadcast.setNestedScrollingEnabled(false);

        getBroadCastData();


       /* RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        rcv_follows.addItemDecoration(itemDecoration);*/






        ((NestedScrollView)findViewById(R.id.activity_main_scv)).smoothScrollTo(0,0);
    }

    private List<FollowsDataItem> getFollowsData() {
        List<FollowsDataItem> results = new ArrayList();
        for (int index = 0; index < 25; index++) {
            FollowsDataItem followsDataItem = new FollowsDataItem();
            followsDataItem.setChannel_id(index+"");
            followsDataItem.setSubscriber_vehicle_name("Demo Channel #");
            followsDataItem.setSubscriber_name("Jerry Stone");
            followsDataItem.setSubscriber_mobile_no("8889552622");
            followsDataItem.setSubscriber_vehicle_no("MP04 SB 1234");
            followsDataItem.setVehicle_type("Personal");
            followsDataItem.setVehicle_category("Car");
            followsDataItem.setVehicle_location("Indore");
            followsDataItem.setTime(index+"m");

            if(index%2==0)
                followsDataItem.setStatus("Active");
            else
                followsDataItem.setStatus("Busy");

            results.add(index, followsDataItem);
        }
        return results;
    }

    private void getchanneldetails(final DataSnapshot child) {

       /* for (int index = 0; index < 25; index++) {
            BroadCastDataItem broadCastDataItem = new BroadCastDataItem();

            broadCastDataItem.setChannel_id(index+"");
            broadCastDataItem.setChannel_vehicle_name("Zambo");
            broadCastDataItem.setChannel_name("Vishal");
            broadCastDataItem.setChannel_mobile_no("8889552622");
            broadCastDataItem.setChannel_vehicle_no("MP04 1234");
            broadCastDataItem.setVehicle_type("Personal");
            broadCastDataItem.setVehicle_category("Truck");
            broadCastDataItem.setVehicle_location("Bhopal");
            if(index%2==0)
                broadCastDataItem.setStatus("Active");
            else
                broadCastDataItem.setStatus("Busy");

            results.add(index, broadCastDataItem);
        }*/

        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(child.getKey()).child("status");
        user_ref.keepSynced(true);
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //results.clear();
                final String status;
                if (dataSnapshot != null && dataSnapshot.getValue()!=null) {
                    status = dataSnapshot.getValue().toString();
                } else {
                    status = "0";
                }
                if (child != null) {


                    try {
                        Map<String, Object> map = (Map<String, Object>) child.getValue();


                        BroadCastDataItem broadCastDataItem = new BroadCastDataItem();
                        if (map != null && map.get("owner") != null && map.get("vehicle_number") != null && map.get("category") != null && map.get("vtype") != null && map.get("visible") != null) {
                            broadCastDataItem.setChannel_name(capitalize_string(map.get("owner").toString()));
                            broadCastDataItem.setChannel_id(child.getKey());
                            broadCastDataItem.setVehicle_category(capitalize_string(map.get("category").toString()));
                            broadCastDataItem.setChannel_vehicle_no(capitalize_string(map.get("vehicle_number").toString()));
                            broadCastDataItem.setVehicle_type(capitalize_string(map.get("vtype").toString()));
                            broadCastDataItem.setVehicle_location("Bhopal");

                            if (map.get("refresh_status") != null) {
                                broadCastDataItem.setChannel_mobile_no(map.get("refresh_status").toString());
                            } else {
                                broadCastDataItem.setChannel_mobile_no("20 secs");

                            }
                            String act = map.get("visible").toString();
                            //String status = map.get("status").toString();
                            if (act.equalsIgnoreCase("1")) {
                                broadCastDataItem.setStatus(true);


                            } else {
                                broadCastDataItem.setStatus(false);


                            }


                            if (status.equalsIgnoreCase("0")) {
                                SharedPreferences prefs = getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
                                String channel_broadcasting = prefs.getString("broadcasting", "NA");
                                if(channel_broadcasting.equalsIgnoreCase(child.getKey()))
                                {
                                    SharedPreferences.Editor editor = getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                                    editor.putString("broadcasting","NA");
                                    editor.apply();
                                }
                                status_update_v2("0", child.getKey());
                                broadCastDataItem.setImageid(images[1]);


                            } else {

                                status_update_v2("1", child.getKey());

                                SharedPreferences.Editor editor = getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                                editor.putString("broadcasting",child.getKey());
                                editor.apply();
                                broadCastDataItem.setImageid(images[0]);

                            }

                            if (map.get("image") != null) {
                                broadCastDataItem.setImage(download_image_to_firebase1(map.get("image").toString()));
                            } else {
                                broadCastDataItem.setImage(download_image_to_firebase1("default"));
                            }


                            if (map.get("vehicle_name") != null) {
                                broadCastDataItem.setChannel_vehicle_name(capitalize_string(map.get("vehicle_name").toString()));
                            } else {
                                broadCastDataItem.setChannel_vehicle_name(capitalize_string("VN : NA"));

                            }


                            results.add(broadcast_index,broadCastDataItem);
                            broadcast_index++;
                        }


                    } catch (ClassCastException ce) {

                    }

                }



                final BroadCastDataAdapter broadCastDataAdapter = new BroadCastDataAdapter(Dashboard_v3.this, results);
                rcv_broadcast.setAdapter(broadCastDataAdapter);





            }













            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Toast.makeText(MyChannels_RV.this, error.toException().toString(), Toast.LENGTH_LONG).show();


            }
        });
        //return results;
    }




    private void getBroadCastData(){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();

        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(name).child("channels");
        user_ref.keepSynced(true);

        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                results.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null) {

                        getchanneldetails(child);
                    }
                }





                //spinner.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Toast.makeText(MyChannels_RV.this, error.toException().toString(), Toast.LENGTH_LONG).show();



            }
        });


    }



    private String capitalize_string(String data)
    {

        String data1="";
        try {
            data1 = data.substring(0, 1).toUpperCase() + data.substring(1).toLowerCase();
        }catch(Exception e)
        {

        }
        return data1;
    }

    private void status_update_v2(final String update,final String channelid)
    {
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channelid).child("status");
        user_ref.setValue(update);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);
    }

    @Override
    public void onBackPressed() {


        Intent intent = new Intent(Dashboard_v3.this, Dashboard.class);
        startActivity(intent);
        finish();
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

    private void get_follows_data(){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //adapter=null;
        //results.clear();
        FirebaseDatabase firebase_database = FirebaseDatabase.getInstance();
        DatabaseReference firebase_dbreference=firebase_database.getReference("JustIn");
        user_ref = firebase_dbreference.child("USERS").child(name).child("Subscribers");
        user_ref.keepSynced(true);
        subscriber_listener=user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                follows_results.clear();
                channel_count=0;
                follows_index=0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){

                        try {

                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            FollowsDataItem sr1 = new FollowsDataItem();

                            sr1.setChannel_id(child.getKey());
                            sr1.setVehicle_location("Bhopal");
                            if (map.get("unblock") != null) {
                                sr1.setStatus(map.get("unblock").toString());
                            } else {
                                sr1.setStatus("1");
                            }




                            follows_results.add(follows_index,sr1);
                            follows_index++;
                            channel_count++;
                            map.clear();
                            sr1 = null;
                            // }


                        }
                        catch (ClassCastException ce) {
                            //Toast.makeText(Dashboard.this, "Filtered few invalid Channels", Toast.LENGTH_LONG).show();
                           // Snackbar snackbar = Snackbar.make(coordinatorLayout, "Filtered few invalid Channels", Snackbar.LENGTH_LONG);
                           // snackbar.show();
                        }



                    }

                }


                //adapter = new Subscriber_list_view_adapter(getApplicationContext(), results);
                //lv1.setAdapter(adapter);
                //adapter.setContext(getApplicationContext());
                //spinner.setVisibility(View.GONE);

                FollowsDataItem sr1 = new FollowsDataItem();
                sr1.setChannel_id("Demo");
                sr1.setStatus("0");
                //sr1.setImageid(images[0]);
                sr1.setSubscriber_name("Name");
                sr1.setSubscriber_mobile_no("0000000000");
                sr1.setSubscriber_vehicle_no("MP04ZZ0000");
                sr1.setVehicle_type("Sedan");
                sr1.setVehicle_category("Personal");
                sr1.setSubscriber_vehicle_name("Demo");
                sr1.setVehicle_location("Bhopal");
                sr1.setImageid(status_images[0]);
                sr1.setImage(download_image_to_firebase1("default"));
                sr1.setTime("online");
                follows_results.add(sr1);

                //adapter = new Subscriber_list_view_adapter(getApplicationContext(), results);
                //if(adapter!=null) {
                   // lv1.setAdapter(adapter);
                followsDataAdapter = new FollowsDataAdapter(Dashboard_v3.this, follows_results);
                rcv_follows.setAdapter(followsDataAdapter);
                Global.save_channel_count(Dashboard_v3.this,channel_count);
                    //Toast.makeText(activity,"Channel Count is"+String.valueOf(Global.get_channel_count(activity)),Toast.LENGTH_LONG).show();
                    //adapter.setContext(getApplicationContext());
                //}

                update_data();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Toast.makeText(Dashboard.this, error.toException().toString(), Toast.LENGTH_LONG).show();
                //Snackbar snackbar = Snackbar.make(coordinatorLayout, error.toException().toString(), Snackbar.LENGTH_LONG);
                //snackbar.show();
            }
        });

    }



    private void update_data()
    {

        for (int i = 0; i < follows_index+1; i++) {
            Object o = follows_results.get(i);
            final FollowsDataItem fullObject = (FollowsDataItem) o;
            String Channel_id=fullObject.getChannel_id();
            //Toast.makeText(activity,"channel_id"+Channel_id,Toast.LENGTH_LONG).show();
            subscriber_detail = Global.firebase_dbreference.child("CHANNELS").child(Channel_id);
            subscriber_detail.keepSynced(true);
            subscriber_detail_listener= subscriber_detail.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        if (dataSnapshot != null) {
                            if (dataSnapshot.getValue() != null) {
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                //Suscriber_results sr1 = new Suscriber_results();
                                if (map.get("status").toString().equalsIgnoreCase("1")) {
                                    fullObject.setImageid(status_images[0]);
                                } else {
                                    fullObject.setImageid(status_images[1]);
                                }
                                if (map.get("owner") != null) {
                                    fullObject.setSubscriber_name(capitalize_string(map.get("owner").toString()));
                                } else {
                                    fullObject.setSubscriber_name("NA");
                                }
                                if (map.get("mobile") != null) {
                                    fullObject.setSubscriber_mobile_no(capitalize_string(map.get("mobile").toString()));
                                } else {
                                    fullObject.setSubscriber_mobile_no("NA");
                                }
                                if (map.get("vehicle_number") != null) {
                                    fullObject.setSubscriber_vehicle_no(capitalize_string(map.get("vehicle_number").toString()));
                                } else {
                                    fullObject.setSubscriber_vehicle_no("NA");
                                }
                                if (map.get("vehicle_name") != null) {
                                    fullObject.setSubscriber_vehicle_name(capitalize_string(map.get("vehicle_name").toString()));
                                } else {
                                    fullObject.setSubscriber_vehicle_name("NA");
                                }
                                if (map.get("image") != null) {
                                    fullObject.setImage(download_image_to_firebase1(map.get("image").toString()));
                                } else {
                                    fullObject.setImage(download_image_to_firebase1("default"));
                                }
                                if (map.get("vtype") != null) {
                                    fullObject.setVehicle_type(capitalize_string(map.get("vtype").toString()));
                                } else {
                                    fullObject.setVehicle_type("NA");
                                }
                                if (map.get("category") != null) {
                                    fullObject.setVehicle_category(capitalize_string(map.get("category").toString()));
                                } else {
                                    fullObject.setVehicle_category("NA");
                                }
                                if (map.get("locations") != null) {
                                    Map<String, Object> map1 = (Map<String, Object>) map.get("locations");
                                    if (map1.get("latest_location") != null) {

                                        String data[] = map1.get("latest_location").toString().split(";");

                                        if (data.length >= 3) {


                                            time_stamp = data[2];

                                            SimpleDateFormat simpleDateFormat =
                                                    new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                                            SimpleDateFormat simpleDateFormat1 =
                                                    new SimpleDateFormat("dd MMM HH:mm:ss");
                                            try {
                                                date1 = simpleDateFormat.parse(time_stamp);
                                                date2 = simpleDateFormat.parse(Global.date_time_mod());

                                                String time_stamp_mod = simpleDateFormat1.format(date1);
                                                //map_style.setText(time_stamp_mod + " (" + printDifference(date1, date2) + " )");
                                                fullObject.setTime(printDifference(date1, date2));

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                                Toast.makeText(Dashboard_v3.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }


                                        } else {
                                            fullObject.setTime("NA");
                                        }
                                    } else {
                                        fullObject.setTime("NA");
                                    }


                                }
                            }

                            followsDataAdapter.notifyDataSetChanged();
                            rcv_follows.invalidate();
                       /* if (adapter != null) {
                            adapter.notifyDataSetChanged();
                            lv1.invalidate();
                        }*/
                        }
                    }catch(Exception e) {
                        //Toast.makeText(activity,"Error on loading subscribers"+e.getMessage(),Toast.LENGTH_LONG).show();
                        //Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error on loading subscribers"+e.getMessage(), Snackbar.LENGTH_LONG);
                        //snackbar.show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Toast.makeText(Dashboard.this, error.toException().toString(), Toast.LENGTH_LONG).show();
                   // Snackbar snackbar = Snackbar.make(coordinatorLayout, error.toException().toString(), Snackbar.LENGTH_LONG);
                   // snackbar.show();
                }
            });


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
            diff = " now";
        } else {
            diff = diff ;
        }
        return diff;

    }










}
