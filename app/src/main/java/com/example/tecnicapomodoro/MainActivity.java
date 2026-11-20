package com.example.tecnicapomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tecnicapomodoro.controller.PantallaPrincipalActivity;
import com.example.tecnicapomodoro.controller.RegisterActivity;
import com.example.tecnicapomodoro.model.Estudiante;
import com.example.tecnicapomodoro.model.EstudianteRepositorio;
import com.example.tecnicapomodoro.model.SesionUsuario;

public class MainActivity extends AppCompatActivity {
    TextView tvregistrate;
    EditText etCorreo;
    EditText etPassword;
    Button btnIniciarSesion;
    EstudianteRepositorio repositorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SesionUsuario.id(this) != -1) {
            startActivity(new Intent(this, PantallaPrincipalActivity.class));
            finish();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repositorio = new EstudianteRepositorio(this);

        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        tvregistrate = findViewById(R.id.tvRegistrate);

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());

        tvregistrate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    public void iniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Escribe correo y contrasena", Toast.LENGTH_SHORT).show();
            return;
        }

        repositorio.iniciarSesion(correo, password, new EstudianteRepositorio.Sesion() {
            @Override
            public void onEncontrado(Estudiante estudiante) {
                SesionUsuario.guardar(MainActivity.this, estudiante.getId(), estudiante.getNombre());
                Intent intent = new Intent(MainActivity.this, PantallaPrincipalActivity.class);
                intent.putExtra(PantallaPrincipalActivity.EXTRA_NOMBRE, estudiante.getNombre());
                startActivity(intent);
                finish();
            }

            @Override
            public void onNoEncontrado() {
                Toast.makeText(MainActivity.this, "Correo o contrasena incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}