package cz.destil.glasquare.api;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import cz.destil.glasquare.App;
import cz.destil.glasquare.R;
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

    @POST("/checkins/add")
    void add(@Query("venueId") String venueId, @Query("ll") String ll, @Query("shout") String shout, @Query("broadcast") String broadcast,
             @Query("llAcc") int accuracy, @Query("alt") int altitude,
             Callback<CheckInResponse> callback);

    @POST("/checkins/{check_in_id}/addcomment")
    void addComment(@Path("check_in_id") String checkInId, @Query("text") String text, Callback<CheckInResponse> callback);

    public static class CheckInResponse extends Api.FoursquareResponse {
        FoursquareContent response;

        public String getCheckInId() {
            return response.checkin.id;
        }

        public String getPrimaryNotification() {
            List<String> notifications = processNotifications();
            if (notifications.size() > 0) {
                return notifications.get(0);
            }
            return null;
        }

        private List<String> processNotifications() {
            List<String> messages = new ArrayList<String>();
            if (response.notifications != null) {
                for (Api.FoursquareNotification notification : response.notifications) {
                    String type = notification.type;
                    Api.FoursquareNotificationDetail detail = notification.item;
                    if (type.equals("tip")) {
                        if (!TextUtils.isEmpty(detail.text)) {
                            messages.add(App.get().getString(R.string.popular_tip, detail.text));
                        }
                    } else if (type.equals("leaderboard")) {
                        if (!TextUtils.isEmpty(detail.message)) {
                            messages.add(detail.message);
                        }
                    } else if (type.equals("mayorship")) {
                        if (!TextUtils.isEmpty(detail.message)) {
                            messages.add(detail.message);
                        }
                    } else if (type.equals("specials")) {
                        if (!TextUtils.isEmpty(detail.message)) {
                            messages.add(detail.message);
                        }
                    } else if (type.equals("pageUpdate")) {
                        if (!TextUtils.isEmpty(detail.shout)) {
                            messages.add(detail.shout);
                        }
                    } else if (type.equals("message")) {
                        if (!TextUtils.isEmpty(detail.message)) {
                            messages.add(detail.message);
                        }
                    }
                }
            }
            return messages;
        }

        public String getSecondaryNotification() {
            List<String> notifications = processNotifications();
            if (notifications.size() > 1) {
                return notifications.get(1);
            }
            return null;
        }
    }

    public static class AddCommentResponse extends Api.FoursquareResponse {
    }

    // parsing classes;

    class FoursquareContent {
        FoursquareCheckin checkin;
        List<Api.FoursquareNotification> notifications;
    }

    class FoursquareCheckin {
        String id;
    }
}
