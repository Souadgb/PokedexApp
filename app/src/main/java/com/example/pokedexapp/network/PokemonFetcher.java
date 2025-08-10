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
import java.util.Locale;
import java.util.function.Consumer;

public class PokemonFetcher {

    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    private final RequestQueue requestQueue;

    public PokemonFetcher(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /** Récupère les 151 premiers noms de Pokémon */
    public void fetchPokemonList(Consumer<List<String>> onSuccess, Consumer<String> onError) {
        String url = BASE_URL + "pokemon?limit=151";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        List<String> names = new ArrayList<>(results.length());
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

    /**
     * Détails d’un Pokémon par nom OU id (ex: "pikachu" ou "25").
     * Retourne: id, name, height(dm), weight(hg), types, image officielle (fallback: front_default).
     */
    public void fetchPokemonDetailsByNameOrId(String nameOrId,
                                              Consumer<PokemonDetail> onSuccess,
                                              Consumer<String> onError) {
        if (nameOrId == null || nameOrId.trim().isEmpty()) {
            onError.accept("Invalid Pokémon identifier.");
            return;
        }

        String url = BASE_URL + "pokemon/" + nameOrId.toLowerCase(Locale.ROOT);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        String apiName = response.getString("name");
                        int id = response.getInt("id");
                        int height = response.getInt("height"); // dm
                        int weight = response.getInt("weight"); // hg

                        // types
                        List<String> types = new ArrayList<>();
                        JSONArray typeArray = response.getJSONArray("types");
                        for (int i = 0; i < typeArray.length(); i++) {
                            JSONObject slot = typeArray.getJSONObject(i);
                            JSONObject typeObj = slot.getJSONObject("type");
                            types.add(typeObj.getString("name"));
                        }

                        // image officielle + fallback
                        String imageUrl = null;
                        try {
                            JSONObject sprites = response.getJSONObject("sprites");
                            JSONObject other = sprites.optJSONObject("other");
                            if (other != null) {
                                JSONObject official = other.optJSONObject("official-artwork");
                                if (official != null) {
                                    imageUrl = official.optString("front_default", null);
                                }
                            }
                            if (imageUrl == null) {
                                imageUrl = sprites.optString("front_default", null);
                            }
                        } catch (Exception ignored) { }

                        PokemonDetail detail = new PokemonDetail(id, apiName, height, weight, types, imageUrl);
                        onSuccess.accept(detail);

                    } catch (Exception e) {
                        onError.accept("Parsing error: " + e.getMessage());
                    }
                },
                error -> onError.accept("Volley error: " + error.toString())
        );

        requestQueue.add(request);
    }

    /** Compat: par nom */
    public void fetchPokemonDetails(String name,
                                    Consumer<PokemonDetail> onSuccess,
                                    Consumer<String> onError) {
        fetchPokemonDetailsByNameOrId(name, onSuccess, onError);
    }
}
