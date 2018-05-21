package io.oisin.esportsnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Entry>> {

    // This is our query URL
    // It shows esports articles, sorted by new, showing the contributor tags so we can get the author
    private static final String JSON_URL = "http://content.guardianapis.com/search?tag=sport/esports&show-tags=contributor&order-by=newest&api-key=test";
    private EntryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView entryListView = findViewById(R.id.listview);
        adapter = new EntryAdapter(this, new ArrayList<Entry>());
        entryListView.setAdapter(adapter);

        TextView emptyStateTextView = findViewById(R.id.emptyview);
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
        return new EntryLoader(this, JSON_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Entry>> loader, List<Entry> entries) {
        adapter.clear();

        // If entries contains an entry, we add it to the adapter
        if (entries != null && !entries.isEmpty()) {
            adapter.addAll(entries);
        } else {
            // Otherwise, we show the empty view
            TextView emptyView = findViewById(R.id.emptyview);
            emptyView.setText(R.string.empty);
        }

        // Either way, we hide the progress bar now
        ProgressBar bar = findViewById(R.id.progress_bar);
        bar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Entry>> loader) {
        adapter.clear();
    }
}