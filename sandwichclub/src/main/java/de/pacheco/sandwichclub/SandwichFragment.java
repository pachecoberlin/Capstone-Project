package de.pacheco.sandwichclub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

public class SandwichFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(de.pacheco.sandwichclub.R.layout.fragment_sandwich, container,
                false);
        String[] sandwiches = getResources().getStringArray(R.array.sandwich_names);
        @SuppressWarnings("ConstantConditions") ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, sandwiches);

        // Simplification: Using a ListView instead of a RecyclerView
        ListView listView = root.findViewById(R.id.sandwiches_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                launchDetailActivity(position);
            }
        });
        return root;
    }

    private void launchDetailActivity(int position) {
        Intent intent = new Intent(getContext(), SandwichDetailActivity.class);
        intent.putExtra(SandwichDetailActivity.EXTRA_POSITION, position);
        startActivity(intent);
    }
}
