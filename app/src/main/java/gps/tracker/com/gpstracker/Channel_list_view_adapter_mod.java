package gps.tracker.com.gpstracker;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by bhupendramishra on 22/10/16.
 */

class Channel_list_view_adapter_mod extends BaseAdapter {
    private static ArrayList<Channel_list> channellist;
    // --Commented out by Inspection (14/12/16, 10:13 PM):private final List<String>filteredData = null;
    private  Context context;
    private final Integer[] images = { R.drawable.broadcast_icon,R.drawable.red_circle };
    private final Integer[] aimages = { R.drawable.broadcast_auto,R.drawable.broadcast_auto_off };
    private final Integer[] visible_images={R.drawable.visible,R.drawable.invisible};

    private final LayoutInflater mInflater;
    //private final Intent i;
    private static PendingIntent pendingIntent;
    private static Intent alarmIntent,alarmIntent1;
    private static AlarmManager manager,manager2;
    Typeface robotoLight;
    Typeface robotoThin;
    Typeface robotoBold;


    public Channel_list_view_adapter_mod(Context context, ArrayList<Channel_list> results) {
        channellist = results;
        this.context=context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        robotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_thin.ttf");
        robotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_light.ttf");
        robotoBold = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_bold.ttf");
        //i=new Intent(this.context, TimeServiceGPS.class);
        alarmIntent = new Intent(this.context, Broadcast_Receiver.class);
        alarmIntent1 = new Intent(this.context, Auto_Broadcast_Reciever.class);
       // alarmIntent = new Intent(this.context, Br_rx.class);

    }

    public int getCount() {
        return channellist.size();
    }

    public Object getItem(int position) {
        return channellist.get(position);
    }

    public  void setContext(Context context){this.context=context;}

    public long getItemId(int position) {
        return position;
    }

