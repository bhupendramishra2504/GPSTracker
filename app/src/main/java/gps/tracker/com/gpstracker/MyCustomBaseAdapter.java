package gps.tracker.com.gpstracker;

/**
 * Created by bhupendramishra on 10/10/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;




public class MyCustomBaseAdapter extends BaseAdapter {
    private static ArrayList<SearchResults> searchArrayList;
    // --Commented out by Inspection (01/12/16, 10:19 PM):private List<String>originalData = null;
    private final List<String>filteredData = null;
    // --Commented out by Inspection (01/12/16, 10:19 PM):private final Context context1;
    //private final List<SearchResults> stocks;

    private final LayoutInflater mInflater;
    // --Commented out by Inspection (01/12/16, 10:20 PM):Bitmap bitmap;



    public MyCustomBaseAdapter(Context context, ArrayList<SearchResults> results) {
        searchArrayList = results;
        //context1=context;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

// --Commented out by Inspection START (01/12/16, 10:20 PM):
//    public String getName1(int position)
//    {
//        return searchArrayList.get(position).getName();
//    }
// --Commented out by Inspection STOP (01/12/16, 10:20 PM)

// --Commented out by Inspection START (01/12/16, 10:20 PM):
//    public String getCity1(int position)
//    {
//        return searchArrayList.get(position).getVnumber();
//    }
// --Commented out by Inspection STOP (01/12/16, 10:20 PM)

// --Commented out by Inspection START (01/12/16, 10:20 PM):
//    public String getmobile1(int position)
//    {
//        return searchArrayList.get(position).getPhone();
//    }
// --Commented out by Inspection STOP (01/12/16, 10:20 PM)

// --Commented out by Inspection START (01/12/16, 10:20 PM):
//    public String getVname(int position)
//    {
//        return searchArrayList.get(position).getVname();
//    }
// --Commented out by Inspection STOP (01/12/16, 10:20 PM)

    public long getItemId(int position) {
        return position;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_list_layout, parent, false);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.name);
            holder.txtCityState = (TextView) convertView.findViewById(R.id.city);
            holder.txtPhone = (TextView) convertView.findViewById(R.id.mobile);
            holder.txtvname=(TextView)convertView.findViewById(R.id.vname);
            holder.txtchannelid=(TextView)convertView.findViewById(R.id.channelid);
            holder.image=(ImageView)convertView.findViewById(R.id.pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(searchArrayList.get(position).getName());
        holder.txtCityState.setText(searchArrayList.get(position).getVnumber());
        holder.txtPhone.setText(searchArrayList.get(position).getPhone());
        holder.txtvname.setText(searchArrayList.get(position).getVname());
        holder.txtchannelid.setText(searchArrayList.get(position).getChannelid());
        holder.image.setImageBitmap(searchArrayList.get(position).getImage());
        //imageset(context1,searchArrayList.get(position).getChannelid().split(":")[1].trim());
        //holder.image.setImageBitmap(searchArrayList.get(position).getChannelid());

        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtCityState;
        TextView txtPhone;
        TextView txtvname;
        TextView txtchannelid;
        ImageView image;
    }




// --Commented out by Inspection START (01/12/16, 10:20 PM):
//    public int getCount_filter() {
//        return filteredData.size();
//    }
// --Commented out by Inspection STOP (01/12/16, 10:20 PM)

// --Commented out by Inspection START (01/12/16, 10:20 PM):
//    public Object getItem_filter(int position) {
//        return filteredData.get(position);
//    }
// --Commented out by Inspection STOP (01/12/16, 10:20 PM)

// --Commented out by Inspection START (01/12/16, 10:20 PM):
//    public long getItemId_filter(int position) {
//        return position;
//    }
// --Commented out by Inspection STOP (01/12/16, 10:20 PM)

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
