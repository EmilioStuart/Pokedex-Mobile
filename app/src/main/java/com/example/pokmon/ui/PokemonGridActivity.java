package com.example.pokmon.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pokmon.R;
import com.example.pokmon.adapters.PokemonAdapter;
import com.example.pokmon.data.api.PokeApiService;
import com.example.pokmon.data.models.Generation;
import com.example.pokmon.data.models.PokemonDetail;
import com.example.pokmon.data.models.PokemonSpeciesInfo;
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

public class PokemonGridActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private PokemonAdapter pokemonAdapter;
    private SearchView searchView;
    private FloatingActionButton fabSort;
    private ProgressBar progressBar;
    private PokeApiService pokeApiService;

    private final List<PokemonDetail> currentPokemonDetails = new ArrayList<>();
    private int detailsFetchedCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_grid);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String regionName = getIntent().getStringExtra("REGION_NAME");
        int generationId = getIntent().getIntExtra("GENERATION_ID", 1); // Padrão para Gen 1
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(regionName);
        }

        recyclerView = findViewById(R.id.recycler_view_pokedex);
        searchView = findViewById(R.id.search_view);
        fabSort = findViewById(R.id.fab_sort);
        progressBar = findViewById(R.id.progress_bar);

        setupRetrofit();
        pokemonAdapter = new PokemonAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(pokemonAdapter);

        setupSearchView();
        setupSortFab();

        fetchGenerationData(generationId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = retrofit.create(PokeApiService.class);
    }

    private void fetchGenerationData(int generationId) {
        progressBar.setVisibility(View.VISIBLE);
        pokemonAdapter.submitList(new ArrayList<>());

        pokeApiService.getGenerationData(generationId).enqueue(new Callback<Generation>() {
            @Override
            public void onResponse(@NonNull Call<Generation> call, @NonNull Response<Generation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PokemonSpeciesInfo> speciesList = response.body().getPokemonSpecies();
                    currentPokemonDetails.clear();
                    detailsFetchedCounter = 0;
                    if (speciesList.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    for (PokemonSpeciesInfo species : speciesList) {
                        fetchDetailsForPokemon(species, speciesList.size());
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Generation> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PokemonGridActivity.this, "Falha ao buscar dados da geração", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDetailsForPokemon(PokemonSpeciesInfo species, int totalToFetch) {
        pokeApiService.getPokemonDetail(species.getName()).enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetail> call, @NonNull Response<PokemonDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPokemonDetails.add(response.body());
                }
                detailsFetchedCounter++;
                if (detailsFetchedCounter == totalToFetch) {
                    onAllDetailsFetched();
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonDetail> call, @NonNull Throwable t) {
                detailsFetchedCounter++;
                if (detailsFetchedCounter == totalToFetch) {
                    onAllDetailsFetched();
                }
            }
        });
    }

    private void onAllDetailsFetched() {
        progressBar.setVisibility(View.GONE);
        currentPokemonDetails.sort(Comparator.comparingInt(PokemonDetail::getId));
        pokemonAdapter.submitList(new ArrayList<>(currentPokemonDetails));
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
                filterLocalList(newText);
                return true;
            }
        });
    }

    private void filterLocalList(String query) {
        List<PokemonDetail> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(currentPokemonDetails);
        } else {
            for (PokemonDetail pokemon : currentPokemonDetails) {
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