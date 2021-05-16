package de.pacheco.sandwichclub

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import de.pacheco.sandwichclub.model.Sandwich
import de.pacheco.sandwichclub.utils.parseSandwichJson

class SandwichDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_detail)
        val ingredientsIv = findViewById<ImageView>(R.id.image_iv)
        val intent = intent
        if (intent == null) {
            closeOnError()
        }
        val position = intent!!.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION)
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError()
            return
        }
        val sandwiches = resources.getStringArray(R.array.sandwich_details)
        val json = sandwiches[position]
        val sandwich = parseSandwichJson(json)
        populateUI(sandwich)
        Picasso.get()
                .load(sandwich.image)
                .into(ingredientsIv)
        title = sandwich.mainName
    }

    private fun closeOnError() {
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun populateUI(sandwich: Sandwich) {
        setText(R.id.also_known_tv, sandwich.alsoKnownAs)
        setText(R.id.ingredients_tv, sandwich.ingredients)
        setText(R.id.origin_tv, sandwich.placeOfOrigin)
        setText(R.id.description_tv, sandwich.description)
    }

    private fun setText(id: Int, text: Any) {
        val view = findViewById<TextView>(id)
        val finalText = stripText(text.toString()) + System.lineSeparator()
        view.text = finalText
    }

    private fun stripText(originalText: String): String {
        var text = originalText
        text = if (text.startsWith("[")) text.substring(1) else text
        text = if (text.endsWith("]")) text.substring(0, text.length - 1) else text
        return text
    }

    companion object {
        const val EXTRA_POSITION = "extra_position"
        private const val DEFAULT_POSITION = -1
    }
}