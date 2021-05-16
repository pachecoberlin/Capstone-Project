package de.pacheco.bakingapp.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.pacheco.android.utilities.calculateNoOfColumns
import de.pacheco.bakingapp.BakingTimeWidget
import de.pacheco.bakingapp.R
import de.pacheco.bakingapp.model.Recipe
import de.pacheco.bakingapp.model.RecipesViewModel
import de.pacheco.bakingapp.utils.SimpleIdlingResource
import de.pacheco.bakingapp.utils.getIngredients

/**
 * Props for image to:
 * <div>Icon made from [Icon Fonts](http://www.onlinewebfonts.com/icon) is licensed by CC BY 3.0</div>
 */
class RecipeListFragment : Fragment() {
    @VisibleForTesting
    private var idlingResource: SimpleIdlingResource = SimpleIdlingResource()

    private var recipeRecyclerViewAdapter: RecipeRecyclerViewAdapter? = null
    private var receiver: BakingTimeWidget? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_recipe_list, container, false)
        val recyclerView = root.findViewById<View>(R.id.recipe_list)!!
        setupRecyclerView(recyclerView as RecyclerView)
        setupViewModel()
        receiver = BakingTimeWidget()
        context?.registerReceiver(receiver, IntentFilter("my.action.string"))
        return root
    }

    override fun onDestroy() {
        context?.unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun setupViewModel() {
        context ?: return
        val recipesViewModel = ViewModelProvider(
                (context as ViewModelStoreOwner?)!!).get(RecipesViewModel::class.java)

        idlingResource.setIdleState(false)
        recipesViewModel.recipes.observe((context as LifecycleOwner?)!!, { list: List<Recipe> ->
            recipes = list
            recipeRecyclerViewAdapter!!.setRecipes(recipes)
            recipeRecyclerViewAdapter!!.notifyDataSetChanged()
            idlingResource.setIdleState(true)
        })
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = GridLayoutManager(context,
                calculateNoOfColumns(context),
                GridLayoutManager.VERTICAL, false)
        recipeRecyclerViewAdapter = RecipeRecyclerViewAdapter(recipes, this)
        recyclerView.adapter = recipeRecyclerViewAdapter
    }

    class RecipeRecyclerViewAdapter internal constructor(private var mValues: List<Recipe>?, private val recipeListFragment: RecipeListFragment) : RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder>() {
        private val mOnClickListener = View.OnClickListener { view: View ->
            val activity = view.context
            val intent = Intent(activity, StepListActivity::class.java)
            val recipe = view.tag as Recipe
            intent.putExtra(activity.getString(R.string.recipe), recipe)
            activity.startActivity(intent)
            val widgetIntent = Intent("my.action.string")
            widgetIntent.putExtra("howto", getIngredients(recipe).replace("\n\n", "\n"))
            activity.sendBroadcast(widgetIntent)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recipe_list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val recipe = mValues!![position]
            val url = if (recipe.image.isEmpty()) "empty" else recipe.image
            Picasso.get().load(url).placeholder(R.drawable.ic_food).error(R.drawable.ic_food).into(
                    holder.recipeImage)
            holder.recipeTitle.text = recipe.name
            holder.recipeSubtitle.text = recipeListFragment.getString(R.string.servings,
                    recipe.servings)
            holder.itemView.tag = recipe
            holder.itemView.setOnClickListener(mOnClickListener)
        }

        override fun getItemCount(): Int {
            return if (mValues == null) 0 else mValues!!.size
        }

        fun setRecipes(recipes: List<Recipe>?) {
            mValues = recipes
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val recipeImage: ImageView = view.findViewById(R.id.recipe_image)
            val recipeTitle: TextView = view.findViewById(R.id.recipe_title)
            val recipeSubtitle: TextView = view.findViewById(R.id.recipe_subtitle)
        }
    }

    companion object {
        @JvmField
        var recipes: List<Recipe> = emptyList()
    }
}