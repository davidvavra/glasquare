package cz.destil.glasquare.api;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Processor for 4sq checkings/add API.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public interface CheckIn {

    @POST("/checkins/add?shout=%23throughglass" + Api.API_ACCESS)
    void add(@Query("oauth_token") String token, @Query("venueId") String venueId, @Query("ll") String ll,
             Callback<CheckInResponse> callback);

    public static class CheckInResponse extends Auth.FoursquareResponse {
    }
}
