package com.example.pokmon;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {

    private ImageView pokemonSprite;
    private TextView pokemonNumber, pokemonName, pokemonHeight, pokemonWeight;
    private LinearLayout typesContainer;
    private PokeApiService pokeApiService;
    private Map<String, Integer> typeBackgrounds = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        setupRetrofit();
        populateTypeBackgrounds();

        // Eu recupero o ID do Pokémon que foi passado pela MainActivity.
        int pokemonId = getIntent().getIntExtra("POKEMON_ID", -1);

        // Eu verifico se o ID é válido antes de fazer a chamada de rede.
        if (pokemonId != -1) {
            fetchPokemonDetails(pokemonId);
        } else {
            Toast.makeText(this, "Erro: ID do Pokémon não encontrado", Toast.LENGTH_SHORT).show();
            finish(); // Fecha a activity se não houver ID
        }
    }

    private void initializeViews() {
        pokemonSprite = findViewById(R.id.iv_detail_sprite);
        pokemonNumber = findViewById(R.id.tv_detail_number);
        pokemonName = findViewById(R.id.tv_detail_name);
        pokemonHeight = findViewById(R.id.tv_detail_height);
        pokemonWeight = findViewById(R.id.tv_detail_weight);
        typesContainer = findViewById(R.id.ll_detail_types_container);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = retrofit.create(PokeApiService.class);
    }

    private void fetchPokemonDetails(int pokemonId) {
        // Eu uso o ID recebido para fazer uma chamada específica para os detalhes deste Pokémon.
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

    private void populateUI(PokemonDetail detail) {
        // Eu preencho todos os campos da UI com os dados recebidos da API.
        pokemonName.setText(detail.getName().substring(0, 1).toUpperCase() + detail.getName().substring(1));
        pokemonNumber.setText(String.format(Locale.getDefault(), "Nº %04d", detail.getId()));

        // A API retorna altura em decímetros e peso em hectogramas. Eu converto para metros e kg.
        float heightInMeters = detail.getHeight() / 10.0f;
        float weightInKg = detail.getWeight() / 10.0f;
        pokemonHeight.setText(String.format(Locale.getDefault(), "%.1f m", heightInMeters));
        pokemonWeight.setText(String.format(Locale.getDefault(), "%.1f kg", weightInKg));

        // Lógica para carregar a imagem 3D (igual à do adapter)
        String imageUrl = detail.getSprites().getOther().getHome().getFrontDefault();
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.pokeball_placeholder)
                .into(pokemonSprite);

        // Lógica para criar os tipos (reaproveitada do adapter)
        typesContainer.removeAllViews();
        if (detail.getTypes() != null && !detail.getTypes().isEmpty()) {
            for (PokemonDetail.Types typeWrapper : detail.getTypes()) {
                String typeName = typeWrapper.getType().getName();
                TextView typeTextView = createTypeTextView(typeName);
                typesContainer.addView(typeTextView);
            }
        }
    }

    // Eu extraí a criação do TextView de tipo para um método reutilizável.
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

    // Eu preciso preencher o mapa de cores aqui também para a DetailActivity saber como colorir os tipos.
    private void populateTypeBackgrounds() {
        typeBackgrounds.put("grass", R.drawable.type_background_grass);
        typeBackgrounds.put("poison", R.drawable.type_background_poison);
        // ... adicione todos os outros tipos aqui, igual ao seu adapter ...
    }
}