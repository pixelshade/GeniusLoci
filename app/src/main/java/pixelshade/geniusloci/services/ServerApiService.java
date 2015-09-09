package pixelshade.geniusloci.services;

import android.content.Context;

import java.lang.ref.WeakReference;

import pixelshade.geniusloci.Constants;
import pixelshade.geniusloci.helpers.NotificationHelper;
import pixelshade.geniusloci.imgurmodel.ImageResponse;
import pixelshade.geniusloci.imgurmodel.ImgurAPI;
import pixelshade.geniusloci.imgurmodel.Upload;
import pixelshade.geniusloci.model.GhostEntry;
import pixelshade.geniusloci.model.ServerAPI;
import pixelshade.geniusloci.model.ServerNewEntryResponse;
import pixelshade.geniusloci.utils.NetworkUtils;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by pixelshade on 8.9.2015.
 */
public class ServerApiService {
    public final static String TAG = UploadImageService.class.getSimpleName();

    private WeakReference<Context> mContext;

    public ServerApiService(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    public void Execute(GhostEntry ghostEntry, Callback<ServerNewEntryResponse> callback) {
        final Callback<ServerNewEntryResponse> cb = callback;

        if (!NetworkUtils.isConnected(mContext.get())) {
            //Callback will be called, so we prevent a unnecessary notification
            cb.failure(null);
            return;
        }

        final NotificationHelper notificationHelper = new NotificationHelper(mContext.get());
        notificationHelper.createUploadingNotification();

        RestAdapter restAdapter = buildRestAdapter();

        restAdapter.create(ServerAPI.class).postEntry(
                ghostEntry.name,
                ghostEntry.content,
                new Callback<ServerNewEntryResponse>() {
                    @Override
                    public void success(ServerNewEntryResponse serverNewResponse, Response response) {
                        if (cb != null) cb.success(serverNewResponse, response);
                        if (response == null) {
                            /*
                             Notify image was NOT uploaded successfully
                            */
                            notificationHelper.createFailedUploadNotification();
                            return;
                        }
                        /*
                        Notify image was uploaded successfully
                        */
                        if (serverNewResponse != null) {
                            notificationHelper.createNotification(serverNewResponse.name, serverNewResponse.content);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (cb != null) cb.failure(error);
                        notificationHelper.createNotification(error.getKind().toString(),error.getCause().getLocalizedMessage());
//                        notificationHelper.createFailedUploadNotification();
                    }
                });
    }


    private RestAdapter buildRestAdapter() {
        RestAdapter serverAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_URL)
                .build();
        /*
        Set rest adapter logging if we're already logging
        */
        if (Constants.LOGGING)
            serverAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        return serverAdapter;
    }
}
