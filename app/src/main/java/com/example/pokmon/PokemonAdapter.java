package com.example.pokmon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    public enum SortType {
        ID_ASC,
        ID_DESC,
        NAME_ASC,
        NAME_DESC
    }

    private final List<Pokemon> pokemonList = new ArrayList<>();
    private final List<PokemonDetail> pokemonDetailList = new ArrayList<>();
    private final Context context;
    private final PokeApiService pokeApiService;
    private final Map<String, Integer> typeBackgrounds = new HashMap<>();

    public PokemonAdapter(Context context) {
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = retrofit.create(PokeApiService.class);

        populateTypeBackgrounds();
    }

    public void sortList(SortType sortType) {
        Comparator<PokemonDetail> comparator;

        switch (sortType) {
            case ID_DESC:
                comparator = Comparator.comparingInt(PokemonDetail::getId).reversed();
                break;
            case NAME_ASC:
                comparator = Comparator.comparing(PokemonDetail::getName);
                break;
            case NAME_DESC:
                comparator = Comparator.comparing(PokemonDetail::getName).reversed();
                break;
            case ID_ASC:
            default:
                comparator = Comparator.comparingInt(PokemonDetail::getId);
                break;
        }

        pokemonDetailList.sort(comparator);
        notifyDataSetChanged();
    }

    public void setPokemonList(List<Pokemon> pokemonList) {
        this.pokemonList.clear();
        this.pokemonList.addAll(pokemonList);
        this.pokemonDetailList.clear();
        notifyDataSetChanged();

        for (Pokemon pokemon : pokemonList) {
            fetchPokemonDetails(pokemon);
        }
    }

    private void fetchPokemonDetails(Pokemon pokemon) {
        String[] urlParts = pokemon.getUrl().split("/");
        String idOrName = urlParts[urlParts.length - 1];

        pokeApiService.getPokemonDetail(idOrName).enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetail> call, @NonNull Response<PokemonDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pokemonDetailList.add(response.body());
                    // Eu reordeno a lista por ID a cada nova adição para garantir que a UI
                    // se mantenha consistente, já que as chamadas de rede são assíncronas
                    // e podem terminar fora da ordem original da requisição.
                    sortList(SortType.ID_ASC);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonDetail> call, @NonNull Throwable t) {
                // Falha silenciosa
            }
        });
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_card_item, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        if (position < pokemonDetailList.size()) {
            PokemonDetail detail = pokemonDetailList.get(position);

            holder.pokemonNumber.setText(String.format(Locale.getDefault(), "Nº %04d", detail.getId()));
            holder.pokemonName.setText(detail.getName().substring(0, 1).toUpperCase() + detail.getName().substring(1));

            // Eu implemento uma estratégia de fallback para a imagem do sprite. Dou prioridade
            // ao sprite animado da 5ª geração, depois ao sprite 'home' de alta resolução e,
            // por fim, ao sprite padrão, garantindo que eu sempre tenha uma imagem para exibir.
            String imageUrl = null;
            if (detail.getSprites() != null &&
                    detail.getSprites().getVersions() != null &&
                    detail.getSprites().getVersions().getGenerationV() != null &&
                    detail.getSprites().getVersions().getGenerationV().getBlackWhite() != null &&
                    detail.getSprites().getVersions().getGenerationV().getBlackWhite().getAnimated() != null &&
                    detail.getSprites().getVersions().getGenerationV().getBlackWhite().getAnimated().getFrontDefault() != null) {
                imageUrl = detail.getSprites().getVersions().getGenerationV().getBlackWhite().getAnimated().getFrontDefault();
            } else if (detail.getSprites() != null &&
                    detail.getSprites().getOther() != null &&
                    detail.getSprites().getOther().getHome() != null &&
                    detail.getSprites().getOther().getHome().getFrontDefault() != null) {
                imageUrl = detail.getSprites().getOther().getHome().getFrontDefault();
            } else if (detail.getSprites() != null && detail.getSprites().getFrontDefault() != null) {
                imageUrl = detail.getSprites().getFrontDefault();
            }

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.pokeball_placeholder)
                    .error(R.drawable.pokeball_placeholder)
                    .into(holder.pokemonSprite);

            holder.pokemonTypesContainer.removeAllViews();
            if (detail.getTypes() != null && !detail.getTypes().isEmpty()) {
                for (PokemonDetail.Types typeWrapper : detail.getTypes()) {
                    String typeName = typeWrapper.getType().getName();
                    TextView typeTextView = new TextView(context);

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
                    holder.pokemonTypesContainer.addView(typeTextView);
                }
            }
            holder.pokemonTypesContainer.setVisibility(View.VISIBLE);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("POKEMON_ID", detail.getId());
                context.startActivity(intent);
            });

        } else {
            holder.pokemonTypesContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return pokemonDetailList.size();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        TextView pokemonNumber;
        ImageView pokemonSprite;
        TextView pokemonName;
        LinearLayout pokemonTypesContainer;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            pokemonNumber = itemView.findViewById(R.id.tv_pokemon_number);
            pokemonSprite = itemView.findViewById(R.id.iv_pokemon_sprite);
            pokemonName = itemView.findViewById(R.id.tv_pokemon_name);
            pokemonTypesContainer = itemView.findViewById(R.id.ll_pokemon_types_container);
        }
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