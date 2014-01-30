package cz.destil.glasquare.api;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Processor for 4sq photos/add API.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public interface Photos {

    @Multipart
    @POST("/photos/add")
    void add(@Query("checkinId") String checkInId, @Part("photo") TypedFile file, Callback<PhotoAddResponse> callback);

    public static class PhotoAddResponse extends Api.FoursquareResponse {
    }

}
