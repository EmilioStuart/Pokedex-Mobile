package com.example.pokmon.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Pokedex {
    @SerializedName("pokemon_entries")
    private List<PokemonEntry> pokemonEntries;

    public List<PokemonEntry> getPokemonEntries() {
        return pokemonEntries;
    }
}