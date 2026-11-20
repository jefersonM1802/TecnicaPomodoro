package com.example.tecnicapomodoro.controller;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tecnicapomodoro.R;
import com.example.tecnicapomodoro.model.AppRepositorio;
import com.example.tecnicapomodoro.model.ConfiguracionPomodoro;
import com.example.tecnicapomodoro.model.SesionUsuario;
import com.example.tecnicapomodoro.view.UiUtil;

public class ConfiguracionActivity extends AppCompatActivity {

    private AppRepositorio repo;
    private ConfiguracionPomodoro config;
    private EditText etTrabajo, etCorto, etLargo, etCiclo;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        UiUtil.ajustarBordes(findViewById(R.id.raiz));

        repo = new AppRepositorio(this);
        etTrabajo = findViewById(R.id.etMinTrabajo);
        etCorto = findViewById(R.id.etMinCorto);
        etLargo = findViewById(R.id.etMinLargo);
        etCiclo = findViewById(R.id.etPorCiclo);
        btnGuardar = findViewById(R.id.btnGuardarConfig);
        btnGuardar.setEnabled(false);
        btnGuardar.setOnClickListener(v -> guardar());

        int estudianteId = SesionUsuario.id(this);
        repo.ejecutar(() -> repo.configuracion().obtener(estudianteId), c -> {
            config = (c != null) ? c : ConfiguracionPomodoro.porDefecto(estudianteId);
            etTrabajo.setText(String.valueOf(config.getMinTrabajo()));
            etCorto.setText(String.valueOf(config.getMinDescansoCorto()));
            etLargo.setText(String.valueOf(config.getMinDescansoLargo()));
            etCiclo.setText(String.valueOf(config.getPomodorosPorCiclo()));
            btnGuardar.setEnabled(true);
        }, this::mostrarError);
    }

    private void guardar() {
        int trabajo = numero(etTrabajo.getText().toString());
        int corto = numero(etCorto.getText().toString());
        int largo = numero(etLargo.getText().toString());
        int ciclo = numero(etCiclo.getText().toString());

        if (trabajo < 1 || trabajo > 90) {
            etTrabajo.setError("Entre 1 y 90 minutos");
            return;
        }
        if (corto < 1 || corto > 30) {
            etCorto.setError("Entre 1 y 30 minutos");
            return;
        }
        if (largo <= corto || largo > 60) {
            etLargo.setError("Debe ser mayor que el descanso corto y máximo 60");
            return;
        }
        if (ciclo < 2 || ciclo > 8) {
            etCiclo.setError("Entre 2 y 8 pomodoros");
            return;
        }
        config.actualizar(trabajo, corto, largo, ciclo);
        repo.ejecutar(() -> {
            repo.configuracion().guardar(config);
            return true;
        }, ok -> {
            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            finish();
        }, this::mostrarError);
    }

    private int numero(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}