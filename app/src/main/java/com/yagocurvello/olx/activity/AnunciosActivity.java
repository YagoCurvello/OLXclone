 package com.yagocurvello.olx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yagocurvello.olx.R;
import com.yagocurvello.olx.adapter.AdapterAnuncios;
import com.yagocurvello.olx.config.ConfigFirebase;
import com.yagocurvello.olx.helper.RecyclerItemClickListener;
import com.yagocurvello.olx.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

 public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private RecyclerView recyclerViewAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> anunciosPublicosList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private android.app.AlertDialog dialog;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean filtrandoEstado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        inicializarComponentes();

        adapterAnuncios = new AdapterAnuncios(anunciosPublicosList, this);

        recyclerViewAnunciosPublicos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewAnunciosPublicos.setHasFixedSize(true);
        recyclerViewAnunciosPublicos.setAdapter(adapterAnuncios);

        buttonRegiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtrarPorEstado();
            }
        });
        buttonCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtrarPorCategoria();
            }
        });

        recyclerViewAnunciosPublicos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,recyclerViewAnunciosPublicos,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view,int position) {
                Anuncio anuncioSelecionado = anunciosPublicosList.get(position);
                Intent intent = new Intent(AnunciosActivity.this, DetalhesProdutoActivity.class);
                intent.putExtra("anuncio", anuncioSelecionado);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view,int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i,long l) {

            }
        }
        ));

    }

     private void inicializarComponentes(){

         recyclerViewAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);
         buttonRegiao = findViewById(R.id.buttonRegiao);
         buttonCategoria = findViewById(R.id.buttonCategoria);

         auth = ConfigFirebase.getFirebaseAutenticacao();
         databaseReference = ConfigFirebase.getFirebaseDatabase().child("anuncios");

     }

     private void recuperarAnunciosPublicos(){

         dialog = new SpotsDialog.Builder()
                 .setContext(this)
                 .setMessage("Carregando Anuncios")
                 .setCancelable(false)
                 .build();
         dialog.show();

        anunciosPublicosList.clear();

         eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {

                 for (DataSnapshot estados : snapshot.getChildren()){
                     for (DataSnapshot categorias : estados.getChildren()){
                         for (DataSnapshot anuncios : categorias.getChildren()){
                              anunciosPublicosList.add(anuncios.getValue(Anuncio.class));
                         }
                     }
                 }

                 Collections.reverse(anunciosPublicosList);
                 adapterAnuncios.notifyDataSetChanged();

             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
         dialog.dismiss();
     }

     private void filtrarPorEstado(){

         AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
         dialogEstado.setTitle("Selecione o estado desejado");

         View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

         Spinner spinnerFiltro = viewSpinner.findViewById(R.id.spinnerFiltro);
         String[] estados = new String[]{
                 "Estado", "AL", "SE", "CE", "BA", "RJ", "SP"
         };
         ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                 this, android.R.layout.simple_spinner_item, estados);
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spinnerFiltro.setAdapter(adapter);

         dialogEstado.setView(viewSpinner);

         dialogEstado.setPositiveButton("OK",new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface,int i) {
                 filtroEstado = spinnerFiltro.getSelectedItem().toString();
                 recuperarAnunciosPorEstado();
                 filtrandoEstado = true;
             }
         });

         dialogEstado.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface,int i) {

             }
         });

         AlertDialog alert = dialogEstado.create();
         alert.show();
     }

     private void filtrarPorCategoria(){

         AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
         dialogCategoria.setTitle("Selecione a Categoria desejada");

         View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

         Spinner spinnerFiltro = viewSpinner.findViewById(R.id.spinnerFiltro);
         String[] categorias = new String[]{
                 "Categoria", "Automóveis", "Imóveis", "Eletrônicos", "Moda", "Esporte", "Musica"
         };
         ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                 this, android.R.layout.simple_spinner_item, categorias);
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spinnerFiltro.setAdapter(adapter);

         dialogCategoria.setView(viewSpinner);

         dialogCategoria.setPositiveButton("OK",new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface,int i) {
                 filtroCategoria = spinnerFiltro.getSelectedItem().toString();
                 recuperarAnunciosPorCategoria();
             }
         });

         dialogCategoria.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface,int i) {

             }
         });

         AlertDialog alert = dialogCategoria.create();
         alert.show();
     }

     private void recuperarAnunciosPorEstado(){

         dialog = new SpotsDialog.Builder()
                 .setContext(this)
                 .setMessage("Carregando Anuncios")
                 .setCancelable(false)
                 .build();
         dialog.show();

        DatabaseReference anunciosRef = ConfigFirebase.getFirebaseDatabase().child("anuncios").child(filtroEstado);
        anunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                anunciosPublicosList.clear();
                    for (DataSnapshot categorias : snapshot.getChildren()){
                        for (DataSnapshot anuncios : categorias.getChildren()){
                            anunciosPublicosList.add(anuncios.getValue(Anuncio.class));
                        }
                    }

                Collections.reverse(anunciosPublicosList);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

     }

     private void recuperarAnunciosPorCategoria(){

         dialog = new SpotsDialog.Builder()
                 .setContext(this)
                 .setMessage("Carregando Anuncios")
                 .setCancelable(false)
                 .build();
         dialog.show();

         if (filtrandoEstado){
             DatabaseReference anunciosRef = ConfigFirebase.getFirebaseDatabase().child("anuncios").child(filtroEstado).child(filtroCategoria);
             anunciosRef.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                     anunciosPublicosList.clear();
                     for (DataSnapshot anuncios : snapshot.getChildren()){
                         anunciosPublicosList.add(anuncios.getValue(Anuncio.class));
                     }

                     Collections.reverse(anunciosPublicosList);
                     adapterAnuncios.notifyDataSetChanged();
                     dialog.dismiss();
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {
                 }
             });
         }else {
             Toast.makeText(this, "Filtrar primeiro por estado", Toast.LENGTH_SHORT).show();
             dialog.dismiss();
         }


     }

     @Override
     protected void onStart() {
         super.onStart();
         recuperarAnunciosPublicos();
     }

     @Override
     protected void onStop() {
         super.onStop();
         databaseReference.removeEventListener(eventListener);
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (auth.getCurrentUser() == null){
            menu.setGroupVisible(R.id.grupo_deslogado, true);
            menu.setGroupVisible(R.id.grupo_logado, false);
        }else {
            menu.setGroupVisible(R.id.grupo_deslogado, false);
            menu.setGroupVisible(R.id.grupo_logado, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_anuncios:
                startActivity(new Intent(AnunciosActivity.this, MeusAnunciosActivity.class));
                break;

            case R.id.menu_sair:
                auth.signOut();
                invalidateOptionsMenu();
                break;

            case R.id.menu_cadastro:
                startActivity(new Intent(AnunciosActivity.this, CadastroActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
     }
 }