package de.pacheco.capstone.jokes;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.udacity.gradle.builditbigger.R;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class JokesActivity extends AppCompatActivity {
    private static final String TAG = JokesActivity.class.getSimpleName();
    public static final String JOKE = "joke";
    public static final String NAME = "name";
    public static final String JOKES = "jokes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //TODO login
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Connects to Firestore, picks one joke out off the joke list and Toasts it.
     *
     */
    public void tellJoke(final View view) {
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(JOKES)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                            String toastText = username + getString(R.string.says) + jokeText;
                            Toast.makeText(JokesActivity.this, toastText, Toast.LENGTH_LONG).show();
                        } else {
                            Log.w(TAG, "Error getting Jokes.", task.getException());
                        }
                    }
                });
    }
}