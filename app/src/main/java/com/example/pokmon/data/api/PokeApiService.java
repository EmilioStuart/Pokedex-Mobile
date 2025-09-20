package com.example.pokmon.data.api;

import com.example.pokmon.data.models.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface PokeApiService {
    // Busca a lista inicial de Pokémon
    @GET("pokemon")
    Call<PokemonResponse> getPokemonList(@Query("limit") int limit);

    // Busca os detalhes principais de um Pokémon (sprites, stats...)
    @GET("pokemon/{idOrName}")
    Call<PokemonDetail> getPokemonDetail(@Path("idOrName") String idOrName);

    // Busca os dados da "espécie", que contém os nomes e descrições traduzidos
    @GET("pokemon-species/{id}")
    Call<PokemonSpecies> getPokemonSpecies(@Path("id") int id);

    // Busca a cadeia evolutiva a partir de uma URL completa
    @GET
    Call<EvolutionChainResponse> getEvolutionChainByUrl(@Url String url);
}