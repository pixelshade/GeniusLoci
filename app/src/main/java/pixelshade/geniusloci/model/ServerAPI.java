package pixelshade.geniusloci.model;

import pixelshade.geniusloci.imgurmodel.ImageResponse;
import retrofit.Callback;
import retrofit.http.Body;
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
     * @param auth        #Type of authorization for upload
     * @param title       #Title of image
     * @param description #Description of image
     * @param cb          Callback used for success/failures
     */
    @POST("/3/image")
    void postEntry(
            @Header("Authorization") String auth,
            @Query("title") String title,
            @Query("description") String description,
            Callback<ServerNewEntryResponse> cb
    );
}
