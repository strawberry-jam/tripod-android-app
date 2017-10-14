package ca.antonious.tripod;

import com.google.gson.annotations.SerializedName;

/**
 * Created by George on 2017-10-14.
 */

public class PhotoCaptureResponse {
    @SerializedName("name")
    private String name;
    @SerializedName("cameraModel")
    private String camerModel;
    @SerializedName("base64Image")
    private String base64EncodedImage;

    public String getName() {
        return name;
    }

    public String getCamerModel() {
        return camerModel;
    }

    public String getBase64EncodedImage() {
        return base64EncodedImage;
    }
}
