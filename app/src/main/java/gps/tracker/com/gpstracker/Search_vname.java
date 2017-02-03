package gps.tracker.com.gpstracker;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class Search_vname extends Fragment {

    private TextView desc;


    private ListView lv2;
    // --Commented out by Inspection (01/12/16, 10:25 PM):ArrayList<Suscriber_results> results = new ArrayList<Suscriber_results>();
    private final ArrayList<Channel_search> search_results = new ArrayList<Channel_search>();
    private Channel_search_vname search_adapter;

    private int count=0,follower_count=0;
    private int LIMIT_SEARCH_RESULT=30;

    private String channel_mobile;
    private String channel_name;
    private String channel_vnumber;
    private String channel_vname;
    private String channel_invite;
    // --Commented out by Inspection (01/12/16, 10:26 PM):String channel_bmp;
    private String channel_category;
    private String channel_vtype;
    private String follower_set;
    private int MAX_CHANNEL_FOLLOWED_USER=40;
    private int MAX_FOLLOWER_COUNT=50;
    private ProgressBar spinner;
    MyReceiver r;
    View rootview;
    int search_type=1;

    public Search_vname() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        rootview =lf.inflate(R.layout.fragment_search_vname, container, false);
        lv2=(ListView)rootview.findViewById(R.id.search_list);
        desc=(TextView)rootview.findViewById(R.id.desc);
        //Channel_search sr1 = new Channel_search();
       // sr1.setName("");

        spinner=(ProgressBar)rootview.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        //search_results.add(sr1);
        // search_adapter.setContext(Search_channel.this);
        search_adapter = new Channel_search_vname(getActivity(), search_results);


        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                if(Global.isNetworkAvailable(getActivity())) {
//
                    Object o = lv2.getItemAtPosition(position);
                    Channel_search fullObject = (Channel_search) o;

                    channel_mobile = fullObject.getPhone();
                    channel_name = fullObject.getName();
                    channel_vnumber = fullObject.getVnumber();
                    channel_vname = fullObject.getsvname();
                    channel_invite = fullObject.getChannelid();
                    channel_category = fullObject.getvcategory();
                    channel_vtype = fullObject.getvtype();
                    follower_set=fullObject.getfollower();


                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    //new AlertDialog.Builder(getActivity())
                    builder
                            .setTitle("Subscribe Channel")
                            .setMessage("Are you sure you want Subscribe Channel " + channel_invite)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if (!Global.username.equalsIgnoreCase("") | !Global.username.equalsIgnoreCase(null) | !Global.username.equalsIgnoreCase("not valid")) {
                                        // continue with delete
                                        /*add_subscribe_details();
                                        add_follower_details();
                                        write_bmp_to_firebase();
                                        //write_image_to_firebase(bmp);
                                        lv2.setVisibility(View.GONE);
                                        Toast.makeText(Search_activity.this, "Channel is subscribed successfully ", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Search_activity.this, Dashboard.class);
                                        startActivity(intent);
                                        finish();*/

                                        int channel_count=Global.get_channel_count(getActivity());
                                        if(channel_count<MAX_CHANNEL_FOLLOWED_USER) {
                                            Global.save_channel_count(getActivity(), channel_count+1);
                                            subscribe_channel();
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(),"You have reached maximum subscribers limits either switch to premium plan or delete some channel to add new",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                {
                    Toast.makeText(getActivity(),"No Active Network Connection Found",Toast.LENGTH_LONG).show();
                }

            }
        });
        //show_search_results();
        return rootview;

    }

    public void show_search_results()
    {
        if(!Global.search_string.equalsIgnoreCase("NA"))
        {
            spinner.setVisibility(View.VISIBLE);
            GetChannelSearchResults(Global.search_string,Global.search_type);
        }
    }

    private void GetChannelSearchResults(final String query,final int search){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("vehicle_name").startAt(query).endAt(query+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count=0;
                search_results.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null ) {
                        if (count <= LIMIT_SEARCH_RESULT) {
                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            if (map != null && map.get("owner") != null && map.get("vehicle_number") != null && map.get("vehicle_name") != null && map.get("mobile") != null && map.get("city") != null) {
                                if (map.get("visible").toString().equalsIgnoreCase("1")) {
                                    if (search == 1) {
                                        if (Global.city.equalsIgnoreCase(map.get("city").toString())) {
                                            Channel_search sr1 = new Channel_search();
                                            sr1.setName("Name : " + map.get("owner").toString());
                                            sr1.setChannelid("Channel Id :" + child.getKey());
                                            sr1.setPhone("Mobile No. : " + map.get("mobile").toString());
                                            sr1.setVnumber("Viehicle No. : " + map.get("vehicle_number").toString());
                                            sr1.setvname("Vehicle Name : " + map.get("vehicle_name").toString());
                                            sr1.setCity("City : " + map.get("city").toString());
                                            if (map.get("category") != null) {
                                                sr1.setvcategory(map.get("category").toString());
                                            } else {
                                                sr1.setvcategory(map.get("NA").toString());
                                            }
                                            if (map.get("vtype") != null) {
                                                sr1.setvtype(map.get("vtype").toString());
                                            } else {
                                                sr1.setvtype(map.get("NA").toString());
                                            }
                                            if (map.get("follower_setting") != null) {
                                                sr1.setfollower(map.get("follower_setting").toString());
                                            } else {
                                                sr1.setfollower("0");
                                            }

                                            //Toast.makeText(Search_channel.this,"item added to search list"+String.valueOf(count),Toast.LENGTH_LONG).show();
                                            search_results.add(sr1);
                                            count++;
                                            desc.setText("Showing results for your City : " + String.valueOf(count) + " results found");

                                        }
                                    } else {
                                        Channel_search sr1 = new Channel_search();
                                        sr1.setName("Name : " + map.get("owner").toString());
                                        sr1.setChannelid("Channel Id :" + child.getKey());
                                        sr1.setPhone("Mobile No. : " + map.get("mobile").toString());
                                        sr1.setVnumber("Viehicle No. : " + map.get("vehicle_number").toString());
                                        sr1.setvname("Vehicle Name : " + map.get("vehicle_name").toString());
                                        sr1.setCity("City : " + map.get("city").toString());
                                        if (map.get("category") != null) {
                                            sr1.setvcategory(map.get("category").toString());
                                        } else {
                                            sr1.setvcategory(map.get("NA").toString());
                                        }
                                        if (map.get("vtype") != null) {
                                            sr1.setvtype(map.get("vtype").toString());
                                        } else {
                                            sr1.setvtype(map.get("NA").toString());
                                        }
                                        if (map.get("follower_setting") != null) {
                                            sr1.setfollower(map.get("follower_setting").toString());
                                        } else {
                                            sr1.setfollower("0");
                                        }

                                        //Toast.makeText(Search_channel.this,"item added to search list"+String.valueOf(count),Toast.LENGTH_LONG).show();
                                        search_results.add(sr1);
                                        count++;
                                        desc.setText("Showing all results for your search : " + String.valueOf(count) + " results found");

                                    }
                                }
                            }

                        } else {
                            desc.setText("More Results available....Limiting search to 30 results only...Refine your search String");
                        }
                    }
                    else if(count==0)
                    {
                        desc.setText("No results found for your query... Try Again");

                    }

                }




                search_adapter = new Channel_search_vname(getActivity(), search_results);
                search_adapter.notifyDataSetChanged();
                lv2.setAdapter(search_adapter);
                lv2.invalidateViews();
                //search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);

                //search_button.setEnabled(true);



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Toast.makeText(Search_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();
                //search_button.setEnabled(true);
            }
        });

    }

    public void refresh() {
        //yout code in refresh.
        show_search_results();
       // search_adapter.notifyDataSetChanged();
      //  lv2.invalidateViews();

        Log.i("Refresh", "YES");
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Search_vname.this.refresh();
            //search_adapter.notifyDataSetChanged();

        }
    }


    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new Search_vname.MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r,
                new IntentFilter("TAG_REFRESH"));
       // search_results.add(sr1);
        // search_adapter.setContext(Search_channel.this);


    }


    private void subscribe_channel()
    {
        try {
            DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channel_invite.split(":")[1].trim()).child("followers");
            user_ref.keepSynced(true);
            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    follower_count = 0;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        if (child != null) {
                            follower_count++;
                        }

                    }
                    if (follower_count <= MAX_FOLLOWER_COUNT) {
                        add_subscribe_details();
                        add_follower_details();
                        write_bmp_to_firebase();
                        //write_image_to_firebase(bmp);
                        lv2.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Channel is subscribed successfully ", Toast.LENGTH_LONG).show();
                        //Intent intent = new Intent(getActivity(), Dashboard.class);
                        //startActivity(intent);
                        //finish();
                    } else {
                        Toast.makeText(getActivity(), "Cannot follow this channel as it exceeds the maximum number of follwers limit", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getActivity(), error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });
        }catch(Exception e){
            Toast.makeText(getActivity(),"Fatal Error while Subscribing a channel",Toast.LENGTH_LONG).show();
        }

    }


    private void add_subscribe_details()
    {
        DatabaseReference userdata = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(channel_invite.split(":")[1].trim()).child("name");
        userdata.setValue(channel_name.split(":")[1].trim());
        DatabaseReference userdata1 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(channel_invite.split(":")[1].trim()).child("vehicle_number");
        userdata1.setValue(channel_vnumber.split(":")[1].trim());
        DatabaseReference userdata3 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(channel_invite.split(":")[1].trim()).child("vname");
        userdata3.setValue(channel_vname.split(":")[1].trim());
        DatabaseReference userdata4 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(channel_invite.split(":")[1].trim()).child("active");
        userdata4.setValue("1");
        DatabaseReference userdata5 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(channel_invite.split(":")[1].trim()).child("status");
        userdata5.setValue("0");
        DatabaseReference userdata6 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(channel_invite.split(":")[1].trim()).child("mobile");
        userdata6.setValue(channel_mobile.split(":")[1].trim());
        DatabaseReference userdata7 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(channel_invite.split(":")[1].trim()).child("vtype");
        userdata7.setValue(channel_vtype);
        DatabaseReference userdata8 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(channel_invite.split(":")[1].trim()).child("category");
        userdata8.setValue(channel_category);
        DatabaseReference userdata9 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(channel_invite.split(":")[1].trim()).child("unblock");
        userdata9.setValue(follower_set);

    }

    private void add_follower_details()
    {
        DatabaseReference userdata = Global.firebase_dbreference.child("CHANNELS").child(channel_invite.split(":")[1].trim()).child("followers").child(Global.username).child("name");
        userdata.setValue(Global.user_desc_name);
        DatabaseReference userdata2 = Global.firebase_dbreference.child("CHANNELS").child(channel_invite.split(":")[1].trim()).child("followers").child(Global.username).child("unblock");
        userdata2.setValue(follower_set);
        DatabaseReference userdata1 = Global.firebase_dbreference.child("USERS").child(channel_invite.split(":")[1].trim()).child("followers").child(Global.username).child("unblock");
        userdata1.setValue(follower_set);

    }



    private void write_bmp_to_firebase() {
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channel_invite.split(":")[1].trim()).child("image");
        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot!=null) {
                    DatabaseReference userdata6 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(channel_invite.split(":")[1].trim()).child("image");
                    userdata6.setValue(dataSnapshot.getValue());

                }


            }






            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

    }




}
