package cz.destil.glasquare.util;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.destil.glasquare.App;

/**
 * Set of location-related utils.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class LocationUtils {

    public static Location getLastLocation() {
        LocationManager manager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        List<String> providers = manager.getProviders(criteria, true);
        List<Location> locations = new ArrayList<Location>();
        for (String provider : providers) {
            Location location = manager.getLastKnownLocation(provider);
            if (location != null) {
                locations.add(location);
            }
        }
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location location, Location location2) {
                return (int) (location.getAccuracy() - location2.getAccuracy());
            }
        });
        if (locations.size() > 0) {
            return locations.get(0);
        }
        return null;
    }


    public static String getLatLon() {
        Location location = getLastLocation();
        if (location == null) {
            return null;
        }
        return location.getLatitude() + "," + location.getLongitude();
    }
}
