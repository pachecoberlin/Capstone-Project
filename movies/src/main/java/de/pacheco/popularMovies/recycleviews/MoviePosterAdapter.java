package de.pacheco.popularMovies.recycleviews;

import de.pacheco.popularMovies.R;
import de.pacheco.popularMovies.model.Movie;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterViewHolder> {
    private final Activity activity;
    private List<Movie> movies;

    public MoviePosterAdapter(Activity mainActivity) {
        this.activity = mainActivity;
    }

    @NonNull
    @Override
    public MoviePosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.movie_poster_item, parent,
                false);
        return new MoviePosterViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviePosterViewHolder holder, int position) {
        if (movies != null && movies.size() - 1 >= position) {
            holder.setMovie(movies.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    public void setMovieData(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}
