package gps.tracker.com.gpstracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class OTP extends AppCompatActivity {
    private String imei;
    private EditText otp;
    private String name;
    private String dob;
    private String mobile;
    private String city;
    private String country;
    private String gender;
    private String new_user;
    private String new_number;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otp=(EditText)findViewById(R.id.o1);
        Button register = (Button) findViewById(R.id.rregister);
        Intent i=getIntent();
        new_user=i.getStringExtra("new_user");
        if(new_user.equalsIgnoreCase("1")) {
            name = i.getStringExtra("name");
            dob = i.getStringExtra("dob");
            mobile = i.getStringExtra("mobile");
            city = i.getStringExtra("city");
            country = i.getStringExtra("country");
            gender = i.getStringExtra("gender");
            Global.set_action_bar_details(OTP.this, "OTP", "[ " + mobile + " ]");


        }
        else
        {
            new_number = i.getStringExtra("new_number");
        }
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        assert spinner != null;
        spinner.setVisibility(View.GONE);

        assert register != null;
        register.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HardwareIds")
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);

                if(Global.isNetworkAvailable(OTP.this)) {
                    if (otp.getText().toString().equalsIgnoreCase("1234")) {
                        if (new_user.equalsIgnoreCase("1")) {
                            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            imei = mngr.getDeviceId();

                            DatabaseReference userdata = Global.firebase_dbreference.child("USERS").child(mobile).child("name");
                            userdata.setValue(name);
                            DatabaseReference userdata1 = Global.firebase_dbreference.child("USERS").child(mobile).child("dob");
                            userdata1.setValue(dob);
                            DatabaseReference userdata2 = Global.firebase_dbreference.child("USERS").child(mobile).child("city");
                            userdata2.setValue(city);
                            DatabaseReference userdata3 = Global.firebase_dbreference.child("USERS").child(mobile).child("country");
                            userdata3.setValue(country);
                            DatabaseReference userdata4 = Global.firebase_dbreference.child("USERS").child(mobile).child("id");
                            userdata4.setValue(imei);
                            DatabaseReference userdata5 = Global.firebase_dbreference.child("USERS").child(mobile).child("gender");
                            userdata5.setValue(gender);
                            DatabaseReference userdata6 = Global.firebase_dbreference.child("USERS").child(mobile).child("active");
                            userdata6.setValue("1");
                            DatabaseReference userdata7 = Global.firebase_dbreference.child("USERS").child(mobile).child("followers");
                            userdata7.setValue("0");
                            DatabaseReference userdata8 = Global.firebase_dbreference.child("USERS").child(mobile).child("reg_date");
                            userdata8.setValue(Global.date_time());
                            DatabaseReference userdata9 = Global.firebase_dbreference.child("USERS").child(mobile).child("cancel_date");
                            userdata9.setValue("none");


                            Toast.makeText(OTP.this, "User Created Successfully" + imei, Toast.LENGTH_LONG).show();
                            save_user_profile(mobile);
                            default_channel_dashboard(mobile);
                            spinner.setVisibility(View.GONE);
                            Intent intent = new Intent(OTP.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            imei = mngr.getDeviceId();
                            save_user_profile(new_number);
                            write_channel_data();
                            change_user_data();
                            default_channel_dashboard(new_number);
                            Intent intent = new Intent(OTP.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(OTP.this, "Mobile changed successfully", Toast.LENGTH_LONG).show();
                        }

                    }
                }
                else
                {
                    Toast.makeText(OTP.this,"No Active Internet Connection Found",Toast.LENGTH_LONG).show();
                }

            }
        });



    }


    private void change_user_data()
    {
        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(Global.username);



        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    try {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(new_number);
                    ref.setValue(map);
                    } catch (ClassCastException ce) {
                        //Toast.makeText(MyChannels_RV.this, "Filtered few invalid Channels", Toast.LENGTH_LONG).show();
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(OTP.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }


    private void write_channel_data(){
        //ArrayList<SearchResults> results = new ArrayList<SearchResults>();

        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels");
        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child != null){

                        DatabaseReference ref=Global.firebase_dbreference.child("CHANNELS").child(child.getKey()).child("mobile");
                        ref.setValue(new_number);
                    }

                }





            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(OTP.this, error.toException().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onBackPressed() {


        Intent intent = new Intent(OTP.this, Register.class);
        startActivity(intent);
        finish();
    }

    private void save_user_profile(String number)
    {

           try {

               SharedPreferences.Editor editor = getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
               editor.putString("mobile", number);
               editor.putString("id", imei);
               editor.apply();
           }catch(Exception e)
           {
               Toast.makeText(OTP.this,"Fatal Error while saving user profile"+e.getMessage(),Toast.LENGTH_LONG).show();

           }

    }

    private void default_channel_dashboard(String mobile)
    {
        try {


            String channel_id = Global.generate_channel_id();


            DatabaseReference userdatafd = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("follower_setting");
            userdatafd.setValue("1");

            DatabaseReference userdata = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("owner");
            userdata.setValue(name);
            DatabaseReference userdata1 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("vehicle_name");
            userdata1.setValue(name);
            DatabaseReference userdata2 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("vehicle_number");
            userdata2.setValue("Default");
            DatabaseReference userdata3 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("city");
            userdata3.setValue(city);
            DatabaseReference userdata4 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("visible");
            userdata4.setValue("1");
            DatabaseReference userdata5 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("refresh_status");
            userdata5.setValue("1min");
            DatabaseReference userdata6 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("intercity");
            userdata6.setValue("WithinCity");
            DatabaseReference userdata7 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("status");
            userdata7.setValue("0");
            DatabaseReference userdata8 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("vtype");
            userdata8.setValue("Sedan");
            DatabaseReference userdata9 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("category");
            userdata9.setValue("a");


            DatabaseReference userdata10 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("owner");
            userdata10.setValue(name);
            DatabaseReference userdata11 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("vehicle_name");
            userdata11.setValue(name);
            DatabaseReference userdata12 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("vehicle_number");
            userdata12.setValue("Default");
            DatabaseReference userdata13 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("city");
            userdata13.setValue(city);
            DatabaseReference userdata14 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("visible");
            userdata14.setValue("1");
            DatabaseReference userdata15 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("refresh_status");
            userdata15.setValue("1min");
            DatabaseReference userdata16 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("intercity");
            userdata16.setValue("WithinCity");
            DatabaseReference userdata17 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
            userdata17.setValue("0");
            DatabaseReference userdata18 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("vtype");
            userdata18.setValue("Sedan");
            DatabaseReference userdata19 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("category");
            userdata19.setValue("a");
            DatabaseReference userdata20 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("mobile");
            userdata20.setValue(Global.username);

            DatabaseReference userdata000 = Global.firebase_dbreference.child("USERS").child(mobile).child("Subscribers").child(channel_id).child("name");
            userdata000.setValue(name);
            DatabaseReference userdata111 = Global.firebase_dbreference.child("USERS").child(mobile).child("Subscribers").child(channel_id).child("vehicle_number");
            userdata111.setValue("Default");
            DatabaseReference userdata333 = Global.firebase_dbreference.child("USERS").child(mobile).child("Subscribers").child(channel_id).child("vname");
            userdata333.setValue(name);
            DatabaseReference userdata444 = Global.firebase_dbreference.child("USERS").child(mobile).child("Subscribers").child(channel_id).child("active");
            userdata444.setValue("1");
            DatabaseReference userdata555 = Global.firebase_dbreference.child("USERS").child(mobile).child("Subscribers").child(channel_id).child("status");
            userdata555.setValue("0");
            DatabaseReference userdata666 = Global.firebase_dbreference.child("USERS").child(mobile).child("Subscribers").child(channel_id).child("mobile");
            userdata666.setValue(Global.username);
            DatabaseReference userdata777 = Global.firebase_dbreference.child("USERS").child(mobile).child("Subscribers").child(channel_id).child("vtype");
            userdata777.setValue("Sedan");
            DatabaseReference userdata888 = Global.firebase_dbreference.child("USERS").child(mobile).child("Subscribers").child(channel_id).child("category");
            userdata888.setValue("a");
            DatabaseReference userdata999 = Global.firebase_dbreference.child("USERS").child(mobile).child("Subscribers").child(channel_id).child("unblock");
            userdata999.setValue("1");

            DatabaseReference userdata0000 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("followers").child(mobile).child("name");
            userdata0000.setValue(name);
            DatabaseReference userdata2222 = Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("followers").child(mobile).child("unblock");
            userdata2222.setValue("1");
            DatabaseReference userdata1111 = Global.firebase_dbreference.child("USERS").child(channel_id).child("followers").child(mobile).child("unblock");
            userdata1111.setValue("1");
        }catch(Exception e)
        {
            Toast.makeText(OTP.this,"Fatal Error while creating default channel in dashboard"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        //upload_image_to_firebase1();

    }






}
