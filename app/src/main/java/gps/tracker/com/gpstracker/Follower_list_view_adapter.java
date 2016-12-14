package gps.tracker.com.gpstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhupendramishra on 12/10/16.
 */

class Follower_list_view_adapter extends BaseAdapter {
    private static ArrayList<Follower_results> searchArrayList;
    // --Commented out by Inspection (01/12/16, 10:06 PM):private List<String> originalData = null;
    private final List<String>filteredData = null;
    //private final List<SearchResults> stocks;

    private final LayoutInflater mInflater;



    public Follower_list_view_adapter(Context context, ArrayList<Follower_results> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

// --Commented out by Inspection START (01/12/16, 10:06 PM):
//    public String getName1(int position)
//    {
//        return searchArrayList.get(position).getfName();
//    }
// --Commented out by Inspection STOP (01/12/16, 10:06 PM)

// --Commented out by Inspection START (01/12/16, 10:06 PM):
//    public String getmobile1(int position)
//    {
//        return searchArrayList.get(position).getfPhone();
//    }
// --Commented out by Inspection STOP (01/12/16, 10:06 PM)


    public long getItemId(int position) {
        return position;
    }



    public View getView(final int position, View convertView, ViewGroup parent) {
        final Follower_list_view_adapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.follower_list_view, parent, false);
            holder = new ViewHolder();

            holder.txtName = (TextView) convertView.findViewById(R.id.fname);
            holder.txtPhone = (TextView) convertView.findViewById(R.id.fmobile);
            holder.block=(ImageButton) convertView.findViewById(R.id.block);
            convertView.setTag(holder);
        } else {
            holder = (Follower_list_view_adapter.ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(searchArrayList.get(position).getfName());
        holder.txtPhone.setText(searchArrayList.get(position).getfPhone());
        holder.block.setImageResource(searchArrayList.get(position).getImageid());
        holder.block.setTag(R.id.resource,R.drawable.unblock_icon);
        holder.status=searchArrayList.get(position).getstatus();
        holder.block.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(Global.block) {
                    holder.block.setImageResource(R.drawable.unblock_icon);
                    holder.block.setTag(R.id.resource,R.drawable.unblock_icon);
                    holder.status="0";
                    Global.block=false;
                    DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(searchArrayList.get(position).getfPhone()).child("Subscribers").child(Global.channel_id).child("unblock");
                    ref.setValue("1");
                    DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("followers").child(searchArrayList.get(position).getfPhone()).child("unblock");
                    ref2.setValue("1");
                }
                else
                {
                    holder.block.setImageResource(R.drawable.block_icon);
                    holder.block.setTag(R.id.resource,R.drawable.block_icon);
                    holder.status="1";
                    Global.block=true;
                    DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(searchArrayList.get(position).getfPhone()).child("Subscribers").child(Global.channel_id).child("unblock");
                    ref.setValue("0");
                    DatabaseReference ref2=Global.firebase_dbreference.child("CHANNELS").child(Global.channel_id).child("followers").child(searchArrayList.get(position).getfPhone()).child("unblock");
                    ref2.setValue("0");
                }
                //holder.notifyAll();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtPhone;
        ImageButton block;
        String status;

    }

// --Commented out by Inspection START (01/12/16, 10:06 PM):
//    public int getCount_filter() {
//        return filteredData.size();
//    }
// --Commented out by Inspection STOP (01/12/16, 10:06 PM)

// --Commented out by Inspection START (01/12/16, 10:06 PM):
//    public Object getItem_filter(int position) {
//        return filteredData.get(position);
//    }
// --Commented out by Inspection STOP (01/12/16, 10:06 PM)

// --Commented out by Inspection START (01/12/16, 10:07 PM):
//    public long getItemId_filter(int position) {
//        return position;
//    }
// --Commented out by Inspection STOP (01/12/16, 10:07 PM)

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
}
