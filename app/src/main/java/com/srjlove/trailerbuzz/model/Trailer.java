package com.srjlove.trailerbuzz.model;

/**
 * Created by Suraj on 11/15/2017.
 */

public class Trailer {

    private String VideoThumbURL, videoURL;

    public Trailer() {}// required

    public Trailer(String videoThumbURL, String videoURL) {
        VideoThumbURL = videoThumbURL;
        this.videoURL = videoURL;
    }

    public String getVideoThumbURL() {
        return VideoThumbURL;
    }

    public void setVideoThumbURL(String videoThumbURL) {
        VideoThumbURL = videoThumbURL;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }
}
