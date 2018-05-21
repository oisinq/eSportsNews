package io.oisin.esportsnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


public class EntryLoader extends AsyncTaskLoader<List<Entry>> {

    private static final String LOG_TAG = EntryLoader.class.getName();

    private String url;

    public EntryLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Entry> loadInBackground() {
        if (url == null) {
            return null;
        }

        // This gets the data from the URL (i.e. performs the network request), parses the InputStream
        // and finally returns the list of entries
        List<Entry> entries = QueryUtils.fetchEntries(url);
        return entries;
    }
}
