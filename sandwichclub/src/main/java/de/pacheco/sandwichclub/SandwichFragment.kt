package de.pacheco.sandwichclub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import de.pacheco.sandwichclub.databinding.FragmentSandwichBinding

class SandwichFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentSandwichBinding.inflate(inflater)
        val sandwiches = resources.getStringArray(R.array.sandwich_names)
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, sandwiches)
        val listView = binding.sandwichesListview
        listView.adapter = adapter
        listView.onItemClickListener = OnItemClickListener { _, _, position, _ -> launchDetailActivity(position) }
        return binding.root
    }

    private fun launchDetailActivity(position: Int) {
        val intent = Intent(context, SandwichDetailActivity::class.java)
        intent.putExtra(SandwichDetailActivity.EXTRA_POSITION, position)
        startActivity(intent)
    }
}