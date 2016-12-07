package gps.tracker.com.gpstracker;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by bhupendramishra on 05/12/16.
 */

public class Channel_list_Rv_adapter extends RecyclerView.Adapter<Channel_list_Rv_adapter.ViewHolder>{

    private static ArrayList<Channel_list> mDataset;
    private  Context context;
    private final Integer[] images = { R.drawable.broadcast_icon,R.drawable.red_circle };
    private final Intent i;
    private boolean status=false;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtName,txtvnumber,txtvtype,txtcategary,vname;
        public ImageView channel_pic;
        public ImageButton broadcast,visible;


        public ViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.cname);
            txtvnumber = (TextView) v.findViewById(R.id.cvnumber);
            txtvtype = (TextView) v.findViewById(R.id.ctype);
            txtcategary=(TextView)v.findViewById(R.id.ccategary);
            broadcast=(ImageButton)v.findViewById(R.id.bross);
            visible=(ImageButton)v.findViewById(R.id.visible);
            channel_pic=(ImageView)v.findViewById(R.id.pic);
            vname=(TextView)v.findViewById(R.id.cvname);
        }
    }

        @Override
        public Channel_list_Rv_adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_list_view, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            //final String name = mDataset.get(position);
            holder.txtName.setText(mDataset.get(position).getsName());
            holder.txtvnumber.setText(mDataset.get(position).getsVnumber());
            holder.txtvtype.setText(mDataset.get(position).getsvtype());
            holder.txtcategary.setText(mDataset.get(position).getsvcategary());
            holder.vname.setText(mDataset.get(position).getvname());
            holder.visible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Global.channel_id=mDataset.get(position).getChannelid().split(":")[1].trim();
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

                            }
                            else
                            {
                                DatabaseReference ref=Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("visible");
                                ref.setValue("1");
                                DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(Global.channel_id).child("visible");
                                ref1.setValue("1");
                                holder.visible.setImageResource(R.drawable.visible);
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

            holder.broadcast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Global.channel_id=mDataset.get(position).getChannelid().split(":")[1].trim();
                    if((Global.broadcasting && Global.ch_list_pos==position) | !Global.broadcasting) {
                        if (!isMyServiceRunning(TimeServiceGPS.class)) {
                            LocationManager locationManager = (LocationManager) context
                                    .getSystemService(Context.LOCATION_SERVICE);
                            boolean isGPSEnabled = locationManager
                                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

                            if(isGPSEnabled) {
                                context.startService(i);

                                Global.rr = mDataset.get(position).getsvcategary().split(":")[2].trim();
                                Global.broadcasting = true;
                                Global.ch_list_pos = position;
                                status = true;
                                Global.channel_broadcasting_name = mDataset.get(position).getsName().split(":")[1].trim();
                                Global.channel_broadcasting_vnumber = mDataset.get(position).getsVnumber().split(":")[1].trim();
                                Global.channel_id_bd = mDataset.get(position).getChannelid().split(":")[1].trim();
                                status_update("1", position);
                                play_sound();
                                //holder.broadcast.setImageResource(setImageid(images[0]));
                                holder.broadcast.setImageResource(R.drawable.broadcast_icon);
                                Broadcasting_on_Notification();
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


                        }
                    }
                    else
                    {
                        Toast.makeText(context,"Other Channel is Broadcasting",Toast.LENGTH_LONG).show();
                    }

                }
            });




        }





    public Channel_list_Rv_adapter(ArrayList<Channel_list> myDataset,Context context) {
        this.context=context;
        mDataset = myDataset;
        i=new Intent(this.context, TimeServiceGPS.class);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



    private void status_update(final String update,final int position)
    {
        Global.channel_id=mDataset.get(position).getChannelid().split(":")[1].trim();
        DatabaseReference user_ref = Global.firebase_dbreference.child("USERS").child(mDataset.get(position).getChannelid().split(":")[1].trim()).child("followers");
        DatabaseReference ref1=Global.firebase_dbreference.child("USERS").child(Global.username).child("channels").child(mDataset.get(position).getChannelid().split(":")[1].trim()).child("status");
        //ref1.onDisconnect().setValue("0");
        ref1.setValue(update);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);

        if(user_ref!=null) {

            user_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        if (child != null) {

                            DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(child.getKey()).child("Subscribers").child(mDataset.get(position).getChannelid().split(":")[1].trim()).child("status");
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


}
