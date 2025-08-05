package com.example.pokedexapp.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pokedexapp.PokemonDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PokemonFetcher {

    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    private final RequestQueue requestQueue;

    public PokemonFetcher(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    // Récupère les 151 premiers noms de Pokémon
    public void fetchPokemonList(Consumer<List<String>> onSuccess, Consumer<String> onError) {
        String url = BASE_URL + "pokemon?limit=151";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        List<String> names = new ArrayList<>();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject p = results.getJSONObject(i);
                            names.add(p.getString("name"));
                        }
                        onSuccess.accept(names);
                    } catch (Exception e) {
                        onError.accept("Parsing error: " + e.getMessage());
                    }
                },
                error -> onError.accept("Volley error: " + error.toString())
        );

        requestQueue.add(request);
    }

    // Récupère les détails d’un Pokémon à partir de son nom
    public void fetchPokemonDetails(String name, Consumer<PokemonDetail> onSuccess, Consumer<String> onError) {
        String url = BASE_URL + "pokemon/" + name.toLowerCase();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int id = response.getInt("id");
                        int height = response.getInt("height");
                        int weight = response.getInt("weight");
                        String imageUrl = response.getJSONObject("sprites")
                                .getString("front_default");

                        List<String> types = new ArrayList<>();
                        JSONArray typeArray = response.getJSONArray("types");
                        for (int i = 0; i < typeArray.length(); i++) {
                            types.add(typeArray.getJSONObject(i)
                                    .getJSONObject("type")
                                    .getString("name"));
                        }

                        PokemonDetail detail = new PokemonDetail(id, name, height, weight, types, imageUrl);
                        onSuccess.accept(detail);

                    } catch (Exception e) {
                        onError.accept("Parsing error: " + e.getMessage());
                    }
                },
                error -> onError.accept("Volley error: " + error.toString())
        );

        requestQueue.add(request);
    }
}
