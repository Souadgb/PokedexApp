package com.example.pokedexapp.ui.notifications;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.pokedexapp.AppDatabase;
import com.example.pokedexapp.FavoritePokemon;
import com.example.pokedexapp.FavoritePokemonDao;
import com.example.pokedexapp.PokemonDetail;
import com.example.pokedexapp.R;
import com.example.pokedexapp.network.PokemonFetcher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;
import java.util.Locale;

public class DetailFragment extends Fragment {

    public static final String ARG_NAME_OR_ID = "name_or_id";

    private ImageView imgArtwork;
    private TextView txtName;
    private TextView txtTypes;
    private TextView txtHeight;
    private TextView txtWeight;
    private CircularProgressIndicator progress;
    private TextView errorView;
    private MaterialButton btnFavorite;

    private PokemonDetail lastDetail;
    private String lastImageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgArtwork = view.findViewById(R.id.img_artwork);
        txtName    = view.findViewById(R.id.txt_name);
        txtTypes   = view.findViewById(R.id.txt_types);
        txtHeight  = view.findViewById(R.id.txt_height);
        txtWeight  = view.findViewById(R.id.txt_weight);
        progress   = view.findViewById(R.id.progress);
        errorView  = view.findViewById(R.id.error_view);
        btnFavorite = view.findViewById(R.id.btn_favorite);
        btnFavorite.setVisibility(View.GONE);

        // Ensure content sits below the app bar
        ensureBelowAppBar(view);

        String nameOrId = getArguments() != null ? getArguments().getString(ARG_NAME_OR_ID) : null;
        if (nameOrId == null || nameOrId.trim().isEmpty()) {
            showError("Aucun identifiant Pokémon.");
            return;
        }

        setLoading(true);
        PokemonFetcher fetcher = new PokemonFetcher(requireContext());
        fetcher.fetchPokemonDetailsByNameOrId(nameOrId, detail -> {
            if (!isAdded()) return;
            bindDetail(detail);
            setLoading(false);
        }, err -> {
            if (!isAdded()) return;
            setLoading(false);
            showError(err);
        });

        btnFavorite.setOnClickListener(v -> {
            if (lastDetail == null) return;
            addToFavorites(lastDetail, lastImageUrl);
        });
    }

    private void bindDetail(PokemonDetail d) {
        if (d == null) { showError("Données indisponibles."); return; }
        lastDetail = d;

        // Name (capitalize for display)
        String displayName = d.name != null && d.name.length() > 0
                ? d.name.substring(0,1).toUpperCase(Locale.ROOT) + d.name.substring(1)
                : "";
        txtName.setText(displayName);

        // Types (e.g., Fire • Flying)
        if (d.types != null && !d.types.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < d.types.size(); i++) {
                String t = d.types.get(i);
                if (t != null && !t.isEmpty()) {
                    String cap = t.substring(0,1).toUpperCase(Locale.ROOT) + t.substring(1);
                    sb.append(cap);
                    if (i < d.types.size() - 1) sb.append(" • ");
                }
            }
            txtTypes.setText(sb.toString());
        } else {
            txtTypes.setText("—");
        }

        // Height (dm -> m), Weight (hg -> kg)
        double heightM = d.height / 10.0;
        double weightKg = d.weight / 10.0;
        txtHeight.setText(String.format(Locale.getDefault(), "Taille: %.1f m", heightM));
        txtWeight.setText(String.format(Locale.getDefault(), "Poids: %.1f kg", weightKg));

        // Image (official artwork if available)
        String url = d.getImageUrl();
        if (url == null || url.isEmpty()) {
            url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + d.id + ".png";
        }
        lastImageUrl = url;

        Glide.with(this).load(url).fitCenter().into(imgArtwork);

        // Show the favorite button now that data is ready
        btnFavorite.setVisibility(View.VISIBLE);
    }

    private void addToFavorites(PokemonDetail d, String imageUrl) {
        new Thread(() -> {
            FavoritePokemonDao dao = AppDatabase
                    .getInstance(requireContext().getApplicationContext())
                    .favoritePokemonDao();

            // Duplicate check by name (PK = name)
            boolean exists = false;
            try {
                List<FavoritePokemon> all = dao.getAllFavorites();
                if (all != null) {
                    for (FavoritePokemon fp : all) {
                        if (fp != null && fp.name != null && d.name != null
                                && fp.name.equalsIgnoreCase(d.name)) {
                            exists = true;
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}

            if (!exists) {
                try {
                    // ✅ Your entity: FavoritePokemon(@NonNull String name, int id, String imageUrl)
                    FavoritePokemon fav = new FavoritePokemon(
                            d.name,            // keep API name (usually lowercase) as PK
                            d.id,              // numeric Pokémon id
                            (imageUrl != null ? imageUrl : "")
                    );
                    dao.insert(fav);
                } catch (Exception e) {
                    // If insert() is ABORT on conflict, a duplicate would throw here.
                    exists = true;
                }
            }

            final boolean finalExists = exists;
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    if (finalExists) {
                        Toast.makeText(requireContext(), "Déjà dans les favoris", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void setLoading(boolean show) {
        if (progress != null) progress.setVisibility(show ? View.VISIBLE : View.GONE);
        if (errorView != null && show) errorView.setVisibility(View.GONE);
        if (btnFavorite != null && show) btnFavorite.setVisibility(View.GONE);
    }

    private void showError(String msg) {
        if (errorView != null) {
            errorView.setText(msg != null ? msg : "Erreur inconnue.");
            errorView.setVisibility(View.VISIBLE);
        }
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void ensureBelowAppBar(View root) {
        if (root == null) return;
        int minTop = getActionBarSizePx();
        int current = root.getPaddingTop();
        int newTop = Math.max(current, minTop);
        if (newTop != current) {
            root.setPadding(root.getPaddingLeft(), newTop, root.getPaddingRight(), root.getPaddingBottom());
        }
    }

    private int getActionBarSizePx() {
        TypedValue tv = new TypedValue();
        if (requireContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }
}
