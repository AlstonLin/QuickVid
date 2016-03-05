package io.alstonlin.quickvid;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * The Data Access Object where all Networking will be handled.
 */
public class DAO {

//    /**
//     * Uploads the video that is located at activity.getFilesDir() + CameraFragment.VIDEO_FILE_NAME
//     *
//     * @param activity The activity this is being called from
//     */
//    public static void uploadVideo(Activity activity){
//        final String BASE_URL = "http://159.203.23.36:8080/";
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        UploadService service = retrofit.create(UploadService.class);
//        MediaType MEDIA_TYPE = MediaType.parse("video/mp4");
//        File file = new File(activity.getFilesDir() + CameraFragment.VIDEO_FILE_NAME);
//        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, file);
//        Call<ResponseBody> call = service.uploadVideo(requestBody);
//        call.enqueue(new Callback<ResponseBody>(){
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccess()){
//                    Log.i("NETWORKING", "SUCCESS");
//                }
//                else {
//                    Log.i("NETWORKING", "ERROR");
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }

    public static void uploadVideo(Activity activity) {
        UploadVideoTask task = new UploadVideoTask();
        task.execute(activity.getFilesDir() + CameraFragment.VIDEO_FILE_NAME);
    }

    private static class UploadVideoTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
            multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            ContentType contentType = ContentType.create("video/mp4");
            multipartEntity.addPart("content", new FileBody(new File(strings[0]), contentType, "temp.mp4"));
            HttpPost post = new HttpPost("http://159.203.23.36:8080/upload");
            post.setEntity(multipartEntity.build());
            HttpClient client = new DefaultHttpClient();
            try {
                HttpResponse response = client.execute(post);
                Log.d("asdfasd", EntityUtils.toString(response.getEntity()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}