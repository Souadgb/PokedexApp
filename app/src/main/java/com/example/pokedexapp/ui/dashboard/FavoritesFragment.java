package com.example.pokedexapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pokedexapp.AppDatabase;
import com.example.pokedexapp.FavoritePokemon;
import com.example.pokedexapp.FavoritePokemonDao;
import com.example.pokedexapp.Pokemon;
import com.example.pokedexapp.PokemonAdapter;
import com.example.pokedexapp.R;
import com.example.pokedexapp.ui.notifications.DetailFragment;

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

        loadFavorites(); // first load
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites(); // refresh when returning to tab
    }

    private void loadFavorites() {
        new Thread(() -> {
            FavoritePokemonDao dao = AppDatabase
                    .getInstance(requireContext().getApplicationContext())
                    .favoritePokemonDao();

            List<FavoritePokemon> favEntities = dao.getAllFavorites();

            List<Pokemon> favorites = new ArrayList<>();
            for (FavoritePokemon e : favEntities) {
                favorites.add(new Pokemon(e.name, e.imageUrl));
            }

            requireActivity().runOnUiThread(() -> {
                PokemonAdapter.OnItemClickListener onClick = pokemon -> {
                    Bundle args = new Bundle();
                    args.putString(DetailFragment.ARG_NAME_OR_ID, pokemon.getName());
                    NavHostFragment.findNavController(FavoritesFragment.this)
                            .navigate(R.id.navigation_detail, args);
                };

                adapter = new PokemonAdapter(favorites, onClick);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}
