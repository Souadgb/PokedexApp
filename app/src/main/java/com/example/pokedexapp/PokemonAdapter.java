package com.example.pokedexapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Pokemon pokemon);
    }

    private List<Pokemon> list;
    private OnItemClickListener listener;

    public PokemonAdapter(List<Pokemon> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.pokemon_image);
            name = itemView.findViewById(R.id.pokemon_name);
        }

        public void bind(Pokemon pokemon, OnItemClickListener listener) {
            name.setText(pokemon.getName());
            image.setImageResource(pokemon.getImageResId());
            itemView.setOnClickListener(v -> listener.onItemClick(pokemon));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pokemon, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
