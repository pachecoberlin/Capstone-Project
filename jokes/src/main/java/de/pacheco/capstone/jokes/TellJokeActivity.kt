package de.pacheco.capstone.jokes

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.*
import java.util.*

class TellJokeActivity : AppCompatActivity() {
    private var mMessageAdapter: JokeAdapter? = null
    private var mMessageEditText: EditText? = null
    private var mSendButton: Button? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: AuthStateListener? = null
    private var mUsername: String? = null
    private var databaseReference: CollectionReference? = null
    private var registration: ListenerRegistration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tell_joke)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mUsername = ANONYMOUS
        databaseReference = FirebaseFirestore.getInstance().collection(JOKES)
        firebaseAuth = FirebaseAuth.getInstance()
        val mMessageListView = findViewById<ListView>(R.id.messageListView)
        mMessageEditText = findViewById(R.id.messageEditText)
        mSendButton = findViewById(R.id.sendButton)
        val jokes: List<Joke> = ArrayList()
        mMessageAdapter = JokeAdapter(this, R.layout.item_joke, jokes)
        mMessageListView.adapter = mMessageAdapter
        setTextListener()
        setButtonListener()
        setAuthenticationListener()
    }

    private fun setTextListener() {
        // Enable Send button when there's text to send
        mMessageEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                mSendButton!!.isEnabled = charSequence.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        mMessageEditText!!.filters = arrayOf<InputFilter>(LengthFilter(DEFAULT_MSG_LENGTH_LIMIT))
    }

    private fun setButtonListener() {
        // Send button writes a joke to db and clears the EditText
        mSendButton!!.setOnClickListener {
            val joke: MutableMap<String, Any?> = HashMap()
            joke["name"] = mUsername
            joke["joke"] = mMessageEditText!!.text.toString()
            databaseReference!!.add(joke).addOnSuccessListener { documentReference: DocumentReference ->
                Log.d(TAG,
                        "Joke added with ID: " + documentReference.id)
            }
                    .addOnFailureListener { e: Exception? -> Log.w(TAG, "Error adding document", e) }
            // Clear input box
            mMessageEditText!!.setText("")
        }
    }

    private fun setAuthenticationListener() {
        authStateListener = AuthStateListener { firebaseAuth: FirebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                onSignedInInitialize(user.displayName)
            } else {
                onSignedOutCleanUp()
                val providers = listOf(
                        EmailBuilder().build() //                           , new AuthUI.IdpConfig.PhoneBuilder().build(),
                        //                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                        //                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                        //                            new AuthUI.IdpConfig.TwitterBuilder().build()
                )
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN)
            }
        }
    }

    private fun onSignedInInitialize(displayName: String?) {
        mUsername = displayName
        attachDatabaseReadListener()
    }

    private fun onSignedOutCleanUp() {
        mUsername = ANONYMOUS
        mMessageAdapter!!.clear()
        removeDbListener()
    }

    private fun attachDatabaseReadListener() {
        if (registration == null) {
            registration = databaseReference!!.addSnapshotListener { snapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                if (snapshots == null) {
                    return@addSnapshotListener
                }
                for (dc in snapshots.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val joke = dc.document.toObject(Joke::class.java)
                        mMessageAdapter!!.add(joke)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (authStateListener != null) {
            firebaseAuth!!.removeAuthStateListener(authStateListener!!)
        }
        removeDbListener()
        mMessageAdapter!!.clear()
    }

    private fun removeDbListener() {
        if (registration == null) {
            return
        }
        registration!!.remove()
        registration = null
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth!!.addAuthStateListener(authStateListener!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_tell_joke, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                AuthUI.getInstance().signOut(this)
                true
            }
            android.R.id.home -> {
//            NavUtils.navigateUpTo(this, new Intent(this, RecipeListActivity.class));
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish()
            }
        }
    }

    companion object {
        private val TAG = TellJokeActivity::class.java.simpleName
        const val ANONYMOUS = "anonymous"
        const val DEFAULT_MSG_LENGTH_LIMIT = 1000
        private const val RC_SIGN_IN = 1
        const val JOKES = "jokes"
    }
}