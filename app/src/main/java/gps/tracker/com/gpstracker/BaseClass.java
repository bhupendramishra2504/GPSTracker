package gps.tracker.com.gpstracker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.support.design.widget.Snackbar;

/**
 * Created by bhupendramishra on 28/12/16.
 */

public class BaseClass extends AppCompatActivity
{
    public void showSnackBar(String message)
    {
        try
        {
            View view = null;
            if (this != null)
            {
                view = this.findViewById(android.R.id.content);
            }
            if (view != null)
            {
                Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
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


    public static void printLog(String tag, String message)
    {
        if (BuildConfig.DEBUG)
        {
            Log.e(tag, "***Error while call " + message + " method");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }
}
