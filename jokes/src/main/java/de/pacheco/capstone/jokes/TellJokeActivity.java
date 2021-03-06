package de.pacheco.capstone.jokes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TellJokeActivity extends AppCompatActivity {

    private static final String TAG = TellJokeActivity.class.getSimpleName();

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_SIGN_IN = 1;
    public static final String JOKES = "jokes";

    private JokeAdapter mMessageAdapter;
    private EditText mMessageEditText;
    private Button mSendButton;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    private String mUsername;
    private CollectionReference databaseReference;
    private ListenerRegistration registration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tell_joke);
        enableUpNavigation();
        mUsername = ANONYMOUS;
        databaseReference = FirebaseFirestore.getInstance().collection(JOKES);
        firebaseAuth = FirebaseAuth.getInstance();
        ListView mMessageListView = findViewById(R.id.messageListView);
        mMessageEditText = findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);
        List<Joke> jokes = new ArrayList<>();
        mMessageAdapter = new JokeAdapter(this, R.layout.item_joke, jokes);
        mMessageListView.setAdapter(mMessageAdapter);
        setTextListener();
        setButtonListener();
        setAuthenticationListener();
    }

    private void enableUpNavigation() {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void setTextListener() {
        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(
                new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
    }

    private void setButtonListener() {
        // Send button writes a joke to db and clears the EditText
        mSendButton.setOnClickListener(view -> {
            Map<String, Object> joke = new HashMap<>();
            joke.put("name", mUsername);
            joke.put("joke", mMessageEditText.getText().toString());
            databaseReference.add(joke).addOnSuccessListener(
                    documentReference -> Log.d(TAG,
                            "Joke added with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
            // Clear input box
            mMessageEditText.setText("");
        });
    }

    private void setAuthenticationListener() {
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                onSignedInInitialize(user.getDisplayName());
            } else {
                onSignedOutCleanUp();
                List<AuthUI.IdpConfig> providers = Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()
//                           , new AuthUI.IdpConfig.PhoneBuilder().build(),
//                            new AuthUI.IdpConfig.GoogleBuilder().build(),
//                            new AuthUI.IdpConfig.FacebookBuilder().build(),
//                            new AuthUI.IdpConfig.TwitterBuilder().build()
                );
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }
        };
    }

    private void onSignedInInitialize(String displayName) {
        mUsername = displayName;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanUp() {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        removeDbListener();
    }


    private void attachDatabaseReadListener() {
        if (registration == null) {
            registration = databaseReference.addSnapshotListener(
                    (snapshots, e) -> {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        if (snapshots == null) {
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Joke joke = dc.getDocument().toObject(Joke.class);
                                mMessageAdapter.add(joke);
                            }
                        }
                    });
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        removeDbListener();
        mMessageAdapter.clear();
    }

    private void removeDbListener() {
        if (registration == null) {
            return;
        }
        registration.remove();
        registration = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tell_joke, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out_menu) {
            AuthUI.getInstance().signOut(this);
            return true;
        } else if (id == android.R.id.home) {
//            NavUtils.navigateUpTo(this, new Intent(this, RecipeListActivity.class));
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
}
