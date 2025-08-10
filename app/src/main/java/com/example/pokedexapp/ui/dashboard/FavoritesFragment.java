package com.example.pokedexapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexapp.AppDatabase;
import com.example.pokedexapp.Pokemon;
import com.example.pokedexapp.PokemonAdapter;
import com.example.pokedexapp.PokemonEntity;
import com.example.pokedexapp.R;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PokemonAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recycler_favorites);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // premier chargement
        loadFavorites();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // recharger quand on revient sur l’onglet
        loadFavorites();
    }

    private void loadFavorites() {
        new Thread(() -> {
            List<PokemonEntity> entities = AppDatabase
                    .getInstance(requireContext())
                    .pokemonDao()
                    .getAllFavorites();

            List<Pokemon> favorites = new ArrayList<>();
            for (PokemonEntity e : entities) {
                favorites.add(new Pokemon(e.name, e.imageUrl));
            }

            requireActivity().runOnUiThread(() -> {
                if (adapter == null) {
                    adapter = new PokemonAdapter(favorites, pokemon -> {
                        // optionnel : ouvrir la fiche depuis Favoris
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    // mettre à jour les données
                    // (simplement recréer l’adapter si tu n’as pas de setData())
                    adapter = new PokemonAdapter(favorites, null);
                    recyclerView.setAdapter(adapter);
                }
            });
        }).start();
    }
}
