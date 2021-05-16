package de.pacheco.bakingapp.activities

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import de.pacheco.bakingapp.R
import de.pacheco.bakingapp.model.Recipe
import de.pacheco.bakingapp.model.Step
import de.pacheco.bakingapp.utils.getIngredients
import de.pacheco.bakingapp.utils.getNextStep
import de.pacheco.bakingapp.utils.getPreviousStep
import de.pacheco.bakingapp.utils.getStep
import java.net.URL

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [StepListActivity]
 * in two-pane mode (on tablets) or a [StepDetailActivity]
 * on handsets.
 */
class StepDetailFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    : Fragment() {
    /** The Recipe this fragment is presenting.*/
    internal var recipe: Recipe? = null

    /** The Step number this fragment is presenting.*/
    internal var step: Step? = null
    private var fragmentActivity: FragmentActivity? = null
    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null
    private var urlString: String? = null
    private var isVideo = false
    private var playbackStateListener: PlaybackStateListener? = null
    private var title: TextView? = null
    private var rootView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedStepId = savedInstanceState?.getInt(STEPS_ID) ?: -1
        val argumentStepId = arguments?.getInt(STEPS_ID) ?: 0
        val stepsId = if (savedStepId >= 0) savedStepId else argumentStepId
        fragmentActivity = activity
        if (fragmentActivity == null) {
            return
        }
        recipe = fragmentActivity!!.intent.getParcelableExtra(getString(R.string.recipe))
        if (recipe == null) {
            return
        }
        saveStepIdInShared(stepsId)
        step = getStep(recipe!!.steps, stepsId)
        title = fragmentActivity!!.findViewById(R.id.detail_title)
        setTitle()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (step != null) {
            outState.putInt(STEPS_ID, step!!.id)
        }
    }

    override fun onDestroy() {
        saveStepIdInShared(step!!.id)
        super.onDestroy()
    }

    private fun saveStepIdInShared(stepsId: Int) {
        val sp = fragmentActivity!!.getSharedPreferences(getString(R.string.recipe), 0)
        val editor = sp.edit()
        editor.putInt(RECIPE_ID, recipe!!.id)
        editor.putInt(STEPS_ID, stepsId)
        editor.apply()
    }

    private fun setTitle() {
        if (title == null) {
            return
        }
        title!!.text = step!!.shortDescription
    }

    private fun refresh(increment: Int) {
        if (recipe == null || step == null) {
            return
        }
        step = if (increment > 0) {
            getNextStep(step!!.id, recipe!!)
        } else {
            getPreviousStep(step!!.id, recipe!!)
        }
        currentWindow = 0
        playbackPosition = 0L
        if (player != null) {
            player!!.stop()
        }
        setTitle()
        setStepContents()
    }

    private fun hideSystemUiWhenInLandscape() {
        val orientation = resources.configuration.orientation
        if (!StepListActivity.mTwoPane && orientation == Configuration.ORIENTATION_LANDSCAPE && playerView != null && playerView!!.visibility == View.VISIBLE) {
            val decorView = fragmentActivity!!.window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.step_detail, container, false)
        val nextStep: FloatingActionButton? = rootView?.findViewById(R.id.next_step)
        nextStep?.setOnClickListener { refresh(1) }
        val previousStep: FloatingActionButton? = rootView?.findViewById(R.id.previous_step)
        previousStep?.setOnClickListener { refresh(-1) }
        setStepContents()
        return rootView
    }

    private fun setStepContents() {
        if (recipe != null) {
            val textView = rootView!!.findViewById<TextView>(R.id.item_detail)
            if (step != null) {
                val text = if (step!!.id == 0) getIngredients(recipe!!) else step!!.description
                textView.text = text
            }
            setupPlayer(rootView)
        }
    }

    private fun setupPlayer(rootView: View?) {
        playerView = rootView!!.findViewById(R.id.playerView)
        val textView: NestedScrollView = rootView.findViewById(R.id.item_detail_scrollview)
        val imageView = rootView.findViewById<ImageView>(R.id.step_image)
        if (step == null) {
            return
        }
        urlString = if (step!!.videoURL == null || step!!.videoURL!!.isEmpty()) step!!
                .thumbnailURL else step!!.videoURL
        if (urlString == null || urlString!!.isEmpty()) {
            switchToOnlyText(playerView, textView, imageView)
            return
        }
        playbackStateListener = PlaybackStateListener()
        val thread = Thread(setPlayerOrImageContent(playerView, textView, imageView,
                urlString!!))
        thread.start()
    }

    private fun switchToOnlyText(playerView: PlayerView?, textView: NestedScrollView?, imageView: ImageView?) {
        if (playerView != null) {
            playerView.visibility = View.INVISIBLE
        }
        if (imageView != null) {
            imageView.visibility = View.INVISIBLE
        }
        if (textView != null && textView.layoutParams != null) {
            textView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun switchToImageOrVideo(playerView: PlayerView?, textView: NestedScrollView?, imageView: ImageView?,
                                     isVideo: Boolean) {
        if (playerView != null) {
            playerView.visibility = if (isVideo) View.VISIBLE else View.INVISIBLE
            hideSystemUiWhenInLandscape()
        }
        if (imageView != null) {
            imageView.visibility = if (isVideo) View.INVISIBLE else View.VISIBLE
        }
        if (textView != null && textView.layoutParams != null) {
            textView.layoutParams.height = 0
        }
    }

    private fun setPlayerOrImageContent(playerView: PlayerView?, textView: NestedScrollView?, imageView: ImageView?, urlString: String): Runnable {
        return Runnable run@{
            try {
                val url = URL(urlString)
                val urlConnection = url.openConnection()
                val contentType = if (urlConnection == null) "" else urlConnection.contentType
                when {
                    contentType.startsWith(IMAGE) -> {
                        if (imageView == null) {
                            return@run
                        }
                        imageView.post(Runnable {
                            switchToImageOrVideo(playerView, textView, imageView, false)
                            Picasso.get().load(urlString).error(R.drawable.ic_food).into(imageView)
                        })
                    }
                    contentType.startsWith(VIDEO) -> {
                        isVideo = true
                        if (playerView == null || fragmentActivity == null) {
                            return@run
                        }
                        playerView.post(Runnable {
                            initializePlayer()
                            switchToImageOrVideo(playerView, textView, imageView, true)
                        })
                    }
                    else -> {
                        switchToOnlyText(playerView, textView, imageView)
                    }
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Error while checking URL", e)
                textView?.post { switchToOnlyText(playerView, textView, imageView) }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        if (!isVideo) {
            return
        }
        if (player == null) {
            val trackSelector = DefaultTrackSelector(fragmentActivity!!)
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())
            player = SimpleExoPlayer.Builder(fragmentActivity!!).setTrackSelector(trackSelector).build()
        }
        playerView!!.player = player
        val uri = Uri.parse(urlString)
        val mediaSource = buildMediaSource(uri)
        if (actualUrl != urlString) {
            currentWindow = 0
            playbackPosition = 0L
            actualUrl = if (urlString == null) "" else urlString!!
        }
        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)
        player!!.addListener(playbackStateListener!!)
        player!!.setMediaSource(mediaSource)
        player!!.prepare()
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.removeListener(playbackStateListener!!)
            player!!.release()
            player = null
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(fragmentActivity!!, "bakingStep")
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem
                .fromUri(uri))
    }

    private class PlaybackStateListener : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean,
                                          playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to " + stateString
                    + " playWhenReady: " + playWhenReady)
        }
    }

    companion object {
        /**
         * The fragment argument representing the Step ID that this fragment
         * represents.
         */
        const val STEPS_ID = "stepsID"
        const val RECIPE_ID = "recipeID"
        const val IMAGE = "image"
        const val VIDEO = "video"
        val TAG: String = StepDetailFragment::class.java.simpleName
        private var playWhenReady = true
        private var currentWindow = 0
        private var playbackPosition: Long = 0
        private var actualUrl = ""
    }
}