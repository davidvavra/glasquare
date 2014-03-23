package cz.destil.glasquare.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

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
			if (location != null && location.getAccuracy() != 0.0f) {
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

	public static String getLatLon(Location location) {
		if (location == null) {
			return null;
		}
		return location.getLatitude() + "," + location.getLongitude();
	}

	public static int getAgeInSeconds(long time) {
		return (int) ((System.currentTimeMillis() - time) / 1000);
	}

	public static void getRecentLocation(final LocationListener listener) {
		Location last = LocationUtils.getLastLocation();
		if (last==null || LocationUtils.getAgeInSeconds(last.getTime()) > 60) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
			final LocationManager locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
			List<String> providers = locationManager.getProviders(criteria, true /* enabledOnly */);
			if (providers.size()==0) {
				listener.onLocationFailed();
				return;
			}
			locationSuccess = false;
			final android.location.LocationListener locationListener = new android.location.LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					locationSuccess = true;
					locationManager.removeUpdates(this);
					listener.onLocationAcquired(location);
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
			};

			for (String provider : providers) {
				locationManager.requestLocationUpdates(provider, 1000, 5, locationListener);
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (!locationSuccess) {
						locationManager.removeUpdates(locationListener);
						listener.onLocationFailed();
					}
				}
			}, 5000);
		} else {
			listener.onLocationAcquired(last);
		}
	}

private static boolean locationSuccess = false;

	public interface LocationListener {
		public void onLocationAcquired(Location location);

		public void onLocationFailed();
	}
}
