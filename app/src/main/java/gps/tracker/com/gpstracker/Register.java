package gps.tracker.com.gpstracker;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class Register extends AppCompatActivity {

    private Button gender;
    private Button dob;
    private EditText name;
    private EditText mobile;
    private EditText city;
    private EditText country;
    private PopupMenu popup;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Global.set_action_bar_details(Register.this,"JoinIn-Register","");
        Button otp = (Button) findViewById(R.id.rregister);
        gender=(Button)findViewById(R.id.rgender);
        name=(EditText)findViewById(R.id.rname);
        dob=(Button)findViewById(R.id.rdob);
        mobile=(EditText)findViewById(R.id.rmobile);
        city=(EditText)findViewById(R.id.rcity);
        country=(EditText)findViewById(R.id.rcountry);
        ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        grant_all_permission();



        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popup=new PopupMenu(Register.this,gender);
                popup.getMenu().add("MALE");
                popup.getMenu().add("FEMALE");
                popup.getMenu().add("OTHER");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        gender.setText(item.getTitle());
                        gender.setTextColor(Color.WHITE);
                        return true;
                    }
                });

                popup.show();

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
                new DatePickerDialog(Register.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });



        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Global.validate_string(name.getText().toString())==1 && Global.validate_string(dob.getText().toString()) ==1 && Global.validate_string(mobile.getText().toString())==1 && Global.validate_string(city.getText().toString())==1 && Global.validate_string(country.getText().toString())==1 && grant_permission())
                {
                    if(Global.isNetworkAvailable(Register.this)) {
                        Intent intent = new Intent(Register.this, OTP.class);
                        intent.putExtra("name", name.getText().toString());
                        intent.putExtra("dob", dob.getText().toString());
                        intent.putExtra("gender", gender.getText().toString());
                        intent.putExtra("mobile", mobile.getText().toString());
                        intent.putExtra("city", city.getText().toString());
                        intent.putExtra("country", country.getText().toString());
                        intent.putExtra("new_user", "1");
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(Register.this,"No Active Internet Connection Found",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(Register.this,"Check All Data and Try Again or check all permissions provided Give full permission to app, without it can not work properly , restart your app to get permission request page, if you are not seeing it then go to settings app permissions then give all permissions", Toast.LENGTH_LONG).show();
                }



            }
        });




    }


    private boolean grant_permission()
    {
        boolean all_permission_granted=true;
        int android_Version = Build.VERSION.SDK_INT;
        if (android_Version > Build.VERSION_CODES.LOLLIPOP_MR1) {

            all_permission_granted = !(ContextCompat.checkSelfPermission(Register.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Register.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Register.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(Register.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED);
        }
        return all_permission_granted;

        //location_enable();
    }

    private boolean has_permissions(String[] permissions) {
        boolean all_permission_granted = true;
        int android_Version = Build.VERSION.SDK_INT;
        if (android_Version > Build.VERSION_CODES.LOLLIPOP_MR1) {

            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(Register.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void grant_all_permission()
    {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.INTERNET, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
        if(!has_permissions(PERMISSIONS)){
            ActivityCompat.requestPermissions(Register.this, PERMISSIONS, PERMISSION_ALL);
        }

    }




    private void updateLabel() {

        String myFormat = "ddMMyyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob.setText(sdf.format(myCalendar.getTime()));
    }




}
