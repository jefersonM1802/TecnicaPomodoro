package com.example.tecnicapomodoro.controller;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tecnicapomodoro.R;
import com.example.tecnicapomodoro.model.Estudiante;
import com.example.tecnicapomodoro.model.EstudianteRepositorio;
import com.example.tecnicapomodoro.view.EstudianteAdapter;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText etNombre;
    EditText etCorreo;
    EditText etPassword;
    Button btnRegistrar;
    RecyclerView recyUsuarios;

    EstudianteRepositorio repositorio;
    EstudianteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.edNombre);
        etCorreo = findViewById(R.id.edCorreo);
        etPassword = findViewById(R.id.edPassword);
        btnRegistrar = findViewById(R.id.edRegistrar);
        recyUsuarios = findViewById(R.id.recyUsuarios);

        repositorio = new EstudianteRepositorio(this);
        adapter = new EstudianteAdapter(new ArrayList<>());
        recyUsuarios.setLayoutManager(new LinearLayoutManager(this));
        recyUsuarios.setAdapter(adapter);

        btnRegistrar.setOnClickListener(v -> registrar());
        cargarLista();
    }

    void cargarLista() {
        repositorio.obtenerTodos(estudiantes -> adapter.setEstudiantes(estudiantes));
    }

    void registrar() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (nombre.isEmpty()) {
            etNombre.setError("Escribe tu nombre");
            return;
        }
        if (correo.isEmpty()) {
            etCorreo.setError("Escribe tu correo");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo no valido");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Minimo 6 caracteres");
            return;
        }

        btnRegistrar.setEnabled(false);
        repositorio.registrar(new Estudiante(0, nombre, correo, password),
                new EstudianteRepositorio.Resultado() {
                    @Override
                    public void onExito() {
                        btnRegistrar.setEnabled(true);
                        etNombre.setText("");
                        etCorreo.setText("");
                        etPassword.setText("");
                        Toast.makeText(RegisterActivity.this, "Registro guardado", Toast.LENGTH_SHORT).show();
                        cargarLista();
                    }

                    @Override
                    public void onError(String mensaje) {
                        btnRegistrar.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}