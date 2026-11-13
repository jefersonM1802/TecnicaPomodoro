package com.example.tecnicapomodoro.controller;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tecnicapomodoro.R;
import com.example.tecnicapomodoro.model.Estudiante;
import com.example.tecnicapomodoro.model.EstudianteRepositorio;
import com.example.tecnicapomodoro.view.EstudianteAdapter;

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

        repositorio = new EstudianteRepositorio();
        adapter = new EstudianteAdapter(repositorio.obtenerTodos());
        recyUsuarios.setLayoutManager(new LinearLayoutManager(this));
        recyUsuarios.setAdapter(adapter);

        btnRegistrar.setOnClickListener(v -> registrar());
    }

    private void registrar() {
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
            etCorreo.setError("Correo no válido");
            return;
        }
        if (repositorio.existeCorreo(correo)) {
            etCorreo.setError("Este correo ya está registrado");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");
            return;
        }

        repositorio.guardar(new Estudiante(0, nombre, correo, password));
        adapter.notifyItemInserted(repositorio.obtenerTodos().size() - 1);

        etNombre.setText("");
        etCorreo.setText("");
        etPassword.setText("");
        Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show();
    }

}
