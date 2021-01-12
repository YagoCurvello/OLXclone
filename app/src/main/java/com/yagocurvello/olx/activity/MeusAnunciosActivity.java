package com.yagocurvello.olx.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yagocurvello.olx.R;
import com.yagocurvello.olx.adapter.AdapterAnuncios;
import com.yagocurvello.olx.config.ConfigFirebase;
import com.yagocurvello.olx.helper.RecyclerItemClickListener;
import com.yagocurvello.olx.helper.UsuarioFirebase;
import com.yagocurvello.olx.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerViewAnuncios;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> anuncioList = new ArrayList<>();

    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inicializarComponentes();

        adapterAnuncios = new AdapterAnuncios(anuncioList, this);

        recyclerViewAnuncios.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewAnuncios.setHasFixedSize(true);
        recyclerViewAnuncios.setAdapter(adapterAnuncios);

        recyclerViewAnuncios.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerViewAnuncios,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view,int position) {

            }

            @Override
            public void onLongItemClick(View view,int position) {
                Anuncio anuncioSelecionado = anuncioList.get(position);
                anuncioSelecionado.excluir();
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i,long l) {

            }
        }));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));
            }
        });
    }

    private void inicializarComponentes(){

        recyclerViewAnuncios = findViewById(R.id.recyclerAnuncios);
        progressBar = findViewById(R.id.progressBarMeusAnuncios);

        databaseReference = ConfigFirebase.getFirebaseDatabase().child("meus-anuncios")
        .child(UsuarioFirebase.getIdUsuario());

    }

    private void recuperarMeusAnuncios(){

        progressBar.setVisibility(View.VISIBLE);

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                anuncioList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    anuncioList.add(dataSnapshot.getValue(Anuncio.class));
                }

                Collections.reverse(anuncioList);
                adapterAnuncios.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMeusAnuncios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(eventListener);
    }
}