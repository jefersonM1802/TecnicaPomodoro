package com.example.tecnicapomodoro.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tecnicapomodoro.R;
import com.example.tecnicapomodoro.model.AppRepositorio;
import com.example.tecnicapomodoro.model.Asignatura;
import com.example.tecnicapomodoro.model.Objetivo;
import com.example.tecnicapomodoro.model.SesionUsuario;
import com.example.tecnicapomodoro.view.ListaAdapter;
import com.example.tecnicapomodoro.view.UiUtil;

import java.util.Arrays;

public class AsignaturaActivity extends AppCompatActivity {

    private static String[] tipos = {"Asignatura", "Proyecto"};
    private static String[] colores = {"Rojo", "Azul", "Verde", "Naranja", "Morado"};
    private static String[] colorHex = {"#E53935", "#1E88E5", "#43A047", "#FB8C00", "#8E24AA"};

    private AppRepositorio repo;
    private int estudianteId;
    private ListaAdapter<Asignatura> adapter;
    private TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignatura);
        UiUtil.ajustarBordes(findViewById(R.id.raiz));

        repo = new AppRepositorio(this);
        estudianteId = SesionUsuario.id(this);
        tvVacio = findViewById(R.id.tvVacio);

        adapter = new ListaAdapter<>(
                a -> a.getNombre(),
                a -> a.getTipo(),
                a -> a.getColor());
        adapter.alTocar(this::abrirTareas);
        adapter.alMantener(this::mostrarOpciones);

        RecyclerView recy = findViewById(R.id.recyAsignaturas);
        recy.setLayoutManager(new LinearLayoutManager(this));
        recy.setAdapter(adapter);

        setTitle("Asignaturas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.opNuevo) {
            abrirFormulario(null, 0);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    private void cargar() {
        repo.ejecutar(() -> repo.asignaturas().listar(estudianteId), lista -> {
            adapter.setItems(lista);
            tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        }, this::mostrarError);
    }

    private void abrirTareas(Asignatura a) {
        Intent i = new Intent(this, TareaActivity.class);
        i.putExtra("asignaturaId", a.getId());
        i.putExtra("asignaturaNombre", a.getNombre());
        startActivity(i);
    }

    private void mostrarOpciones(Asignatura a) {
        String[] opciones = {"Editar", "Eliminar"};
        new AlertDialog.Builder(this)
                .setTitle(a.getNombre())
                .setItems(opciones, (d, i) -> {
                    if (i == 0) editar(a);
                    else confirmarEliminar(a);
                })
                .show();
    }

    private void editar(Asignatura a) {
        repo.ejecutar(() -> {
            Objetivo o = repo.asignaturas().obtenerObjetivo(a.getId());
            return o != null ? o.getMetaSemanalMin() : 0;
        }, meta -> abrirFormulario(a, meta), this::mostrarError);
    }

    // RF03: eliminar (borra en cascada tareas, objetivo y pomodoros)
    private void confirmarEliminar(Asignatura a) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar asignatura")
                .setMessage("Se eliminarán también sus tareas, su meta y el tiempo registrado. ¿Continuar?")
                .setPositiveButton("Eliminar", (d, w) -> repo.ejecutar(() -> {
                    repo.asignaturas().eliminar(a);
                    return true;
                }, ok -> {
                    Toast.makeText(this, "Asignatura eliminada", Toast.LENGTH_SHORT).show();
                    cargar();
                }, this::mostrarError))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // RF03 + RF04: crear / editar con meta semanal
    private void abrirFormulario(Asignatura existente, int metaActual) {
        View vista = getLayoutInflater().inflate(R.layout.dialog_asignatura, null);
        EditText etNombre = vista.findViewById(R.id.etNombreAsignatura);
        Spinner spTipo = vista.findViewById(R.id.spTipo);
        Spinner spColor = vista.findViewById(R.id.spColor);
        EditText etMeta = vista.findViewById(R.id.etMetaSemanal);

        spTipo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos));
        spColor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, colores));

        if (existente != null) {
            etNombre.setText(existente.getNombre());
            spTipo.setSelection(Math.max(0, Arrays.asList(tipos).indexOf(existente.getTipo())));
            spColor.setSelection(Math.max(0, Arrays.asList(colorHex).indexOf(existente.getColor())));
            if (metaActual > 0) etMeta.setText(String.valueOf(metaActual));
        }

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(existente == null ? "Nueva asignatura" : "Editar asignatura")
                .setView(vista)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .show();

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            int meta = numero(etMeta.getText().toString());

            if (nombre.length() < 3 || nombre.length() > 40) {
                etNombre.setError("El nombre debe tener entre 3 y 40 caracteres");
                return;
            }
            if (meta < 30 || meta > 3000) {
                etMeta.setError("La meta debe estar entre 30 y 3000 minutos");
                return;
            }

            Asignatura a = (existente != null) ? existente : new Asignatura();
            a.setEstudianteId(estudianteId);
            a.setNombre(nombre);
            a.setTipo(tipos[spTipo.getSelectedItemPosition()]);
            a.setColor(colorHex[spColor.getSelectedItemPosition()]);
            guardar(a, meta, dialogo);
        });
    }

    private void guardar(Asignatura a, int meta, AlertDialog dialogo) {
        repo.ejecutar(() -> {
            if (repo.asignaturas().contarPorNombre(estudianteId, a.getNombre(), a.getId()) > 0) {
                throw new Exception("Ya tienes una asignatura con ese nombre");
            }
            if (a.getId() == 0) a.setId((int) repo.asignaturas().insertar(a));
            else repo.asignaturas().actualizar(a);

            Objetivo objetivo = new Objetivo();
            a.asignarObjetivo(objetivo);
            objetivo.ajustarMeta(meta);
            repo.asignaturas().guardarObjetivo(objetivo);
            return true;
        }, ok -> {
            dialogo.dismiss();
            Toast.makeText(this, "Asignatura guardada", Toast.LENGTH_SHORT).show();
            cargar();
        }, mensaje -> {
            mostrarError(mensaje);
            cargar();
        });
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
