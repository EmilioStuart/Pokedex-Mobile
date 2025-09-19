package com.example.pokmon;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PokemonAdapter pokemonAdapter;
    private SearchView searchView;
    private FloatingActionButton fabSort;
    private PokeApiService pokeApiService;

    private final List<PokemonDetail> initialPokemonDetails = new ArrayList<>();
    private int detailsFetchedCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        recyclerView = findViewById(R.id.recycler_view_pokedex);
        searchView = findViewById(R.id.search_view);
        fabSort = findViewById(R.id.fab_sort);

        setupRetrofit();
        pokemonAdapter = new PokemonAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(pokemonAdapter);

        setupSearchView();
        setupSortFab();
        fetchInitialPokemonData();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = retrofit.create(PokeApiService.class);
    }

    private void fetchInitialPokemonData() {
        pokeApiService.getPokemonList(151).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call, @NonNull Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pokemon> pokemonNameList = response.body().getResults();
                    initialPokemonDetails.clear();
                    detailsFetchedCounter = 0;
                    if (pokemonNameList.isEmpty()) {
                        return;
                    }
                    for (Pokemon pokemon : pokemonNameList) {
                        fetchDetailsForPokemon(pokemon, pokemonNameList.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Falha ao buscar dados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDetailsForPokemon(Pokemon pokemon, int totalToFetch) {
        String idOrName = pokemon.getUrl().split("/")[6];
        pokeApiService.getPokemonDetail(idOrName).enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetail> call, @NonNull Response<PokemonDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    initialPokemonDetails.add(response.body());
                }
                detailsFetchedCounter++;
                if (detailsFetchedCounter == totalToFetch) {
                    onAllInitialDetailsFetched();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonDetail> call, @NonNull Throwable t) {
                detailsFetchedCounter++;
                if (detailsFetchedCounter == totalToFetch) {
                    onAllInitialDetailsFetched();
                }
            }
        });
    }

    private void onAllInitialDetailsFetched() {
        initialPokemonDetails.sort(Comparator.comparingInt(PokemonDetail::getId));
        pokemonAdapter.submitList(new ArrayList<>(initialPokemonDetails));
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterLocalList(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    filterLocalList(newText);
                }
                return true;
            }
        });
    }

    private void filterLocalList(String query) {
        List<PokemonDetail> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(initialPokemonDetails);
        } else {
            for (PokemonDetail pokemon : initialPokemonDetails) {
                if (pokemon.getName().toLowerCase(Locale.ROOT).startsWith(query.toLowerCase(Locale.ROOT))) {
                    filteredList.add(pokemon);
                }
            }
        }
        pokemonAdapter.submitList(filteredList);
    }


    private void setupSortFab() {
        fabSort.setOnClickListener(view -> {
            final String[] options = {"Número (Crescente)", "Número (Decrescente)", "Nome (A-Z)", "Nome (Z-A)"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ordenar por");
            builder.setItems(options, (dialog, which) -> {
                PokemonAdapter.SortType sortType;
                switch (which) {
                    case 1: sortType = PokemonAdapter.SortType.ID_DESC; break;
                    case 2: sortType = PokemonAdapter.SortType.NAME_ASC; break;
                    case 3: sortType = PokemonAdapter.SortType.NAME_DESC; break;
                    default: sortType = PokemonAdapter.SortType.ID_ASC; break;
                }
                pokemonAdapter.sortList(sortType);
            });
            builder.show();
        });
    }
}