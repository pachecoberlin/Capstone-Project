package de.pacheco.bakingapp.activities;

import de.pacheco.bakingapp.BakingTimeWidget;
import de.pacheco.bakingapp.R;
import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.RecipesViewModel;
import de.pacheco.bakingapp.utils.SimpleIdlingResource;
import de.pacheco.bakingapp.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Props for image to:
 * <div>Icon made from <a href="http://www.onlinewebfonts.com/icon">Icon Fonts</a> is licensed by CC BY 3.0</div>
 */
public class RecipeListActivity extends Fragment {

    public static List<Recipe> recipes = Collections.emptyList();
    //TODO @Reviewer how am i supposed to create the IdlingResource in my Test before the
    // onCreate method is called? Because now i create and assign it allways not only in Tests.
    @Nullable
    private SimpleIdlingResource idlingResource = new SimpleIdlingResource();
    private RecipeRecyclerViewAdapter recipeRecyclerViewAdapter;
    private BakingTimeWidget receiver;
    private Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_recipe_list, container, false);
        View recyclerView = root.findViewById(R.id.recipe_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        setupViewModel();
        Toast.makeText(getContext(),
                "A Picture is taking from <div>Icon made from <a href=\"http://www" +
                        ".onlinewebfonts" +
                        ".com/icon\">Icon Fonts</a> is licensed by CC BY 3.0</div>",
                Toast.LENGTH_LONG).show();
        receiver = new BakingTimeWidget();
        context = getActivity();
        if (context != null) {
            context.registerReceiver(receiver, new IntentFilter("my.action.string"));
        }
        return root;
    }

    @Override
    public void onDestroy() {
        if (context != null) {
            context.unregisterReceiver(receiver);
        }
        super.onDestroy();
    }
Irgendwo hier so es werden keine Rezepte angezeigt
    private void setupViewModel() {
        if (context == null) {
            return;
        }
        RecipesViewModel recipesViewModel = new ViewModelProvider(
                (ViewModelStoreOwner) context).get(RecipesViewModel.class);
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }
        recipesViewModel.getRecipes().observe((LifecycleOwner) context, list -> {
            recipes = list;
            recipeRecyclerViewAdapter.setRecipes(recipes);
            recipeRecyclerViewAdapter.notifyDataSetChanged();
            if (idlingResource != null) idlingResource.setIdleState(true);
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(context,
                Utils.calculateNoOfColumns(context),
                GridLayoutManager.VERTICAL, false));
        recipeRecyclerViewAdapter = new RecipeRecyclerViewAdapter(recipes, this);
        recyclerView.setAdapter(recipeRecyclerViewAdapter);
    }

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }

    public static class RecipeRecyclerViewAdapter
            extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {
        private final RecipeListActivity recipeListActivity;
        private final View.OnClickListener mOnClickListener = view -> {
            Context context = view.getContext();
            Intent intent = new Intent(context, StepListActivity.class);
            Recipe recipe = (Recipe) view.getTag();
            intent.putExtra(context.getString(R.string.recipe), recipe);
            context.startActivity(intent);

            Intent widgetIntent = new Intent("my.action.string");
            widgetIntent.putExtra("howto", Utils.getIngredients(recipe).replace("\n\n", "\n"));
            context.sendBroadcast(widgetIntent);
        };
        private List<Recipe> mValues;

        RecipeRecyclerViewAdapter(List<Recipe> items, RecipeListActivity recipeListActivity) {
            mValues = items;
            this.recipeListActivity = recipeListActivity;
        }

        @NonNull
        @Override
        public RecipeRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_item, parent, false);
            return new RecipeRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecipeRecyclerViewAdapter.ViewHolder holder, int position) {
            Recipe recipe = mValues.get(position);
            String url = recipe.image.isEmpty() ? "empty" : recipe.image;
            Picasso.get().load(url).placeholder(R.drawable.ic_food).error(R.drawable.ic_food).into(
                    holder.recipeImage);
            holder.recipeTitle.setText(recipe.name);
            holder.recipeSubtitle.setText(recipeListActivity.getString(R.string.servings,
                    recipe.servings));
            holder.itemView.setTag(recipe);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues == null ? 0 : mValues.size();
        }

        public void setRecipes(List<Recipe> recipes) {
            mValues = recipes;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final MaterialCardView mIdView;
            final ImageView recipeImage;
            final TextView recipeTitle;
            final TextView recipeSubtitle;

            ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.recipe_list_item_card);
                recipeImage = view.findViewById(R.id.recipe_image);
                recipeTitle = view.findViewById(R.id.recipe_title);
                recipeSubtitle = view.findViewById(R.id.recipe_subtitle);
            }
        }
    }
}