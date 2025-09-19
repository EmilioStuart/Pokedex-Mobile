package com.example.pokmon;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    public enum SortType {
        ID_ASC, ID_DESC, NAME_ASC, NAME_DESC
    }

    private final List<PokemonDetail> pokemonDetailList = new ArrayList<>();
    private final Context context;
    private final PokeApiService pokeApiService;

    public PokemonAdapter(Context context) {
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokeApiService = retrofit.create(PokeApiService.class);
    }

    public void sortList(SortType sortType) {
        Comparator<PokemonDetail> comparator;
        switch (sortType) {
            case ID_DESC: comparator = Comparator.comparingInt(PokemonDetail::getId).reversed(); break;
            case NAME_ASC: comparator = Comparator.comparing(PokemonDetail::getName); break;
            case NAME_DESC: comparator = Comparator.comparing(PokemonDetail::getName).reversed(); break;
            default: comparator = Comparator.comparingInt(PokemonDetail::getId); break;
        }
        pokemonDetailList.sort(comparator);
        notifyDataSetChanged();
    }

    public void submitList(List<PokemonDetail> newPokemonDetails) {
        pokemonDetailList.clear();
        pokemonDetailList.addAll(newPokemonDetails);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_card_item, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        if (position >= pokemonDetailList.size()) return;

        PokemonDetail detail = pokemonDetailList.get(position);
        holder.pokemonNumber.setText(String.format(Locale.getDefault(), "Nº %04d", detail.getId()));
        holder.pokemonName.setText(detail.getName().substring(0, 1).toUpperCase() + detail.getName().substring(1));

        // Eu implemento uma estratégia de fallback para a imagem do sprite. Dou prioridade
        // ao sprite animado da 5ª geração, depois ao sprite 'home' de alta resolução e,
        // por fim, ao sprite padrão, garantindo que eu sempre tenha uma imagem para exibir.
        String imageUrl = null;
        if (detail.getSprites() != null) {
            if (detail.getSprites().getVersions() != null && detail.getSprites().getVersions().getGenerationV() != null && detail.getSprites().getVersions().getGenerationV().getBlackWhite() != null && detail.getSprites().getVersions().getGenerationV().getBlackWhite().getAnimated() != null && detail.getSprites().getVersions().getGenerationV().getBlackWhite().getAnimated().getFrontDefault() != null) {
                imageUrl = detail.getSprites().getVersions().getGenerationV().getBlackWhite().getAnimated().getFrontDefault();
            } else if (detail.getSprites().getOther() != null && detail.getSprites().getOther().getHome() != null && detail.getSprites().getOther().getHome().getFrontDefault() != null) {
                imageUrl = detail.getSprites().getOther().getHome().getFrontDefault();
            } else {
                imageUrl = detail.getSprites().getFrontDefault();
            }
        }

        Glide.with(context).load(imageUrl).placeholder(R.drawable.pokeball_placeholder).error(R.drawable.pokeball_placeholder).into(holder.pokemonSprite);

        holder.pokemonTypesContainer.removeAllViews();
        if (detail.getTypes() != null && !detail.getTypes().isEmpty()) {
            holder.pokemonTypesContainer.setVisibility(View.VISIBLE);
            for (PokemonDetail.Types typeWrapper : detail.getTypes()) {
                ImageView typeImageView = createTypeImageView(typeWrapper.getType().getName());
                holder.pokemonTypesContainer.addView(typeImageView);
            }
        } else {
            holder.pokemonTypesContainer.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("POKEMON_ID", detail.getId());
            context.startActivity(intent);
        });
    }

    private ImageView createTypeImageView(String typeName) {
        ImageView imageView = new ImageView(context);
        String resourceName = typeName.toLowerCase(Locale.ROOT) + "_type";

        int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        if (resourceId != 0) {
            imageView.setImageResource(resourceId);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(70), dpToPx(30));
        params.setMarginEnd(dpToPx(8));
        imageView.setLayoutParams(params);
        return imageView;
    }

    @Override
    public int getItemCount() {
        return pokemonDetailList.size();
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        TextView pokemonNumber, pokemonName;
        ImageView pokemonSprite;
        LinearLayout pokemonTypesContainer;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            pokemonNumber = itemView.findViewById(R.id.tv_pokemon_number);
            pokemonSprite = itemView.findViewById(R.id.iv_pokemon_sprite);
            pokemonName = itemView.findViewById(R.id.tv_pokemon_name);
            pokemonTypesContainer = itemView.findViewById(R.id.ll_pokemon_types_container);
        }
    }
}