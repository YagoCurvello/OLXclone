package com.yagocurvello.olx.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.yagocurvello.olx.config.ConfigFirebase;
import com.yagocurvello.olx.helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.List;

public class Anuncio implements Serializable {

    private String idAnuncio, estado, categoria, titulo, valor, descricao, telefone;
    private List <String> listaFotos;


    public Anuncio() {
        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase().child("meus-anuncios");
        this.setIdAnuncio(databaseReference.push().getKey());
    }

    public List<String> getListaFotos() {
        return listaFotos;
    }

    public void setListaFotos(List<String> listaFotos) {
        this.listaFotos = listaFotos;
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void salvar(){
        String idUsuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase().child("meus-anuncios");
        databaseReference.child(idUsuario)
                .child(getIdAnuncio()).setValue(this);

        salvarPublico();
    }

    public void salvarPublico(){
        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase().child("anuncios");
        databaseReference.child(getEstado())
                .child(getCategoria()).child(getIdAnuncio()).setValue(this);
    }

    public void excluir(){
        String idUsuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase()
                .child("meus-anuncios")
                .child(idUsuario)
                .child(getIdAnuncio());

        databaseReference.removeValue();

        excluirAnuncioPublico();
    }

    private void excluirAnuncioPublico(){
        DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase()
                .child("anuncios")
                .child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio());

        databaseReference.removeValue();
    }
}
