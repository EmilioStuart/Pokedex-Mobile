package com.example.pokmon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.SearchView; // Importação necessária
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale; // Importação para o toLowerCase

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PokemonAdapter pokemonAdapter;
    private SearchView searchView;

    private List<Pokemon> originalPokemonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view_pokedex);
        searchView = findViewById(R.id.search_view);

        pokemonAdapter = new PokemonAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(pokemonAdapter);

        setupSearchView();
        fetchPokemonData();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPokemonList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPokemonList(newText);
                return true;
            }
        });
    }

    private void filterPokemonList(String query) {
        if (query.isEmpty()) {
            pokemonAdapter.setPokemonList(originalPokemonList);
        } else {
            List<Pokemon> filteredList = new ArrayList<>();
            for (Pokemon pokemon : originalPokemonList) {
                if (pokemon.getName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                    filteredList.add(pokemon);
                }
            }
            pokemonAdapter.setPokemonList(filteredList);
        }
    }


    private void fetchPokemonData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PokeApiService service = retrofit.create(PokeApiService.class);

        Call<PokemonResponse> call = service.getPokemonList(1025);
        call.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    originalPokemonList.clear();
                    originalPokemonList.addAll(response.body().getResults());

                    pokemonAdapter.setPokemonList(originalPokemonList);
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Falha ao buscar dados: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}