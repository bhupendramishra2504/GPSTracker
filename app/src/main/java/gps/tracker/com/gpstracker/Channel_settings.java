package gps.tracker.com.gpstracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;


public class Channel_settings extends AppCompatActivity{
    //private String data;
    private ListView lv1;
    private final ArrayList<Follower_results> results = new ArrayList<Follower_results>();
    private Follower_list_view_adapter adapter;

   // private Intent i;
    private String channel_id;
    private EditText new_number;
    private final Integer[] images2 = { R.drawable.block_icon1,R.drawable.unblock_icon1 };
    private Button vtype;
    private Button category;
    private Button withincity;
    private Button refresh_rate;
    private Button dob;
    private PopupMenu popup,popup_travel,popup_category,popup_refresh;
    private EditText owner,vname,vnumber,city1;
    private ImageView add_pic;
    private final int SELECT_PICTURE=100;
    private Bitmap resized;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private boolean validated1=false,validated2=false,validated3=false,validated4=false;
    private RadioGroup rg;

    String [] city_list_string;
    ListView city_list;
    ArrayAdapter<String> adapter1;
    Typeface robotoLight;
    Typeface robotoThin;
    Typeface robotoBold;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_channel);

        try {
            activity =Channel_settings.this;

            robotoThin = Typeface.createFromAsset(activity.getAssets(), "fonts/roboto_thin.ttf");
            robotoLight = Typeface.createFromAsset(activity.getAssets(), "fonts/roboto_light.ttf");
            robotoBold = Typeface.createFromAsset(activity.getAssets(), "fonts/roboto_bold.ttf");

            rg = (RadioGroup) findViewById(R.id.rg);
            RadioButton allow=(RadioButton) findViewById(R.id.rallow);
            RadioButton block=(RadioButton) findViewById(R.id.rblock);
            allow.setTypeface(robotoLight);
            block.setTypeface(robotoLight);

            //RadioButton block = (RadioButton) findViewById(R.id.rblock);
            //RadioButton allow = (RadioButton) findViewById(R.id.rallow);
            Button change = (Button) findViewById(R.id.change);
            Button delete = (Button) findViewById(R.id.delete);
            Button edit = (Button) findViewById(R.id.edit);
            channel_id = getIntent().getExtras().getString("subscriber");
            new_number = (EditText) findViewById(R.id.newno);
            dob = (Button) findViewById(R.id.dob);
            Global.channel_id = channel_id;


            city_list_string = getResources().getStringArray(R.array.cities_name);
            city_list=(ListView)findViewById(R.id.list_city);
            adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, city_list_string);
            city_list.setAdapter(adapter1);
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
            vtype.setTypeface(robotoLight);
            withincity = (Button) findViewById(R.id.atravel);
            withincity.setTypeface(robotoLight);
            category = (Button) findViewById(R.id.acategory);
            category.setTypeface(robotoLight);
            refresh_rate = (Button) findViewById(R.id.arefresh);
            refresh_rate.setTypeface(robotoLight);
            //add_channel=(Button)findViewById(R.id.rregister);
            owner = (EditText) findViewById(R.id.aowner);
            owner.setTypeface(robotoLight);
            vname = (EditText) findViewById(R.id.avname);
            vname.setTypeface(robotoLight);
            vnumber = (EditText) findViewById(R.id.avnumber);
            vnumber.setTypeface(robotoLight);
            city1 = (EditText) findViewById(R.id.acity);
            city1.setTypeface(robotoLight);
            city_list.setTextFilterEnabled(true);
            city1.addTextChangedListener(new TextWatcher(){

                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text

                    Channel_settings.this.adapter1.getFilter().filter(cs);
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






            add_pic = (ImageView) findViewById(R.id.pic);
            add_pic.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    openImageChooser();
                }
            });

            //owner.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
           // lv1.setVisibility(View.GONE);

            lv1 = (ListView) findViewById(R.id.list);
            // Activity activity = this;
            //i=new Intent(Channel_settings.this, TimeServiceGPS.class);
            Channel_settings.Broadcast_channel_class bcc = new Channel_settings.Broadcast_channel_class();
            bcc.execute();
            GetfollowerResults();
            Global.set_action_bar_details(Channel_settings.this, "Edit Channel", "");
            assert change != null;
            change.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    change_channel();
                }
            });

            assert delete != null;
            delete.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    delete_channel();
                    Intent i1 = new Intent(Channel_settings.this, MyChannels_RV.class);
                    startActivity(i1);
                    finish();
                }
            });

            assert edit != null;
            edit.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (Global.isNetworkAvailable(Channel_settings.this)) {
                        if (validated1 && validated2 && validated3 && validated4 && !vtype.getText().toString().equalsIgnoreCase("VEHICLE TYPE") && !category.getText().toString().equalsIgnoreCase("CATEGORY") && !withincity.getText().toString().equalsIgnoreCase("TRAVEL TYPE") && !refresh_rate.getText().toString().equalsIgnoreCase("REFRESH RATE")) {

                            Channel_settings.Add_channel_class acc = new Channel_settings.Add_channel_class();
                            acc.execute();
                        } else {
                            Toast.makeText(Channel_settings.this, "Fill all the details correctly", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Channel_settings.this, "No Active Internet Connection found", Toast.LENGTH_LONG).show();
                    }


                }
            });

            myCalendar = Calendar.getInstance();

            date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }

            };


            dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(Channel_settings.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                }
            });


            vtype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popup = new PopupMenu(Channel_settings.this, vtype);
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
                           // vtype.setTextColor(Color.WHITE);
                            return true;
                        }
                    });

                    popup.show();

                }
            });

            withincity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popup_travel = new PopupMenu(Channel_settings.this, withincity);
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

                    popup_refresh = new PopupMenu(Channel_settings.this, refresh_rate);
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

                    popup_category = new PopupMenu(Channel_settings.this, category);
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
                    if (!s.toString().equalsIgnoreCase("") && !s.toString().equalsIgnoreCase(null) && s.toString().length() > 3 && s.toString().length() < 12) {
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
                    if (!s.toString().equalsIgnoreCase("") && !s.toString().equalsIgnoreCase(null) && s.toString().length() > 6 && s.toString().length() < 12) {
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
            Toast.makeText(Channel_settings.this,"Fatal Error in editing channel",Toast.LENGTH_LONG).show();
        }







        // hideSystemUI();


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
                    InputStream inputStream = Channel_settings.this.getContentResolver().openInputStream(data.getData());
                    assert (inputStream != null ? inputStream : null) != null;
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                    Bitmap resized1 = Bitmap.createScaledBitmap(bmp,150, 150, true);

                    resized = Bitmap.createScaledBitmap(bmp,50, 50, true);


                    add_pic.setImageBitmap(resized1);
                }
                else
                {
                    Toast.makeText(Channel_settings.this,"picture cannnot uploaded from this location",Toast.LENGTH_LONG).show();
                }
            }
            catch(FileNotFoundException ignored) {
            }

            //add_pic.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean broadcast_channel()
    {


        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channel_id);

        boolean channel_exist=false;
        if(user_ref!=null) {
            channel_exist=true;
            user_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null && (dataSnapshot.getValue() instanceof Map)) {
                        try {
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            //data = "Owner : " + map.get("owner").toString() + Global.separator + "Vehicle Name : " + map.get("vehicle_name").toString() + Global.separator + "Vehicle Number : " + map.get("vehicle_number").toString() + Global.separator + "Vehicle type :" + map.get("vtype").toString() + Global.separator + "City : " + map.get("city").toString() + Global.separator + "Refresh Rate : " + map.get("refresh_status").toString() + Global.separator + "Intercity : " + map.get("intercity").toString() + Global.separator + "Category :" + map.get("category").toString() + Global.separator + "Visible : " + map.get("visible").toString() + Global.separator;
                            //Global.rr=map.get("refresh_status").toString();
                            owner.setText(map.get("owner").toString());
                            vname.setText(map.get("vehicle_name").toString());
                            vnumber.setText(map.get("vehicle_number").toString());
                            vtype.setText(map.get("vtype").toString());
                            city1.setText(map.get("city").toString());
                            refresh_rate.setText(map.get("refresh_status").toString());
                            withincity.setText(map.get("intercity").toString());
                            category.setText(map.get("category").toString());
                            if (map.get("image") != null) {
                                add_pic.setImageBitmap(download_image_to_firebase1(map.get("image").toString()));
                            }
                            if (map.get("follower_setting") != null) {
                                if (map.get("follower_setting").toString().equalsIgnoreCase("1")) {
                                    rg.check(R.id.rallow);
                                } else if (map.get("follower_setting").toString().equalsIgnoreCase("0")) {
                                    rg.check(R.id.rblock);
                                }
                            } else {
                                rg.check(R.id.rblock);
                            }

                            city_list.setVisibility(View.GONE);
                        }
                    catch (ClassCastException ce) {
                        //Toast.makeText(MyChannels_RV.this, "Filtered few invalid Channels", Toast.LENGTH_LONG).show();
                    }
                    }


                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });
        }
        return channel_exist;
    }




    private class Broadcast_channel_class extends AsyncTask<String, String, String> {

        //private String resp;
        //private boolean res;

        @Override
        protected String doInBackground(String... params) {

          broadcast_channel();
            return "0";
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation



           /* if(c_realtime)
            {
                Toast.makeText(Channel_settings.this,"Connected to realtime.co",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(Channel_settings.this,"could not connected to realtime.co",Toast.LENGTH_LONG).show();
            }*/
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
            //channel_info.setText("please wait while we loading data from server ....");
        }
    }


    private void change_channel()
    {
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channel_id);

       // boolean channel_exist=false;
        if(user_ref!=null) {
          //  channel_exist=true;
            user_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                       try {
                           Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                           String dob_get = Global.dob;
                           if (dob_get.equalsIgnoreCase(dob.getText().toString().trim()) && !new_number.getText().toString().equalsIgnoreCase(null) && !new_number.getText().toString().equalsIgnoreCase("") && !dob.getText().toString().equalsIgnoreCase(null) && !dob.getText().toString().equalsIgnoreCase(null)) {
                               DatabaseReference ref1 = Global.firebase_dbreference.child("USERS").child(new_number.getText().toString().trim()).child("channels").child(channel_id);
                               ref1.setValue(map);
                               DatabaseReference ref2 = Global.firebase_dbreference.child("USERS").child(new_number.getText().toString().trim()).child("channels").child(channel_id).child("mobile");
                               ref2.setValue(null);
                               DatabaseReference ref3 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("mobile");
                               ref3.setValue(new_number.getText().toString());
                               DatabaseReference ref4 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id);
                               ref4.setValue(null);
                               Toast.makeText(Channel_settings.this, "Channel Transferred to new User Successfully", Toast.LENGTH_LONG).show();
                           } else {
                               Toast.makeText(Channel_settings.this, "Channel cannot be transferred to new user check your detials", Toast.LENGTH_LONG).show();
                           }
                       }
                     catch (ClassCastException ce) {
                        //Toast.makeText(MyChannels_RV.this, "Filtered few invalid Channels", Toast.LENGTH_LONG).show();
                    }


                    }


                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    private boolean chk_delete_channel()
    {
        //View v;
        boolean delete_flag=false;
        //ImageButton buib;
        for(int i=0;i<lv1.getCount();i++)
        {
            Object o = lv1.getItemAtPosition(i);
            //final Follower_results fullObject = (Follower_results) o;

            //buib=(ImageButton)v.findViewById(R.id.block);
            //int resource=(Integer)buib.getTag(R.id.resource);
            if(((Follower_results) o).getstatus().equalsIgnoreCase("1"))
            {
                delete_flag=true;
            }

        }
        return delete_flag;
    }









    private void delete_channel()
    {
        if(!chk_delete_channel())
        {
            delete_firebase_channel();
            Toast.makeText(Channel_settings.this,"Channel deleted successfully",Toast.LENGTH_LONG).show();
            SharedPreferences prefs = activity.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
            String channel_broadcasting = prefs.getString("broadcasting", "NA");
            if(channel_broadcasting.equalsIgnoreCase(channel_id))
            {
                SharedPreferences.Editor editor = activity.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                editor.putString("broadcasting","NA");
                editor.apply();
            }

        }
        else
        {
           Toast.makeText(Channel_settings.this,"Channel cannot be deleted : Ensure to block all followers",Toast.LENGTH_LONG).show();
        }
    }

    private void delete_firebase_channel()
    {
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("followers");
        //DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channellist.get(position).getChannelid().split(":")[1].trim()).child("status");
        //ref1.setValue(update);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);

        if(user_ref!=null) {

            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        if (child != null) {

                            DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(child.getKey()).child("Subscribers").child(channel_id);
                            ref.setValue(null);

                        }
                    }
                    DatabaseReference ref1=Global.firebase_dbreference.child("CHANNELS").child(channel_id);
                    ref1.setValue(null);
                    DatabaseReference ref3=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id);
                    ref3.setValue(null);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });
        }
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


    private void GetfollowerResults(){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();

        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("followers");
        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter=null;
                results.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){

                        try {
                            Map<String, Object> map = (Map<String, Object>) child.getValue();
                            Follower_results sr1 = new Follower_results();
                            sr1.setfName(map.get("name").toString());
                            sr1.setfPhone(child.getKey());
                            if (map.get("unblock") != null) {
                                if (map.get("unblock").toString().equalsIgnoreCase("1")) {
                                    sr1.setImageid(images2[1]);
                                    sr1.setstatus("1");
                                } else {
                                    sr1.setImageid(images2[0]);
                                    sr1.setstatus("0");
                                }
                            } else {
                                sr1.setImageid(images2[1]);
                                sr1.setstatus("1");
                            }

                            results.add(sr1);

                        }
                     catch (ClassCastException ce) {
                        //Toast.makeText(MyChannels_RV.this, "Filtered few invalid Channels", Toast.LENGTH_LONG).show();
                    }

                    }

                }

                adapter=new Follower_list_view_adapter(getApplicationContext(), results);
                lv1.setAdapter(adapter);

                lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        Object o = lv1.getItemAtPosition(position);
                        Follower_results fullObject = (Follower_results) o;
                        Toast.makeText(Channel_settings.this, "You have chosen: " + " " + fullObject.getfName()+Global.separator+fullObject.getfPhone(), Toast.LENGTH_LONG).show();
                        //subscriber_invite=fullObject.getfPhone();
                        //subscriber_name=fullObject.getfName();

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onPause()
    {
        super.onPause();
        //Toast.makeText(Channel_settings.this,"Service Paused is called",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed()
    {
       // Toast.makeText(Channel_settings.this,"Back pressed is called",Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Channel_settings.this, MyChannels_RV.class);
        startActivity(intent);
        finish();
    }


    private class Add_channel_class extends AsyncTask<String, String, String> {

        //private String resp;

        @Override
        protected String doInBackground(String... params) {

            add_channel_details();
            return "0";
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            Intent intent = new Intent(Channel_settings.this, MyChannels_RV.class);
            startActivity(intent);
            finish();
            Toast.makeText(Channel_settings.this,"Channel created for user : "+Global.username+" Channel Id : "+Global.channel_id, Toast.LENGTH_LONG).show();
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


    private void add_channel_details()
    {

        int selectedId = rg.getCheckedRadioButtonId();
        RadioButton radiobutton =(RadioButton)findViewById(selectedId);
        assert radiobutton != null;
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


    private void updateLabel() {

        String myFormat = "ddMMyyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(popup!=null) {
            popup.dismiss();
        }
        if(popup_travel!=null)
        {
            popup_travel.dismiss();
        }
        if(popup_category!=null) {
            popup_category.dismiss();
        }
        if(popup_refresh!=null) {
            popup_refresh.dismiss();
        }
    }

}
