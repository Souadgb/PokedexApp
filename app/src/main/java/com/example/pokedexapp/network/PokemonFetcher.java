package com.example.pokedexapp.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pokedexapp.Pokemon;
import com.example.pokedexapp.PokemonDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
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

    // --------------------------------------------------------------------
    // ✅ NOUVEAU: récupère les 151 premiers Pokémon en objets `Pokemon`
    // (champs prêts pour le filtrage: types lowercase, weightKg, heightM, generation)
    // --------------------------------------------------------------------
    public void fetchFirst151Pokemon(Consumer<List<Pokemon>> onSuccess, Consumer<String> onError) {
        fetchPokemonList(names -> {
            if (names == null || names.isEmpty()) {
                onSuccess.accept(new ArrayList<>());
                return;
            }

            List<Pokemon> acc = Collections.synchronizedList(new ArrayList<>(names.size()));
            final int total = names.size();
            final int[] done = {0};

            for (String name : names) {
                fetchPokemonDetailsByNameOrId(name, detail -> {
                    acc.add(mapToPokemon(detail));
                    int d = ++done[0];
                    if (d == total) {
                        onSuccess.accept(new ArrayList<>(acc));
                    }
                }, err -> {
                    // ignore this one, continue
                    int d = ++done[0];
                    if (d == total) {
                        onSuccess.accept(new ArrayList<>(acc));
                    }
                });
            }

        }, onError);
    }

    // ---- mapping helpers ----
    private static Pokemon mapToPokemon(PokemonDetail d) {
        if (d == null) return null;

        List<String> lowered = new ArrayList<>();
        if (d.getTypes() != null) {
            for (String t : d.getTypes()) {
                if (t != null) lowered.add(t.toLowerCase(Locale.ROOT));
            }
        }

        double weightKg = d.getWeight() / 10.0; // hg -> kg
        double heightM  = d.getHeight() / 10.0; // dm -> m
        String gen = genFromId(d.getId());

        String imageUrl = d.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + d.getId() + ".png";
        }

        // Optionnel: capitaliser le nom
        String name = d.getName();
        if (name != null && !name.isEmpty()) {
            name = name.substring(0,1).toUpperCase(Locale.ROOT) + name.substring(1);
        }

        return new Pokemon(name, imageUrl, lowered, weightKg, heightM, gen);
    }

    private static String genFromId(int id) {
        if (id >= 1   && id <= 151) return "Gen I";
        if (id >= 152 && id <= 251) return "Gen II";
        if (id >= 252 && id <= 386) return "Gen III";
        if (id >= 387 && id <= 493) return "Gen IV";
        if (id >= 494 && id <= 649) return "Gen V";
        if (id >= 650 && id <= 721) return "Gen VI";
        if (id >= 722 && id <= 809) return "Gen VII";
        if (id >= 810 && id <= 898) return "Gen VIII";
        if (id >= 899 && id <= 1025) return "Gen IX";
        return "Gen ?";
    }
}
