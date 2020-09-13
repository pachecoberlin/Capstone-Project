package de.pacheco.capstone.jokes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class JokerDbAccess {
    private static final String TAG = JokerDbAccess.class.getSimpleName();

    public static void createJoke(FirebaseFirestore db) {
        Map<String, Object> joke = new HashMap<>();
        joke.put("name", "Pacheco");
        joke.put("joke",
                "Light travels faster than sound. This is why some people appear bright until you hear them speak.");
        db.collection("jokes")
                .add(joke)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,
                                "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
