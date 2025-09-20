package com.example.pokmon.data.api;

import com.example.pokmon.data.models.TranslationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TranslationApiService {
    @GET("get")
    Call<TranslationResponse> translate(
            @Query("q") String textToTranslate,
            @Query("langpair") String languagePair
    );
}