package gps.tracker.com.gpstracker;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Vishal on 2/26/2017.
 */

public class BroadCastDataAdapter extends RecyclerView.Adapter<BroadCastDataAdapter.ViewHolder> {

    private Context context;
    private List<BroadCastDataItem> broadCastDataItemList;
    private static PendingIntent pendingIntent;
    private static Intent alarmIntent,i,alarmIntent1;
    private static AlarmManager manager,manager2;
    private final Integer[] images = { R.drawable.ic_broadcast,R.drawable.red_circle };

    public BroadCastDataAdapter(Context context, List<BroadCastDataItem> broadCastDataItemList) {
        this.broadCastDataItemList = broadCastDataItemList;
        this.context = context;
        alarmIntent = new Intent(this.context, Broadcast_Receiver.class);
        i=new Intent(context, TimeServiceGPS.class);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_broadcast, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        BroadCastDataItem broadCastDataItem = broadCastDataItemList.get(position);

        holder.tv_channel_vehicle_name.setText(broadCastDataItem.getChannel_vehicle_name());
        holder.tv_channel_name.setText(broadCastDataItem.getChannel_name());
        holder.tv_vehicle_type.setText(broadCastDataItem.getVehicle_type());
        holder.tv_vehicle_category.setText(broadCastDataItem.getVehicle_category());
        holder.tv_channel_mobile_no.setText(broadCastDataItem.getChannel_mobile_no());
        holder.tv_channel_vehicle_no.setText(broadCastDataItem.getChannel_vehicle_no());
        holder.tv_vehicle_location.setText(broadCastDataItem.getVehicle_location());
        holder.iv_channel_broadcast.setImageResource(broadCastDataItem.getImageid());
        holder.civ_car.setImageBitmap(broadCastDataItem.getImage());

        if (broadCastDataItem.getStatus()) {
            Glide.with(context)
                    .load(R.drawable.ic_tick)
                    .crossFade(1000)
                    .override(500, 500)
                    .thumbnail(0.5f)
                    .centerCrop()// good for profile image
                    .into(holder.iv_status);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_busy)
                    .crossFade(1000)
                    .override(500, 500)
                    .thumbnail(0.5f)
                    .centerCrop()// good for profile image
                    .into(holder.iv_status);
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return broadCastDataItemList.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, BroadCastDataItem broadCastDataItem) {
        broadCastDataItemList.add(position, broadCastDataItem);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(BroadCastDataItem broadCastDataItem) {
        int position = broadCastDataItemList.indexOf(broadCastDataItem);
        broadCastDataItemList.remove(position);
        notifyItemRemoved(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(int position) {
        broadCastDataItemList.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView tv_channel_vehicle_name, tv_channel_name, tv_vehicle_type, tv_vehicle_category,
                tv_channel_mobile_no, tv_channel_vehicle_no,tv_vehicle_location;
        private CircleImageView civ_car;
        private ImageView iv_status, iv_channel_popup, iv_channel_broadcast;


        public ViewHolder(View v) {
            super(v);

            civ_car = (CircleImageView) v.findViewById(R.id.item_broadcast_civ_car);
            iv_status = (ImageView) v.findViewById(R.id.item_broadcast_iv_status);
            iv_channel_popup = (ImageView) v.findViewById(R.id.item_broadcast_iv_channel_popup);
            iv_channel_broadcast = (ImageView) v.findViewById(R.id.item_broadcast_iv_channel_broadcast);
            tv_channel_vehicle_name = (TextView) v.findViewById(R.id.item_broadcast_tv_channel_vehicle_name);
            tv_channel_name = (TextView) v.findViewById(R.id.item_broadcast_tv_channel_name);
            tv_vehicle_type = (TextView) v.findViewById(R.id.item_broadcast_tv_vehicle_type);
            tv_vehicle_category = (TextView) v.findViewById(R.id.item_broadcast_tv_vehicle_category);
            tv_channel_mobile_no = (TextView) v.findViewById(R.id.item_broadcast_tv_channel_mobile_no);
            tv_channel_vehicle_no = (TextView) v.findViewById(R.id.item_broadcast_tv_channel_vehicle_no);
            tv_vehicle_location = (TextView) v.findViewById(R.id.item_broadcast_tv_vehicle_location);

            iv_channel_popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    int adapposition = getAdapterPosition();
                    Log.e("List Length", position + " -- " + adapposition + " --- " + broadCastDataItemList.size());
                    Toast.makeText(context, "Popup Item Click Position =" + position, Toast.LENGTH_SHORT).show();
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    int adapposition = getAdapterPosition();
                    Log.e("List Length", position + " -- " + adapposition + " --- " + broadCastDataItemList.size());
                    Toast.makeText(context, "Item Click Position =" + position, Toast.LENGTH_SHORT).show();

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getLayoutPosition();
                    Toast.makeText(context, "Broadcast Long Click Position - "+position, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            iv_channel_broadcast.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    int adapposition = getAdapterPosition();
                    SharedPreferences prefs = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE);
                    String channel_broadcasting = prefs.getString("broadcasting", "NA");

                    Global.channel_id=broadCastDataItemList.get(position).getChannel_id();
                    if(channel_broadcasting.equalsIgnoreCase("NA")) {

                        LocationManager locationManager = (LocationManager) context
                                .getSystemService(Context.LOCATION_SERVICE);
                        boolean isGPSEnabled = locationManager
                                .isProviderEnabled(LocationManager.GPS_PROVIDER);

                        if(isGPSEnabled) {
                            if(broadCastDataItemList.get(position).getChannel_mobile_no().equalsIgnoreCase("1 min")) {
                                //status = true;
                                SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                                editor.putString("broadcasting", broadCastDataItemList.get(position).getChannel_id());
                                editor.putString("broadcasting_sticky", broadCastDataItemList.get(position).getChannel_id());
                                editor.putString("broadcasting_cmd", broadCastDataItemList.get(position).getChannel_id());
                                editor.apply();
                                status_update_v2("1", broadCastDataItemList.get(position).getChannel_id());
                                play_sound();
                                iv_channel_broadcast.setImageResource(R.drawable.ic_broadcast);

                                //channellist.get(position).setImageid(images[0]);
                                //alarmIntent.setAction("gps.tracker.com.gpstracker.Broadcast_Receiver");
                                alarmIntent.putExtra("channel_id", broadCastDataItemList.get(position).getChannel_id());
                                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                int interval = 40000;

                                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

                                //edit in v9.2

                                //pendingIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                //manager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                //int interval2 = 50000;

                                // manager2.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval2, pendingIntent);


                                //Toast.makeText(context,"Alarm service activated",Toast.LENGTH_LONG).show();
                                //Snackbar snackbar = Snackbar.make(cl1, "Broadcast Started", Snackbar.LENGTH_LONG);
                                //snackbar.show();
                                Global.show_notification_dead(context, "CHANNEL BROADCASTING", "CHANNEL : " + broadCastDataItemList.get(position).getChannel_name() + Global.separator + "Broadcast Started at" + Global.date_time());
                            }
                            else
                            {
                                SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                                editor.putString("broadcasting", broadCastDataItemList.get(position).getChannel_id());
                                editor.putString("broadcasting_sticky", broadCastDataItemList.get(position).getChannel_id());
                                editor.putString("broadcasting_cmd", broadCastDataItemList.get(position).getChannel_id());
                                editor.putString("refresh_rate", broadCastDataItemList.get(position).getChannel_mobile_no());
                                editor.apply();
                                status_update_v2("1", broadCastDataItemList.get(position).getChannel_id());
                                play_sound();
                                iv_channel_broadcast.setImageResource(R.drawable.ic_broadcast);
                                i.putExtra("refresh_rate", broadCastDataItemList.get(position).getChannel_mobile_no());
                                i.putExtra("channel_id",broadCastDataItemList.get(position).getChannel_id());
                                context.startService(i);
                                //channellist.get(position).setImageid(images[0]);

                                //Snackbar snackbar = Snackbar.make(cl1, "Broadcast Started via Job Scheduler", Snackbar.LENGTH_LONG);
                                //snackbar.show();
                                Global.show_notification_dead(context, "CHANNEL BROADCASTING : Job Scheduler", "CHANNEL : " + broadCastDataItemList.get(position).getChannel_name() + Global.separator + "Broadcast Started at" + Global.date_time());

                            }
                            // Broadcasting_on_Notification();
                        }
                        else
                        {
                            Toast.makeText(context,"Location services off,Enable location service from settings",Toast.LENGTH_LONG).show();
                        }
                        //subscribe();


                    }
                    else if(channel_broadcasting.equalsIgnoreCase(broadCastDataItemList.get(position).getChannel_id())) {
                        //context.stopService(i);
                        //status = false;
                        if (broadCastDataItemList.get(position).getChannel_mobile_no().equalsIgnoreCase("1 min")) {
                            SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                            editor.putString("broadcasting", "NA");
                            editor.putString("broadcasting_cmd", "NA");
                            editor.putString("broadcasting_sticky", "NA");

                            editor.apply();
                            play_sound_bstop();

                            status_update_v2("0", broadCastDataItemList.get(position).getChannel_id());
                            iv_channel_broadcast.setImageResource(R.drawable.broadcast_off);
                            //channellist.get(position).setImageid(images[1]);
                            Intent intent = new Intent(context, Broadcast_Receiver.class);
                            //Intent intent = new Intent(context, Br_rx.class);
                            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            manager.cancel(pendingIntent);

                            //edit in v9.2

                            // pendingIntent = PendingIntent.getBroadcast(context,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            // manager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            // manager2.cancel(pendingIntent);
                            //pendingIntent.cancel();
                            //Toast.makeText(context,"Alarm service stopped",Toast.LENGTH_LONG).show();
                            //Snackbar snackbar = Snackbar.make(cl1, "Broadcast stopped", Snackbar.LENGTH_LONG);
                            //snackbar.show();
                            NotificationManager nMgr = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            nMgr.cancelAll();
                        }
                        else
                        {

                            SharedPreferences.Editor editor = context.getSharedPreferences("GPSTRACKER", MODE_PRIVATE).edit();
                            editor.putString("broadcasting", "NA");
                            editor.putString("broadcasting_cmd", "NA");
                            editor.putString("broadcasting_sticky", "NA");

                            editor.apply();
                            play_sound_bstop();
                            context.stopService(i);
                            status_update_v2("0", broadCastDataItemList.get(position).getChannel_id());
                            iv_channel_broadcast.setImageResource(R.drawable.broadcast_off);
                            //channellist.get(position).setImageid(images[1]);
                            //Snackbar snackbar = Snackbar.make(cl1, "Broadcast stopped", Snackbar.LENGTH_LONG);
                            //snackbar.show();
                            NotificationManager nMgr = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            nMgr.cancelAll();
                        }
                    }

                    else if(!channel_broadcasting.equalsIgnoreCase(broadCastDataItemList.get(position).getChannel_id()))
                    {
                        //Toast.makeText(context,"Other Channel is Broadcasting",Toast.LENGTH_LONG).show();
                        //Snackbar snackbar = Snackbar.make(cl1, "Other Channel is Broadcasting", Snackbar.LENGTH_LONG);
                        //snackbar.show();
                    }
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

    private void status_update_v2(final String update,final String channelid)
    {
        DatabaseReference user_ref = Global.firebase_dbreference.child("CHANNELS").child(channelid).child("status");
        user_ref.setValue(update);
        //FirebaseMessaging.getInstance().subscribeToTopic(Global.username);
    }


}