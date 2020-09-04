package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
    }

    public  void Login(View v){
        Intent sig_login = new Intent(v.getContext(), MainActivity.class);
        startActivity(sig_login);
    }

    public  void Sing_In(View v){
        Intent sig_registro = new Intent(v.getContext(), Registro.class);
        startActivity(sig_registro);
    }
}
