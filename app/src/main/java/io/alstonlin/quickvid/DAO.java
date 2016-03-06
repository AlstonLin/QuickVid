package io.alstonlin.quickvid;

import android.app.Activity;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Singleton Data Access Object where all Networking will be handled.
 */
public class DAO {

    public static final String BASE_URL = "http://159.203.23.36:8080";
    public static final String UPLOAD_ENDPOINT = "/upload";
    public static final String DOWNLOAD_ENDPOINT = "/download";
    public static final String LIKE_ENDPOINT = "/like/";
    public static final String PASS_ENDPOINT = "/pass/";
    public static final int URLS_PER_DOWNLOAD = 3;
    public static DAO instance;

    /**
     * Gets the Singleton DAO Object (lazy instantiation).
     * @return The Singleton
     */
    public static DAO getInstance(){
        if (instance == null) instance = new DAO();
        return instance;
    }

    private DAO(){};

    /**
     * Starts an asynchronous task to Upload the video located at activity.getFilesDir()
     * + CameraFragment.VIDEO_FILE_NAME.
     * @param activity The Activity this is being called from
     */
    public void uploadVideo(Activity activity) {
        UploadVideoTask task = new UploadVideoTask();
        task.execute(activity.getFilesDir() + CameraFragment.VIDEO_FILE_NAME);
    }

    /**
     * Makes a RESTful call and uses the information to create a list of VideoItems.
     *
     * @param adapter The FlingAdapter to add the new VideoItems to
     * @param belowId Optional; the ID that the Items IDs must be below
     * @param aboveId Optional; the ID that the Items IDs must be above
     */
    public void getVideoItems(FlingAdapter adapter, String belowId, String aboveId){
        GetVideoItemsTask task = new GetVideoItemsTask(adapter);
        task.execute(aboveId, belowId);
    }

    /**
     * Likes a given VideoItem;
     * @param item The VideoItem to like
     */
    public void likeItem(VideoItem item){
        LikeItemTask task = new LikeItemTask();
        task.execute(item);
    }

    /**
     * Passes a given VideoItem;
     * @param item The VideoItem to pass
     */
    public void passItem(VideoItem item){
        PassItemTask task = new PassItemTask();
        task.execute(item);
    }

    private class UploadVideoTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
            multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            ContentType contentType = ContentType.create("video/mp4");
            multipartEntity.addPart("content", new FileBody(new File(strings[0]), contentType, "temp.mp4"));
            HttpPost post = new HttpPost(BASE_URL + UPLOAD_ENDPOINT);
            post.setEntity(multipartEntity.build());
            HttpClient client = new DefaultHttpClient();
            try {
                client.execute(post);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                client.getConnectionManager().shutdown();
            }
            return null;
        }
    }

    private class GetVideoItemsTask extends AsyncTask<String, Void, ArrayList<VideoItem>> {

        private FlingAdapter adapter;

        public GetVideoItemsTask(FlingAdapter adapter){
            this.adapter = adapter;
        }

        @Override
        protected ArrayList<VideoItem> doInBackground(String... strings) {
            String aboveId = null;
            String belowId = null;
            if (strings.length > 0){
                aboveId = strings[0];
                belowId = strings[1];
            }
            ArrayList<VideoItem> items = new ArrayList<>();
            HttpClient client = new DefaultHttpClient();
            // Sets up POST params
            HttpPost httpPost = new HttpPost(BASE_URL + DOWNLOAD_ENDPOINT);
            List<NameValuePair> params = new ArrayList<>(2);
            params.add(new BasicNameValuePair("aboveId", aboveId));
            params.add(new BasicNameValuePair("belowId", belowId));
            params.add(new BasicNameValuePair("amount", Integer.toString(URLS_PER_DOWNLOAD)));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                JSONArray a = new JSONArray(EntityUtils.toString(entity, "UTF-8"));
                for (int i = 0; i < a.length(); i++) {
                    String url = BASE_URL + "/" + a.getJSONObject(i).getString("url");
                    String id = a.getJSONObject(i).getString("_id");
                    int likes = a.getJSONObject(i).getInt("likes");
                    int passes = a.getJSONObject(i).getInt("passes");
                    items.add(new VideoItem(url, id, likes, passes));
                }
            } catch(IOException | JSONException e){
                e.printStackTrace();
            } finally {
                client.getConnectionManager().shutdown();
            }
            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<VideoItem> results) {
            adapter.setItems(results);
        }
    }

    private class LikeItemTask extends AsyncTask<VideoItem, Void, Void> {
        @Override
        protected Void doInBackground(VideoItem... params) {
            VideoItem item = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(BASE_URL + LIKE_ENDPOINT + item.getId());
            try{
                client.execute(post);
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private class PassItemTask extends AsyncTask<VideoItem, Void, Void> {
        @Override
        protected Void doInBackground(VideoItem... params) {
            VideoItem item = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(BASE_URL + PASS_ENDPOINT + item.getId());
            try{
                client.execute(post);
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}