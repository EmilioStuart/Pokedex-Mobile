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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {

    private ViewFlipper pokemonSpriteFlipper;
    private ImageButton btnPrevSprite, btnNextSprite;
    private TextView pokemonNumber, pokemonName, pokemonHeight, pokemonWeight, pokemonDescription;
    private LinearLayout typesContainer;
    private LinearLayout individualStatsContainer;
    private LinearLayout evolutionChainContainer;
    private FloatingActionButton fabShiny;
    private PokeApiService pokeApiService;
    private TranslationApiService translationApiService;
    private int currentPokemonId;

    private final List<String> normalSpritesUrls = new ArrayList<>();
    private final List<String> shinySpritesUrls = new ArrayList<>();
    private boolean isShinyShowing = false;

    private final List<PokemonDetail> evolutionChainDetails = new ArrayList<>();
    private int evolutionDetailsFetchedCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        setupRetrofitServices();
        setupShinyFab();
        setupSpriteNavigationButtons();

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
        pokemonSpriteFlipper = findViewById(R.id.vf_pokemon_sprites);
        btnPrevSprite = findViewById(R.id.btn_prev_sprite);
        btnNextSprite = findViewById(R.id.btn_next_sprite);
        pokemonNumber = findViewById(R.id.tv_detail_number);
        pokemonName = findViewById(R.id.tv_detail_name);
        pokemonHeight = findViewById(R.id.tv_detail_height);
        pokemonWeight = findViewById(R.id.tv_detail_weight);
        pokemonDescription = findViewById(R.id.tv_pokemon_description);
        typesContainer = findViewById(R.id.ll_detail_types_container);
        individualStatsContainer = findViewById(R.id.ll_individual_stats_container);
        evolutionChainContainer = findViewById(R.id.ll_evolution_chain_container);
        fabShiny = findViewById(R.id.fab_shiny);
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

    private void setupSpriteNavigationButtons() {
        btnPrevSprite.setOnClickListener(v -> pokemonSpriteFlipper.showPrevious());
        btnNextSprite.setOnClickListener(v -> pokemonSpriteFlipper.showNext());
    }

    private void toggleShinySprite() {
        isShinyShowing = !isShinyShowing;
        updateMainSprites();
        populateEvolutionChart();
    }

    private void fetchPokemonDetails(int pokemonId) {
        pokeApiService.getPokemonDetail(String.valueOf(pokemonId)).enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetail> call, @NonNull Response<PokemonDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonDetail detail = response.body();
                    collectSpriteUrls(detail);
                    populateUI(detail);
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonDetail> call, @NonNull Throwable t) {
                Toast.makeText(DetailActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void collectSpriteUrls(PokemonDetail detail) {
        normalSpritesUrls.clear();
        shinySpritesUrls.clear();

        if (detail.getSprites() != null) {
            if (detail.getSprites().getOther() != null && detail.getSprites().getOther().getHome() != null) {
                if (detail.getSprites().getOther().getHome().getFrontDefault() != null)
                    normalSpritesUrls.add(detail.getSprites().getOther().getHome().getFrontDefault());
                if (detail.getSprites().getOther().getHome().getFrontShiny() != null)
                    shinySpritesUrls.add(detail.getSprites().getOther().getHome().getFrontShiny());
            }
            if (detail.getSprites().getOther() != null && detail.getSprites().getOther().getOfficialArtwork() != null) {
                if (detail.getSprites().getOther().getOfficialArtwork().getFrontDefault() != null)
                    normalSpritesUrls.add(detail.getSprites().getOther().getOfficialArtwork().getFrontDefault());
                if (detail.getSprites().getOther().getOfficialArtwork().getFrontShiny() != null)
                    shinySpritesUrls.add(detail.getSprites().getOther().getOfficialArtwork().getFrontShiny());
            }
            if (detail.getSprites().getFrontDefault() != null)
                normalSpritesUrls.add(detail.getSprites().getFrontDefault());
            if (detail.getSprites().getFrontShiny() != null)
                shinySpritesUrls.add(detail.getSprites().getFrontShiny());
        }
    }

    private void updateMainSprites() {
        int currentIndex = pokemonSpriteFlipper.getDisplayedChild();
        pokemonSpriteFlipper.removeAllViews();
        List<String> urlsToLoad = isShinyShowing ? shinySpritesUrls : normalSpritesUrls;

        if (urlsToLoad.isEmpty()) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.pokeball_placeholder);
            pokemonSpriteFlipper.addView(imageView);
            return;
        }

        for (String url : urlsToLoad) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(200), dpToPx(200));
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            Glide.with(this).load(url).placeholder(R.drawable.pokeball_placeholder).error(R.drawable.pokeball_placeholder).into(imageView);
            pokemonSpriteFlipper.addView(imageView);
        }

        if (pokemonSpriteFlipper.getChildCount() > 0 && currentIndex >= 0 && currentIndex < pokemonSpriteFlipper.getChildCount()) {
            pokemonSpriteFlipper.setDisplayedChild(currentIndex);
        }
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

                    evolutionChainDetails.clear();
                    evolutionDetailsFetchedCounter = 0;
                    if (evolutionLine.isEmpty()) return;

                    for (PokemonSpeciesInfo species : evolutionLine) {
                        fetchDetailsForEvolutionStage(species, evolutionLine.size());
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<EvolutionChainResponse> call, @NonNull Throwable t) {}
        });
    }

    private void fetchDetailsForEvolutionStage(PokemonSpeciesInfo species, int totalToFetch) {
        pokeApiService.getPokemonDetail(species.getName()).enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetail> call, @NonNull Response<PokemonDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    evolutionChainDetails.add(response.body());
                }
                evolutionDetailsFetchedCounter++;
                if (evolutionDetailsFetchedCounter == totalToFetch) {
                    onAllEvolutionDetailsFetched();
                }
            }
            @Override
            public void onFailure(@NonNull Call<PokemonDetail> call, @NonNull Throwable t) {
                evolutionDetailsFetchedCounter++;
                if (evolutionDetailsFetchedCounter == totalToFetch) {
                    onAllEvolutionDetailsFetched();
                }
            }
        });
    }

    private void onAllEvolutionDetailsFetched() {
        evolutionChainDetails.sort(Comparator.comparingInt(PokemonDetail::getId));
        populateEvolutionChart();
    }

    private void parseEvolutionChain(ChainLink chainLink, List<PokemonSpeciesInfo> evolutionLine) {
        if (chainLink == null) return;
        evolutionLine.add(chainLink.getSpecies());
        if (chainLink.getEvolvesTo() != null && !chainLink.getEvolvesTo().isEmpty()) {
            parseEvolutionChain(chainLink.getEvolvesTo().get(0), evolutionLine);
        }
    }

    private void populateEvolutionChart() {
        evolutionChainContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < evolutionChainDetails.size(); i++) {
            PokemonDetail detail = evolutionChainDetails.get(i);

            View stageView = inflater.inflate(R.layout.evolution_stage_item, evolutionChainContainer, false);
            ImageView sprite = stageView.findViewById(R.id.iv_evolution_sprite);
            TextView name = stageView.findViewById(R.id.tv_evolution_name);

            name.setText(detail.getName().substring(0, 1).toUpperCase() + detail.getName().substring(1));

            String imageUrl;
            if (isShinyShowing) {
                imageUrl = detail.getSprites().getOther().getOfficialArtwork().getFrontShiny();
            } else {
                imageUrl = detail.getSprites().getOther().getOfficialArtwork().getFrontDefault();
            }

            Glide.with(this).load(imageUrl).into(sprite);

            if (detail.getId() != this.currentPokemonId) {
                stageView.setOnClickListener(v -> {
                    Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                    intent.putExtra("POKEMON_ID", detail.getId());
                    startActivity(intent);
                    finish();
                });
            }

            evolutionChainContainer.addView(stageView);

            if (i < evolutionChainDetails.size() - 1) {
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

        isShinyShowing = false;
        updateMainSprites();

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