package io.alstonlin.quickvid;

public class VideoItem {
    private String url;
    private String id;

    public VideoItem(String url, String id) {
        this.url = url;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }
}