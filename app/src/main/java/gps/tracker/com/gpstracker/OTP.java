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
        spinner.setVisibility(View.GONE);

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

            SharedPreferences.Editor editor = getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
            editor.putString("mobile",number );
            editor.putString("id", imei);
            editor.apply();

    }

}
