package com.example.pokedexapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexapp.Pokemon;
import com.example.pokedexapp.PokemonAdapter;
import com.example.pokedexapp.R;
import com.example.pokedexapp.filters.FilterState;
import com.example.pokedexapp.filters.FiltersBottomSheet;
import com.example.pokedexapp.network.PokemonFetcher;
import com.example.pokedexapp.ui.notifications.DetailFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ListFragment extends Fragment implements FiltersBottomSheet.OnApplyFilters {

    private RecyclerView recycler;
    private PokemonAdapter adapter;

    // Full source (unfiltered)
    private final List<Pokemon> originalList = new ArrayList<>();
    // Display list (filtered)
    private final List<Pokemon> filteredList = new ArrayList<>();

    private FilterState currentState = new FilterState();

    private final ArrayList<String> ALL_TYPES = new ArrayList<>(Arrays.asList(
            "normal","fire","water","grass","electric","ice","fighting","poison","ground",
            "flying","psychic","bug","rock","ghost","dragon","dark","steel","fairy"
    ));
    private final ArrayList<String> ALL_GENS = new ArrayList<>(Arrays.asList(
            "Gen I","Gen II","Gen III","Gen IV","Gen V","Gen VI","Gen VII","Gen VIII","Gen IX"
    ));

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pokemon_list, container, false);

        recycler = v.findViewById(R.id.recycler_pokemon);
        recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        PokemonAdapter.OnItemClickListener onClick = pokemon -> {
            Bundle args = new Bundle();
            args.putString(DetailFragment.ARG_NAME_OR_ID, pokemon.getName());
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, fragment)
                    .addToBackStack(null)
                    .commit();
        };

        adapter = new PokemonAdapter(filteredList, onClick);
        recycler.setAdapter(adapter);

        TextView chipType   = v.findViewById(R.id.chip_type);
        TextView chipGen    = v.findViewById(R.id.chip_gen);
        TextView chipWeight = v.findViewById(R.id.chip_weight);
        TextView chipHeight = v.findViewById(R.id.chip_height);

        View.OnClickListener openSheet = vv -> {
            FiltersBottomSheet s = FiltersBottomSheet.newInstance(ALL_TYPES, ALL_GENS, currentState);
            s.show(getParentFragmentManager(), "filters");
        };
        chipType.setOnClickListener(openSheet);
        chipGen.setOnClickListener(openSheet);
        chipWeight.setOnClickListener(openSheet);
        chipHeight.setOnClickListener(openSheet);

        // 🔗 Charge ta vraie longue liste via PokeAPI
        loadFromPokeApi();

        // Affiche (au cas où la liste arrive un peu plus tard)
        applyFilters();

        return v;
    }

    private void loadFromPokeApi() {
        PokemonFetcher fetcher = new PokemonFetcher(requireContext());

        fetcher.fetchFirst151Pokemon(list -> {
            // Callback (main thread via Volley). On pousse dans le filtre.
            updatePokemonData(list);
        }, err -> {
            Toast.makeText(requireContext(), "Erreur PokeAPI: " + err, Toast.LENGTH_SHORT).show();
        });
    }

    // ---- Public entrypoint for (re)loading data ----
    public void updatePokemonData(List<Pokemon> newList) {
        originalList.clear();
        if (newList != null) originalList.addAll(newList);
        applyFilters(); // shows all first; filters apply when user picks them
    }

    // ---- Filters ----
    @Override public void onApply(FilterState state) {
        currentState = state;
        applyFilters();
    }

    private void applyFilters() {
        filteredList.clear();
        for (Pokemon p : originalList) {
            if (!matchesTypes(p, currentState.selectedTypes)) continue;
            if (!matchesGens(p, currentState.selectedGenerations)) continue;
            if (p.getWeightKg() < currentState.minWeightKg || p.getWeightKg() > currentState.maxWeightKg) continue;
            if (p.getHeightM()  < currentState.minHeightM  || p.getHeightM()  > currentState.maxHeightM)  continue;
            filteredList.add(p);
        }
        adapter.notifyDataSetChanged();
    }

    private boolean matchesTypes(Pokemon p, List<String> selectedTypes) {
        if (selectedTypes == null || selectedTypes.isEmpty()) return true; // no filter -> include
        for (String t : p.getTypes()) {
            if (selectedTypes.contains(t.toLowerCase(Locale.ROOT))) return true;
        }
        return false;
    }

    private boolean matchesGens(Pokemon p, List<String> selectedGens) {
        if (selectedGens == null || selectedGens.isEmpty()) return true; // no filter -> include
        return selectedGens.contains(p.getGeneration());
    }
}
