package gps.tracker.com.gpstracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends AppCompatActivity implements View.OnClickListener{

    Button btnMyAccount;
    TextView name,mobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Global.set_action_bar_details(Settings.this,"Settings","");
        btnMyAccount = (Button)findViewById(R.id.btnMyAccount);
        name=(TextView)findViewById(R.id.name);
        mobile=(TextView)findViewById(R.id.mobile);

        SharedPreferences prefs = getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
        //name = prefs.getString("mobile", "not valid");

        name.setText(prefs.getString("username", "Error"));
        mobile.setText(Global.username);
        btnMyAccount.setOnClickListener(this);
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnMyAccount:
                Intent myAccountIntent = new Intent(Settings.this, User_setting.class);
                Settings.this.startActivity(myAccountIntent);
                break;
        }
    }

}
