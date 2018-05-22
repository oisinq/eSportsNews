package io.oisin.esportsnews;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Entry>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    // This is our base JSON url - we add the query itself on later
    private static final String JSON_URL = "https://content.guardianapis.com/search";
    private static final int ENTRY_LOADER_ID = 1;
    private EntryAdapter adapter;
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView entryListView = findViewById(R.id.listview);
        emptyTextView =  findViewById(R.id.emptyview);
        entryListView.setEmptyView(emptyTextView);

        adapter = new EntryAdapter(this, new ArrayList<Entry>());
        entryListView.setAdapter(adapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        entryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // When clicked, we open the entry's corresponding URL
                Entry currentEntry = adapter.getItem(position);
                Uri entryUri = Uri.parse(currentEntry.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, entryUri);

                startActivity(websiteIntent);
            }
        });

        // This lets us see the status of the network connection
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ENTRY_LOADER_ID, null, this);
        } else {
            // If we've no connection, we can display the empty state textview and remove the progress bar
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            emptyTextView.setText(R.string.no_connection);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        // If one of the preferences changes, we can clear the adapter and fire up the loader, which
        // will use the new input parameters from the preferences
        if (key.equals(getString(R.string.settings_num_articles_key)) || key.equals(getString(R.string.settings_order_by_key))){
            adapter.clear();

            emptyTextView.setVisibility(View.GONE);
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.VISIBLE);

            getLoaderManager().restartLoader(ENTRY_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<Entry>> onCreateLoader(int i, Bundle bundle) {
        // First, we extract the values from Preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String numArticles = sharedPrefs.getString(getString(R.string.settings_num_articles_key),
                getString(R.string.settings_num_articles_default));

        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // This builds up the query URL
        Uri baseUri = Uri.parse(JSON_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("tag", "sport/esports");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", numArticles);
        uriBuilder.appendQueryParameter("api-key", "test");

        return new EntryLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Entry>> loader, List<Entry> entries) {
        // After the loader is finished, we hide the porgress bar and reset the adapter with the new entries
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        emptyTextView.setText(R.string.empty);

        if (entries != null && !entries.isEmpty()) {
            adapter.clear();
            adapter.addAll(entries);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Entry>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This sends us to the required activity
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
