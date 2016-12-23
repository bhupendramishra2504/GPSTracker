package gps.tracker.com.gpstracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

public class Search_activity extends AppCompatActivity {

    private TextView desc;

    private Button search_type;
    private Button search;
    private EditText search_string;
    //ListView lv1;
    private ListView lv2;
    // --Commented out by Inspection (01/12/16, 10:25 PM):ArrayList<Suscriber_results> results = new ArrayList<Suscriber_results>();
    private final ArrayList<Channel_search> search_results = new ArrayList<Channel_search>();
    private Channel_search_list_view search_adapter;
    // --Commented out by Inspection (01/12/16, 10:26 PM):String status="offline";
    private PopupMenu popup;
    private ProgressBar spinner;
    // --Commented out by Inspection (01/12/16, 10:26 PM):SearchManager searchManager;
    // --Commented out by Inspection (01/12/16, 10:26 PM):SearchView searchView;
    // --Commented out by Inspection (01/12/16, 10:26 PM):ImageView add_channel;
    private int count=0;
    private int LIMIT_SEARCH_RESULT=30;
    private String name;
    private String id;
    private String channel_mobile;
    private String channel_name;
    private String channel_vnumber;
    private String channel_vname;
    private String channel_invite;
    // --Commented out by Inspection (01/12/16, 10:26 PM):String channel_bmp;
    private String channel_category;
    private String channel_vtype;
    private String follower_set;
    // --Commented out by Inspection (01/12/16, 10:26 PM):ActionBar ab;
    // --Commented out by Inspection (01/12/16, 10:26 PM):private int search_filter=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_activity);
        lv2=(ListView)findViewById(R.id.search_list);
        search_type=(Button)findViewById(R.id.stype);
        search_string=(EditText) findViewById(R.id.search_string);
        search=(Button)findViewById(R.id.search);
        desc=(TextView)findViewById(R.id.desc);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        Global.set_action_bar_details(Search_activity.this,"Search","");


        Channel_search sr1 = new Channel_search();
        sr1.setName("Search Results");



        search_results.add(sr1);
        // search_adapter.setContext(Search_channel.this);
        search_adapter = new Channel_search_list_view(Search_activity.this, search_results);

