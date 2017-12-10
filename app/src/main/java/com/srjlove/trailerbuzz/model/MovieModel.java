package com.srjlove.trailerbuzz.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Suraj on 11/13/2017.
 */

@Parcel // this help me alot for getting and wrapping  data as in Parcel
public class MovieModel {

    public MovieModel() {
    }

    // keep public for generating parcel class
    public int movie_id;
    public String movie_poster, movie_overview, movie_reales_date, movie_runtime, movie_original_title, movie_vote_average;


    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getMovie_poster() {
        return movie_poster;
    }

    public void setMovie_poster(String movie_poster) {
        this.movie_poster = movie_poster;
    }

    public String getMovie_overview() {
        return movie_overview;
    }

    public void setMovie_overview(String movie_overview) {
        this.movie_overview = movie_overview;
    }

    public String getMovie_reales_date() {
        return movie_reales_date;
    }

    public void setMovie_reales_date(String movie_reales_date) {
        this.movie_reales_date = movie_reales_date;
    }

    public String getMovie_runtime() {
        return movie_runtime;
    }

    public void setMovie_runtime(String movie_runtime) {
        this.movie_runtime = movie_runtime;
    }

    public String getMovie_original_title() {
        return movie_original_title;
    }

    public void setMovie_original_title(String movie_original_title) {
        this.movie_original_title = movie_original_title;
    }

    public String getMovie_vote_average() {
        return movie_vote_average;
    }

    public void setMovie_vote_average(String movie_vote_average) {
        this.movie_vote_average = movie_vote_average;
    }


}
