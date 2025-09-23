package com.example.pokmon.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Generation {

    @SerializedName("pokemon_species")
    private List<PokemonSpeciesInfo> pokemonSpecies;

    public List<PokemonSpeciesInfo> getPokemonSpecies() {
        return pokemonSpecies;
    }
}