package io.oisin.esportsnews;

import android.app.LoaderManager;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Entry>> {

    // This is our query URL
    // It shows esports articles, sorted by new, showing the contributor tags so we can get the author
   // private static final String JSON_URL = "https://content.guardianapis.com/search?tag=sport/esports&show-tags=contributor&order-by=newest&page-size=10&api-key=test";
    private static final String JSON_URL = "https://content.guardianapis.com/search";
    private EntryAdapter adapter;
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView entryListView = findViewById(R.id.listview);
        adapter = new EntryAdapter(this, new ArrayList<Entry>());
        entryListView.setAdapter(adapter);

        emptyStateTextView = findViewById(R.id.emptyview);
        entryListView.setEmptyView(emptyStateTextView);

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
            // If we have a connection, we can start up the new loader
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, this);
        } else {
            // If we've no connection, we can display the empty state textview and remove the progress bar
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            emptyStateTextView.setText(R.string.no_connection);
        }
    }

    @Override
    public Loader<List<Entry>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String numArticles = sharedPrefs.getString(getString(R.string.settings_num_articles_key),
                getString(R.string.settings_num_articles_default));

        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(JSON_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("tag", "sport/esports");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", numArticles);
        uriBuilder.appendQueryParameter("api-key", "test");

        // https://content.guardianapis.com/search?tag=sport/esports&show-tags=contributor&order-by=newest&page-size=10&api-key=test

        return new EntryLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Entry>> loader, List<Entry> entries) {
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        emptyStateTextView.setText(R.string.empty);

        if (entries != null && !entries.isEmpty()) {
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}