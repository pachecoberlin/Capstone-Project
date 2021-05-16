package de.pacheco.capstone.jokes

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import de.pacheco.capstone.jokes.databinding.FragmentJokesBinding
import java.util.*

class JokesActivityFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding: FragmentJokesBinding = FragmentJokesBinding.inflate(inflater)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        binding.btnJoker.setOnClickListener(tellJoke(binding.jokeText))
        binding.btnCreateJoke.setOnClickListener(login())
        binding.jokeText.movementMethod = ScrollingMovementMethod()
        return binding.root
    }

    private fun login(): View.OnClickListener {
        return View.OnClickListener {
            val intent = Intent(activity, TellJokeActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Connects to Firestore, picks one joke out off the joke list and Toasts it.
     */
    private fun tellJoke(jokeTextView: TextView): View.OnClickListener {
        return View.OnClickListener {
            activity?.let { FirebaseApp.initializeApp(it) }
            val db = FirebaseFirestore.getInstance()
            db.collection(JOKES)
                    .get()
                    .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                        if (task.isSuccessful && task.result != null) {
                            val rand = Random()
                            val jokes = task.result!!.documents
                            val joke = jokes[rand.nextInt(jokes.size)].data
                                    ?: return@addOnCompleteListener
                            val jokeText = joke[JOKE].toString()
                            val username = joke[NAME].toString()
                            val toastText = username + getString(
                                    R.string.says) + jokeText
                            jokeTextView.text = resources.getString(R.string.joke_text, toastText, jokeTextView.text)
                        } else {
                            Log.w(TAG, "Error getting Jokes.", task.exception)
                        }
                    }
        }
    }

    companion object {
        private val TAG = JokesActivity::class.java.simpleName
        private const val JOKE = "joke"
        private const val NAME = "name"
        private const val JOKES = "jokes"
    }
}