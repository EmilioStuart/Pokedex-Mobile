package com.example.pokmon;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.*;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PokemonAdapter pokemonAdapter;
    private SearchView searchView;
    private FloatingActionButton fabSort;

    private List<Pokemon> originalPokemonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        recyclerView = findViewById(R.id.recycler_view_pokedex);
        searchView = findViewById(R.id.search_view);
        fabSort = findViewById(R.id.fab_sort);

        pokemonAdapter = new PokemonAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(pokemonAdapter);

        setupSearchView();
        setupSortFab();
        fetchPokemonData();
    }

    private void setupSortFab() {
        fabSort.setOnClickListener(view -> {
            final String[] options = {"Número (Crescente)", "Número (Decrescente)", "Nome (A-Z)", "Nome (Z-A)"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ordenar por");
            builder.setItems(options, (dialog, escolha) -> {
                if (escolha == 0) {
                    pokemonAdapter.sortList(PokemonAdapter.SortType.ID_ASC);
                } else if (escolha == 1) {
                    pokemonAdapter.sortList(PokemonAdapter.SortType.ID_DESC);
                } else if (escolha == 2) {
                    pokemonAdapter.sortList(PokemonAdapter.SortType.NAME_ASC);
                } else if (escolha == 3) {
                    pokemonAdapter.sortList(PokemonAdapter.SortType.NAME_DESC);
                }
            });
            builder.show();
        });
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