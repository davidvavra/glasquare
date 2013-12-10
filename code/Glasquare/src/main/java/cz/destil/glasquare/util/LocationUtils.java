package cz.destil.glasquare.util;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import cz.destil.glasquare.App;

/**
 * Set of location-related utils.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class LocationUtils {

	public static Location getCurrentLocation() {
		LocationManager locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		String provider = locationManager.getBestProvider(criteria, true);
		return locationManager.getLastKnownLocation(provider);
	}

	public static Location getAnyLocation() {
		LocationManager manager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		Location location = null;
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		List<String> providers = manager.getProviders(criteria, true);
		for (String provider : providers) {
			location = manager.getLastKnownLocation(provider);
			if (location != null) {
				return location;
			}
		}
		return null;
	}
}
