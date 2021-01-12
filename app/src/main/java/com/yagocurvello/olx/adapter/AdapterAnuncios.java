package com.yagocurvello.olx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yagocurvello.olx.R;
import com.yagocurvello.olx.model.Anuncio;

import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Anuncio> anuncioList;
    private Context context;

    public AdapterAnuncios(List<Anuncio> anuncioList,Context context) {
        this.anuncioList = anuncioList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncios, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {
        Anuncio anuncio = anuncioList.get(position);
        holder.titulo.setText(anuncio.getTitulo());
        holder.valor.setText(anuncio.getValor());
        Picasso.get().load(anuncio.getListaFotos().get(0)).into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return anuncioList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, valor;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textViewTitulo);
            valor = itemView.findViewById(R.id.textViewValor);
            foto = itemView.findViewById(R.id.imageViewFoto);
        }
    }

}
