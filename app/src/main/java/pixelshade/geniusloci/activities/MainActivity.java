package pixelshade.geniusloci.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pixelshade.geniusloci.model.GhostEntry;
import pixelshade.geniusloci.model.ServerNewEntryResponse;
import pixelshade.geniusloci.services.ServerApiService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import pixelshade.geniusloci.R;
import pixelshade.geniusloci.helpers.DocumentHelper;
import pixelshade.geniusloci.helpers.IntentHelper;
import pixelshade.geniusloci.imgurmodel.ImageResponse;
import pixelshade.geniusloci.imgurmodel.Upload;
import pixelshade.geniusloci.services.UploadImageService;


public class MainActivity extends AppCompatActivity {
    public final static String TAG = MainActivity.class.getSimpleName();

    private double mLatitude = 9;
    private double mLongitude = 9;
    private WeakReference<Context> mContext;

    /*
      These annotations are for ButterKnife by Jake Wharton
      https://github.com/JakeWharton/butterknife
     */
    @Bind(R.id.selected_imageview)
    ImageView uploadImage;

    @Bind(R.id.editText_upload_title)
    EditText uploadTitle;
    @Bind(R.id.editText_upload_desc)
    EditText uploadDesc;
    @Bind(R.id.til_desc)
    TextInputLayout uploadDescLayout;
//    @Bind(R.id.toolbar)
//    Toolbar toolbar;


    private Upload upload; // Upload object containging image and meta data
    private File chosenFile; //chosen file from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mLatitude = intent.getDoubleExtra("lat", 9);
        mLongitude = intent.getDoubleExtra("lon", 9);
//        setSupportActionBar(toolbar);
        mContext = new WeakReference<Context>(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri returnUri;

        if (requestCode != IntentHelper.FILE_PICK) {
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        returnUri = data.getData();
        String filePath = DocumentHelper.getPath(this, returnUri);
        //Safety check to prevent null pointer exception
        if (filePath == null || filePath.isEmpty()) return;
        chosenFile = new File(filePath);

                /*
                    Picasso is a wonderful image loading tool from square inc.
                    https://github.com/square/picasso
                 */
        Picasso.with(getBaseContext())
                .load(chosenFile)
                .placeholder(R.drawable.ic_photo_library_black)
                .fit()
                .into(uploadImage);

    }

    @OnClick(R.id.btnShowImage)
    public void showImagePicker(View v) {
        uploadImage.setVisibility(View.VISIBLE);
        uploadDescLayout.setVisibility(View.GONE);
        uploadTitle.requestFocus();
        uploadImage.clearAnimation();
    }

    @OnClick(R.id.btnShowText)
    public void showContentEditBox(View v) {
        uploadDescLayout.setVisibility(View.VISIBLE);
        uploadImage.setVisibility(View.GONE);

        uploadTitle.requestFocus();
        uploadImage.clearAnimation();
    }

    @OnClick(R.id.btnShowImage)
    public void showYoutubePicker(View v) {
        uploadDescLayout.setVisibility(View.GONE);
        uploadImage.setVisibility(View.GONE);
        uploadTitle.requestFocus();
    }


    @OnClick(R.id.btnGoToMap)
    public void goToMap(View v) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
        finish();
    }


    @OnClick(R.id.selected_imageview)
    public void onChooseImage() {
        uploadDesc.clearFocus();
        uploadTitle.clearFocus();
        IntentHelper.chooseFileIntent(this);
    }

    private void clearInput() {
        uploadTitle.setText("");
        uploadDesc.clearFocus();
        uploadDesc.setText("");
        uploadTitle.clearFocus();
        uploadImage.setImageResource(R.drawable.ic_photo_library_black);
    }


    @OnClick(R.id.fab)
    public void uploadImage() {
    /*
      Create the @Upload object
     */


        if (chosenFile == null) {
            GhostEntry entry = createEntryFromFields();
            if (entry == null) return;

            new ServerApiService(this).PostEntry(entry, new UiServerApiCallback());

        } else {
            // we are uploading image to imgur
            createUpload(chosenFile);
            new UploadImageService(this).Execute(upload, new UiImgurImageUploadCallback());
        }
    }


    private GhostEntry createEntryFromFields() {
        if (uploadTitle.getText().toString().isEmpty()) {
            uploadDesc.setError("Name cannot be empty");
        }

        if (uploadDesc.getText().toString().isEmpty()) {
            uploadDesc.setError("Content cannot be empty");
        }
        if (uploadTitle.getText().toString().isEmpty() || uploadDesc.getText().toString().isEmpty())
            return null;

        GhostEntry entry = new GhostEntry(
                uploadTitle.getText().toString(),
                uploadDesc.getText().toString(),
                mLongitude,
                mLatitude
        );


        return entry;
    }

    private void createUpload(File image) {
        upload = new Upload();

        upload.image = image;
        upload.title = uploadTitle.getText().toString();
        upload.description = uploadDesc.getText().toString();
    }

    private class UiServerApiCallback implements Callback<ServerNewEntryResponse> {

        @Override
        public void success(ServerNewEntryResponse serverNewEntryResponse, Response response) {
            clearInput();
        }


        @Override
        public void failure(RetrofitError error) {
            //Assume we have no connection, since error is null
            if (error == null) {
                Snackbar.make(findViewById(R.id.rootView), "No internet connection", Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    private class UiImgurImageUploadCallback implements Callback<ImageResponse> {

        @Override
        public void success(ImageResponse imageResponse, Response response) {
            uploadDesc.setText(imageResponse.data.link);
            GhostEntry entry = createEntryFromFields();
            if (entry == null) return;
            Context context = mContext.get();

            new ServerApiService(context).PostEntry(entry, new UiServerApiCallback());
            clearInput();

        }

        @Override
        public void failure(RetrofitError error) {
            //Assume we have no connection, since error is null
            if (error == null) {
                Snackbar.make(findViewById(R.id.rootView), "No internet connection", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
