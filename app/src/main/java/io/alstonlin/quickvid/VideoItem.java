package io.alstonlin.quickvid;

public class VideoItem {
    private String url;
    private String id;
    private int likes;
    private int passes;

    public VideoItem(String url, String id, int likes, int passes) {
        this.url = url;
        this.id = id;
        this.likes = likes;
        this.passes = passes;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public int getLikes() {
        return likes;
    }

    public int getPasses() {
        return passes;
    }
}