package com.example.tecnicapomodoro.controller;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tecnicapomodoro.R;

public class RegisterActivity extends AppCompatActivity {

    EditText etNombre;
    EditText etCorreo;
    EditText etPassword;
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.edNombre);
        etCorreo = findViewById(R.id.edCorreo);
        etPassword = findViewById(R.id.edPassword);
        btnRegistrar = findViewById(R.id.edRegistrar);
    }

}
