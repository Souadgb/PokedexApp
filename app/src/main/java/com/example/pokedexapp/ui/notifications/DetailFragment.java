package com.example.pokedexapp.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.pokedexapp.AppDatabase;
import com.example.pokedexapp.PokemonEntity;
import com.example.pokedexapp.R;

public class DetailFragment extends Fragment {

    private String pokemonName;
    private String imageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView detailImage = view.findViewById(R.id.detail_image);
        TextView detailName = view.findViewById(R.id.detail_name);
        Button addFavoriteButton = view.findViewById(R.id.button_add_favorite);

        if (getArguments() != null) {
            pokemonName = getArguments().getString("name");
            imageUrl = getArguments().getString("imageUrl");
        }

        detailName.setText(pokemonName != null ? pokemonName : "Aucun Pokémon");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(detailImage);
        }

        addFavoriteButton.setOnClickListener(v -> {
            if (pokemonName == null) return;

            new Thread(() -> {
                try {
                    AppDatabase db = AppDatabase.getInstance(requireContext());
                    db.pokemonDao().insert(new PokemonEntity(pokemonName, imageUrl));

                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(),
                                    pokemonName + " ajouté aux favoris",
                                    Toast.LENGTH_SHORT).show()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(),
                                    "Erreur: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );
                }
            }).start();
        });


        return view;
    }
}
