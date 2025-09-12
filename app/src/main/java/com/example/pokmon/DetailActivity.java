package com.example.pokmon;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.*;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {

    private ImageView pokemonSprite;
    private TextView pokemonNumber, pokemonName, pokemonHeight, pokemonWeight, pokemonDescription;
    private LinearLayout typesContainer;
    private LinearLayout individualStatsContainer;
    private PokeApiService pokeApiService;
    private final Map<String, Integer> typeBackgrounds = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        setupRetrofit();
        populateTypeBackgrounds();

        int pokemonId = getIntent().getIntExtra("POKEMON_ID", -1);
        if (pokemonId != -1) {
            // Eu disparo as duas chamadas de API em paralelo para otimizar o tempo de carregamento.
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

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = retrofit.create(PokeApiService.class);
    }

    private void fetchPokemonDetails(int pokemonId) {
        pokeApiService.getPokemonDetail(String.valueOf(pokemonId)).enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(Call<PokemonDetail> call, Response<PokemonDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateUI(response.body());
                } else {
                    Toast.makeText(DetailActivity.this, "Falha ao carregar detalhes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PokemonDetail> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Eu crio este novo método para buscar os dados da "espécie", que contém a descrição.
    private void fetchPokemonSpecies(int pokemonId) {
        pokeApiService.getPokemonSpecies(pokemonId).enqueue(new Callback<PokemonSpecies>() {
            @Override
            public void onResponse(Call<PokemonSpecies> call, Response<PokemonSpecies> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // A API retorna múltiplas descrições; eu preciso filtrar para encontrar a primeira em inglês.
                    for (PokemonSpecies.FlavorTextEntry entry : response.body().getFlavorTextEntries()) {
                        if (entry.getLanguage().getName().equals("en")) {
                            // Eu limpo o texto de caracteres especiais como quebras de linha.
                            String cleanedText = entry.getFlavorText().replace('\n', ' ').replace('\f', ' ');
                            pokemonDescription.setText(cleanedText);
                            return; // Eu encerro o loop assim que encontrar a primeira correspondência.
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PokemonSpecies> call, Throwable t) {
                // Eu opto por uma falha silenciosa aqui, pois a descrição não é um dado crítico para a tela.
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
                TextView typeTextView = createTypeTextView(typeName);
                typesContainer.addView(typeTextView);
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

    private TextView createTypeTextView(String typeName) {
        TextView typeTextView = new TextView(this);
        typeTextView.setText(typeName.substring(0, 1).toUpperCase() + typeName.substring(1));
        typeTextView.setTextColor(Color.WHITE);
        typeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        typeTextView.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));
        typeTextView.setGravity(Gravity.CENTER);

        Integer backgroundResId = typeBackgrounds.get(typeName.toLowerCase(Locale.getDefault()));
        if (backgroundResId != null) {
            typeTextView.setBackgroundResource(backgroundResId);
        } else {
            typeTextView.setBackgroundResource(R.drawable.type_background_default);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(dpToPx(8));
        typeTextView.setLayoutParams(params);

        return typeTextView;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void populateTypeBackgrounds() {
        typeBackgrounds.put("grass", R.drawable.type_background_grass);
        typeBackgrounds.put("poison", R.drawable.type_background_poison);
        typeBackgrounds.put("fire", R.drawable.type_background_fire);
        typeBackgrounds.put("flying", R.drawable.type_background_flying);
        typeBackgrounds.put("water", R.drawable.type_background_water);
        typeBackgrounds.put("bug", R.drawable.type_background_bug);
        typeBackgrounds.put("normal", R.drawable.type_background_normal);
        typeBackgrounds.put("electric", R.drawable.type_background_electric);
        typeBackgrounds.put("ground", R.drawable.type_background_ground);
        typeBackgrounds.put("fairy", R.drawable.type_background_fairy);
        typeBackgrounds.put("fighting", R.drawable.type_background_fighting);
        typeBackgrounds.put("psychic", R.drawable.type_background_psychic);
        typeBackgrounds.put("rock", R.drawable.type_background_rock);
        typeBackgrounds.put("steel", R.drawable.type_background_steel);
        typeBackgrounds.put("ice", R.drawable.type_background_ice);
        typeBackgrounds.put("ghost", R.drawable.type_background_ghost);
        typeBackgrounds.put("dragon", R.drawable.type_background_dragon);
        typeBackgrounds.put("dark", R.drawable.type_background_dark);
    }
}