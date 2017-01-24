package gps.tracker.com.gpstracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class User_setting extends AppCompatActivity {
    // --Commented out by Inspection (01/12/16, 10:33 PM):Button delete_account;
    private EditText new_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        Button change_number = (Button) findViewById(R.id.uchg_num);
        new_number=(EditText)findViewById(R.id.uchange);
        Global.set_action_bar_details(User_setting.this,"User Settings","");
        assert change_number != null;
        change_number.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent i1 = new Intent(User_setting.this, OTP.class);
                i1.putExtra("new_user","0");
                i1.putExtra("new_number",new_number.getText().toString());
                startActivity(i1);
                finish();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

// --Commented out by Inspection START (01/12/16, 10:33 PM):
//    public void change_user_data()
//    {
//        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(Global.username);
//
//
//
//            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    if (dataSnapshot.getValue() != null) {
//                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//
//
//                    }
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Failed to read value
//                    Toast.makeText(User_setting.this, error.toException().toString(), Toast.LENGTH_LONG).show();
//
//                }
//            });
//
//    }
// --Commented out by Inspection STOP (01/12/16, 10:33 PM)





    @Override
    public void onBackPressed() {


        Intent intent = new Intent(User_setting.this, Dashboard.class);
        startActivity(intent);
        finish();
    }
}
