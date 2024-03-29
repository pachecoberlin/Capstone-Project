package de.pacheco.popularMovies;

import de.pacheco.popularMovies.databinding.ActivityMainBinding;
import de.pacheco.popularMovies.model.Movie;
import de.pacheco.popularMovies.model.MoviesViewModel;
import de.pacheco.popularMovies.recycleviews.MoviePosterAdapter;
import de.pacheco.popularMovies.util.MoviesUtil;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import static de.pacheco.android.utilities.UtilsKt.calculateNoOfColumns;

public class MoviesFragment extends Fragment {
    public static final String BUNDLE_SELECTION = "Selection";
    private static final String TAG = MoviesFragment.class.getSimpleName();
    private static final String BUNDLE_LAYOUT = "BUNDLE_LAYOUT";
    private MoviePosterAdapter moviePosterAdapter;
    private List<Movie> movies = Collections.emptyList();
    private List<Movie> favorites;
    private List<Movie> populars;
    private List<Movie> topRated;
    private String selection = MoviesUtil.POPULAR;
    private GridLayoutManager layoutManager;
    private String oldSelection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityMainBinding binding = ActivityMainBinding.inflate(inflater);
        binding.spinnerSortBy.setOnItemSelectedListener(getSpinnerListener());
        binding.rvMovieOverview.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), calculateNoOfColumns(getContext()),
                RecyclerView.VERTICAL, false);
        binding.rvMovieOverview.setLayoutManager(layoutManager);
        moviePosterAdapter = new MoviePosterAdapter(getActivity());
        binding.rvMovieOverview.setAdapter(moviePosterAdapter);
        setupViewModel();
        //data is set within spinner listener, which is called after created
        return binding.getRoot();
    }

    /**
     * If the activity is re-created, it receives the same ViewModelProvider instance that was created by the first activity.
     */
    @SuppressWarnings("ConstantConditions")
    private void setupViewModel() {
        MoviesViewModel moviesViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);
        moviesViewModel.getFavouriteMovies().observe(getActivity(),
                list -> {
                    favorites = list;
                    if (selection.equals(MoviesUtil.FAVOURITES)) {
                        setMovieData(MoviesUtil.FAVOURITES);
                    }
                });
        moviesViewModel.getPopularMovies().observe(getActivity(),
                list -> {
                    populars = list;
                    if (selection.equals(MoviesUtil.POPULAR)) {
                        setMovieData(MoviesUtil.POPULAR);
                    }
                });
        moviesViewModel.getTopRatedMovies().observe(getActivity(),
                list -> {
                    topRated = list;
                    if (selection.equals(MoviesUtil.TOP_RATED)) {
                        setMovieData(MoviesUtil.TOP_RATED);
                    }
                });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_LAYOUT,
                layoutManager.onSaveInstanceState());
        outState.putString(BUNDLE_SELECTION, selection);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            selection = savedInstanceState.getString(BUNDLE_SELECTION);
            setMovieData(selection);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_LAYOUT);
            layoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private void setMovieData(String selection) {
        if (selection == null) {
            selection = MoviesUtil.POPULAR;
        }
        switch (selection) {
            case MoviesUtil.POPULAR:
                moviePosterAdapter.setMovieData(populars);
                movies = populars;
                break;
            case MoviesUtil.TOP_RATED:
                moviePosterAdapter.setMovieData(topRated);
                movies = topRated;
                break;
            case MoviesUtil.FAVOURITES:
            default:
                moviePosterAdapter.setMovieData(favorites);
                movies = favorites;
                break;
        }
    }


    public AdapterView.OnItemSelectedListener getSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selection = MoviesUtil.POPULAR;
                } else if (i == 1) {
                    selection = MoviesUtil.TOP_RATED;
                } else {
                    selection = MoviesUtil.FAVOURITES;
                }
                if (oldSelection != null && !oldSelection.equals(selection)) {
                    layoutManager.scrollToPositionWithOffset(0, 0);
                }
                setMovieData(selection);
                oldSelection = selection;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
    }
}