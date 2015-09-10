package pixelshade.geniusloci.model;

import pixelshade.geniusloci.imgurmodel.ImageResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by pixelshade on 07/09/15.
 */
public interface ServerAPI {


    /****************************************
     * Upload
     * Server API
     */

    /**
     * @param title       #Title of image
     * @param description #Description of image
     * @param cb          Callback used for success/failures
     */
    @POST("/ghost")
    void postEntry(
            @Query("name") String title,
            @Query("content") String description,
            @Query("longitude") double longitude,
            @Query("latitude") double latitude,
            Callback<ServerNewEntryResponse> cb
    );

    @GET("/ghost")
    void getAll(
            Callback<ServerListGhostsResponse> cb
    );
    @GET("/ghost/findNear")
    void getNear(
            @Query("longitude") double longitude,
            @Query("latitude") double latitutde,
            Callback<ServerListGhostsResponse> cb
    );
}
