package io.oisin.esportsnews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Oisin Quinn (@oisin1001) on 19/05/2018.
 */

public class EntryAdapter extends ArrayAdapter<Entry> {

    EntryAdapter(Context context, List<Entry> entries) {
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        // This inflates a new view if necessary
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.entry_item, parent, false);
        }

        Entry currentEntry = getItem(position);

        // This gets the textviews from the layout and sets them to the values stored in currentEntry
        TextView titleTextView = listItemView.findViewById(R.id.title);
        TextView authorTextView = listItemView.findViewById(R.id.author);
        TextView sectionTextView = listItemView.findViewById(R.id.section);
        TextView dateTextView = listItemView.findViewById(R.id.date);

        titleTextView.setText(currentEntry.getTitle());
        authorTextView.setText(currentEntry.getAuthor());
        sectionTextView.setText(currentEntry.getSection());

        // I only want to display the date part of the string, so I discard the time
        String[] dateString = currentEntry.getDate().split("T");
        String datePart = dateString[0];
        String formattedDate = null;

        // This formats the date in "Day, xth Month 20xx" format
        try {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dt.parse(datePart);

            // *** same for the format String below
            SimpleDateFormat dt1 = new SimpleDateFormat("EEE, d MMM yyyy");
            formattedDate = dt1.format(date);
        } catch (ParseException e) {
            Log.e("EntryAdapter", "Error formatting date.");
        }
        dateTextView.setText(formattedDate);

        return listItemView;
    }
}
