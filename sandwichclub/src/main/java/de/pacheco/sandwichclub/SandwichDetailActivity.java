package de.pacheco.sandwichclub;

import de.pacheco.sandwichclub.model.Sandwich;
import de.pacheco.sandwichclub.utils.JsonUtils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class SandwichDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);
        ImageView ingredientsIv = findViewById(R.id.image_iv);
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        @SuppressWarnings("ConstantConditions") int position = intent.getIntExtra(EXTRA_POSITION,
                DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }
        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Sandwich sandwich = JsonUtils.parseSandwichJson(json);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }
        populateUI(sandwich);
        Picasso.get()
                .load(sandwich.getImage())
                .into(ingredientsIv);
        setTitle(sandwich.getMainName());
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void populateUI(Sandwich sandwich) {
        setText(R.id.also_known_tv, sandwich.getAlsoKnownAs());
        setText(R.id.ingredients_tv, sandwich.getIngredients());
        setText(R.id.origin_tv, sandwich.getPlaceOfOrigin());
        setText(R.id.description_tv, sandwich.getDescription());
    }

    private void setText(int id, Object text) {
        TextView view = findViewById(id);
        String finalText = stripText(text.toString()) + System.lineSeparator();
        view.setText(finalText);
    }

    private String stripText(String text) {
        text = text.startsWith("[") ? text.substring(1) : text;
        text = text.endsWith("]") ? text.substring(0, text.length() - 1) : text;
        return text;
    }
}