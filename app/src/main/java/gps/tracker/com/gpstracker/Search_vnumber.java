package gps.tracker.com.gpstracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
public class Search_vnumber extends Fragment {
    private TextView desc;


    private ListView lv2;
    // --Commented out by Inspection (01/12/16, 10:25 PM):ArrayList<Suscriber_results> results = new ArrayList<Suscriber_results>();
    private final ArrayList<Channel_search> search_results = new ArrayList<Channel_search>();
    private Channel_search_list_view search_adapter;

    private int count=0,follower_count=0;
    private int LIMIT_SEARCH_RESULT=30;



    public Search_vnumber() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View rootview =lf.inflate(R.layout.fragment_search_vnumber, container, false);
        desc=(TextView)rootview.findViewById(R.id.desc);
        Channel_search sr1 = new Channel_search();
        sr1.setName("Search Results");



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
            GetChannelSearchResults_vnumber(Global.search_string,2);
        }
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
              //  spinner.setVisibility(View.GONE);
               // search_button.setEnabled(true);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Toast.makeText(Search_activity.this, error.toException().toString(), Toast.LENGTH_LONG).show();
                //search_button.setEnabled(true);
            }
        });
    }


}
