package com.srjlove.trailerbuzz.networkHelper;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.srjlove.trailerbuzz.provider.MoviesReaderConstantContract;

/**
 * Created by Suraj on 11/18/2017.
 */

public class NetworkContentProvider extends AsyncTaskLoader<Object> {

    private Context mContext;
    private String[] projection;
    private String[] selectionArgs;
    private String selection;
    private String sortOrder;
    private String type;

    public NetworkContentProvider(Context context, String[] projection, String[] selectionArgs, String selection, String sortOrder, String type) {
        super(context);
        this.mContext = mContext;
        this.projection = projection;
        this.selectionArgs = selectionArgs;
        this.selection = selection;
        this.sortOrder = sortOrder;
        this.type = type;
    }

    @Override
    public Object loadInBackground() {

        Object mCursor = null;
        try {
            switch (type) {
                case "query":
                    mCursor = mContext.getContentResolver().query(MoviesReaderConstantContract.MovieEntry.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
                    break;
                case "delete":
                    break;
                case "insert":
                    break;
            }
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            return mCursor;
        }

        return mCursor;
    }
}
