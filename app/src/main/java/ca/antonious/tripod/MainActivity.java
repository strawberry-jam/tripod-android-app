package ca.antonious.tripod;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.most_recent_picture) protected ImageView mostRecentPictureImageView;

    private ICameraApi cameraApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ensurePermissions();
        ensureCameraApi();
    }

    @OnClick(R.id.take_photo_button)
    protected void onCaptureClicked() {
        cameraApi.capturePhoto().enqueue(new Callback<PhotoCaptureResponse>() {
            @Override
            public void onResponse(Call<PhotoCaptureResponse> call, Response<PhotoCaptureResponse> response) {
                savePhoto(response.body().getBase64EncodedImage());
            }

            @Override
            public void onFailure(Call<PhotoCaptureResponse> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.open_gallery_button)
    protected void openPhotoGallery() {
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        newIntent.setType("image/*");
        startActivity(newIntent);
    }

    private void ensurePermissions() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        showMostRecentImage();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();
    }

    private void ensureCameraApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cameraApi = retrofit.create(ICameraApi.class);
    }

    private void showMostRecentImage() {
        Glide.with(this)
             .load(Uri.fromFile(new File(getMostRecentPhotoUrl())))
             .transition(DrawableTransitionOptions.withCrossFade())
             .into(mostRecentPictureImageView);
    }

    private String getMostRecentPhotoUrl() {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };

        final Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        cursor.moveToNext();
        return cursor.getString(1);
    }

    private void savePhoto(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        MediaStore.Images.Media.insertImage(getContentResolver(), decodedByte, UUID.randomUUID().toString(), "from Canon E0S 60D");
    }
}