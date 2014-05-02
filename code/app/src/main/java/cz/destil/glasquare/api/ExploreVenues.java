package cz.destil.glasquare.api;

import java.util.ArrayList;
import java.util.List;

import cz.destil.glasquare.adapter.VenuesAdapter;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Processor for 4sq venues/explore API.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public interface ExploreVenues {

    public static int LIMIT_VENUES = 10;

    @GET("/venues/explore?openNow=1&sortByDistance=1&venuePhotos=1&limit=" + LIMIT_VENUES)
    void best(@Query("ll") String ll, Callback<ExploreVenuesResponse> callback);

    @GET("/venues/explore?sortByDistance=1&venuePhotos=1&limit=" + LIMIT_VENUES)
    void search(@Query("ll") String ll, @Query("query") String query, Callback<ExploreVenuesResponse> callback);

    @GET("/venues/{id}")
    void detail(@Path("id") String id, Callback<ExploreVenueResponse> callback);

    public static class ExploreVenuesResponse extends Api.FoursquareResponse {
        FoursquareContent response;

        public List<Venue> getVenues() {
            List<Venue> venues = new ArrayList<Venue>();
            for (FoursquareItem item : response.groups.get(0).items) {
                venues.add(parseVenue(item));
            }
            return venues;
        }

        public static Venue parseVenue(FoursquareItem item) {
            String photo = null;
            if (item.venue.photos.groups.size() > 0) {
                FoursquarePhotoGroupItem groupItem = item.venue.photos.groups.get(0).items.get(0);
                photo = groupItem.prefix + "cap" + VenuesAdapter.MAX_IMAGE_HEIGHT + groupItem.suffix;
            }
            boolean hasTips = ((item.tips != null && item.tips.size() > 0) || (item.venue.tips != null && item.venue.tips.count > 0));
            String hours = (item.venue.hours != null) ? item.venue.hours.status : null;
            String category = "";
            if (item.venue.categories.size() > 0) {
                category = item.venue.categories.get(0).name;
            }
            return new Venue(item.venue.name, category, photo, item.venue.location.distance,
                    item.venue.location.lat, item.venue.location.lng, hours, item.venue.id, hasTips);
        }
    }

    public static class ExploreVenueResponse extends Api.FoursquareResponse {
        FoursquareItem response;

        public Venue getVenue() {
            return ExploreVenuesResponse.parseVenue(response);
        }
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

    // parsing classes:

    class FoursquareContent {
        List<FoursquareGroup> groups;
    }

    class FoursquareGroup {
        List<FoursquareItem> items;
    }

    class FoursquareItem {
        FoursquareVenue venue;
        List<FoursquareTip> tips;
    }

    class FoursquareTip {

    }

    class FoursquareVenue {
        String id;
        String name;
        List<FoursquareCategory> categories;
        FoursquarePhotos photos;
        FoursquareLocation location;
        FoursquareHours hours;
        TipsCount tips;
    }

    class TipsCount {
        int count;
    }

    class FoursquareHours {
        String status;
    }

    class FoursquareLocation {
        int distance;
        double lat;
        double lng;
    }

    class FoursquareCategory {
        String name;
    }

    class FoursquarePhotos {
        List<FoursquarePhotoGroup> groups;
    }

    class FoursquarePhotoGroup {
        List<FoursquarePhotoGroupItem> items;
    }

    class FoursquarePhotoGroupItem {
        String prefix;
        String suffix;
    }
}
