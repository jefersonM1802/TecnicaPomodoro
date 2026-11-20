package com.example.tecnicapomodoro.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tecnicapomodoro.MainActivity;
import com.example.tecnicapomodoro.R;
import com.example.tecnicapomodoro.model.SesionUsuario;

public class PantallaPrincipalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        TextView tvBienvenida = findViewById(R.id.tvBienvenida);
        Button btnAsignaturas = findViewById(R.id.btnIrAsignaturas);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        tvBienvenida.setText("Bienvenido, " + SesionUsuario.nombre(this));

        btnAsignaturas.setOnClickListener(v -> startActivity(new Intent(this, AsignaturaActivity.class)));
        btnCerrarSesion.setOnClickListener(v -> {
            SesionUsuario.cerrar(this);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
