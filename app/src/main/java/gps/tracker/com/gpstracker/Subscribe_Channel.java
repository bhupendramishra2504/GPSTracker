package gps.tracker.com.gpstracker;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Base64;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;


public class Subscribe_Channel extends AppCompatActivity {
    private final ArrayList<SearchResults> results = new ArrayList<SearchResults>();
    // --Commented out by Inspection (01/12/16, 10:29 PM):ArrayList<SearchResults> search_results = new ArrayList<SearchResults>();

    private ListView lv1;
    private ArrayList<SearchResults> searchResults;
    // --Commented out by Inspection (01/12/16, 10:30 PM):private EditText search;
    private MyCustomBaseAdapter adapter;
    private MyCustomBaseAdapter adapter1;
    private ArrayList<SearchResults> tempArrayList;
    private String subscriber_invite;
    private String subscriber_name;
    private String subscriber_vnumber;
    private String subscriber_vname;
    private String subscriber_mobile;
    private FirebaseDatabase firebase_database;
    // --Commented out by Inspection (01/12/16, 10:30 PM):private DatabaseReference firebase_dbreference;
    private ProgressBar spinner;
    // --Commented out by Inspection (01/12/16, 10:30 PM):Bitmap bitmap;
    private Bitmap bmp;
    // --Commented out by Inspection (01/12/16, 10:30 PM):private boolean downloaded=false;
    private SearchResults sr1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe__channel);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        firebase_database = FirebaseDatabase.getInstance();
        //firebase_dbreference=firebase_database.getReference("JustIn");
        Global.set_action_bar_details(Subscribe_Channel.this,"JoinIn-Subscribe Channel","[ "+Global.username+" ]");
        lv1 = (ListView) findViewById(R.id.user_subscribe_list);
        //search=(EditText)findViewById(R.id.ssearch);
        searchResults = GetSearchResults();
        tempArrayList = new ArrayList<SearchResults>();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.subscribe_search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.subscribe_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String query)
            {
                // this is your adapter that will be filtered
                tempArrayList.clear();
                lv1.setVisibility(View.VISIBLE);
                //Toast.makeText(Subscribe_Channel.this,"listener worked",Toast.LENGTH_LONG).show();
                if(query.equals(null)|query.equals(""))
                {
                    tempArrayList.addAll(results);
                    adapter.notifyDataSetChanged();
                    //Toast.makeText(Subscribe_Channel.this,"you reached null result",Toast.LENGTH_LONG).show();

                }
                else {
                    for (SearchResults c : results) {
                        if (c.getName().toLowerCase().contains(query)|c.getVnumber().toLowerCase().contains(query.toLowerCase())|c.getPhone().toLowerCase().contains(query.toLowerCase())|c.getVname().toLowerCase().contains(query.toLowerCase())|c.getChannelid().toLowerCase().contains(query.toLowerCase())) {
                            tempArrayList.add(c);
                            // Toast.makeText(Subscribe_Channel.this,"Item Added",Toast.LENGTH_LONG).show();
                        }

                    }
                }
                adapter1 = new MyCustomBaseAdapter(Subscribe_Channel.this, tempArrayList);
                adapter1.notifyDataSetChanged();
                lv1.setAdapter(adapter1);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered

                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);




        return true;
    }




    private ArrayList<SearchResults> GetSearchResults(){

        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS");
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){


                        Map<String, Object> map = (Map<String, Object>) child.getValue();
                        sr1 = new SearchResults();
                        sr1.setName("Owner : "+map.get("owner").toString());
                        sr1.setPhone("Mobile No: "+map.get("mobile").toString());
                        sr1.setVnumber("Vehicle No : "+map.get("vehicle_number").toString());
                        sr1.setVname("Vehicle Name : "+map.get("vehicle_name").toString());
                        sr1.setChannelid("Channel Id: "+ child.getKey());
                        if(map.get("image")!=null) {
                            sr1.setImage(download_image_to_firebase1(map.get("image").toString()));
                        }
                        else
                        {
                            sr1.setImage(download_image_to_firebase1("default"));
                        }



                       // sr1.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.default_photo));
                        results.add(sr1);
                       // downloaded=false;

                    }

                }
                adapter=new MyCustomBaseAdapter(Subscribe_Channel.this, searchResults);
                lv1.setAdapter(adapter);
                spinner.setVisibility(View.GONE);

                lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        Object o = lv1.getItemAtPosition(position);
                        SearchResults fullObject = (SearchResults)o;
                       // Toast.makeText(Subscribe_Channel.this, "You have chosen: " + " " + fullObject.getName()+Global.separator+fullObject.getPhone()+Global.separator+fullObject.getVname(), Toast.LENGTH_LONG).show();
                        subscriber_mobile=fullObject.getPhone();
                        subscriber_name=fullObject.getName();
                        subscriber_vnumber=fullObject.getVnumber();
                        subscriber_vname=fullObject.getVname();
                        subscriber_invite=fullObject.getChannelid();
                        bmp=fullObject.getImage();

                        new AlertDialog.Builder(Subscribe_Channel.this)
                                .setTitle("Subscribe Channel")
                                .setMessage("Are you sure you want Subscribe Channel "+subscriber_invite)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        add_subscribe_details();
                                        add_follower_details();
                                        write_image_to_firebase(bmp);
                                        Toast.makeText(Subscribe_Channel.this,"Channel is subscribed successfully ", Toast.LENGTH_LONG).show();

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
                });



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Subscribe_Channel.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });




        return results;
    }


    private void add_subscribe_details()
    {
        DatabaseReference userdata = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(subscriber_invite.split(":")[1].trim()).child("name");
        userdata.setValue(subscriber_name.split(":")[1].trim());
        DatabaseReference userdata1 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(subscriber_invite.split(":")[1].trim()).child("vehicle_number");
        userdata1.setValue(subscriber_vnumber.split(":")[1].trim());
        DatabaseReference userdata3 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(subscriber_invite.split(":")[1].trim()).child("vname");
        userdata3.setValue(subscriber_vname.split(":")[1].trim());
        DatabaseReference userdata4 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(subscriber_invite.split(":")[1].trim()).child("active");
        userdata4.setValue("0");
        DatabaseReference userdata5 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(subscriber_invite.split(":")[1].trim()).child("status");
        userdata5.setValue("0");
        DatabaseReference userdata6 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(subscriber_invite.split(":")[1].trim()).child("mobile");
        userdata6.setValue(subscriber_mobile.split(":")[1].trim());



    }

    private void add_follower_details()
    {
        DatabaseReference userdata = Global.firebase_dbreference.child("CHANNELS").child(subscriber_invite.split(":")[1].trim()).child("followers").child(Global.username).child("name");
        userdata.setValue(Global.user_desc_name);
        DatabaseReference userdata1 = Global.firebase_dbreference.child("USERS").child(subscriber_invite.split(":")[1].trim()).child("followers").child(Global.username).child("active");
        userdata1.setValue("1");

    }



    private class Add_subscribe_class extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {

            add_subscribe_details();
            add_follower_details();
            return "ok";
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            Toast.makeText(Subscribe_Channel.this,"Channel is subscribed successfully ", Toast.LENGTH_LONG).show();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {

            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }


  @Override
    public void onBackPressed() {


        Intent intent = new Intent(Subscribe_Channel.this, Dashboard.class);
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
          // downloaded=true;
       }
        else
       {
           bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_photo);
          // downloaded=true;
       }

        return bitmap;
    }

    private void write_image_to_firebase(Bitmap b)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        DatabaseReference userdata6 = Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(subscriber_invite.split(":")[1].trim()).child("image");
        String data_string=Base64.encodeToString(data,Base64.DEFAULT);
        userdata6.setValue(data_string);

        }










}
