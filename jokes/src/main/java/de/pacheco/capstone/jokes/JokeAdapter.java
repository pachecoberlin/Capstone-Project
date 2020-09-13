package de.pacheco.capstone.jokes;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class JokeAdapter extends ArrayAdapter<Joke> {
    public JokeAdapter(Context context, int resource, List<Joke> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(
                    R.layout.item_joke, parent, false);
        }
        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        Joke joke = getItem(position);
        if (joke == null) {
            return convertView;
        }
        messageTextView.setText(joke.getJoke());
        authorTextView.setText(joke.getName());
        return convertView;
    }
}