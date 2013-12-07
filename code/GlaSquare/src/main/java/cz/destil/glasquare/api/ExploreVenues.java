package cz.destil.glasquare.api;

import java.util.ArrayList;
import java.util.List;

import cz.destil.glasquare.adapter.VenuesAdapter;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Processor for 4sq venues/explore API.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public interface ExploreVenues {

    public static int LIMIT_VENUES = 10;

    @GET("/venues/explore?limit=" + LIMIT_VENUES + "&client_id=" + Api.CLIENT_ID + "&client_secret=" + Api.CLIENT_SECRET + "&v=" + Api.BUILD_DATE +
            "&openNow=1&sortByDistance=1&venuePhotos=1")
    void get(@Query("ll") String ll, Callback<ExploreVenuesResponse> callback);

    public static class ExploreVenuesResponse {
        public FoursquareResponse response;

        public List<Venue> getVenues() {
            List<Venue> venues = new ArrayList<Venue>();
            for (FoursquareItem group : response.groups.get(0).items) {
                String photo = null;
                if (group.venue.photos.groups.size() > 0) {
                    FoursquarePhotoGroupItem item = group.venue.photos.groups.get(0).items.get(0);
                    photo = item.prefix + "cap" + VenuesAdapter.MAX_IMAGE_HEIGHT + item.suffix;
                }
                venues.add(new Venue(group.venue.name, group.venue.categories.get(0).name, photo, group.venue.location.distance,
                        group.venue.location.lat, group.venue.location.lng, group.venue.hours.status, group.venue.id, group.tips.size() > 0));
            }
            return venues;
        }
    }

    public static class FoursquareResponse {
        List<FoursquareGroup> groups;
    }

    public static class FoursquareGroup {
        List<FoursquareItem> items;
    }

    public static class FoursquareItem {
        FoursquareVenue venue;
        List<FoursquareTip> tips;
    }

    public static class FoursquareTip {

    }

    public static class FoursquareVenue {
        public String id;
        public String name;
        public List<FoursquareCategory> categories;
        public FoursquarePhotos photos;
        public FoursquareLocation location;
        public FoursquareHours hours;
    }

    public static class FoursquareHours {
        public String status;
    }

    public static class FoursquareLocation {
        public int distance;
        public double lat;
        public double lng;
    }

    public static class FoursquareCategory {
        public String name;
    }

    public static class FoursquarePhotos {
        public List<FoursquarePhotoGroup> groups;
    }

    public static class FoursquarePhotoGroup {
        public List<FoursquarePhotoGroupItem> items;
    }

    public static class FoursquarePhotoGroupItem {
        public String prefix;
        public String suffix;
    }

    public static class Venue {
        public String id;
        public String name;
        public String category;
        public String imageUrl;
        public int distance;
        public double latitude;
        public double longitude;
        public String hours;
        public boolean hasTips;

        public Venue(String name, String category, String imageUrl, int distance, double latitude, double longitude, String hours, String id,
                     boolean hasTips) {
            this.name = name;
            this.category = category;
            this.imageUrl = imageUrl;
            this.distance = distance;
            this.latitude = latitude;
            this.longitude = longitude;
            this.hours = hours;
            this.id = id;
            this.hasTips = hasTips;
        }

        @Override
        public String toString() {
            return "Venue{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", category='" + category + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", distance=" + distance +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", hours='" + hours + '\'' +
                    ", hasTips=" + hasTips +
                    '}';
        }
    }
}
