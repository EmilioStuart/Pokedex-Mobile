package com.example.pokmon.adapters;

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
import com.example.pokmon.R;
import com.example.pokmon.data.models.PokemonDetail;
import com.example.pokmon.ui.DetailActivity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    public enum SortType {
        ID_ASC, ID_DESC, NAME_ASC, NAME_DESC
    }

    private final List<PokemonDetail> pokemonDetailList = new ArrayList<>();
    private final Context context;

    public PokemonAdapter(Context context) {
        this.context = context;
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
        View view = LayoutInflater.from(context).inflate(R.layout.pokemon_card_item, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        if (position >= pokemonDetailList.size()) return;

        PokemonDetail detail = pokemonDetailList.get(position);
        holder.pokemonNumber.setText(String.format(Locale.getDefault(), "#%03d", detail.getId()));
        holder.pokemonName.setText(detail.getName().substring(0, 1).toUpperCase() + detail.getName().substring(1));

        String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/" + detail.getId() + ".gif";

        Glide.with(context)
                .asGif()
                .load(imageUrl)
                .placeholder(R.drawable.pokeball_placeholder)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.pokemonSprite);

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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(50), dpToPx(18));
        params.setMargins(0,0,0, dpToPx(4));
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
            pokemonName = itemView.findViewById(R.id.tv_pokemon_name);
            pokemonSprite = itemView.findViewById(R.id.iv_pokemon_sprite);
            pokemonTypesContainer = itemView.findViewById(R.id.ll_pokemon_types_container);
        }
    }
}