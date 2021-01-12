package com.yagocurvello.olx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.yagocurvello.olx.R;
import com.yagocurvello.olx.config.ConfigFirebase;

public class CadastroActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextSenha;
    private Switch switchLogin;
    private Button buttonAcessar;

    private String email, senha;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        buttonAcessar = findViewById(R.id.buttonAcessar);
        switchLogin = findViewById(R.id.switchLogin);

        auth = ConfigFirebase.getFirebaseAutenticacao();
        auth.signOut();

        buttonAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextEmail.getText().toString();
                senha = editTextSenha.getText().toString();

                if (verificaTexto(email, senha)){
                    if (switchLogin.isChecked()){
                        cadastrar();
                    }else {
                        logar();
                    }
                }
            }
        });

    }

    private void cadastrar(){
        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Usuario Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CadastroActivity.this, AnunciosActivity.class));
                } else {
                    String error;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        error = "Senha fraca";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        error = "email inválido";
                    } catch (FirebaseAuthUserCollisionException e){
                        error = "email já cadastrado";
                    }catch (Exception e){
                        error = "Erro: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void logar(){
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Usuario Logado com sucesso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CadastroActivity.this, AnunciosActivity.class));
                }else {
                    String error;
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e ){
                        error = "E-mail e/ou Senha inválidos";
                    }catch (FirebaseAuthInvalidCredentialsException e ){
                        error = "Senha incorreta";
                    } catch (Exception e) {
                        error = "Erro: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, error, Toast.LENGTH_LONG).show();
                    Log.i("Erro login", error);
                }
            }
        });
    }

    private boolean verificaTexto(String email, String senha){
        if (!email.isEmpty()){
            if (!senha.isEmpty()){
                return true;
            }else {
                Toast.makeText(this, "Campo Senha vazio", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(this, "Campo Email vazio", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}