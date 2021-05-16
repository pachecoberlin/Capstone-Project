package de.pacheco.capstone.jokes

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class JokeAdapter(context: Context?, resource: Int, objects: List<Joke?>?) : ArrayAdapter<Joke?>(context!!, resource, objects!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
                ?: (context as Activity).layoutInflater.inflate(R.layout.item_joke, parent, false)
        val messageTextView = view.findViewById<TextView>(R.id.messageTextView)
        val authorTextView = view.findViewById<TextView>(R.id.nameTextView)
        val joke = getItem(position) ?: return view
        messageTextView.text = joke.joke
        authorTextView.text = joke.name
        return view
    }
}