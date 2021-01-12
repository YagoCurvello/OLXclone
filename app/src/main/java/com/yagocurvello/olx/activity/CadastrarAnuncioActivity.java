package com.yagocurvello.olx.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;
import com.yagocurvello.olx.R;
import com.yagocurvello.olx.config.ConfigFirebase;
import com.yagocurvello.olx.helper.Permissao;
import com.yagocurvello.olx.model.Anuncio;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity
            implements View.OnClickListener {

    private EditText editTextTitulo, editTextDescricao;
    private CurrencyEditText editTextValor;
    private MaskEditText editTextTelefone;
    private ImageView imageAnuncio0, imageAnuncio1, imageAnuncio2;
    private Spinner spinnerCEP, spinnerCategoria;
    private Button buttonSalvarAnuncio;

    private List<String> listFotosRecuperadas;
    private List<String> listFotosUrl;

    private StorageReference storageReference;


    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private Anuncio anuncio;

    private android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        Permissao.validarPermissoes(permissoes, this, 100);

        configIniciais();

        carregarDadosSpinner();

        buttonSalvarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anuncio = configurarAnuncio();
                if (validarDadosAnuncio(anuncio)){
                    salvarAnuncio(anuncio);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.imageAnuncio1:
                escolherImagem(1);
                break;

            case R.id.imageAnuncio2:
                escolherImagem(2);
                break;

            case R.id.imageAnuncio3:
                escolherImagem(3);
                break;
        }
    }

    private void escolherImagem (int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    private void salvarAnuncio(Anuncio anuncio){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anúncio")
                .setCancelable(false)
                .build();
        dialog.show();

        for (int i=0; i < listFotosRecuperadas.size(); i++){
            String urlImagem = listFotosRecuperadas.get(i);
            salvarFotosStorage(urlImagem, listFotosRecuperadas.size(), i);
        }

    }

    private void salvarFotosStorage(String urlString, int totalFotos, int i){

        String nomeArquivo = UUID.randomUUID().toString();

        StorageReference storageReferenceAnuncios = storageReference
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+i)
                .child(nomeArquivo+".jpeg");

        //Fazer Upload
        UploadTask uploadTask = storageReferenceAnuncios.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReferenceAnuncios.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri firebaseUrl = task.getResult();
                        listFotosUrl.add(firebaseUrl.toString());

                        if (totalFotos == listFotosUrl.size()){
                            anuncio.setListaFotos(listFotosUrl);
                            anuncio.salvar();
                            dialog.dismiss();
                            finish();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMsgErro("Falha no upload das imagens");
            }
        });
    }

    private Anuncio configurarAnuncio (){
        String estado = spinnerCEP.getSelectedItem().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String titulo = editTextTitulo.getText().toString();
        String valor = editTextValor.getText().toString();
        String descrição = editTextDescricao.getText().toString();
        String telefone = editTextTelefone.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setCategoria(categoria);
        anuncio.setDescricao(descrição);
        anuncio.setEstado(estado);
        anuncio.setTelefone(telefone);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);

        return anuncio;
    }

    private void configIniciais(){
        editTextDescricao = findViewById(R.id.editTextDescricaoAnuncio);
        editTextTitulo = findViewById(R.id.editTextTituloAnuncio);
        editTextValor = findViewById(R.id.editTextValorAnuncio);
        editTextTelefone = findViewById(R.id.editTextTelefoneAnuncio);
        imageAnuncio0 = findViewById(R.id.imageAnuncio1);
        imageAnuncio0.setOnClickListener(this);
        imageAnuncio1 = findViewById(R.id.imageAnuncio2);
        imageAnuncio1.setOnClickListener(this);
        imageAnuncio2 = findViewById(R.id.imageAnuncio3);
        imageAnuncio2.setOnClickListener(this);
        spinnerCEP = findViewById(R.id.spinnerCEP);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        buttonSalvarAnuncio = findViewById(R.id.buttonSalvarAnuncio);

        //Configura Localização para PT-BR
        Locale locale = new Locale("pt","BR");
        editTextValor.setLocale(locale);

        listFotosRecuperadas = new ArrayList<>();
        listFotosUrl = new ArrayList<>();

        storageReference = ConfigFirebase.getFirebaseStorage().child("imagens");

    }

    private void carregarDadosSpinner(){

        String[] estados = new String[]{
                "Estado", "AL", "SE", "CE", "BA", "RJ", "SP"
        };

        String[] categorias = new String[]{
                "Categoria", "Automóveis", "Imóveis", "Eletrônicos", "Moda", "Esporte", "Musica"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categorias);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCEP.setAdapter(adapter);
        spinnerCategoria.setAdapter(adapter2);
    }

    private boolean validarDadosAnuncio(Anuncio anuncio){

        String valor = String.valueOf(editTextValor.getRawValue());

        if (listFotosRecuperadas.size() != 0){
            if (!anuncio.getEstado().isEmpty()){
                if (!anuncio.getCategoria().isEmpty()){
                    if (!valor.isEmpty() && !valor.equals("0")){
                        if (!anuncio.getDescricao().isEmpty()){
                            if (!anuncio.getTelefone().isEmpty()){
                                if (!anuncio.getTitulo().isEmpty()){
                                    return true;
                                }else {
                                    exibirMsgErro("Adicionar um titulo para o anuncio");
                                    return false;
                                }
                            }else {
                                exibirMsgErro("Adicionar um telefone para o anuncio");
                                return false;
                            }
                        }else {
                            exibirMsgErro("Adicionar uma descrição para o anuncio");
                            return false;
                        }
                    }else {
                        exibirMsgErro("Selecionar um valor para o anuncio");
                        return false;
                    }
                }else {
                    exibirMsgErro("Selecionar uma categoria válida");
                    return false;
                }
            }else {
                exibirMsgErro("Selecionar um Estado válido");
                return false;
            }
        }else {
           exibirMsgErro("Selecione fotos para o anuncio");
            return false;
        }
    }

    private void exibirMsgErro (String erro){
        Toast.makeText(getApplicationContext(), erro, Toast.LENGTH_SHORT).show();
    }

    private void alertaValidacaoPermissao (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissoes Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissoes");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface,int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == Activity.RESULT_OK){

            //Recupera Imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no ImageView
            if (requestCode == 1){
                imageAnuncio0.setImageURI(imagemSelecionada);
                Log.i("uri", "ok");
            }else if (requestCode == 2){
                imageAnuncio1.setImageURI(imagemSelecionada);
            }else if (requestCode == 3){
                imageAnuncio2.setImageURI(imagemSelecionada);
            }
            listFotosRecuperadas.add(caminhoImagem);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        for (int permissaoResultado : grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }

    }

}