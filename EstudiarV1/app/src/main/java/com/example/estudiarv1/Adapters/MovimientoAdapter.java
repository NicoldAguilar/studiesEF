package com.example.estudiarv1.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.estudiarv1.R;
import com.example.estudiarv1.entities.Movimientos;
import com.example.estudiarv1.entities.Pokemon;

import java.util.List;

public class MovimientoAdapter extends RecyclerView.Adapter{

    private List<Movimientos> items;
    Context context;

    public MovimientoAdapter(List<Movimientos> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovimientoAdapter.NameViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == 1) {
            View view = inflater.inflate(R.layout.item_string_coment, parent, false);
            viewHolder = new MovimientoAdapter.NameViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_progressbar, parent, false);
            viewHolder = new MovimientoAdapter.NameViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Movimientos mov = new Movimientos();
        mov = items.get(position);

        if(mov == null) return;

        View view = holder.itemView;

        Log.i("MAIN_APP", items.get(position).getComentario()+"B");
        TextView tvMovimiento = view.findViewById(R.id.tvMovimientos);
        tvMovimiento.setText(items.get(position).getComentario()+".");

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public int getItemViewType(int position) {
        Movimientos item = items.get(position);
        return item == null ? 0 : 1;
    }

    public class NameViewHolder extends RecyclerView.ViewHolder {
        public NameViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setMovimiento(List<Movimientos> movimientos) {
        this.items = movimientos;
    }
}
