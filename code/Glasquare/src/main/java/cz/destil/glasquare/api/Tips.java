package cz.destil.glasquare.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Processor for 4sq venues/tips API.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public interface Tips {

    public static int LIMIT_TIPS = 10;

    @GET("/venues/{venueId}/tips?limit=" + LIMIT_TIPS + Api.API_ACCESS)
    void get(@Query("oauth_token") String token, @Path("venueId") String venueId, Callback<TipsResponse> callback);

    public static class TipsResponse extends Auth.FoursquareResponse {
        FoursquareContent response;

        public List<Tip> getTips() {
            List<Tip> tips = new ArrayList<Tip>();
            for (FoursquareTipItem tipItem : response.tips.items) {
                tips.add(new Tip(tipItem.text, new Date(tipItem.createdAt * 1000), tipItem.photourl));
            }
            return tips;
        }
    }

    public static class Tip {
        public String text;
        public Date createdAt;
        public String photoUrl;

        public Tip(String text, Date createdAt, String photoUrl) {
            this.text = text;
            this.createdAt = createdAt;
            this.photoUrl = photoUrl;
        }

        @Override
        public String toString() {
            return "Tip{" +
                    "text='" + text + '\'' +
                    ", createdAt=" + createdAt +
                    ", photoUrl='" + photoUrl + '\'' +
                    '}';
        }
    }

    // parsing classes:

    class FoursquareContent {
        FoursquareTips tips;
    }

    class FoursquareTips {
        List<FoursquareTipItem> items;
    }

    class FoursquareTipItem {
        String text;
        long createdAt;
        String photourl;
    }
}
