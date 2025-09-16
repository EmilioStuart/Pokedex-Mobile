package com.example.pokmon;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
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
    private PokeApiService pokeApiService;
    private TranslationApiService translationApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        setupRetrofitServices();

        int pokemonId = getIntent().getIntExtra("POKEMON_ID", -1);
        if (pokemonId != -1) {
            fetchPokemonDetails(pokemonId);
            fetchPokemonSpecies(pokemonId);
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
    }

    private void setupRetrofitServices() {
        // Eu crio uma instância do Retrofit para a PokeAPI.
        Retrofit pokeApiRetrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = pokeApiRetrofit.create(PokeApiService.class);

        // Eu crio uma segunda instância do Retrofit para a Translation API.
        Retrofit translationRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.mymemory.translated.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        translationApiService = translationRetrofit.create(TranslationApiService.class);
    }

    private void fetchPokemonDetails(int pokemonId) {
        pokeApiService.getPokemonDetail(String.valueOf(pokemonId)).enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetail> call, @NonNull Response<PokemonDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateUI(response.body());
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
                    response.body().getFlavorTextEntries().stream()
                            .filter(entry -> entry.getLanguage().getName().equals("en"))
                            .findFirst()
                            .ifPresent(entry -> {
                                String cleanedText = entry.getFlavorText().replace('\n', ' ').replace('\f', ' ');
                                pokemonDescription.setText("Traduzindo para o Português...");
                                translateDescription(cleanedText);
                            });
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonSpecies> call, @NonNull Throwable t) {
            }
        });
    }

    private void translateDescription(String textInEnglish) {
        translationApiService.translate(textInEnglish, "en|pt-br").enqueue(new Callback<TranslationResponse>() {
            @Override
            public void onResponse(@NonNull Call<TranslationResponse> call, @NonNull Response<TranslationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String translatedText = response.body().getResponseData().getTranslatedText();
                    pokemonDescription.setText(translatedText);
                } else {
                    // Se a tradução falhar, eu exibo o texto em inglês
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

        String imageUrl = null;
        if (detail.getSprites() != null && detail.getSprites().getOther() != null && detail.getSprites().getOther().getHome() != null && detail.getSprites().getOther().getHome().getFrontDefault() != null) {
            imageUrl = detail.getSprites().getOther().getHome().getFrontDefault();
        }
        Glide.with(this).load(imageUrl).placeholder(R.drawable.pokeball_placeholder).into(pokemonSprite);

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