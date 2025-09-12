package com.example.pokmon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokeApiService {
    // Busca a lista inicial de Pokémon
    @GET("pokemon")
    Call<PokemonResponse> getPokemonList(@Query("limit") int limit);

    // Busca os detalhes principais de um Pokémon (sprites, stats, etc.)
    @GET("pokemon/{idOrName}")
    Call<PokemonDetail> getPokemonDetail(@Path("idOrName") String idOrName);

    // Busca os dados da "espécie", que contém os nomes e descrições traduzidos
    @GET("pokemon-species/{id}")
    Call<PokemonSpecies> getPokemonSpecies(@Path("id") int id);
}