package de.pacheco.popularMovies.recycleviews;

import de.pacheco.popularMovies.R;
import de.pacheco.popularMovies.model.Review;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;
    private final TextView textViewAuthor;

    public ReviewViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.tv_review);
        textViewAuthor = itemView.findViewById(R.id.tv_review_author);

    }

    public void setReview(Review review) {
        textView.setText(review.content);
        textViewAuthor.setText(review.author);
    }
}
