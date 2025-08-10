package com.example.pokedexapp.ui.notifications;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.pokedexapp.AppDatabase;
import com.example.pokedexapp.FavoritePokemon;
import com.example.pokedexapp.FavoritePokemonDao;
import com.example.pokedexapp.PokemonDetail;
import com.example.pokedexapp.R;
import com.example.pokedexapp.network.PokemonFetcher;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class DetailFragment extends Fragment {

    public static final String ARG_NAME_OR_ID = "name"; // pass name or id

    private String pokemonNameOrId;

    private ImageView detailImage;
    private TextView detailName, detailId, detailHeight, detailWeight;
    private LinearLayout typesContainer;
    private Button favBtn;

    private AppDatabase db;
    private FavoritePokemonDao favoriteDao;

    private @Nullable PokemonDetail current;
    private boolean isFavorite = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        detailImage = view.findViewById(R.id.detail_image);
        detailName = view.findViewById(R.id.detail_name);
        detailId = view.findViewById(R.id.detail_id);
        detailHeight = view.findViewById(R.id.detail_height);
        detailWeight = view.findViewById(R.id.detail_weight);
        typesContainer = view.findViewById(R.id.types_container);
        favBtn = view.findViewById(R.id.button_add_favorite);

        if (getArguments() != null) {
            pokemonNameOrId = getArguments().getString(ARG_NAME_OR_ID);
        }
        if (TextUtils.isEmpty(pokemonNameOrId)) {
            Toast.makeText(getContext(), "No Pokémon selected", Toast.LENGTH_SHORT).show();
            return view;
        }

        db = AppDatabase.getInstance(requireContext().getApplicationContext());
        favoriteDao = db.favoritePokemonDao();

        fetchDetails(pokemonNameOrId);
        hookFavoriteButton();

        return view;
    }

    private void fetchDetails(String nameOrId) {
        PokemonFetcher fetcher = new PokemonFetcher(requireContext().getApplicationContext());
        fetcher.fetchPokemonDetailsByNameOrId(nameOrId, detail -> {
            if (!isAdded()) return;
            current = detail;
            bindUi(detail);
            checkIfFavorite(detail.name);
        }, error -> {
            if (!isAdded()) return;
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void bindUi(PokemonDetail p) {
        detailName.setText(capitalize(p.name));
        detailId.setText(String.format(Locale.getDefault(), "ID: %d", p.id));

        float meters = p.height / 10f;     // dm -> m
        float kilograms = p.weight / 10f;  // hg -> kg
        detailHeight.setText(String.format(Locale.getDefault(), "Height: %.1f m", meters));
        detailWeight.setText(String.format(Locale.getDefault(), "Weight: %.1f kg", kilograms));

        typesContainer.removeAllViews();
        List<String> types = p.types;
        if (types != null) {
            for (String t : types) {
                TextView chip = makeTypeChip(capitalize(t));
                typesContainer.addView(chip);
            }
        }

        Glide.with(this)
                .load(p.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(detailImage);
    }

    private void hookFavoriteButton() {
        favBtn.setOnClickListener(v -> {
            if (current == null) return;
            Executors.newSingleThreadExecutor().execute(() -> {
                if (isFavorite) {
                    favoriteDao.deleteByName(current.name);
                    isFavorite = false;
                    requireActivity().runOnUiThread(() -> {
                        favBtn.setText("Add to favorites");
                        Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    FavoritePokemon fp = new FavoritePokemon(current.name, current.id, current.imageUrl);
                    favoriteDao.insert(fp);
                    isFavorite = true;
                    requireActivity().runOnUiThread(() -> {
                        favBtn.setText("Remove from favorites");
                        Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    private void checkIfFavorite(String name) {
        Executors.newSingleThreadExecutor().execute(() -> {
            int c = favoriteDao.isFavorite(name);
            isFavorite = c > 0;
            requireActivity().runOnUiThread(() ->
                    favBtn.setText(isFavorite ? "Remove from favorites" : "Add to favorites")
            );
        });
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1);
    }

    private TextView makeTypeChip(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        int padH = (int) (12 * getResources().getDisplayMetrics().density);
        int padV = (int) (6 * getResources().getDisplayMetrics().density);
        tv.setPadding(padH, padV, padH, padV);
        tv.setTextSize(14f);
        tv.setBackgroundResource(android.R.drawable.btn_default_small);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.rightMargin = (int) (8 * getResources().getDisplayMetrics().density);
        tv.setLayoutParams(lp);
        return tv;
    }
}
