package gps.tracker.com.gpstracker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class MyChannels_RV extends AppCompatActivity {

    private ListView lv1;
    private final ArrayList<Channel_list> results = new ArrayList<Channel_list>();
    private Channel_list_view_adapter_mod adapter;
    private String subscriber_invite;
    //subscriber_name;
    private final String status="offline";
    private final Integer[] images = { R.drawable.broadcast_icon,R.drawable.red_circle };
    private final Integer[] visible_images={R.drawable.visible,R.drawable.invisible};
    private int position=0;
    private ProgressBar spinner;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_channels);
        adapter=null;

        lv1=(ListView)findViewById(R.id.channel_list);
        TextView desc = (TextView) findViewById(R.id.dd);
        desc.setText("Click red button to start broadcast"+Global.separator+"Click on eye button to make channel visible or invisible");
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        Global.set_action_bar_details(MyChannels_RV.this,"My Broadcasts","");


        // hideSystemUI();

        GetChannelResults_mod();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.ab_add_channel, menu);
        MenuItem add = menu.findItem(R.id.add);
        add.expandActionView();
        // Associate searchable configuration with the SearchView

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.add) {
            if(Global.isNetworkAvailable(MyChannels_RV.this)) {
                Intent intent = new Intent(MyChannels_RV.this, Add_Channel.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(MyChannels_RV.this,"No Active Internet Connection Found",Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if(id==android.R.id.home) {
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);



    }



    @Override
    public void onBackPressed() {


        Intent intent = new Intent(MyChannels_RV.this, Dashboard.class);
        startActivity(intent);
        finish();
    }



    private void GetChannelResults(){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();

        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels");
        user_ref.keepSynced(true);

        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null) {


                        try {
                            Map<String, Object> map = (Map<String, Object>) child.getValue();


                            Channel_list sr1 = new Channel_list();
                            if (map != null && map.get("owner") != null && map.get("vehicle_number") != null && map.get("category") != null && map.get("vtype") != null && map.get("visible") != null) {
                                sr1.setsName("Owner : " + map.get("owner").toString());
                                sr1.setChannelid("Channel Id :" + child.getKey());
                                sr1.setscategary("Category : " + map.get("category").toString() + Global.separator + "Refresh Rate: " + map.get("refresh_status").toString());
                                sr1.setsVnumber("Vehicle No. : " + map.get("vehicle_number").toString());
                                sr1.setsvtype("Vehicle Type : " + map.get("vtype").toString());
                                String act = map.get("visible").toString();
                                String status = map.get("status").toString();
                                if (act.equalsIgnoreCase("1")) {
                                    sr1.setvisibleimageid(visible_images[0]);

                                } else {
                                    sr1.setvisibleimageid(visible_images[1]);

                                }


                                if (status.equalsIgnoreCase("0")) {
                                    status_update("0", child.getKey());
                                    sr1.setImageid(images[1]);
                                    Global.channel_broadcasting_name = "NONE";
                                    Global.channel_broadcasting_vnumber = "NONE";

                                } else {
                                    if(!isMyServiceRunning(TimeServiceGPS.class))
                                    {
                                        status_update("0", child.getKey());
                                        sr1.setImageid(images[1]);
                                        Global.channel_broadcasting_name = "NONE";
                                        Global.channel_broadcasting_vnumber = "NONE";
                                    }
                                    else {
                                        status_update("1", child.getKey());
                                        sr1.setImageid(images[0]);
                                        Global.broadcasting = true;
                                        Global.ch_list_pos = position;
                                        Global.channel_broadcasting_name = map.get("owner").toString();
                                        Global.channel_broadcasting_vnumber = map.get("vehicle_number").toString();
                                    }

                                }
                                if (map.get("image") != null) {
                                    sr1.setImage(download_image_to_firebase1(map.get("image").toString()));
                                } else {
                                    sr1.setImage(download_image_to_firebase1("default"));
                                }

                                if (map.get("vehicle_name") != null) {
                                    sr1.setvname("vehicle name : " + map.get("vehicle_name").toString());
                                } else {
                                    sr1.setvname("vehicle name : NA");
                                }


                                results.add(sr1);
                                position++;
                            }


                        }
                        catch(ClassCastException ce)
                        {
                            Toast.makeText(MyChannels_RV.this,"Filtered few invalid Channels",Toast.LENGTH_LONG).show();
                        }
                    }
                }

                adapter = new Channel_list_view_adapter_mod(MyChannels_RV.this, results);
                lv1.setAdapter(adapter);
                adapter.setContext(MyChannels_RV.this);


                lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        if(Global.isNetworkAvailable(MyChannels_RV.this)) {
                            Object o = lv1.getItemAtPosition(position);
                            Channel_list fullObject = (Channel_list) o;
                            Toast.makeText(MyChannels_RV.this, "You have chosen: " + " " + fullObject.getsName() + Global.separator + fullObject.getsPhone(), Toast.LENGTH_LONG).show();
                            subscriber_invite = fullObject.getChannelid();
                            String subscriber = subscriber_invite.split(":")[1].trim();
                            //subscriber_name = fullObject.getsName();
                            Global.getUserdetails();
                            Intent i1 = new Intent(MyChannels_RV.this, Channel_settings.class);
                            i1.putExtra("subscriber", subscriber);
                            i1.putExtra("status", status);
                            startActivity(i1);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(MyChannels_RV.this,"No Internet connection found chekc wifi/mobile networks",Toast.LENGTH_LONG).show();
                        }


                    }
                });


                spinner.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MyChannels_RV.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });


    }


    private void GetChannelResults_mod(){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();

        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels");
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





                spinner.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MyChannels_RV.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });


    }




    private void getchanneldetails(final DataSnapshot child)
    {
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(child.getKey().toString()).child("status");
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


                        Channel_list sr1 = new Channel_list();
                        if (map != null && map.get("owner") != null && map.get("vehicle_number") != null && map.get("category") != null && map.get("vtype") != null && map.get("visible") != null) {
                            sr1.setsName("Owner : " + map.get("owner").toString());
                            sr1.setChannelid("Channel Id :" + child.getKey());
                            sr1.setscategary("Category : " + map.get("category").toString() + Global.separator + "Refresh Rate: " + map.get("refresh_status").toString());
                            sr1.setsVnumber("Vehicle No. : " + map.get("vehicle_number").toString());
                            sr1.setsvtype("Vehicle Type : " + map.get("vtype").toString());
                            String act = map.get("visible").toString();
                            //String status = map.get("status").toString();
                            if (act.equalsIgnoreCase("1")) {
                                sr1.setvisibleimageid(visible_images[0]);

                            } else {
                                sr1.setvisibleimageid(visible_images[1]);

                            }


                            if (status.equalsIgnoreCase("0")) {
                                status_update("0", child.getKey());
                                sr1.setImageid(images[1]);
                                Global.channel_broadcasting_name = "NONE";
                                Global.channel_broadcasting_vnumber = "NONE";

                            } else {
                                if (!Global.broadcasting) {
                                    status_update("0", child.getKey());
                                    sr1.setImageid(images[1]);
                                    Global.channel_broadcasting_name = "NONE";
                                    Global.channel_broadcasting_vnumber = "NONE";
                                } else {
                                    status_update("1", child.getKey());
                                    sr1.setImageid(images[0]);
                                    Global.broadcasting = true;
                                    Global.ch_list_pos = position;
                                    Global.channel_broadcasting_name = map.get("owner").toString();
                                    Global.channel_broadcasting_vnumber = map.get("vehicle_number").toString();
                                }

                            }
                            if (map.get("image") != null) {
                                sr1.setImage(download_image_to_firebase1(map.get("image").toString()));
                            } else {
                                sr1.setImage(download_image_to_firebase1("default"));
                            }

                            if (map.get("vehicle_name") != null) {
                                sr1.setvname("vehicle name : " + map.get("vehicle_name").toString());
                            } else {
                                sr1.setvname("vehicle name : NA");
                            }


                            results.add(sr1);
                            position++;
                        }


                    } catch (ClassCastException ce) {
                        Toast.makeText(MyChannels_RV.this, "Filtered few invalid Channels", Toast.LENGTH_LONG).show();
                    }

                }

                adapter = new Channel_list_view_adapter_mod(MyChannels_RV.this, results);
                lv1.setAdapter(adapter);
                adapter.setContext(MyChannels_RV.this);


                lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        if (Global.isNetworkAvailable(MyChannels_RV.this)) {
                            Object o = lv1.getItemAtPosition(position);
                            Channel_list fullObject = (Channel_list) o;
                            Toast.makeText(MyChannels_RV.this, "You have chosen: " + " " + fullObject.getsName() + Global.separator + fullObject.getsPhone(), Toast.LENGTH_LONG).show();
                            subscriber_invite = fullObject.getChannelid();
                            String subscriber = subscriber_invite.split(":")[1].trim();
                            //subscriber_name = fullObject.getsName();
                            Global.getUserdetails();
                            Intent i1 = new Intent(MyChannels_RV.this, Channel_settings.class);
                            i1.putExtra("subscriber", subscriber);
                            i1.putExtra("status", status);
                            startActivity(i1);
                            finish();
                        } else {
                            Toast.makeText(MyChannels_RV.this, "No Internet connection found chekc wifi/mobile networks", Toast.LENGTH_LONG).show();
                        }


                    }
                });
            }













            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MyChannels_RV.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });
    }


// --Commented out by Inspection START (01/12/16, 10:11 PM):
//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
// --Commented out by Inspection STOP (01/12/16, 10:11 PM)

    private void status_update(final String update,final String channelid)
    {
        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(channelid).child("followers");
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);

        if(user_ref!=null) {

            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        if (child != null) {

                            DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(child.getKey()).child("Subscribers").child(channelid).child("status");
                            ref.setValue(update);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MyChannels_RV.this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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


}
