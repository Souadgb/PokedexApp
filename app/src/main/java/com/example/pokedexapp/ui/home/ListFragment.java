package com.example.pokedexapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexapp.Pokemon;
import com.example.pokedexapp.PokemonAdapter;
import com.example.pokedexapp.R;
import com.example.pokedexapp.network.PokemonFetcher;
import com.example.pokedexapp.ui.notifications.DetailFragment;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PokemonAdapter adapter;
    private List<Pokemon> pokemonList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new PokemonAdapter( pokemonList, pokemon -> {
            Fragment detailFragment = new DetailFragment();
            Bundle args = new Bundle();
            args.putString("name", pokemon.getName());
            args.putString("imageUrl", pokemon.getImageUrl()); // ✅ send image too
            detailFragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        // Fetch Pokémon list from API
        PokemonFetcher fetcher = new PokemonFetcher(requireContext());
        fetcher.fetchPokemonList(
                names -> {
                    int idCounter = 1;
                    for (String name : names) {
                        String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + idCounter + ".png";
                        pokemonList.add(new Pokemon(name, imageUrl));
                        idCounter++;
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    // Optionally log or display error
                }
        );

        return view;
    }
}
