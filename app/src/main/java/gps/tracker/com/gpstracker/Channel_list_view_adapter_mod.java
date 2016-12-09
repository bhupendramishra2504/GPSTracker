package gps.tracker.com.gpstracker;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by bhupendramishra on 22/10/16.
 */

class Channel_list_view_adapter_mod extends BaseAdapter {
    private static ArrayList<Channel_list> channellist;
    private final List<String>filteredData = null;
    private  Context context;
    private final Integer[] images = { R.drawable.broadcast_icon,R.drawable.red_circle };
    private final Integer[] visible_images={R.drawable.visible,R.drawable.invisible};

    private final LayoutInflater mInflater;
    private final Intent i;



    public Channel_list_view_adapter_mod(Context context, ArrayList<Channel_list> results) {
        channellist = results;
        this.context=context;
        mInflater = LayoutInflater.from(context);
        i=new Intent(this.context, TimeServiceGPS.class);
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

    private boolean status=false;

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
            convertView = mInflater.inflate(R.layout.channel_list_view, null);
            holder = new ViewHolder();


            holder.txtName = (TextView) convertView.findViewById(R.id.cname);
            holder.txtvnumber = (TextView) convertView.findViewById(R.id.cvnumber);
            holder.txtvtype = (TextView) convertView.findViewById(R.id.ctype);
            holder.txtcategary=(TextView)convertView.findViewById(R.id.ccategary);
            holder.broadcast=(ImageButton)convertView.findViewById(R.id.bross);
            holder.visible=(ImageButton)convertView.findViewById(R.id.visible);
            holder.channel_pic=(ImageView)convertView.findViewById(R.id.pic);
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
        holder.visible.setImageResource(channellist.get(position).getvisibleimageid());
        holder.channel_pic.setImageBitmap(channellist.get(position).getImage());

        holder.broadcast.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Global.channel_id=channellist.get(position).getChannelid().split(":")[1].trim();
                if((Global.broadcasting && Global.ch_list_pos==position) | !Global.broadcasting) {
                    if (!isMyServiceRunning(TimeServiceGPS.class)) {
                        LocationManager locationManager = (LocationManager) context
                                .getSystemService(Context.LOCATION_SERVICE);
                        boolean isGPSEnabled = locationManager
                                .isProviderEnabled(LocationManager.GPS_PROVIDER);

                        if(isGPSEnabled) {
                            context.startService(i);

                            Global.rr = channellist.get(position).getsvcategary().split(":")[2].trim();
                            Global.broadcasting = true;
                            Global.ch_list_pos = position;
                            status = true;
                            Global.channel_broadcasting_name = channellist.get(position).getsName().split(":")[1].trim();
                            Global.channel_broadcasting_vnumber = channellist.get(position).getsVnumber().split(":")[1].trim();
                            Global.channel_id_bd = channellist.get(position).getChannelid().split(":")[1].trim();
                            status_update("1", position);
                            play_sound();
                            //holder.broadcast.setImageResource(setImageid(images[0]));
                            holder.broadcast.setImageResource(R.drawable.broadcast_icon);
                            channellist.get(position).setImageid(images[0]);
                            // Broadcasting_on_Notification();
                        }
                        else
                        {
                            Toast.makeText(context,"Location services off,Enable location service from settings",Toast.LENGTH_LONG).show();
                        }
                        //subscribe();


                    } else {
                        context.stopService(i);
                        status = false;
                        Global.broadcasting = false;
                        Global.ch_list_pos = -1;
                        Global.channel_broadcasting_name="NONE";
                        Global.channel_broadcasting_vnumber="NONE";
                        Global.channel_id_bd ="none";
                        play_sound_bstop();
                        // Broadcasting_off_Notification();
                        //DatabaseReference ref=Global.firebase_dbreference.child("CHANNELS").child(Global.username).child("status");
                        status_update("0", position);
                        holder.broadcast.setImageResource(R.drawable.red_circle);
                        channellist.get(position).setImageid(images[1]);


                    }
                }
                else
                {
                    Toast.makeText(context,"Other Channel is Broadcasting",Toast.LENGTH_LONG).show();
                }

                //holder.notifyAll();
            }
        });

        holder.visible.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Global.channel_id=channellist.get(position).getChannelid().split(":")[1].trim();
                DatabaseReference user_ref=Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id);
                user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if(map.get("visible").toString().equalsIgnoreCase("1"))
                        {
                            DatabaseReference ref=Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("visible");
                            ref.setValue("0");
                            DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(Global.channel_id).child("visible");
                            ref1.setValue("0");
                            holder.visible.setImageResource(R.drawable.invisible);
                            channellist.get(position).setvisibleimageid(visible_images[1]);
                        }
                        else
                        {
                            DatabaseReference ref=Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("visible");
                            ref.setValue("1");
                            DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(Global.channel_id).child("visible");
                            ref1.setValue("1");
                            holder.visible.setImageResource(R.drawable.visible);
                            channellist.get(position).setvisibleimageid(visible_images[0]);
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
        ImageButton visible;
        ImageView channel_pic;


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


    private void status_update(final String update,final int position)
    {
        Global.channel_id=channellist.get(position).getChannelid().split(":")[1].trim();
        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(channellist.get(position).getChannelid().split(":")[1].trim()).child("followers");
        DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(channellist.get(position).getChannelid().split(":")[1].trim()).child("status");
        DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(channellist.get(position).getChannelid().split(":")[1].trim()).child("status");
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

                            DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(child.getKey()).child("Subscribers").child(channellist.get(position).getChannelid().split(":")[1].trim()).child("status");
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


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void Broadcasting_on_Notification() {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle("Broadcasting On")
                .setContentText("Channel : "+Global.channel_broadcasting_name+":"+Global.separator+Global.channel_broadcasting_vnumber)
                .setOngoing(true);

        builder.setLights(0xff00ff00, 300, 100);
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

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