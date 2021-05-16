package de.pacheco.bakingapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.pacheco.bakingapp.R
import de.pacheco.bakingapp.model.Recipe
import de.pacheco.bakingapp.model.Step
import de.pacheco.bakingapp.utils.getIngredients
import de.pacheco.bakingapp.utils.getNextRecipe
import de.pacheco.bakingapp.utils.getPreviousRecipe

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [StepDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class StepListActivity : AppCompatActivity() {
    private var layoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_list)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        mTwoPane = findViewById<View?>(R.id.item_detail_container) != null
        var recipe: Recipe? = intent.getParcelableExtra(this.getString(R.string.recipe))
        val recyclerView = findViewById<RecyclerView>(R.id.item_list)!!
        layoutManager = recyclerView.layoutManager as LinearLayoutManager?
        val sp = getSharedPreferences(getString(R.string.recipe), 0)
        val recipes = RecipeListFragment.recipes
        if (recipe == null && recipes.isNotEmpty()) {
            val recipeNumber = sp.getInt(StepDetailFragment.RECIPE_ID, 1) - 1
            recipe = recipes[if (recipeNumber < recipes.size) recipeNumber else 0]
        }
        if (recipe == null) {
            return
        }
        setNavigationButtonListeners(recipe)
        val stepId = intent.getIntExtra(StepDetailFragment.STEPS_ID, 0)
        val scrollPosition = stepId.coerceAtMost(recipe.steps.size - 1)
        layoutManager!!.scrollToPosition(scrollPosition)
        title = recipe.name
        setupRecyclerView(recyclerView, recipe)
    }

    private fun setNavigationButtonListeners(recipe: Recipe) {
        val nextButton = findViewById<View>(R.id.next_recipe)
        nextButton.setOnClickListener { view: View ->
            val nextRecipe = getNextRecipe(recipe)
            changeRecipe(view, nextRecipe)
        }
        val previousButton = findViewById<View>(R.id.previous_recipe)
        previousButton.setOnClickListener { view: View ->
            val prevRecipe = getPreviousRecipe(recipe)
            changeRecipe(view, prevRecipe)
        }
    }

    private fun changeRecipe(view: View, recipe: Recipe?) {
        val context = view.context
        val intent = Intent(context, StepListActivity::class.java)
        intent.putExtra(context.getString(R.string.recipe), recipe)
        context.startActivity(intent)
        val widgetIntent = Intent("my.action.string")
        widgetIntent.putExtra("howto", getIngredients(recipe!!).replace("\n\n", "\n"))
        context.sendBroadcast(widgetIntent)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_LAYOUT,
                layoutManager!!.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedRecyclerLayoutState = savedInstanceState.getParcelable<Parcelable>(BUNDLE_LAYOUT)
        layoutManager!!.onRestoreInstanceState(savedRecyclerLayoutState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, Intent(this, RecipeListFragment::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, recipe: Recipe) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, recipe, mTwoPane)
    }

    class SimpleItemRecyclerViewAdapter internal constructor(parent: StepListActivity,
                                                             recipe: Recipe, twoPane: Boolean) : RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {
        private val mParentActivity: StepListActivity = parent
        private val mValues: List<Step> = recipe.steps
        private val mTwoPane: Boolean = twoPane
        private val mOnClickListener = View.OnClickListener { view ->
            val step = view.tag as Step
            if (mTwoPane) {
                val arguments = Bundle()
                arguments.putInt(StepDetailFragment.STEPS_ID, step.id)
                arguments.putParcelable(view.context.getString(R.string.recipe),
                        recipe)
                val fragment = StepDetailFragment()
                fragment.arguments = arguments
                mParentActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit()
            } else {
                val context = view.context
                val intent = Intent(context, StepDetailActivity::class.java)
                intent.putExtra(StepDetailFragment.STEPS_ID, step.id)
                intent.putExtra(context.getString(R.string.recipe), recipe)
                context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.step_list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.mIdView.text = mValues[position].id.toString()
            holder.mContentView.text = mValues[position].shortDescription
            holder.itemView.tag = mValues[position]
            holder.itemView.setOnClickListener(mOnClickListener)
        }

        override fun getItemCount(): Int {
            return mValues.size
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val mIdView: TextView = view.findViewById(R.id.item_list_item_header)
            val mContentView: TextView = view.findViewById(R.id.item_list_item_content)
        }
    }

    companion object {
        private const val BUNDLE_LAYOUT = "BUNDLE_LAYOUT"

        /**
         * Whether or not the activity is in two-pane mode, i.e. running on a tablet
         * device.
         */
        var mTwoPane = false
    }
}