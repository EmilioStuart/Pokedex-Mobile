package com.example.pokmon;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;
import retrofit2.*;
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

        // Eu forço o modo claro diretamente nesta Activity para garantir a consistência
        // visual, independentemente da configuração do sistema ou da classe Application.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        recyclerView = findViewById(R.id.recycler_view_pokedex);
        searchView = findViewById(R.id.search_view);

        pokemonAdapter = new PokemonAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(pokemonAdapter);

        setupSearchView();
        fetchPokemonData();
    }

    // Eu configuro os listeners da SearchView aqui. O 'onQueryTextChange' me permite
    // filtrar a lista dinamicamente enquanto o usuário digita, oferecendo uma
    // experiência de busca em tempo real.
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

    // Eu implemento a lógica de filtragem da lista de Pokémon. Se a busca estiver
    // vazia, eu restauro a lista original. Caso contrário, eu crio uma nova lista
    // contendo apenas os Pokémon cujo nome corresponde à query.
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
                // No recebimento de uma resposta bem-sucedida, eu primeiro limpo a lista de
                // backup para evitar duplicatas. Em seguida, eu a preencho com os novos dados
                // da API e, finalmente, atualizo o adapter para que a lista completa seja exibida.
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