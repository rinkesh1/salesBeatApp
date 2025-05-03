package com.newsalesbeatApp.utilityclass;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

public class MockLocationChecker {
    private Context context;
    private LocationManager locationManager;

    public MockLocationChecker(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean isMockLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return android.provider.Settings.Secure.getInt(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
        }
        return false;
    }

    public boolean isLocationFromMockProvider(Location location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return location.isFromMockProvider();
        }
        return false;
    }

    public boolean isAnyLocationMock() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (String provider : locationManager.getAllProviders()) {
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null && isLocationFromMockProvider(location)) {
                    return true;
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
