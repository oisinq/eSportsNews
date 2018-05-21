package io.oisin.esportsnews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String JSON_URL = "http://content.guardianapis.com/search?q=esports&show-tags=contributor&api-key=test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Entry> entries = QueryUtils.fetchEntries(JSON_URL);


    }
}