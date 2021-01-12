package com.yagocurvello.olx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.yagocurvello.olx.R;
import com.yagocurvello.olx.model.Anuncio;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private Anuncio anuncio;
    private TextView titulo, valor, estado, descricao;
    private CarouselView carouselView;
    private Button buttonLigar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        getSupportActionBar().setTitle("Detalhe Anuncio");

        configuracoesIniciais();

        anuncio = (Anuncio) getIntent().getSerializableExtra("anuncio");
        if (anuncio!=null){
            titulo.setText(anuncio.getTitulo());
            valor.setText(anuncio.getValor());
            estado.setText(anuncio.getEstado());
            descricao.setText(anuncio.getDescricao());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position,ImageView imageView) {
                    String url = anuncio.getListaFotos().get(position);
                    Picasso.get().load(url).into(imageView);
                }
            };
            carouselView.setPageCount(anuncio.getListaFotos().size());
            carouselView.setImageListener(imageListener);
        }

        buttonLigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visualizarTelefone();
            }
        });


    }

    private void configuracoesIniciais(){
        titulo = findViewById(R.id.textViewDetalheTitulo);
        valor = findViewById(R.id.textViewDetalheValor);
        estado = findViewById(R.id.textViewDetalheEstado);
        descricao = findViewById(R.id.textViewDetalheDescricao);
        carouselView = findViewById(R.id.carouselView);
        buttonLigar = findViewById(R.id.buttonLigar);
    }

    private void visualizarTelefone(){
        Intent i = new Intent(Intent.ACTION_DIAL,Uri.fromParts("tel", anuncio.getTelefone(), null));
        startActivity(i);
    }
}