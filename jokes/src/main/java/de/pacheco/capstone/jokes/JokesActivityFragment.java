package de.pacheco.capstone.jokes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class JokesActivityFragment extends Fragment {

    private static final String TAG = JokesActivity.class.getSimpleName();
    public static final String JOKE = "joke";
    public static final String NAME = "name";
    public static final String JOKES = "jokes";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_jokes, container, false);
        AdView mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        root.findViewById(R.id.joker).setOnClickListener(tellJoke());
        return root;
    }

    /**
     * Connects to Firestore, picks one joke out off the joke list and Toasts it.
     */
    public View.OnClickListener tellJoke() {
        return v -> {
            FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }
            FirebaseApp.initializeApp(activity);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(JOKES)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Random rand = new Random();
                            List<DocumentSnapshot> jokes = task.getResult().getDocuments();
                            Map<String, Object> joke = jokes.get(
                                    rand.nextInt(jokes.size())).getData();
                            if (joke == null) {
                                return;
                            }
                            String jokeText = String.valueOf(joke.get(JOKE));
                            String username = String.valueOf(joke.get(NAME));
                            String toastText = username + getString(
                                    R.string.says) + jokeText;
                            Toast.makeText(activity, toastText,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.w(TAG, "Error getting Jokes.", task.getException());
                        }
                    });
        };
    }
}