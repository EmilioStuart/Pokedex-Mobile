package com.example.pokmon.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PokemonSpecies {

    @SerializedName("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries;
    private List<NameEntry> names;
    @SerializedName("evolution_chain")
    private EvolutionChainInfo evolutionChain;

    public List<FlavorTextEntry> getFlavorTextEntries() { return flavorTextEntries; }
    public List<NameEntry> getNames() { return names; }
    public EvolutionChainInfo getEvolutionChain() { return evolutionChain; }

    public static class FlavorTextEntry {
        @SerializedName("flavor_text")
        private String flavorText;
        private Language language;
        public String getFlavorText() { return flavorText; }
        public Language getLanguage() { return language; }
    }

    public static class NameEntry {
        private String name;
        private Language language;
        public String getName() { return name; }
        public Language getLanguage() { return language; }
    }

    public static class Language {
        private String name;
        public String getName() { return name; }
    }

    public static class EvolutionChainInfo {
        private String url;
        public String getUrl() { return url; }
    }
}