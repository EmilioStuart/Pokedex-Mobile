package com.example.pokmon.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChainLink {
    private PokemonSpeciesInfo species;
    @SerializedName("evolves_to")
    private List<ChainLink> evolvesTo;
    public PokemonSpeciesInfo getSpecies() { return species; }
    public List<ChainLink> getEvolvesTo() { return evolvesTo; }
}