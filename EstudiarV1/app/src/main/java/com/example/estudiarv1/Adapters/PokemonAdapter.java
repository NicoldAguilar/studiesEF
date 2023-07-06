package com.example.estudiarv1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.estudiarv1.DetalleActivity;
import com.example.estudiarv1.R;
import com.example.estudiarv1.entities.Pokemon;
import com.google.android.gms.common.internal.ConnectionTelemetryConfiguration;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter {
    private List<Pokemon> items;
    Context context;
    public PokemonAdapter(List<Pokemon> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NameViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == 1) {
            View view = inflater.inflate(R.layout.item_string, parent, false);
            viewHolder = new NameViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_progressbar, parent, false);
            viewHolder = new NameViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Pokemon item = items.get(position);

        if(item == null) return;

        View view = holder.itemView;

        TextView tvName = view.findViewById(R.id.tvNamePoke);
        TextView tvTipe = view.findViewById(R.id.tvTipoPoke);
        ImageView imageView = view.findViewById(R.id.imageView);
        tvName.setText(item.nombre);
        tvTipe.setText(item.tipo);

        //Picasso.get().load("https://i.imgur.com/DvpvklR.png").into(imageView);
        Picasso.get().load(item.getFoto())
                .resize(300, 400) //tamaño específico
                .into(imageView);

        //evita el uso de un boton "ver detalle"
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(context, DetalleActivity.class);
                intent.putExtra("position", item.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Pokemon item = items.get(position);
        return item == null ? 0 : 1;
    }

    public class NameViewHolder extends RecyclerView.ViewHolder {

        public NameViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setPokemon(List<Pokemon> pokemon) {
        this.items = pokemon;
    }
}
