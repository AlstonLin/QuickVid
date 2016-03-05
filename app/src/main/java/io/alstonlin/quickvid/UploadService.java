package io.alstonlin.quickvid;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadService {
    public static final String BASE_URL = "http://159.203.23.36:8080";
    @Multipart
    @POST("/upload")
    Call<ResponseBody> uploadVideo(@Part("content") RequestBody video);
}