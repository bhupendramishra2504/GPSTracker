package gps.tracker.com.gpstracker;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Search_channel extends AppCompatActivity {
    // --Commented out by Inspection (01/12/16, 10:28 PM):ArrayList<Channel_search> results = new ArrayList<Channel_search>();
    private ProgressBar spinner;
    private ListView lv1;
    private final ArrayList<Channel_search> search_results = new ArrayList<Channel_search>();
    // --Commented out by Inspection (01/12/16, 10:28 PM):public Channel_search_list_view adapter,adapter1;
    private Channel_search_list_view search_adapter;
    // --Commented out by Inspection (01/12/16, 10:29 PM):private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_channel);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        Global.set_action_bar_details(Search_channel.this,"Search Channel","");
        Channel_search sr1 = new Channel_search();
        sr1.setName("Search Results");

        search_results.add(sr1);
       // search_adapter.setContext(Search_channel.this);
        search_adapter = new Channel_search_list_view(Search_channel.this, search_results);

        lv1 = (ListView) findViewById(R.id.user_list);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lv1.getItemAtPosition(position);
                //Suscriber_results fullObject = (Suscriber_results) o;
                // Toast.makeText(Dashboard.this, "You have chosen: " + " " + fullObject.getsName()+Global.separator+fullObject.getsPhone(), Toast.LENGTH_LONG).show();


            }
        });

        //search_adapter.setContext(Search_channel.this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.subscribe_search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.subscribe_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String query)
            {


                // this is your adapter that will be filtered
                /*tempArrayList.clear();
                //lv1.setVisibility(View.VISIBLE);
                //Toast.makeText(Subscribe_Channel.this,"listener worked",Toast.LENGTH_LONG).show();
                if(query.equals(null)|query.equals(""))
                {
                    tempArrayList.addAll(results);
                    adapter.notifyDataSetChanged();
                    //Toast.makeText(Subscribe_Channel.this,"you reached null result",Toast.LENGTH_LONG).show();

                }
                else {
                    for (Suscriber_results c : results) {
                        if (c.getsName().toLowerCase().contains(query)|c.getsVnumber().toLowerCase().contains(query)|c.getsPhone().toLowerCase().contains(query)|c.getsvname().toLowerCase().contains(query)|c.getChannelid().toLowerCase().contains(query)) {
                            tempArrayList.add(c);
                            // Toast.makeText(Subscribe_Channel.this,"Item Added",Toast.LENGTH_LONG).show();
                        }

                    }
                }
                adapter1 = new Subscriber_list_view_adapter(Dashboard.this, tempArrayList);
                adapter1.notifyDataSetChanged();
                lv1.setAdapter(adapter1);*/
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                search_results.clear();
                if(!query.equalsIgnoreCase("") && !query.equalsIgnoreCase(null)) {
                    GetChannelSearchResults(query);
                    GetChannelSearchResults_city(query);
                    GetChannelSearchResults_mobile(query);
                    GetChannelSearchResults_owner(query);
                    GetChannelSearchResults_vnumber(query);
                    //count=0;
                }
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);




        return true;
    }



    private void GetChannelSearchResults(final String query){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("vehicle_name").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){



                        //count++;
                        Map<String, Object> map = (Map<String, Object>) child.getValue();
                        Channel_search sr1 = new Channel_search();
                              sr1.setName("Name : " + map.get("owner").toString());
                            sr1.setChannelid("Channel Id :" + child.getKey());
                            sr1.setPhone("Mobile No. : "+map.get("mobile").toString());
                            sr1.setVnumber("Viehicle No. : " + map.get("vehicle_number").toString());
                            sr1.setvname("Vehicle Name : "+map.get("vehicle_name").toString());
                            sr1.setCity("City : "+map.get("city").toString());
                            //Toast.makeText(Search_channel.this,"item added to search list"+String.valueOf(count),Toast.LENGTH_LONG).show();
                            search_results.add(sr1);

                    }

                }




               //search_adapter = new Channel_search_list_view(Search_channel.this, search_results);
                lv1.setAdapter(search_adapter);
                search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);




        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Toast.makeText(Search_channel.this, error.toException().toString(), Toast.LENGTH_LONG).show();

        }
    });

}
    private void GetChannelSearchResults_city(final String query){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("city").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){



                        //count++;
                        Map<String, Object> map = (Map<String, Object>) child.getValue();
                        Channel_search sr1 = new Channel_search();
                        sr1.setName("Name : " + map.get("owner").toString());
                        sr1.setChannelid("Channel Id :" + child.getKey());
                        sr1.setPhone("Mobile No. : "+map.get("mobile").toString());
                        sr1.setVnumber("Viehicle No. : " + map.get("vehicle_number").toString());
                        sr1.setvname("Vehicle Name : "+map.get("vehicle_name").toString());
                        sr1.setCity("City : "+map.get("city").toString());
                        //Toast.makeText(Search_channel.this,"item added to search list"+String.valueOf(count),Toast.LENGTH_LONG).show();
                        search_results.add(sr1);

                    }

                }




                //search_adapter = new Channel_search_list_view(Search_channel.this, search_results);
                lv1.setAdapter(search_adapter);
               // search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);




            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Search_channel.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void GetChannelSearchResults_mobile(final String query){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("mobile").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){



                        //count++;
                        Map<String, Object> map = (Map<String, Object>) child.getValue();
                        Channel_search sr1 = new Channel_search();
                        sr1.setName("Name : " + map.get("owner").toString());
                        sr1.setChannelid("Channel Id :" + child.getKey());
                        sr1.setPhone("Mobile No. : "+map.get("mobile").toString());
                        sr1.setVnumber("Viehicle No. : " + map.get("vehicle_number").toString());
                        sr1.setvname("Vehicle Name : "+map.get("vehicle_name").toString());
                        sr1.setCity("City : "+map.get("city").toString());
                        //Toast.makeText(Search_channel.this,"item added to search list"+String.valueOf(count),Toast.LENGTH_LONG).show();
                        search_results.add(sr1);

                    }

                }




                //search_adapter = new Channel_search_list_view(Search_channel.this, search_results);
                lv1.setAdapter(search_adapter);
                // search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);




            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Search_channel.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void GetChannelSearchResults_owner(final String query){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("owner").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){



                        //count++;
                        Map<String, Object> map = (Map<String, Object>) child.getValue();
                        Channel_search sr1 = new Channel_search();
                        sr1.setName("Name : " + map.get("owner").toString());
                        sr1.setChannelid("Channel Id :" + child.getKey());
                        sr1.setPhone("Mobile No. : "+map.get("mobile").toString());
                        sr1.setVnumber("Viehicle No. : " + map.get("vehicle_number").toString());
                        sr1.setvname("Vehicle Name : "+map.get("vehicle_name").toString());
                        sr1.setCity("City : "+map.get("city").toString());
                        //Toast.makeText(Search_channel.this,"item added to search list"+String.valueOf(count),Toast.LENGTH_LONG).show();
                        search_results.add(sr1);

                    }

                }




                //search_adapter = new Channel_search_list_view(Search_channel.this, search_results);
                lv1.setAdapter(search_adapter);
                // search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);




            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Search_channel.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void GetChannelSearchResults_vnumber(final String query){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();
        //lv2.setVisibility(View.VISIBLE);
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");

        user_ref.orderByChild("vehicle_number").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){



                        //count++;
                        Map<String, Object> map = (Map<String, Object>) child.getValue();
                        Channel_search sr1 = new Channel_search();
                        sr1.setName("Name : " + map.get("owner").toString());
                        sr1.setChannelid("Channel Id :" + child.getKey());
                        sr1.setPhone("Mobile No. : "+map.get("mobile").toString());
                        sr1.setVnumber("Viehicle No. : " + map.get("vehicle_number").toString());
                        sr1.setvname("Vehicle Name : "+map.get("vehicle_name").toString());
                        sr1.setCity("City : "+map.get("city").toString());
                        //Toast.makeText(Search_channel.this,"item added to search list"+String.valueOf(count),Toast.LENGTH_LONG).show();
                        search_results.add(sr1);

                    }

                }




                //search_adapter = new Channel_search_list_view(Search_channel.this, search_results);
                lv1.setAdapter(search_adapter);
                // search_adapter.setContext(Search_channel.this);
                spinner.setVisibility(View.GONE);




            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Search_channel.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }




}
