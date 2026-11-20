package com.example.tecnicapomodoro.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tecnicapomodoro.R;
import com.example.tecnicapomodoro.model.AppRepositorio;
import com.example.tecnicapomodoro.model.Pomodoro;
import com.example.tecnicapomodoro.model.Tarea;
import com.example.tecnicapomodoro.view.ListaAdapter;
import com.example.tecnicapomodoro.view.UiUtil;

public class TareaActivity extends AppCompatActivity {


    private AppRepositorio repo;
    private int asignaturaId;
    private ListaAdapter<Tarea> adapter;
    private TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea);
        UiUtil.ajustarBordes(findViewById(R.id.raiz));

        repo = new AppRepositorio(this);
        asignaturaId = getIntent().getIntExtra("asignaturaId", -1);
        String nombre = getIntent().getStringExtra("asignaturaNombre");

        TextView tvTitulo = findViewById(R.id.tvTituloTareas);
        tvTitulo.setText("Tareas de " + nombre);
        tvVacio = findViewById(R.id.tvVacioTareas);

        adapter = new ListaAdapter<>(
                t -> t.getTitulo(),
                t -> t.getEstado() + " · " + t.getPomodorosEstimados() + " pomodoros estimados · "
                        + t.tiempoTotal() + " min",
                t -> colorEstado(t.getEstado()));
        adapter.alTocar(this::abrirTemporizador);
        adapter.alMantener(this::mostrarOpciones);

        RecyclerView recy = findViewById(R.id.recyTareas);
        recy.setLayoutManager(new LinearLayoutManager(this));
        recy.setAdapter(adapter);

        setTitle("Tareas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    private void cargar() {
        repo.ejecutar(() -> repo.tareas().listarPorAsignatura(asignaturaId), lista -> {
            adapter.setItems(lista);
            tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        }, this::mostrarError);
    }

    private String colorEstado(String estado) {
        if ("completada".equals(estado)) return "#43A047";
        if ("en curso".equals(estado)) return "#FB8C00";
        return "#9E9E9E";
    }

    private void abrirTemporizador(Tarea t) {
        if (t.getEstado().equals("completada")) {
            mostrarError("La tarea ya está completada");
            return;
        }
        Intent i = new Intent(this, TemporizadorActivity.class);
        i.putExtra("tareaId", t.getId());
        startActivity(i);
    }

    private void mostrarOpciones(Tarea t) {
        String[] opciones = {"Editar", "Registrar tiempo manual", "Marcar como completada", "Eliminar"};
        new AlertDialog.Builder(this)
                .setTitle(t.getTitulo())
                .setItems(opciones, (d, i) -> {
                    if (i == 0) abrirFormulario(t);
                    else if (i == 1) registrarTiempoManual(t);
                    else if (i == 2) completar(t);
                    else confirmarEliminar(t);
                })
                .show();
    }

    // RF05: crear y editar
    private void abrirFormulario(Tarea existente) {
        View vista = getLayoutInflater().inflate(R.layout.dialog_tarea, null);
        EditText etTitulo = vista.findViewById(R.id.etTituloTarea);
        EditText etPomodoros = vista.findViewById(R.id.etPomodorosEstimados);
        if (existente != null) {
            etTitulo.setText(existente.getTitulo());
            etPomodoros.setText(String.valueOf(existente.getPomodorosEstimados()));
        }

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(existente == null ? "Nueva tarea" : "Editar tarea")
                .setView(vista)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .show();

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String titulo = etTitulo.getText().toString().trim();
            int pomodoros = numero(etPomodoros.getText().toString());
            if (titulo.isEmpty()) {
                etTitulo.setError("Escribe el título de la tarea");
                return;
            }
            if (pomodoros < 1 || pomodoros > 20) {
                etPomodoros.setError("Estima entre 1 y 20 pomodoros");
                return;
            }
            Tarea t = (existente != null) ? existente : new Tarea();
            t.setAsignaturaId(asignaturaId);
            t.setTitulo(titulo);
            t.setPomodorosEstimados(pomodoros);
            repo.ejecutar(() -> {
                if (t.getId() == 0) repo.tareas().insertar(t);
                else repo.tareas().actualizar(t);
                return true;
            }, ok -> {
                dialogo.dismiss();
                Toast.makeText(this, "Tarea guardada", Toast.LENGTH_SHORT).show();
                cargar();
            }, this::mostrarError);
        });
    }

    // RF10: registro manual de tiempo
    private void registrarTiempoManual(Tarea t) {
        if (t.getEstado().equals("completada")) {
            mostrarError("No se puede registrar tiempo en una tarea completada");
            return;
        }
        EditText etMinutos = new EditText(this);
        etMinutos.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMinutos.setHint("Minutos (1 a 480)");

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("Registrar tiempo en " + t.getTitulo())
                .setView(etMinutos)
                .setPositiveButton("Registrar", null)
                .setNegativeButton("Cancelar", null)
                .show();

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int minutos = numero(etMinutos.getText().toString());
            if (minutos < 1 || minutos > 480) {
                etMinutos.setError("Ingresa entre 1 y 480 minutos");
                return;
            }
            repo.ejecutar(() -> {
                Tarea actual = repo.tareas().buscar(t.getId());
                if (actual == null) throw new Exception("La tarea ya no existe");
                long ahora = System.currentTimeMillis();
                repo.pomodoros().insertar(Pomodoro.crear(actual.getId(), ahora - minutos * 60_000L,
                        ahora, minutos, true));
                actual.registrarTiempo(minutos);
                repo.tareas().actualizar(actual);
                return true;
            }, ok -> {
                dialogo.dismiss();
                Toast.makeText(this, "Tiempo registrado", Toast.LENGTH_SHORT).show();
                cargar();
            }, this::mostrarError);
        });
    }

    private void completar(Tarea t) {
        if (t.getMinutosRegistrados() <= 0) {
            mostrarError("Registra tiempo en la tarea antes de completarla");
            return;
        }
        repo.ejecutar(() -> {
            t.completar();
            repo.tareas().actualizar(t);
            return true;
        }, ok -> cargar(), this::mostrarError);
    }

    private void confirmarEliminar(Tarea t) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar tarea")
                .setMessage("¿Eliminar \"" + t.getTitulo() + "\" y su tiempo registrado?")
                .setPositiveButton("Eliminar", (d, w) -> repo.ejecutar(() -> {
                    repo.tareas().eliminar(t);
                    return true;
                }, ok -> cargar(), this::mostrarError))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private int numero(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.opNuevo) {
            abrirFormulario(null);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