        search_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popup=new PopupMenu(Search_activity.this,search_type);
                popup.getMenu().add("By Owner Name");
                popup.getMenu().add("By City");
                popup.getMenu().add("By Mobile");
                popup.getMenu().add("By Vehicle Number");
                popup.getMenu().add("By Vehicle Name");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        search_type.setText(item.getTitle());
                        return true;
                    }
                });

                popup.show();

            }
        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count=0;
                search.requestFocus();
                search_string.clearFocus();
                spinner.setVisibility(View.VISIBLE);
                search_results.clear();
                desc.setText("No result found for your search query.... Try Again....");

                search_adapter.notifyDataSetChanged();
                if(search.getText().toString().equalsIgnoreCase("Search your city"))
                {

                    if(search_type.getText().toString().equalsIgnoreCase("By Vehicle Name"))
                    {
                        GetChannelSearchResults(search_string.getText().toString().toLowerCase(),1);
                    }
                    else if(search_type.getText().toString().equalsIgnoreCase("By Vehicle Number"))
                    {
                        GetChannelSearchResults_vnumber(search_string.getText().toString().toLowerCase(),1);
                    }
                    else if(search_type.getText().toString().equalsIgnoreCase("By Mobile"))
                    {
                        GetChannelSearchResults_mobile(search_string.getText().toString().toLowerCase(),1);
                    }
                    else if(search_type.getText().toString().equalsIgnoreCase("By Owner Name"))
                    {
                        GetChannelSearchResults_owner(search_string.getText().toString().toLowerCase(),1);
                    }
                    else if(search_type.getText().toString().equalsIgnoreCase("By City"))
                    {
                        GetChannelSearchResults_city(search_string.getText().toString().toLowerCase(),1);
                    }

                    else
                    {
                        spinner.setVisibility(View.GONE);
                        Toast.makeText(Search_activity.this,"No Search Result Found",Toast.LENGTH_LONG).show();
                    }
                    search.setText("Search All");
                    //search_filter=2;
                }
                else
                {
                    if(search_type.getText().toString().equalsIgnoreCase("By Vehicle Name"))
                    {
                        GetChannelSearchResults(search_string.getText().toString().toLowerCase(),2);
                    }

                    else if(search_type.getText().toString().equalsIgnoreCase("By Vehicle Number"))
                {
                    GetChannelSearchResults_vnumber(search_string.getText().toString().toLowerCase(),2);
                }
                else if(search_type.getText().toString().equalsIgnoreCase("By Mobile"))
                {
                    GetChannelSearchResults_mobile(search_string.getText().toString().toLowerCase(),2);
                }
                else if(search_type.getText().toString().equalsIgnoreCase("By Owner Name"))
                {
                    GetChannelSearchResults_owner(search_string.getText().toString().toLowerCase(),2);
                }
                else if(search_type.getText().toString().equalsIgnoreCase("By City"))
                {
                    GetChannelSearchResults_city(search_string.getText().toString().toLowerCase(),2);
                }
                    else
                    {
                        spinner.setVisibility(View.GONE);
                        Toast.makeText(Search_activity.this,"No Search Result Found",Toast.LENGTH_LONG).show();
                    }
                    search.setText("Search your city");
                    desc.setText("Showing all results for your search : "+String.valueOf(count)+" results found");
                    //search_filter=1;
                }

            }
        });




        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                if(Global.isNetworkAvailable(Search_activity.this)) {
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


                    new AlertDialog.Builder(Search_activity.this)
                            .setTitle("Subscribe Channel")
                            .setMessage("Are you sure you want Subscribe Channel " + channel_invite)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if (!Global.username.equalsIgnoreCase("") | !Global.username.equalsIgnoreCase(null) | !Global.username.equalsIgnoreCase("not valid")) {
                                        // continue with delete
                                        add_subscribe_details();
                                        add_follower_details();
                                        write_bmp_to_firebase();
                                        //write_image_to_firebase(bmp);
                                        lv2.setVisibility(View.GONE);
                                        Toast.makeText(Search_activity.this, "Channel is subscribed successfully ", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Search_activity.this, Dashboard.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        authenticate();
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
                    Toast.makeText(Search_activity.this,"No Active Network Connection Found",Toast.LENGTH_LONG).show();
                }

            }
        });


        //lv2.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ab_search_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, Dashboard.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void GetChannelSearchResults(final String query,final int search){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("vehicle_name").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count=0;
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




                //search_adapter = new Channel_search_list_view(Search_channel.this, search_results);
                lv2.setAdapter(search_adapter);
                //search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);





            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Search_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }
    private void GetChannelSearchResults_city(final String query,final int search){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("city").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               count=0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null) {

                        if (count <= LIMIT_SEARCH_RESULT) {

                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            if (map != null && map.get("owner") != null && map.get("vehicle_number") != null && map.get("vehicle_name") != null && map.get("vehicle_number") != null && map.get("mobile") != null && map.get("city") != null) {
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

                }




                //search_adapter = new Channel_search_list_view(Search_channel.this, search_results);
                lv2.setAdapter(search_adapter);
                // search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);




            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Search_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void GetChannelSearchResults_mobile(final String query,final int search){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("mobile").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count=0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null) {

                        if (count <= LIMIT_SEARCH_RESULT) {

                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            if (map != null && map.get("owner") != null && map.get("vehicle_number") != null && map.get("vehicle_name") != null && map.get("vehicle_number") != null && map.get("mobile") != null && map.get("city") != null) {
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
                }




                //search_adapter = new Channel_search_list_view(Search_channel.this, search_results);
                lv2.setAdapter(search_adapter);
                // search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);




            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Search_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void GetChannelSearchResults_owner(final String query,final int search){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("owner").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count=0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null) {

                        if (count <= LIMIT_SEARCH_RESULT) {

                            //count++;

                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            if (map != null && map.get("owner") != null && map.get("vehicle_number") != null && map.get("vehicle_name") != null && map.get("vehicle_number") != null && map.get("mobile") != null && map.get("city") != null) {
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




                //search_adapter = new Channel_search_list_view(Search_channel.this, search_results);
                lv2.setAdapter(search_adapter);
                // search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);




            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Search_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void GetChannelSearchResults_vnumber(final String query,final int search) {
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("vehicle_number").startAt(query).endAt(query + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null) {

                        if (count <= LIMIT_SEARCH_RESULT) {
                            //count++;

                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            if (map != null && map.get("owner") != null && map.get("vehicle_number") != null && map.get("vehicle_name") != null && map.get("vehicle_number") != null && map.get("mobile") != null && map.get("city") != null) {
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

                }


                //search_adapter = new Channel_search_list_view(Search_channel.this, search_results);
                lv2.setAdapter(search_adapter);
                // search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Search_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });
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

                        String rx_id = map.get("id").toString();
                        String user_desc = map.get("name").toString();


                        Toast.makeText(Search_activity.this, rx_id, Toast.LENGTH_LONG).show();

                        if (id.equals(rx_id)) {
                            Global.username = name;
                            Global.user_desc_name = user_desc;
                            Global.dob = map.get("dob").toString();
                            //Global.gender = map.get("gender").toString();
                            Global.city = map.get("city").toString();
                            //Global.country = map.get("country").toString();
                            //Global.read_refresh_rate();

                        } else {
                            Intent intent = new Intent(Search_activity.this, Register.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(Search_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });

        } else {
            Intent intent = new Intent(Search_activity.this, Register.class);
            startActivity(intent);
            finish();
        }


    }




    @Override
    public void onBackPressed() {


        Intent intent = new Intent(Search_activity.this, Dashboard.class);
        startActivity(intent);
        finish();
    }

}









