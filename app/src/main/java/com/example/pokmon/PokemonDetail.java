package com.example.pokmon; // Garanta que seu pacote está correto

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PokemonDetail {

    private int id;
    private String name;
    private List<Types> types;
    private Sprites sprites;
    private int height;
    private int weight;

    public int getId() { return id; }
    public String getName() { return name; }
    public List<Types> getTypes() { return types; }
    public Sprites getSprites() { return sprites; }
    public int getHeight() { return height; }
    public int getWeight() { return weight; }

    public static class Types {
        private Type type;
        public Type getType() { return type; }
    }

    public static class Type {
        private String name;
        public String getName() { return name; }
    }

    public static class Sprites {
        @SerializedName("front_default")
        private String frontDefault;
        private Other other;
        private Versions versions;

        public String getFrontDefault() { return frontDefault; }
        public Other getOther() { return other; }
        public Versions getVersions() { return versions; }

        public static class Other {
            private Home home;
            public Home getHome() { return home; }
        }

        public static class Home {
            @SerializedName("front_default")
            private String frontDefault;
            public String getFrontDefault() { return frontDefault; }
        }

        public static class Versions {
            @SerializedName("generation-v")
            private GenerationV generationV;
            public GenerationV getGenerationV() { return generationV; }
        }

        public static class GenerationV {
            @SerializedName("black-white")
            private BlackWhite blackWhite;
            public BlackWhite getBlackWhite() { return blackWhite; }
        }

        public static class BlackWhite {
            private Animated animated;
            public Animated getAnimated() { return animated; }
        }

        public static class Animated {
            @SerializedName("front_default")
            private String frontDefault;
            public String getFrontDefault() { return frontDefault; }
        }
    }
}