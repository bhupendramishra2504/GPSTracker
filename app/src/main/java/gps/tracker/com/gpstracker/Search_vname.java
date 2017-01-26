package gps.tracker.com.gpstracker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Channel_search_list_view search_adapter;

    private int count=0,follower_count=0;
    private int LIMIT_SEARCH_RESULT=30;
    private ProgressBar spinner;
    MyReceiver r;


    public Search_vname() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View rootview =lf.inflate(R.layout.fragment_search_vname, container, false);
        lv2=(ListView)rootview.findViewById(R.id.search_list);
        desc=(TextView)rootview.findViewById(R.id.desc);
        Channel_search sr1 = new Channel_search();
        sr1.setName("");

        spinner=(ProgressBar)rootview.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        search_results.add(sr1);
        // search_adapter.setContext(Search_channel.this);
        search_adapter = new Channel_search_list_view(getActivity(), search_results);
        show_search_results();
        return rootview;

    }

    public void show_search_results()
    {
        if(!Global.search_string.equalsIgnoreCase("NA"))
        {
            spinner.setVisibility(View.VISIBLE);
            GetChannelSearchResults(Global.search_string,2);
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




                search_adapter = new Channel_search_list_view(getActivity(), search_results);
                search_adapter.notifyDataSetChanged();
                lv2.setAdapter(search_adapter);
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
        search_adapter.notifyDataSetChanged();
        lv2.invalidateViews();

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
    }


}
