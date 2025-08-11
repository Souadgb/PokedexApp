package com.example.pokedexapp.filters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pokedexapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FiltersBottomSheet extends BottomSheetDialogFragment {

    public interface OnApplyFilters { void onApply(FilterState state); }

    // Optional explicit callback (safer across config changes)
    private OnApplyFilters externalCallback;
    public void setOnApplyListener(OnApplyFilters cb) { this.externalCallback = cb; }

    private static final String ARG_TYPES = "arg_types";
    private static final String ARG_GENS  = "arg_gens";
    private static final String ARG_STATE = "arg_state";

    private OnApplyFilters callback;      // discovered via host
    private ArrayList<String> allTypes;
    private ArrayList<String> allGens;
    private FilterState state;

    public static FiltersBottomSheet newInstance(ArrayList<String> allTypes, ArrayList<String> allGens, FilterState current) {
        FiltersBottomSheet s = new FiltersBottomSheet();
        Bundle b = new Bundle();
        b.putStringArrayList(ARG_TYPES, allTypes);
        b.putStringArrayList(ARG_GENS, allGens);
        b.putParcelable(ARG_STATE, current);
        s.setArguments(b);
        return s;
    }

    @Override public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Try parent fragment first, then activity
        if (getParentFragment() instanceof OnApplyFilters) {
            callback = (OnApplyFilters) getParentFragment();
        } else if (context instanceof OnApplyFilters) {
            callback = (OnApplyFilters) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allTypes = getArguments() != null ? getArguments().getStringArrayList(ARG_TYPES) : new ArrayList<>();
        allGens  = getArguments() != null ? getArguments().getStringArrayList(ARG_GENS)  : new ArrayList<>();
        state    = getArguments() != null ? getArguments().getParcelable(ARG_STATE)      : null;
        if (state == null) state = new FilterState();

        LinearLayout containerTypes = view.findViewById(R.id.container_types);
        LinearLayout containerGens  = view.findViewById(R.id.container_gens);
        TextView labelWeight        = view.findViewById(R.id.label_weight);
        RangeSlider sliderWeight    = view.findViewById(R.id.slider_weight);
        TextView labelHeight        = view.findViewById(R.id.label_height);
        RangeSlider sliderHeight    = view.findViewById(R.id.slider_height);
        TextView btnReset           = view.findViewById(R.id.btn_reset);
        TextView btnApply           = view.findViewById(R.id.btn_apply);

        // ✅ Types stockés en lowercase, Générations gardées telles quelles
        createToggleChips(containerTypes, allTypes, state.selectedTypes, true);
        createToggleChips(containerGens,  allGens,  state.selectedGenerations, false);

        sliderWeight.setValues((float) state.minWeightKg, (float) state.maxWeightKg);
        labelWeight.setText((int) state.minWeightKg + " – " + (int) state.maxWeightKg + " kg");
        sliderWeight.addOnChangeListener((s, v, fromUser) -> {
            List<Float> vals = s.getValues();
            state.minWeightKg = vals.get(0);
            state.maxWeightKg = vals.get(1);
            labelWeight.setText((int) state.minWeightKg + " – " + (int) state.maxWeightKg + " kg");
        });

        sliderHeight.setValues((float) state.minHeightM, (float) state.maxHeightM);
        labelHeight.setText(String.format("%.1f – %.1f m", state.minHeightM, state.maxHeightM));
        sliderHeight.addOnChangeListener((s, v, fromUser) -> {
            List<Float> vals = s.getValues();
            state.minHeightM = round1(vals.get(0));
            state.maxHeightM = round1(vals.get(1));
            labelHeight.setText(String.format("%.1f – %.1f m", state.minHeightM, state.maxHeightM));
        });

        btnReset.setOnClickListener(v -> {
            state.selectedTypes.clear();
            state.selectedGenerations.clear();
            state.minWeightKg = 0;  state.maxWeightKg = 999;
            state.minHeightM  = 0;  state.maxHeightM  = 20;

            containerTypes.removeAllViews();
            containerGens.removeAllViews();
            // ✅ Recrée avec la même règle
            createToggleChips(containerTypes, allTypes, state.selectedTypes, true);
            createToggleChips(containerGens,  allGens,  state.selectedGenerations, false);

            sliderWeight.setValues(0f, 999f);
            sliderHeight.setValues(0f, 20f);
            labelWeight.setText("0 – 999 kg");
            labelHeight.setText("0.0 – 20.0 m");
        });

        btnApply.setOnClickListener(v -> {
            if (callback != null)         callback.onApply(state);
            if (externalCallback != null) externalCallback.onApply(state);
            dismiss();
        });
    }

    /** @param storeLowercase true => stocke en lowercase (types), false => garde tel quel (générations) */
    private void createToggleChips(LinearLayout container,
                                   List<String> items,
                                   List<String> selected,
                                   boolean storeLowercase) {
        LayoutInflater inf = LayoutInflater.from(getContext());
        for (String it : items) {
            View v = inf.inflate(R.layout.view_chip_toggle, container, false); // root is TextView
            TextView chip = (TextView) v;
            chip.setText(it.toUpperCase());

            String key = storeLowercase ? it.toLowerCase(Locale.ROOT) : it;
            chip.setSelected(selected.contains(key));

            chip.setOnClickListener(view -> {
                String k = storeLowercase ? it.toLowerCase(Locale.ROOT) : it;
                view.setSelected(!view.isSelected());
                if (view.isSelected()) {
                    if (!selected.contains(k)) selected.add(k);
                } else {
                    selected.remove(k);
                }
            });
            container.addView(chip);
        }
    }

    private static double round1(float f) { return Math.round(f * 10.0) / 10.0; }
}
