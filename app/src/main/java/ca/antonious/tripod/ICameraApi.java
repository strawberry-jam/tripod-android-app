package ca.antonious.tripod;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by George on 2017-10-14.
 */

public interface ICameraApi {
    @POST("/capture")
    Call<PhotoCaptureResponse> capturePhoto();
}
