package com.example.pokedexapp.ui.home;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokedexapp.Pokemon;
import com.example.pokedexapp.PokemonAdapter;
import com.example.pokedexapp.R;
import com.example.pokedexapp.ui.notifications.DetailFragment;

import java.util.Arrays;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Pokemon> pokemonList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        pokemonList = Arrays.asList(
                new Pokemon("Pikachu", R.drawable.pikachu),
                new Pokemon("Bulbasaur", R.drawable.bulbasaur),
                new Pokemon("Charmander", R.drawable.charmander),
                new Pokemon("Squirtle", R.drawable.squirtle)
        );

        PokemonAdapter adapter = new PokemonAdapter(pokemonList, pokemon -> {
            Fragment detailFragment = new DetailFragment();
            Bundle args = new Bundle();
            args.putString("name", pokemon.getName());
            detailFragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);
        return view;
    }
}
