package com.example.pokmon.data.models;

import com.google.gson.annotations.SerializedName;

public class PokemonEntry {
    @SerializedName("pokemon_species")
    private PokemonSpeciesInfo pokemonSpecies;

    public PokemonSpeciesInfo getPokemonSpecies() {
        return pokemonSpecies;
    }
}