    // --Commented out by Inspection (14/12/16, 10:14 PM):private boolean status=false;

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Channel_list_view_adapter_mod.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.broadcast_row_new, parent, false);
            holder = new ViewHolder();


            holder.txtName = (TextView) convertView.findViewById(R.id.cname);
            holder.txtvnumber = (TextView) convertView.findViewById(R.id.cvnumber);
            holder.txtvtype = (TextView) convertView.findViewById(R.id.ctype);
            holder.txtcategary=(TextView)convertView.findViewById(R.id.ccategary);
            holder.broadcast=(ImageButton)convertView.findViewById(R.id.bross);
            holder.broadcast_auto=(ImageButton)convertView.findViewById(R.id.bross_auto);
            holder.visible=(ToggleButton)convertView.findViewById(R.id.visible);
            holder.channel_pic=(CircleImageView)convertView.findViewById(R.id.pic);
            holder.vname=(TextView)convertView.findViewById(R.id.cvname);

            convertView.setTag(holder);
        } else {
            holder = (Channel_list_view_adapter_mod.ViewHolder) convertView.getTag();
            //holder.broadcast.setVisibility(View.GONE);


        }

        holder.txtName.setText(channellist.get(position).getsName());
        holder.txtvnumber.setText(channellist.get(position).getsVnumber());
        holder.txtvtype.setText(channellist.get(position).getsvtype());
        holder.vname.setText(channellist.get(position).getvname());
        holder.txtchannelid=channellist.get(position).getChannelid();
        holder.txtcategary.setText(channellist.get(position).getsvcategary());
        holder.txtactive=channellist.get(position).getsActive();
        holder.broadcast.setImageResource(channellist.get(position).getImageid());
        holder.broadcast_auto.setImageResource(channellist.get(position).agetImageid());
        holder.visible.setChecked(channellist.get(position).getstate());
        holder.channel_pic.setImageBitmap(channellist.get(position).getImage());

        holder.broadcast.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
                String channel_broadcasting = prefs.getString("broadcasting", "NA");

                Global.channel_id=channellist.get(position).getChannelid();
                if(channel_broadcasting.equalsIgnoreCase("NA")) {

                        LocationManager locationManager = (LocationManager) context
                                .getSystemService(Context.LOCATION_SERVICE);
                        boolean isGPSEnabled = locationManager
                                .isProviderEnabled(LocationManager.GPS_PROVIDER);

                        if(isGPSEnabled) {

                            //status = true;
                            SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                            editor.putString("broadcasting",channellist.get(position).getChannelid());
                            editor.putString("broadcasting_cmd",channellist.get(position).getChannelid());
                            editor.apply();
                             status_update_mod("1", channellist.get(position).getChannelid());
                            play_sound();
                            holder.broadcast.setImageResource(R.drawable.broadcast);

                            channellist.get(position).setImageid(images[0]);
                            //alarmIntent.setAction("gps.tracker.com.gpstracker.Broadcast_Receiver");
                            alarmIntent.putExtra("channel_id",channellist.get(position).getChannelid());
                            pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            int interval = 40000;

                            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

                            //edit in v9.2

                            //pendingIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            //manager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            //int interval2 = 50000;

                           // manager2.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval2, pendingIntent);


                            Toast.makeText(context,"Alarm service activated",Toast.LENGTH_LONG).show();
                            Global.show_notification_dead(context,"CHANNEL BROADCASTING","CHANNEL : "+channellist.get(position).getvname()+Global.separator+"Broadcast Started at"+Global.date_time());
                            // Broadcasting_on_Notification();
                        }
                        else
                        {
                            Toast.makeText(context,"Location services off,Enable location service from settings",Toast.LENGTH_LONG).show();
                        }
                        //subscribe();


                    }
                    else if(channel_broadcasting.equalsIgnoreCase(channellist.get(position).getChannelid())){
                        //context.stopService(i);
                        //status = false;

                         SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                         editor.putString("broadcasting","NA");
                         editor.putString("broadcasting_cmd","NA");

                    editor.apply();
                        play_sound_bstop();
                         status_update_mod("0", channellist.get(position).getChannelid());
                        holder.broadcast.setImageResource(R.drawable.broadcast_off);
                        channellist.get(position).setImageid(images[1]);
                        Intent intent = new Intent(context, Broadcast_Receiver.class);
                         //Intent intent = new Intent(context, Br_rx.class);
                        pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        manager.cancel(pendingIntent);

                    //edit in v9.2

                   // pendingIntent = PendingIntent.getBroadcast(context,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                   // manager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                   // manager2.cancel(pendingIntent);
                        //pendingIntent.cancel();
                        Toast.makeText(context,"Alarm service stopped",Toast.LENGTH_LONG).show();
                    NotificationManager nMgr = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancelAll();
                    }

                else if(!channel_broadcasting.equalsIgnoreCase(channellist.get(position).getChannelid()))
                {
                    Toast.makeText(context,"Other Channel is Broadcasting",Toast.LENGTH_LONG).show();
                }

                //holder.notifyAll();
            }
        });


        holder.broadcast_auto.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
                String channel_broadcasting = prefs.getString("broadcasting", "NA");

                Global.channel_id=channellist.get(position).getChannelid();
                if(channel_broadcasting.equalsIgnoreCase("NA")) {

                    LocationManager locationManager = (LocationManager) context
                            .getSystemService(Context.LOCATION_SERVICE);
                    boolean isGPSEnabled = locationManager
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);

                    if(isGPSEnabled) {

                        //status = true;
                        SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                        editor.putString("broadcasting",channellist.get(position).getChannelid());
                        editor.putString("broadcasting_cmd",channellist.get(position).getChannelid());
                        editor.apply();
                        status_update_mod("1", channellist.get(position).getChannelid());
                        play_sound();
                        holder.broadcast_auto.setImageResource(R.drawable.broadcast_auto);
                        holder.broadcast.setImageResource(R.drawable.broadcast);
                        channellist.get(position).asetImageid(aimages[0]);
                        //alarmIntent.setAction("gps.tracker.com.gpstracker.Broadcast_Receiver");
                        alarmIntent1.putExtra("channel_id",channellist.get(position).getChannelid());
                        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

                        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        int interval = 40000;

                        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

                        //edit in v9.2


                        Toast.makeText(context,"Alarm service activated",Toast.LENGTH_LONG).show();
                        Global.show_notification_dead(context,"CHANNEL BROADCASTING","CHANNEL : "+channellist.get(position).getvname()+Global.separator+"Broadcast Started at"+Global.date_time());
                        // Broadcasting_on_Notification();
                    }
                    else
                    {
                        Toast.makeText(context,"Location services off,Enable location service from settings",Toast.LENGTH_LONG).show();
                    }
                    //subscribe();


                }
                else if(channel_broadcasting.equalsIgnoreCase(channellist.get(position).getChannelid())){
                    //context.stopService(i);
                    //status = false;

                    SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                    editor.putString("broadcasting","NA");
                    editor.putString("broadcasting_cmd","NA");

                    editor.apply();
                    play_sound_bstop();
                    status_update_mod("0", channellist.get(position).getChannelid());
                    holder.broadcast_auto.setImageResource(R.drawable.broadcast_auto_off);
                    holder.broadcast.setImageResource(R.drawable.broadcast_off);
                    channellist.get(position).asetImageid(images[1]);
                    Intent intent = new Intent(context, Auto_Broadcast_Reciever.class);
                    //Intent intent = new Intent(context, Br_rx.class);
                    pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(pendingIntent);

                    //edit in v9.2
                    //pendingIntent.cancel();
                    Toast.makeText(context,"Alarm service stopped",Toast.LENGTH_LONG).show();
                    NotificationManager nMgr = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancelAll();
                }

                else if(!channel_broadcasting.equalsIgnoreCase(channellist.get(position).getChannelid()))
                {
                    Toast.makeText(context,"Other Channel is Broadcasting",Toast.LENGTH_LONG).show();
                }

                //holder.notifyAll();
            }
        });


        holder.visible.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Global.channel_id=channellist.get(position).getChannelid();
                DatabaseReference user_ref=Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id);
                user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if (map.get("visible").toString().equalsIgnoreCase("1")) {
                                DatabaseReference ref = Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("visible");
                                ref.setValue("0");
                                DatabaseReference ref1 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(Global.channel_id).child("visible");
                                ref1.setValue("0");
                                holder.visible.setChecked(false);
                                channellist.get(position).setvisibleimageid(visible_images[1]);
                            } else {
                                DatabaseReference ref = Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("visible");
                                ref.setValue("1");
                                DatabaseReference ref1 = Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(Global.channel_id).child("visible");
                                ref1.setValue("1");
                                holder.visible.setChecked(true);
                                channellist.get(position).setvisibleimageid(visible_images[0]);
                            }


                        } catch (ClassCastException ce) {
                            //Toast.makeText(MyChannels_RV.this, "Filtered few invalid Channels", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                    }
                });

            }
        });


        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtvnumber;
        TextView txtvtype;
        TextView txtcategary;
        TextView vname;
        String txtchannelid;
        String txtactive;
        ImageButton broadcast;
        ImageButton broadcast_auto;
        ToggleButton visible;
        CircleImageView channel_pic;


    }

    /* public void filter(String charText) {

        stocks.clear();
        if (charText.length() == 0) {
            stocks.addAll(searchArrayList);
        } else {
            for (SearchResults cs : searchArrayList) {
                if (cs.getName().contains(charText)) {
                    stocks.add(cs);
                }
            }
        }
        notifyDataSetChanged();
    }*/


