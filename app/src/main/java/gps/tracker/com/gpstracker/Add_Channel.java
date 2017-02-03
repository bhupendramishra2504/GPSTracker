package gps.tracker.com.gpstracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Add_Channel extends AppCompatActivity {
    private Button vtype;
    private Button category;
    private Button withincity;
    private Button refresh_rate;
    private PopupMenu popup,popup_travel,popup_category,popup_refresh;
    private EditText owner,vname,vnumber,city1;
    private ImageView add_pic;
    private final int SELECT_PICTURE=100;
    private Bitmap resized;
    private String channel_id;
    private boolean validated1=false,validated2=false,validated3=false,validated4=false;
    private RadioGroup rg;
    String [] city_list_string;
    ListView city_list;
    ArrayAdapter<String> adapter;
    //Spinner spinnerUse, spinnerVehicleType, spinnerTravelType, spinnerBroadcastRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_channel);
        try {
            rg = (RadioGroup) findViewById(R.id.rg);
            add_pic = (ImageView) findViewById(R.id.pic);

            Global.set_action_bar_details(Add_Channel.this, "Add Channel", "");
            TextView logged_user = (TextView) findViewById(R.id.logged_user);
            assert logged_user != null;
            logged_user.setText("");
            city_list_string = getResources().getStringArray(R.array.cities_name);
            city_list=(ListView)findViewById(R.id.list_city);
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, city_list_string);
            city_list.setAdapter(adapter);
            city_list.setVisibility(View.GONE);

            city_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition     = position;

                    // ListView Clicked item value
                    String  itemValue    = (String) city_list.getItemAtPosition(position);
                    city1.setText(itemValue);
                    city_list.setVisibility(View.GONE);

                }

            });

            vtype = (Button) findViewById(R.id.vtype);
            withincity = (Button) findViewById(R.id.atravel);
            category = (Button) findViewById(R.id.acategory);
            refresh_rate = (Button) findViewById(R.id.arefresh);
            Button add_channel = (Button) findViewById(R.id.rregister);
            Button browse = (Button) findViewById(R.id.browse);
            owner = (EditText) findViewById(R.id.aowner);
            vname = (EditText) findViewById(R.id.avname);
            vnumber = (EditText) findViewById(R.id.avnumber);
            city1 = (EditText) findViewById(R.id.acity);


            city_list.setTextFilterEnabled(true);
            city1.addTextChangedListener(new TextWatcher(){

                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text

                    Add_Channel.this.adapter.getFilter().filter(cs);
                    city_list.setVisibility(View.VISIBLE);
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }
            });




            vtype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popup = new PopupMenu(Add_Channel.this, vtype);
                    popup.getMenu().add("Train");
                    popup.getMenu().add("Tram");
                    popup.getMenu().add("Shuttle");
                    popup.getMenu().add("Bus");
                    popup.getMenu().add("Bus-Mini");
                    popup.getMenu().add("Van");
                    popup.getMenu().add("Van-Mini");
                    popup.getMenu().add("Car");
                    popup.getMenu().add("Sedan");
                    popup.getMenu().add("SUV");
                    popup.getMenu().add("3-Wheeler");
                    popup.getMenu().add("2-Wheeler");
                    popup.getMenu().add("Bicycle");
                    popup.getMenu().add("Walk");
                    popup.getMenu().add("Truck-Heavy");
                    popup.getMenu().add("Truck-Dumper");
                    popup.getMenu().add("Truck-Tanker");
                    popup.getMenu().add("Truck-Flatbed");
                    popup.getMenu().add("Truck-Light");
                    popup.getMenu().add("Truck-Ultra Light");
                    popup.getMenu().add("Machine-Construction");
                    popup.getMenu().add("Machine-Agriculture");
                    popup.getMenu().add("Machine-Industrial");
                    popup.getMenu().add("Trawler");
                    popup.getMenu().add("Boat");
                    popup.getMenu().add("Ship");
                    popup.getMenu().add("Other");
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            vtype.setText(item.getTitle());
                            //vtype.setTextColor(Color.WHITE);
                            return true;
                        }
                    });

                    popup.show();

                }
            });

            withincity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popup_travel = new PopupMenu(Add_Channel.this, withincity);
                    popup_travel.getMenu().add("Within City");
                    popup_travel.getMenu().add("Outside City");

                    popup_travel.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            withincity.setText(item.getTitle());
                            //withincity.setTextColor(Color.WHITE);
                            return true;
                        }
                    });

                    popup_travel.show();

                }
            });

            refresh_rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popup_refresh = new PopupMenu(Add_Channel.this, refresh_rate);
                    popup_refresh.getMenu().add("10 secs");
                    popup_refresh.getMenu().add("30 secs");
                    popup_refresh.getMenu().add("1 min");
                    popup_refresh.getMenu().add("15 mins");
                    popup_refresh.getMenu().add("30 mins");

                    popup_refresh.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            refresh_rate.setText(item.getTitle());
                            //refresh_rate.setTextColor(Color.WHITE);
                            return true;
                        }
                    });

                    popup_refresh.show();

                }
            });

            category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popup_category = new PopupMenu(Add_Channel.this, category);
                    popup_category.getMenu().add("Public Transport");
                    popup_category.getMenu().add("Intercity Travel");
                    popup_category.getMenu().add("Hotel Fleet");
                    popup_category.getMenu().add("Field Staff");
                    popup_category.getMenu().add("Personal");
                    popup_category.getMenu().add("Taxi");
                    popup_category.getMenu().add("Transport Cargo");
                    popup_category.getMenu().add("Courier");
                    popup_category.getMenu().add("Chartered");
                    popup_category.getMenu().add("Office");
                    popup_category.getMenu().add("Institutional");
                    popup_category.getMenu().add("Millitary");
                    popup_category.getMenu().add("Emergency");
                    popup_category.getMenu().add("Health");
                    popup_category.getMenu().add("Government");
                    popup_category.getMenu().add("Utility");
                    popup_category.getMenu().add("Food");
                    popup_category.getMenu().add("Sports");
                    popup_category.getMenu().add("Shopping");
                    popup_category.getMenu().add("Business");
                    popup_category.getMenu().add("Entertainment");
                    popup_category.getMenu().add("Others");


                    popup_category.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            category.setText(item.getTitle());
                            //category.setTextColor(Color.WHITE);
                            return true;
                        }
                    });

                    popup_category.show();

                }
            });

            assert add_channel != null;
            add_channel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Global.isNetworkAvailable(Add_Channel.this)) {
                        if (validated1 && validated2 && validated3 && validated4 && !vtype.getText().toString().equalsIgnoreCase("VEHICLE TYPE") && !category.getText().toString().equalsIgnoreCase("CATEGORY") && !withincity.getText().toString().equalsIgnoreCase("TRAVEL TYPE") && !refresh_rate.getText().toString().equalsIgnoreCase("REFRESH RATE")) {
                            Add_Channel.Add_channel_class acc = new Add_Channel.Add_channel_class();
                            acc.execute();
                        } else {
                            Toast.makeText(Add_Channel.this, "Fill all details correctly", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Add_Channel.this, "No Active Internet Connection found", Toast.LENGTH_LONG).show();
                    }

                }
            });


            assert browse != null;
            browse.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    openImageChooser();
                }
            });


            owner.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equalsIgnoreCase("") && !s.toString().equalsIgnoreCase(null) && s.toString().length() > 3 && s.toString().length() < 20) {
                        // Drawable myIcon = getResources().getDrawable(R.drawable.tick);
                        owner.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0);
                        validated1 = true;
                        //owner.setError(" ", myIcon);

                    } else {

                        //Drawable myIcon = getResources().getDrawable(R.drawable.error);
                        owner.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                        validated1 = false;
                        //owner.setError(" ", myIcon);

                    }
                }
            });


            vname.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equalsIgnoreCase("") && !s.toString().equalsIgnoreCase(null) && s.toString().length() > 3 && s.toString().length() < 20) {
                        // Drawable myIcon = getResources().getDrawable(R.drawable.tick);
                        vname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0);
                        validated2 = true;
                        //owner.setError(" ", myIcon);

                    } else {

                        //Drawable myIcon = getResources().getDrawable(R.drawable.error);
                        vname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                        validated2 = false;
                        //owner.setError(" ", myIcon);

                    }
                }
            });


            vnumber.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equalsIgnoreCase("") && !s.toString().equalsIgnoreCase(null) && s.toString().length() > 6 && s.toString().length() < 15) {
                        // Drawable myIcon = getResources().getDrawable(R.drawable.tick);
                        vnumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0);
                        validated3 = true;
                        //owner.setError(" ", myIcon);

                    } else {

                        //Drawable myIcon = getResources().getDrawable(R.drawable.error);
                        vnumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                        validated3 = false;
                        //owner.setError(" ", myIcon);

                    }
                }
            });

            city1.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equalsIgnoreCase("") && !s.toString().equalsIgnoreCase(null) && s.toString().length() > 3 && s.toString().length() < 20) {
                        // Drawable myIcon = getResources().getDrawable(R.drawable.tick);
                        city1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0);
                        validated4 = true;
                        //owner.setError(" ", myIcon);

                    } else {

                        //Drawable myIcon = getResources().getDrawable(R.drawable.error);
                        city1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                        validated4 = false;
                        //owner.setError(" ", myIcon);

                    }
                }
            });


        }catch(Exception e)
        {
            Toast.makeText(Add_Channel.this,"Fatal Error on Adding Channel",Toast.LENGTH_LONG).show();
        }




    }

    @Override
    public void onBackPressed() {


        Intent intent = new Intent(Add_Channel.this, MyChannels_RV.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MyChannels_RV.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void add_channel_details()
    {
        channel_id=Global.generate_channel_id();
        int selectedId = rg.getCheckedRadioButtonId();
        RadioButton radiobutton =(RadioButton)findViewById(selectedId);
        if(radiobutton.getText().toString().equalsIgnoreCase("Block"))
        {
            DatabaseReference userdatafd = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("follower_setting");
            userdatafd.setValue("0");
        }
        else if(radiobutton.getText().toString().equalsIgnoreCase("Allow"))
        {
            DatabaseReference userdatafd = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("follower_setting");
            userdatafd.setValue("1");
        }
        else
        {
            DatabaseReference userdatafd = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("follower_setting");
            userdatafd.setValue("0");
        }
        DatabaseReference userdata = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("owner");
        userdata.setValue(owner.getText().toString().toLowerCase());
        DatabaseReference userdata1 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("vehicle_name");
        userdata1.setValue(vname.getText().toString().toLowerCase());
        DatabaseReference userdata2 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("vehicle_number");
        userdata2.setValue(vnumber.getText().toString().toLowerCase());
        DatabaseReference userdata3 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("city");
        userdata3.setValue(city1.getText().toString().toLowerCase());
        DatabaseReference userdata4 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("visible");
        userdata4.setValue("1");
        DatabaseReference userdata5 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("refresh_status");
        userdata5.setValue(refresh_rate.getText().toString().toLowerCase());
        DatabaseReference userdata6 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("intercity");
        userdata6.setValue(withincity.getText().toString().toLowerCase());
        DatabaseReference userdata7 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("status");
        userdata7.setValue("0");
        DatabaseReference userdata8 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("vtype");
        userdata8.setValue(vtype.getText().toString().toLowerCase());
        DatabaseReference userdata9 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("category");
        userdata9.setValue(category.getText().toString().toLowerCase());




        DatabaseReference userdata10 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("owner");
        userdata10.setValue(owner.getText().toString().toLowerCase());
        DatabaseReference userdata11 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("vehicle_name");
        userdata11.setValue(vname.getText().toString().toLowerCase());
        DatabaseReference userdata12 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("vehicle_number");
        userdata12.setValue(vnumber.getText().toString().toLowerCase());
        DatabaseReference userdata13 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("city");
        userdata13.setValue(city1.getText().toString().toLowerCase());
        DatabaseReference userdata14 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("visible");
        userdata14.setValue("1");
        DatabaseReference userdata15 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("refresh_status");
        userdata15.setValue(refresh_rate.getText().toString().toLowerCase());
        DatabaseReference userdata16 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("intercity");
        userdata16.setValue(withincity.getText().toString().toLowerCase());
        DatabaseReference userdata17 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
        userdata17.setValue("0");
        DatabaseReference userdata18 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("vtype");
        userdata18.setValue(vtype.getText().toString().toLowerCase());
        DatabaseReference userdata19 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("category");
        userdata19.setValue(category.getText().toString().toLowerCase());
        DatabaseReference userdata20 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("mobile");
        userdata20.setValue(Global.username);
        upload_image_to_firebase1();

    }

    private class Add_channel_class extends AsyncTask<String, String, String> {

        // --Commented out by Inspection (14/12/16, 10:13 PM):private String resp;

        @Override
        protected String doInBackground(String... params) {

            add_channel_details();
            return "o";
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            Intent intent = new Intent(Add_Channel.this, MyChannels_RV.class);
            startActivity(intent);
            finish();
            Toast.makeText(Add_Channel.this,"Channel created for user : "+Global.username+" Channel Id : "+Global.generate_channel_id(), Toast.LENGTH_LONG).show();
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



    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);

        startActivityForResult(Intent.createChooser(intent,"SELECT PICTURE"),SELECT_PICTURE);



        // Start the Intent
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            try {
                if(data.getData()!=null) {
                    InputStream inputStream = Add_Channel.this.getContentResolver().openInputStream(data.getData());
                    assert inputStream != null;
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                    Bitmap resized1 = Bitmap.createScaledBitmap(bmp,150, 150, true);

                    resized = Bitmap.createScaledBitmap(bmp,50, 50, true);


                    add_pic.setImageBitmap(resized1);
                }
                else
                {
                    Toast.makeText(Add_Channel.this,"picture cannnot uploaded from this location",Toast.LENGTH_LONG).show();
                }
            }
            catch(FileNotFoundException ignored) {

            }

            //add_pic.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }




    private void upload_image_to_firebase1()
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(resized!=null) {
            resized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            DatabaseReference userdata10 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("image");
            String data_string = Base64.encodeToString(data, Base64.DEFAULT);
            userdata10.setValue(data_string);
            DatabaseReference userdata9 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("image");
            userdata9.setValue(data_string);

        }

    }





}
