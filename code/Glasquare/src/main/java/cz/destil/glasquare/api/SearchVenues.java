package cz.destil.glasquare.api;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Processor for 4sq venues/search API.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public interface SearchVenues {

    public static int LIMIT_VENUES = 10;

    @GET("/venues/search?intent=checkin&limit=" + LIMIT_VENUES + Api.API_ACCESS)
    void searchForCheckIn(@Query("oauth_token") String token, @Query("ll") String ll, Callback<SearchResponse> callback);

    public static class SearchResponse extends Api.FoursquareResponse {
        FoursquareContent response;

        public List<Venue> getVenues() {
            return response.venues;
        }
    }

    public static class Venue {
        public String id;
        public String name;
    }

    // parsing classes:

    class FoursquareContent {
        List<Venue> venues;
    }
}
