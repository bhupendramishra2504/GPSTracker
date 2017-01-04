package gps.tracker.com.gpstracker;

/**
 * Created by bhupendramishra on 03/10/16.
 */


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    private boolean canGetLocation = false;

    private Location location;
    private Location location_gps,location_network;// location
    private double latitude,latitude_network,latitude_gps; // latitude
    private double longitude,longitude_network,longitude_gps; // longitude
    private long time_stamp_network=0,time_stamp_gps=0;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute

    // Declaring a Location Manager
    private LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public GPSTracker() {
        mContext = null;
    }

   /* public GPSTracker() {
        //getLocation();
    }*/

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    /**
     * Function to get the user's current location
     *
     * @return
     */
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location_network = null;
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location_network = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location_network != null) {
                            latitude_network = location_network.getLatitude();
                            longitude_network = location_network.getLongitude();
                            time_stamp_network=location_network.getTime();
                            Date date = new Date(location_network.getTime());
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String text = sdf.format(date);
                            Toast.makeText(mContext, "Network Time Stamp for location is " + text, Toast.LENGTH_LONG).show();

                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    location_gps = null;
                    if (location_gps == null) {

                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location_gps = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location_gps != null) {
                                latitude_gps = location_gps.getLatitude();
                                longitude_gps = location_gps.getLongitude();
                                //location.getTime();
                                time_stamp_gps=location_gps.getTime();
                                Date date = new Date(location_gps.getTime());
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                String text = sdf.format(date);
                                Toast.makeText(mContext, "GPS Time Stamp for location is " + text, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
                if(time_stamp_network-time_stamp_gps>120000)
                {
                    location=location_network;
                    Toast.makeText(mContext, "selecting network location", Toast.LENGTH_LONG).show();

                }
                else
                {
                    location=location_gps;
                    Toast.makeText(mContext, "selecting Gps location", Toast.LENGTH_LONG).show();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public String getTimeStamp()
    {
        Date date = new Date(location.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String text = sdf.format(date);
        return text;
    }


    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {


                locationManager.removeUpdates(GPSTracker.this);

        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }


    public void showSettingsAlert1() {
        Toast.makeText(mContext,"Enable Location services, GPS is not enabled",Toast.LENGTH_LONG).show();
    }



    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
