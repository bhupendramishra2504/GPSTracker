package gps.tracker.com.gpstracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Vishal on 2/26/2017.
 */

public class FollowsDataAdapter extends RecyclerView.Adapter<FollowsDataAdapter.ViewHolder> {

    private Context context;
    private List<FollowsDataItem> followsDataItemList;
    private static final Integer[] status_images = {R.drawable.green_circle, R.drawable.red_circle};
    private Activity activity;


    public FollowsDataAdapter(Context context, List<FollowsDataItem> followsDataItemList) {
        this.followsDataItemList = followsDataItemList;
        this.context = context;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follows, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        FollowsDataItem followsDataItem = followsDataItemList.get(position);


        holder.tv_subscriber_vehicle_name.setText(followsDataItem.getSubscriber_vehicle_name());
        holder.tv_subscriber_name.setText(followsDataItem.getSubscriber_name());
        holder.tv_vehicle_type.setText(followsDataItem.getVehicle_type());
        holder.tv_vehicle_category.setText(followsDataItem.getVehicle_category());
        holder.tv_subscriber_mobile_no.setText(followsDataItem.getSubscriber_mobile_no());
        holder.tv_subscriber_vehicle_no.setText(followsDataItem.getSubscriber_vehicle_no());
        holder.tv_time.setText(followsDataItem.getTime());
        holder.tv_vehicle_location.setText(followsDataItem.getVehicle_location());
        holder.iv_status.setImageResource(followsDataItem.getImageid());
        holder.civ_car.setImageBitmap(followsDataItem.getImage());

        /*Glide.with(context)
                .load(R.drawable.ic_car)
                .crossFade(1000)
                .override(500, 500)
                .thumbnail(0.5f)
                .centerCrop()// good for profile image
                .into(holder.civ_car);*/

      /*  if ("Active".equalsIgnoreCase(followsDataItem.getStatus())) {
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
        }*/

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return followsDataItemList.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, FollowsDataItem followsDataItem) {
        followsDataItemList.add(position, followsDataItem);
        notifyItemInserted(position);
    }

    public void setActivity(Activity activity)
    {
        this.activity=activity;
    }



    // Remove a RecyclerView item containing a specified Data object
    public void remove(FollowsDataItem directoryDataItem) {
        int position = followsDataItemList.indexOf(directoryDataItem);
        followsDataItemList.remove(position);
        notifyItemRemoved(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(int position) {
        followsDataItemList.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView tv_subscriber_vehicle_name, tv_subscriber_name, tv_vehicle_type,tv_vehicle_category,
                tv_subscriber_mobile_no,tv_subscriber_vehicle_no,tv_time,tv_vehicle_location;
        private CircleImageView civ_car;
        private ImageView iv_status;


        public ViewHolder(View v) {
            super(v);

            civ_car = (CircleImageView) v.findViewById(R.id.item_follows_civ_car);
            iv_status = (ImageView) v.findViewById(R.id.item_follows_iv_status);
            tv_subscriber_vehicle_name = (TextView) v.findViewById(R.id.item_follows_tv_subscriber_vehicle_name);
            tv_subscriber_name = (TextView) v.findViewById(R.id.item_follows_tv_subscriber_name);
            tv_vehicle_type = (TextView) v.findViewById(R.id.item_follows_tv_vehicle_type);
            tv_vehicle_category = (TextView) v.findViewById(R.id.item_follows_tv_vehicle_category);
            tv_subscriber_mobile_no = (TextView) v.findViewById(R.id.item_follows_tv_subscriber_mobile_no);
            tv_subscriber_vehicle_no = (TextView) v.findViewById(R.id.item_follows_tv_subscriber_vehicle_no);
            tv_vehicle_location = (TextView) v.findViewById(R.id.item_follows_tv_vehicle_location);
            tv_time = (TextView) v.findViewById(R.id.item_follows_tv_time);

            /*civ_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    int adapposition = getAdapterPosition();
                    Log.e("List Length", position + " -- " +adapposition+" --- "+directoryDataItemList.size());

                    if (position > -1 && position<directoryDataItemList.size()) {
                        ViewParent v1 = v.getParent();
                        mExplosionField.explode(v);
                        remove(position);

                    } else
                        Log.e("List Length", position + " --- " +directoryDataItemList.size());
                }
            });*/


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    int adapposition = getAdapterPosition();
                    String status;
                    Log.e("List Length", position + " -- " + adapposition + " --- " + followsDataItemList.size());
                    if (!followsDataItemList.get(position).getChannel_id().equalsIgnoreCase(null) && !followsDataItemList.get(position).getChannel_id().equalsIgnoreCase("") && !followsDataItemList.get(position).getChannel_id().equalsIgnoreCase("NA")) {
                        String subscriber_invite = followsDataItemList.get(position).getChannel_id();
                        String subscriber = subscriber_invite;
                        String block_unblock = followsDataItemList.get(position).getStatus();
                        String subscriber_name = followsDataItemList.get(position).getSubscriber_vehicle_name();
                        if (followsDataItemList.get(position).getImageid() == status_images[0]) {
                            status = "online";
                        } else {
                            status = "offline";
                        }
                        if (block_unblock.equalsIgnoreCase("1") | subscriber_invite.equalsIgnoreCase("Demo")) {


                            Intent i1 = new Intent(context, Map_activity.class);
                            i1.putExtra("subscriber", subscriber);
                            i1.putExtra("status", status);
                            i1.putExtra("name", subscriber_name);
                            i1.putExtra("vnumber", followsDataItemList.get(position).getSubscriber_vehicle_no());
                            context.startActivity(i1);
                            //System.gc();
                            Activity activity=(Activity)context;
                            activity.finish();

                        }


                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getLayoutPosition();
                    Toast.makeText(context, "Follow Long Click Position - "+position, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }


    }
}
