package gps.tracker.com.gpstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bhupendramishra on 31/10/16.
 */

class Channel_search_list_view extends BaseAdapter {

    private static ArrayList<Channel_search> channellistsearch;
    //private Context context;


    private final LayoutInflater mInflater;



    public Channel_search_list_view(Context context, ArrayList<Channel_search> results) {
        channellistsearch = results;
        //this.context=context;
        mInflater = LayoutInflater.from(context);

    }

    public int getCount() {
        return channellistsearch.size();
    }

    public Object getItem(int position) {
        return channellistsearch.get(position);
    }

    // --Commented out by Inspection (14/12/16, 10:16 PM):public  void setContext(Context context){this.context=context;}

    public long getItemId(int position) {
        return position;
    }

    // --Commented out by Inspection (01/12/16, 10:04 PM):public Context getcontext(){return context;}

    // --Commented out by Inspection (14/12/16, 10:17 PM):boolean status=false;

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Channel_search_list_view.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_search_list_view, parent, false);
            holder = new Channel_search_list_view.ViewHolder();


            holder.txtName = (TextView) convertView.findViewById(R.id.cname);
            holder.txtvnumber = (TextView) convertView.findViewById(R.id.cvnumber);
            holder.txtvname = (TextView) convertView.findViewById(R.id.cvname);
            holder.txtcity=(TextView)convertView.findViewById(R.id.ccity);
            holder.txtmobile=(TextView)convertView.findViewById(R.id.cmobile);

            convertView.setTag(holder);
        } else {
            holder = (Channel_search_list_view.ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(channellistsearch.get(position).getName());
        holder.txtvnumber.setText(channellistsearch.get(position).getVnumber());
        holder.txtvname.setText(channellistsearch.get(position).getsvname());
        holder.txtchannelid=channellistsearch.get(position).getChannelid();
        holder.follower_setting=channellistsearch.get(position).getfollower();
        holder.txtcity.setText(channellistsearch.get(position).getCity());
        holder.txtmobile.setText(channellistsearch.get(position).getPhone());
        holder.vtype=channellistsearch.get(position).getvtype();
        holder.vcategory=channellistsearch.get(position).getvcategory();

        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtvnumber;
        TextView txtvname;
        String txtchannelid;
        TextView txtmobile;
        TextView txtcity;
        String vtype,vcategory,follower_setting;

    }

}


