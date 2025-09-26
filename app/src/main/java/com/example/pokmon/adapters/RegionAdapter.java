package com.example.pokmon.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pokmon.R;
import com.example.pokmon.data.models.Region;
import com.example.pokmon.ui.PokemonGridActivity;
import java.util.List;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.RegionViewHolder> {

    private final List<Region> regionList;
    private final Context context;

    public RegionAdapter(List<Region> regionList, Context context) {
        this.regionList = regionList;
        this.context = context;
    }

    @NonNull
    @Override
    public RegionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.region_card_item, parent, false);
        return new RegionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegionViewHolder holder, int position) {
        Region region = regionList.get(position);
        holder.regionName.setText(region.getName());
        holder.pokemonCount.setText(String.format("%d Pokémon", region.getTotalPokemon()));

        Glide.with(context).load(region.getStarter1Url()).into(holder.starter1);
        Glide.with(context).load(region.getStarter2Url()).into(holder.starter2);
        Glide.with(context).load(region.getStarter3Url()).into(holder.starter3);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PokemonGridActivity.class);
            intent.putExtra("REGION_NAME", region.getName());
            intent.putExtra("GENERATION_ID", region.getGenerationId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return regionList.size();
    }

    public static class RegionViewHolder extends RecyclerView.ViewHolder {
        TextView regionName, pokemonCount;
        ImageView starter1, starter2, starter3;

        public RegionViewHolder(@NonNull View itemView) {
            super(itemView);
            regionName = itemView.findViewById(R.id.tv_region_name);
            pokemonCount = itemView.findViewById(R.id.tv_pokemon_count);
            starter1 = itemView.findViewById(R.id.iv_starter1);
            starter2 = itemView.findViewById(R.id.iv_starter2);
            starter3 = itemView.findViewById(R.id.iv_starter3);
        }
    }
}