package com.example.pokmon;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PokemonSpecies {

    // Este campo mapeia a lista de descrições da Pokédex.
    @SerializedName("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries;

    // Este campo mapeia a lista de nomes do Pokémon em diferentes idiomas.
    private List<NameEntry> names;

    public List<FlavorTextEntry> getFlavorTextEntries() {
        return flavorTextEntries;
    }

    public List<NameEntry> getNames() {
        return names;
    }

    public static class FlavorTextEntry {
        @SerializedName("flavor_text")
        private String flavorText;
        private Language language;

        public String getFlavorText() {
            return flavorText;
        }

        public Language getLanguage() {
            return language;
        }
    }

    public static class NameEntry {
        private String name;
        private Language language;

        public String getName() {
            return name;
        }

        public Language getLanguage() {
            return language;
        }
    }

    public static class Language {
        private String name;
        public String getName() {
            return name;
        }
    }
}