// --Commented out by Inspection START (14/12/16, 10:14 PM):
//    private void status_update(final String update,final int position)
//    {
//        Global.channel_id=channellist.get(position).getChannelid().split(":")[1].trim();
//        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(channellist.get(position).getChannelid().split(":")[1].trim()).child("followers");
//        DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channellist.get(position).getChannelid().split(":")[1].trim()).child("status");
//        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(channellist.get(position).getChannelid().split(":")[1].trim()).child("status");
//        ref2.setValue(update);
//        //ref1.onDisconnect().setValue("0");
//        ref1.setValue(update);
//        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);
//
//        if(user_ref!=null) {
//
//            user_ref.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                        if (child != null) {
//
//                            DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(child.getKey()).child("Subscribers").child(channellist.get(position).getChannelid().split(":")[1].trim()).child("status");
//                            //ref.onDisconnect().setValue("0");
//                            ref.setValue(update);
//
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Failed to read value
//                    //Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();
//
//                }
//            });
//        }
//    }
// --Commented out by Inspection STOP (14/12/16, 10:14 PM)



    private void status_update_mod(final String update, final String channel_id)
    {
        Global.channel_id=channel_id;
        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(channel_id).child("followers");
        DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channel_id).child("status");
        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(channel_id).child("status");
        ref2.setValue(update);
        //ref1.onDisconnect().setValue("0");
        ref1.setValue(update);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);

        if(user_ref!=null) {

            user_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        if (child != null) {

                            DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(child.getKey()).child("Subscribers").child(channel_id).child("status");
                            //ref.onDisconnect().setValue("0");
                            ref.setValue(update);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Toast.makeText(Channel_settings.this, error.toException().toString(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }


    private void play_sound()
    {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.beep5);
        mp.start();
    }


    private void play_sound_bstop()
    {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.bstop);
        mp.start();
    }


// --Commented out by Inspection START (14/12/16, 10:15 PM):
//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
// --Commented out by Inspection STOP (14/12/16, 10:15 PM)


    /*private void Broadcasting_on_Notification() {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle("Broadcasting On")
                .setContentText("Channel : "+Global.channel_broadcasting_name+":"+Global.separator+Global.channel_broadcasting_vnumber)
                .setOngoing(true);

        builder.setLights(0xff00ff00, 300, 100);
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }*/

// --Commented out by Inspection START (01/12/16, 10:04 PM):
//    private void Broadcasting_off_Notification() {
//        Notification.Builder builder = new Notification.Builder(context);
//        builder.setSmallIcon(R.drawable.ic_launcher)
//                .setPriority(Notification.PRIORITY_HIGH)
//                .setContentTitle("Broadcasting Off")
//                .setContentText("Channel : "+Global.channel_broadcasting_name+":"+Global.separator+Global.channel_broadcasting_vnumber)
//                .setOngoing(true);
//
//        builder.setLights(0xff00ff00, 300, 100);
//        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());
//    }
// --Commented out by Inspection STOP (01/12/16, 10:04 PM)

}
