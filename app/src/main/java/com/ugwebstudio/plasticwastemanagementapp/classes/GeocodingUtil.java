package com.ugwebstudio.plasticwastemanagementapp.classes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingUtil {

    public static String getAddressFromCoordinates(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String addressText = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                addressText = address.getFeatureName(); // Get the first line of the address
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressText;
    }
}
