/**
 * 
 */
package com.byod.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * @author ifay
 *
 */
public class LocationUtils implements LocationListener{

    private static final String TAG = "LocationUtils";
    private static Double latitude,longitude;
    private LocationManager locationMgr;
    
    public LocationUtils(Context context) {
        locationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this );
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    /**
     * 
     * @return latitude[space]longitude
     */
    public String getLocation() {
        String loc = latitude.toString()+" "+longitude.toString();
        Log.d(TAG,"location is:"+loc);
        return loc;
    }
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

}
