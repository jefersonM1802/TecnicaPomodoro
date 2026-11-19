package com.example.tecnicapomodoro.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tecnicapomodoro.R;

public class PantallaPrincipalActivity extends AppCompatActivity {

    public static final String EXTRA_NOMBRE = "extra_nombre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        TextView tvBienvenida = findViewById(R.id.tvBienvenida);
        Button btnAsignaturas = findViewById(R.id.btnIrAsignaturas);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        String nombre = getIntent().getStringExtra(EXTRA_NOMBRE);
        tvBienvenida.setText("Bienvenido, " + nombre);

        btnAsignaturas.setOnClickListener(v -> startActivity(new Intent(this, AsignaturaActivity.class)));
        btnCerrarSesion.setOnClickListener(v -> finish());
    }
}