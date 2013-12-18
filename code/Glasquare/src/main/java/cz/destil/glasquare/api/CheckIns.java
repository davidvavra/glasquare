package cz.destil.glasquare.api;

import java.util.List;

import cz.destil.glasquare.util.DebugLog;
import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Processor for 4sq checkings/add API.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public interface CheckIns {

    @POST("/checkins/add?shout=%23throughglass")
    void add(@Query("oauth_token") String token, @Query("venueId") String venueId, @Query("ll") String ll,
             Callback<CheckInResponse> callback);

    @POST("/checkins/{check_in_id}/addcomment")
    void addComment(@Query("oauth_token") String token, @Path("check_in_id") String checkInId, @Query("text") String text,
                    Callback<CheckInResponse> callback);

    public static class CheckInResponse extends Auth.FoursquareResponse {
        FoursquareContent response;

        public String getCheckInId() {
            return response.checkin.id;
        }

        public void printNotifications() {
            DebugLog.d("notifications=" + response.notifications);
        }
    }

    public static class AddCommentResponse extends Auth.FoursquareResponse {
    }

    // parsing classes;

    class FoursquareContent {
        FoursquareCheckin checkin;
        List<FoursquareNotification> notifications;
    }

    class FoursquareCheckin {
        String id;
    }

    class FoursquareNotification {
        String type;
        Object item;

        @Override
        public String toString() {
            return type + " : " + item;
        }
    }
}
