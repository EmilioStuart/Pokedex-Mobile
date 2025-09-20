package com.example.pokmon.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import com.bumptech.glide.Glide;
import com.example.pokmon.R;
import com.example.pokmon.data.api.PokeApiService;
import com.example.pokmon.data.api.TranslationApiService;
import com.example.pokmon.data.models.ChainLink;
import com.example.pokmon.data.models.EvolutionChainResponse;
import com.example.pokmon.data.models.PokemonDetail;
import com.example.pokmon.data.models.PokemonSpecies;
import com.example.pokmon.data.models.PokemonSpeciesInfo;
import com.example.pokmon.data.models.TranslationResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {

    private ImageView pokemonSprite;
    private TextView pokemonNumber, pokemonName, pokemonHeight, pokemonWeight, pokemonDescription;
    private LinearLayout typesContainer;
    private LinearLayout individualStatsContainer;
    private LinearLayout evolutionChainContainer;
    private FloatingActionButton fabShiny; // Declarando o FAB
    private PokeApiService pokeApiService;
    private TranslationApiService translationApiService;
    private int currentPokemonId;

    private String normalSpriteUrl; // Para armazenar a URL da sprite normal
    private String shinySpriteUrl;  // Para armazenar a URL da sprite shiny
    private boolean isShinyShowing = false; // Estado para alternar entre normal/shiny

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        setupRetrofitServices();
        setupShinyFab(); // Configura o FAB do shiny

        currentPokemonId = getIntent().getIntExtra("POKEMON_ID", -1);
        if (currentPokemonId != -1) {
            fetchPokemonDetails(currentPokemonId);
            fetchPokemonSpecies(currentPokemonId);
        } else {
            Toast.makeText(this, "Erro: ID do Pokémon não encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        pokemonSprite = findViewById(R.id.iv_detail_sprite);
        pokemonNumber = findViewById(R.id.tv_detail_number);
        pokemonName = findViewById(R.id.tv_detail_name);
        pokemonHeight = findViewById(R.id.tv_detail_height);
        pokemonWeight = findViewById(R.id.tv_detail_weight);
        pokemonDescription = findViewById(R.id.tv_pokemon_description);
        typesContainer = findViewById(R.id.ll_detail_types_container);
        individualStatsContainer = findViewById(R.id.ll_individual_stats_container);
        evolutionChainContainer = findViewById(R.id.ll_evolution_chain_container);
        fabShiny = findViewById(R.id.fab_shiny); // Inicializando o FAB
    }

    private void setupRetrofitServices() {
        Retrofit pokeApiRetrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = pokeApiRetrofit.create(PokeApiService.class);

        Retrofit translationRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.mymemory.translated.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        translationApiService = translationRetrofit.create(TranslationApiService.class);
    }

    private void setupShinyFab() {
        fabShiny.setOnClickListener(v -> toggleShinySprite());
    }

    private void toggleShinySprite() {
        if (normalSpriteUrl == null || shinySpriteUrl == null) {
            Toast.makeText(this, "Sprites shiny não disponíveis", Toast.LENGTH_SHORT).show();
            return;
        }

        isShinyShowing = !isShinyShowing; // Inverte o estado
        String imageUrl = isShinyShowing ? shinySpriteUrl : normalSpriteUrl;
        Glide.with(this).load(imageUrl).placeholder(R.drawable.pokeball_placeholder).into(pokemonSprite);
    }

    private void fetchPokemonDetails(int pokemonId) {
        pokeApiService.getPokemonDetail(String.valueOf(pokemonId)).enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetail> call, @NonNull Response<PokemonDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonDetail detail = response.body();
                    // Armazena os URLs das sprites
                    if (detail.getSprites() != null && detail.getSprites().getOther() != null && detail.getSprites().getOther().getHome() != null) {
                        normalSpriteUrl = detail.getSprites().getOther().getHome().getFrontDefault();
                        shinySpriteUrl = detail.getSprites().getOther().getHome().getFrontShiny();
                    }
                    populateUI(detail);
                } else {
                    Toast.makeText(DetailActivity.this, "Falha ao carregar detalhes", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonDetail> call, @NonNull Throwable t) {
                Toast.makeText(DetailActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPokemonSpecies(int pokemonId) {
        pokeApiService.getPokemonSpecies(pokemonId).enqueue(new Callback<PokemonSpecies>() {
            @Override
            public void onResponse(@NonNull Call<PokemonSpecies> call, @NonNull Response<PokemonSpecies> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonSpecies speciesData = response.body();

                    speciesData.getFlavorTextEntries().stream()
                            .filter(entry -> entry.getLanguage().getName().equals("en"))
                            .findFirst()
                            .ifPresent(entry -> {
                                String cleanedText = entry.getFlavorText().replace('\n', ' ').replace('\f', ' ');
                                pokemonDescription.setText("Traduzindo para o Português...");
                                translateDescription(cleanedText);
                            });

                    if (speciesData.getEvolutionChain() != null && speciesData.getEvolutionChain().getUrl() != null) {
                        fetchEvolutionChain(speciesData.getEvolutionChain().getUrl());
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonSpecies> call, @NonNull Throwable t) {}
        });
    }

    private void fetchEvolutionChain(String url) {
        pokeApiService.getEvolutionChainByUrl(url).enqueue(new Callback<EvolutionChainResponse>() {
            @Override
            public void onResponse(@NonNull Call<EvolutionChainResponse> call, @NonNull Response<EvolutionChainResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PokemonSpeciesInfo> evolutionLine = new ArrayList<>();
                    parseEvolutionChain(response.body().getChain(), evolutionLine);
                    populateEvolutionChart(evolutionLine);
                }
            }
            @Override
            public void onFailure(@NonNull Call<EvolutionChainResponse> call, @NonNull Throwable t) {}
        });
    }

    private void parseEvolutionChain(ChainLink chainLink, List<PokemonSpeciesInfo> evolutionLine) {
        if (chainLink == null) return;
        evolutionLine.add(chainLink.getSpecies());
        if (chainLink.getEvolvesTo() != null && !chainLink.getEvolvesTo().isEmpty()) {
            parseEvolutionChain(chainLink.getEvolvesTo().get(0), evolutionLine);
        }
    }

    private void populateEvolutionChart(List<PokemonSpeciesInfo> evolutionLine) {
        evolutionChainContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < evolutionLine.size(); i++) {
            PokemonSpeciesInfo species = evolutionLine.get(i);

            View stageView = inflater.inflate(R.layout.evolution_stage_item, evolutionChainContainer, false);
            ImageView sprite = stageView.findViewById(R.id.iv_evolution_sprite);
            TextView name = stageView.findViewById(R.id.tv_evolution_name);

            name.setText(species.getName().substring(0, 1).toUpperCase() + species.getName().substring(1));

            String[] urlParts = species.getUrl().split("/");
            String pokemonIdStr = urlParts[urlParts.length - 1];
            int pokemonId = Integer.parseInt(pokemonIdStr);

            String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + pokemonId + ".png";

            Glide.with(this).load(imageUrl).into(sprite);

            if (pokemonId != this.currentPokemonId) {
                stageView.setOnClickListener(v -> {
                    Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                    intent.putExtra("POKEMON_ID", pokemonId);
                    startActivity(intent);
                    finish();
                });
            }

            evolutionChainContainer.addView(stageView);

            if (i < evolutionLine.size() - 1) {
                ImageView arrow = new ImageView(this);
                arrow.setImageResource(R.drawable.ic_arrow_forward);
                evolutionChainContainer.addView(arrow);
            }
        }
    }

    private void translateDescription(String textInEnglish) {
        translationApiService.translate(textInEnglish, "en|pt-br").enqueue(new Callback<TranslationResponse>() {
            @Override
            public void onResponse(@NonNull Call<TranslationResponse> call, @NonNull Response<TranslationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String translatedText = response.body().getResponseData().getTranslatedText();
                    pokemonDescription.setText(translatedText);
                } else {
                    pokemonDescription.setText(textInEnglish);
                }
            }
            @Override
            public void onFailure(@NonNull Call<TranslationResponse> call, @NonNull Throwable t) {
                pokemonDescription.setText(textInEnglish);
            }
        });
    }

    private void populateUI(PokemonDetail detail) {
        pokemonName.setText(detail.getName().substring(0, 1).toUpperCase() + detail.getName().substring(1));
        pokemonNumber.setText(String.format(Locale.getDefault(), "Nº %04d", detail.getId()));

        float heightInMeters = detail.getHeight() / 10.0f;
        float weightInKg = detail.getWeight() / 10.0f;
        pokemonHeight.setText(String.format(Locale.getDefault(), "%.1f m", heightInMeters));
        pokemonWeight.setText(String.format(Locale.getDefault(), "%.1f kg", weightInKg));

        // Aqui, usa o normalSpriteUrl para carregar a imagem inicial.
        // O isShinyShowing é resetado para garantir que sempre comece no normal.
        isShinyShowing = false;
        if (normalSpriteUrl != null) {
            Glide.with(this).load(normalSpriteUrl).placeholder(R.drawable.pokeball_placeholder).into(pokemonSprite);
        } else {
            // Fallback caso normalSpriteUrl seja nulo por algum motivo
            pokemonSprite.setImageResource(R.drawable.pokeball_placeholder);
        }

        typesContainer.removeAllViews();
        if (detail.getTypes() != null && !detail.getTypes().isEmpty()) {
            for (PokemonDetail.Types typeWrapper : detail.getTypes()) {
                String typeName = typeWrapper.getType().getName();
                ImageView typeImageView = createTypeImageView(typeName);
                typesContainer.addView(typeImageView);
            }
        }
        populateIndividualStats(detail);
    }

    private void populateIndividualStats(PokemonDetail detail) {
        individualStatsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        if (detail.getStats() != null) {
            List<String> statLabels = Arrays.asList("HP", "Ataque", "Defesa", "Atq. Esp.", "Def. Esp.", "Velocidade");

            for (int i = 0; i < detail.getStats().size(); i++) {
                if (i >= statLabels.size()) break;

                View statRowView = inflater.inflate(R.layout.stat_item_layout, individualStatsContainer, false);

                TextView statName = statRowView.findViewById(R.id.tv_stat_name);
                ProgressBar statProgressBar = statRowView.findViewById(R.id.pb_stat);
                TextView statValue = statRowView.findViewById(R.id.tv_stat_value);

                PokemonDetail.Stats pokemonStat = detail.getStats().get(i);
                int baseStat = pokemonStat.getBaseStat();

                statName.setText(statLabels.get(i));
                statValue.setText(String.valueOf(baseStat));
                statProgressBar.setMax(200);
                statProgressBar.setProgress(baseStat);

                int color;
                if (baseStat < 60) {
                    color = Color.parseColor("#F44336");
                } else if (baseStat < 90) {
                    color = Color.parseColor("#FFC107");
                } else {
                    color = Color.parseColor("#4CAF50");
                }

                LayerDrawable layerDrawable = (LayerDrawable) statProgressBar.getProgressDrawable();
                Drawable progressLayer = layerDrawable.findDrawableByLayerId(android.R.id.progress);
                DrawableCompat.setTint(progressLayer, color);

                individualStatsContainer.addView(statRowView);
            }
        }
    }

    private ImageView createTypeImageView(String typeName) {
        ImageView imageView = new ImageView(this);
        String resourceName = typeName.toLowerCase(Locale.ROOT) + "_type";
        int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());

        if (resourceId != 0) {
            imageView.setImageResource(resourceId);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(70), dpToPx(30));
        params.setMarginEnd(dpToPx(8));
        imageView.setLayoutParams(params);

        return imageView;
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}