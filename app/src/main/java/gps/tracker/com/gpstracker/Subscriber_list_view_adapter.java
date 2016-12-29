package gps.tracker.com.gpstracker;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by bhupendramishra on 12/10/16.
 */

class Subscriber_list_view_adapter extends BaseAdapter {

    private static ArrayList<Suscriber_results> searchArrayList;
    // --Commented out by Inspection (01/12/16, 10:31 PM):private List<String> originalData = null;
    //private final List<String>filteredData = null;
    // --Commented out by Inspection (01/12/16, 10:31 PM):
    //private   Context context;
    //private final List<SearchResults> stocks;

    private final LayoutInflater mInflater;

    Typeface robotoLight;
    Typeface robotoThin;
    Typeface robotoBold;



    public Subscriber_list_view_adapter(Context context, ArrayList<Suscriber_results> results) {
        searchArrayList = results;
        robotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_thin.ttf");
        robotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_light.ttf");
        robotoBold = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_bold.ttf");
        mInflater = LayoutInflater.from(context);
        //mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    //public  void setContext(Context context){
      //  Subscriber_list_view_adapter.this.context =context;}

    public long getItemId(int position) {
        return position;
    }



    public View getView(final int position, View convertView, ViewGroup parent) {
       final Subscriber_list_view_adapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dashboard_row, parent, false);
            holder = new ViewHolder();

            holder.txtName = (TextView) convertView.findViewById(R.id.sname);
            holder.txtPhone = (TextView) convertView.findViewById(R.id.smobile);
            holder.txtvnumber = (TextView) convertView.findViewById(R.id.svnumber);
            holder.txtvname = (TextView) convertView.findViewById(R.id.svname);
            holder.status=(ImageView)convertView.findViewById(R.id.iv);
            holder.remove=(ImageButton)convertView.findViewById(R.id.remove);
            holder.vtype=(TextView)convertView.findViewById(R.id.vtype);
            holder.category=(TextView)convertView.findViewById(R.id.category);
            holder.icon_pic=(ImageView)convertView.findViewById(R.id.pic);
            convertView.setTag(holder);
        } else {
            holder = (Subscriber_list_view_adapter.ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(searchArrayList.get(position).getsName());
        holder.txtPhone.setText(searchArrayList.get(position).getsPhone());
        holder.txtvnumber.setText(searchArrayList.get(position).getsVnumber());
        holder.txtvname.setText(searchArrayList.get(position).getsvname());
        holder.status.setImageResource(searchArrayList.get(position).getImageid());
        holder.channelid=searchArrayList.get(position).getChannelid();
        holder.vtype.setText(searchArrayList.get(position).getvtype());
        holder.category.setText(searchArrayList.get(position).getcategory());
        holder.icon_pic.setImageBitmap(searchArrayList.get(position).getImage());
        holder.status_channel=searchArrayList.get(position).getstatus();

        holder.txtvname.setTypeface(robotoLight);
        holder.vtype.setTypeface(robotoLight);
        holder.txtName.setTypeface(robotoThin);
        holder.category.setTypeface(robotoThin);
        holder.txtvnumber.setTypeface(robotoThin);
        holder.txtPhone.setTypeface(robotoThin);


        holder.remove .setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                DatabaseReference ref=Global.firebase_dbreference.child("USERS").child(Global.username).child("Subscribers").child(holder.channelid);
                ref.setValue(null);
                DatabaseReference ref1=Global.firebase_dbreference.child("CHANNELS").child(holder.channelid).child("followers").child(Global.username);
                ref1.setValue(null);
                System.out.println("User : "+Global.username+" , "+"Channel : "+holder.txtPhone.getText().toString());
                searchArrayList.remove(position);
                //searchArrayList.clear();
                notifyDataSetChanged();

                //holder.notifyAll();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtPhone;
        TextView txtvnumber;
        TextView txtvname,category;
        ImageButton remove;
        ImageView status;
        TextView vtype;
        ImageView icon_pic;
        String status_channel;
        String channelid;

    }

// --Commented out by Inspection START (01/12/16, 10:32 PM):
//    public int getCount_filter() {
//        return filteredData.size();
//    }
// --Commented out by Inspection STOP (01/12/16, 10:32 PM)

// --Commented out by Inspection START (01/12/16, 10:32 PM):
//    public Object getItem_filter(int position) {
//        return filteredData.get(position);
//    }
// --Commented out by Inspection STOP (01/12/16, 10:32 PM)

// --Commented out by Inspection START (01/12/16, 10:32 PM):
//    public long getItemId_filter(int position) {
//        return position;
//    }
// --Commented out by Inspection STOP (01/12/16, 10:32 PM)

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
