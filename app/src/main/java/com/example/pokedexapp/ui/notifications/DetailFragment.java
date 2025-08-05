package com.example.pokedexapp.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.pokedexapp.R;

public class DetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView detailText = view.findViewById(R.id.detail_text);
        String name = getArguments() != null ? getArguments().getString("name") : "Aucun Pokémon";
        detailText.setText("Détails de : " + name);

        return view;
    }
}
