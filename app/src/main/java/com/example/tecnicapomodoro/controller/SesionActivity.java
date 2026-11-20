package com.example.tecnicapomodoro.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.example.tecnicapomodoro.model.SesionEstudio;
import com.example.tecnicapomodoro.model.SesionUsuario;
import com.example.tecnicapomodoro.model.Tarea;
import com.example.tecnicapomodoro.view.ListaAdapter;
import com.example.tecnicapomodoro.view.UiUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SesionActivity extends AppCompatActivity {

    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    private AppRepositorio repo;
    private int estudianteId;
    private ListaAdapter<SesionEstudio> adapter;
    private TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion);
        UiUtil.ajustarBordes(findViewById(R.id.raiz));

        repo = new AppRepositorio(this);
        estudianteId = SesionUsuario.id(this);
        tvVacio = findViewById(R.id.tvVacioSesiones);

        adapter = new ListaAdapter<>(
                s -> s.getTareaTitulo(),
                s -> formato.format(new Date(s.getFechaHora())) + " · " + s.getDuracionMin() + " min",
                s -> s.getFechaHora() < System.currentTimeMillis() ? "#9E9E9E" : "#1E88E5");
        adapter.alMantener(this::confirmarEliminar);

        RecyclerView recy = findViewById(R.id.recySesiones);
        recy.setLayoutManager(new LinearLayoutManager(this));
        recy.setAdapter(adapter);

        setTitle("Sesiones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cargar();
    }

    private void cargar() {
        repo.ejecutar(() -> repo.sesiones().listar(estudianteId), lista -> {
            adapter.setItems(lista);
            tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        }, this::mostrarError);
    }

    private void nuevaSesion() {
        repo.ejecutar(() -> repo.tareas().listarPendientesDe(estudianteId), tareas -> {
            if (tareas.isEmpty()) {
                mostrarError("Primero crea una tarea pendiente en alguna asignatura");
                return;
            }
            elegirFechaHora(tareas);
        }, this::mostrarError);
    }

    private void elegirFechaHora(List<Tarea> tareas) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (dp, anio, mes, dia) -> {
            c.set(anio, mes, dia);
            new TimePickerDialog(this, (tp, hora, minuto) -> {
                c.set(Calendar.HOUR_OF_DAY, hora);
                c.set(Calendar.MINUTE, minuto);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                completarSesion(tareas, c.getTimeInMillis());
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void completarSesion(List<Tarea> tareas, long fechaHora) {
        View vista = getLayoutInflater().inflate(R.layout.dialog_sesion, null);
        TextView tvFecha = vista.findViewById(R.id.tvFechaSesion);
        Spinner spTarea = vista.findViewById(R.id.spTareaSesion);
        EditText etDuracion = vista.findViewById(R.id.etDuracionSesion);

        tvFecha.setText("Fecha: " + formato.format(new Date(fechaHora)));
        List<String> titulos = new ArrayList<>();
        for (Tarea t : tareas) titulos.add(t.getTitulo());
        spTarea.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, titulos));

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("Planificar sesión")
                .setView(vista)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .show();

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int duracion = numero(etDuracion.getText().toString());
            if (fechaHora <= System.currentTimeMillis()) {
                mostrarError("La sesión debe ser en una fecha y hora futura");
                return;
            }
            if (duracion < 15 || duracion > 480) {
                etDuracion.setError("Entre 15 y 480 minutos");
                return;
            }
            Tarea tarea = tareas.get(spTarea.getSelectedItemPosition());
            SesionEstudio s = new SesionEstudio();
            s.setEstudianteId(estudianteId);
            s.setTareaId(tarea.getId());
            s.setTareaTitulo(tarea.getTitulo());
            s.setFechaHora(fechaHora);
            s.setDuracionMin(duracion);
            repo.ejecutar(() -> repo.sesiones().insertar(s), id -> {
                dialogo.dismiss();
                Toast.makeText(this, "Sesión planificada", Toast.LENGTH_SHORT).show();
                cargar();
            }, this::mostrarError);
        });
    }

    private void confirmarEliminar(SesionEstudio s) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar sesión")
                .setMessage("¿Eliminar la sesión del " + formato.format(new Date(s.getFechaHora())) + "?")
                .setPositiveButton("Eliminar", (d, w) -> repo.ejecutar(() -> {
                    repo.sesiones().eliminar(s);
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
            nuevaSesion();
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
