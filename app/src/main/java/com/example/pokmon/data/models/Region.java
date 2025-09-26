package com.example.pokmon.data.models;

public class Region {
    private final String name;
    private final int generationId;
    private final int totalPokemon;
    private final String starter1Url;
    private final String starter2Url;
    private final String starter3Url;

    public Region(String name, int generationId, int totalPokemon, String starter1Url, String starter2Url, String starter3Url) {
        this.name = name;
        this.generationId = generationId;
        this.totalPokemon = totalPokemon;
        this.starter1Url = starter1Url;
        this.starter2Url = starter2Url;
        this.starter3Url = starter3Url;
    }

    public String getName() { return name; }
    public int getGenerationId() { return generationId; }
    public int getTotalPokemon() { return totalPokemon; }
    public String getStarter1Url() { return starter1Url; }
    public String getStarter2Url() { return starter2Url; }
    public String getStarter3Url() { return starter3Url; }
}