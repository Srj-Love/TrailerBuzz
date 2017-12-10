package com.srjlove.trailerbuzz.networkHelper;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Suraj on 12/18/2017.
 * This class mainly request the Url data from Internet
 * and in response will get a JSON data which is in String format
 *
 * Looks pretty similar to an AsyncTask, but AsyncTaskLoader can results in a member variable and immediately return them back after configuration changes.
 */

public class NetworkAsyncTaskLoader extends android.support.v4.content.AsyncTaskLoader<String> {

    private String URL;
    private static final String TAG = "NetworkAsyncTaskLoader";

    public NetworkAsyncTaskLoader(Context context, String URL) {
        super(context);
        this.URL = URL;
    }

    // doing from https://stackoverflow.com/questions/20279216/asynctaskloader-basic-example-android
    @Override
    public String loadInBackground() {

        //referring from :  http://square.github.io/okhttp/
        OkHttpClient client = new OkHttpClient();
        String jsonResponse;
        Request request = new Request.Builder().url(URL).build();

        try {
            Response response = client.newCall(request).execute();
            jsonResponse = response.body().string();
            Log.d(TAG, "loadInBackground: JSON DATA \n" + jsonResponse);
        } catch (IOException e) {
            return null;
        }
        return jsonResponse;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading: checking on loading starts:");
        super.onStartLoading();
    }
}
