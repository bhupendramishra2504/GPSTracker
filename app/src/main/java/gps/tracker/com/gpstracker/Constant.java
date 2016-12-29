package gps.tracker.com.gpstracker;

/**
 * Created by bhupendramishra on 29/12/16.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.common.api.Status;


/**
 * Created by shailesh on 9/19/2016.
 */

@SuppressLint({ "CommitPrefEdits", "UseValueOf" })
public class Constant
{
    private static final String TAG = "[Constant]";

    Activity activity;

    @SuppressLint("CommitPrefEdits")
    public Constant (Activity activity)
    {
        this.activity = activity;
    }

    public void showSnackBar(String message)
    {
        try
        {
            View view = null;
            if (activity != null)
            {
                view = activity.findViewById(android.R.id.content);
            }
            if (view != null)
            {
                Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
                TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snackbar.show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showActionSnackBar(String message, String action)
    {
        try
        {
            View view = null;
            if (activity != null)
            {
                view = activity.findViewById(android.R.id.content);
            }
            if (view != null)
            {
                Snackbar snackbar =Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                        .setAction(action, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //write code when user click on snackBar action

                            }
                        });
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
                TextView tv = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snackbar.setActionTextColor(activity.getResources().getColor(R.color.white));
                snackbar.show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ActionBar actionBar;
    public void setUpActionBar(String title, ActionBar actionBar, boolean isVisible)
    {
        try
        {
            this.actionBar = actionBar;
            //this used for show back arrow in activity
            if (isVisible)
            {
                this.actionBar.setDisplayShowCustomEnabled(true);
                this.actionBar.setDisplayHomeAsUpEnabled(true);
//                this.actionBar.setHomeAsUpIndicator(R.mipmap.ic_action_bar_go_back);//custom back arrow
            }
            else
            {
                this.actionBar.setDisplayShowCustomEnabled(false);
                this.actionBar.setDisplayHomeAsUpEnabled(false);
//                this.actionBar.setHomeAsUpIndicator(R.mipmap.ic_action_bar_go_back);//custom back arrow
            }
            this.actionBar.setTitle(Html.fromHtml("<large>"+ title +"</large>"));


        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void printError(String tag, String message)
    {
        if (BuildConfig.DEBUG)
        {
            Log.e(tag, "***Error while call " + message + " method");
        }
    }
    public void printInfo(String tag, String message)
    {
        if (BuildConfig.DEBUG)
        {
            Log.i(tag, message);
        }
    }
}
