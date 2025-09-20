package com.example.pokmon.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pokmon.R;
import com.example.pokmon.adapter.PokemonAdapter;
import com.example.pokmon.data.api.PokeApiService;
import com.example.pokmon.data.models.Pokemon;
import com.example.pokmon.data.models.PokemonDetail;
import com.example.pokmon.data.models.PokemonResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
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
    private ProgressBar progressBar;
    private PokeApiService pokeApiService;
    private MediaPlayer mediaPlayer;

    private final List<PokemonDetail> initialPokemonDetails = new ArrayList<>();
    private int detailsFetchedCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.opening_pokemon_emerald);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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
        fetchInitialPokemonData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = retrofit.create(PokeApiService.class);
    }

    private void fetchInitialPokemonData() {
        // Eu torno a ProgressBar visível ANTES de iniciar a chamada de rede.
        progressBar.setVisibility(View.VISIBLE);
        pokeApiService.getPokemonList(151).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call, @NonNull Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pokemon> pokemonNameList = response.body().getResults();
                    initialPokemonDetails.clear();
                    detailsFetchedCounter = 0;
                    if (pokemonNameList.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    for (Pokemon pokemon : pokemonNameList) {
                        fetchDetailsForPokemon(pokemon, pokemonNameList.size());
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
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

    // Este método é o ponto final do carregamento inicial. É aqui que eu escondo a ProgressBar.
    private void onAllInitialDetailsFetched() {
        progressBar.setVisibility(View.GONE);
        initialPokemonDetails.sort(Comparator.comparingInt(PokemonDetail::getId));
        pokemonAdapter.submitList(new ArrayList<>(initialPokemonDetails));
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Ao submeter, eu chamo a lógica de filtro local.
                filterLocalList(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Se o usuário limpar o campo, eu restauro a lista inicial.
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
                // Eu troco 'contains' por 'startsWith' para buscar
                // apenas os Pokémon cujo nome começa com o texto da query.
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