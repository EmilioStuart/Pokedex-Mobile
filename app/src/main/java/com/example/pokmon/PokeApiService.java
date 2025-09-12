package com.example.pokmon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokeApiService {
    @GET("pokemon")
    Call<PokemonResponse> getPokemonList(@Query("limit") int limit);

    @GET("pokemon/{idOrName}")
    Call<PokemonDetail> getPokemonDetail(@Path("idOrName") String idOrName);
}