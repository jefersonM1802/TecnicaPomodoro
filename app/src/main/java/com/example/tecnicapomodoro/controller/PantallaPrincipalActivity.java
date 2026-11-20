package com.example.tecnicapomodoro.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tecnicapomodoro.MainActivity;
import com.example.tecnicapomodoro.R;
import com.example.tecnicapomodoro.model.AppRepositorio;
import com.example.tecnicapomodoro.model.Asignatura;
import com.example.tecnicapomodoro.model.Objetivo;
import com.example.tecnicapomodoro.model.Progreso;
import com.example.tecnicapomodoro.model.SesionUsuario;
import com.example.tecnicapomodoro.view.ListaAdapter;
import com.example.tecnicapomodoro.view.UiUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PantallaPrincipalActivity extends AppCompatActivity {


    private AppRepositorio repo;
    private int estudianteId;
    private TextView tvHoy, tvSinMetas;
    private ListaAdapter<Progreso> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        UiUtil.ajustarBordes(findViewById(R.id.raiz));

        repo = new AppRepositorio(this);
        estudianteId = SesionUsuario.id(this);

        TextView tvBienvenida = findViewById(R.id.tvBienvenida);
        tvBienvenida.setText("Bienvenido, " + SesionUsuario.nombre(this));
        tvHoy = findViewById(R.id.tvHoy);
        tvSinMetas = findViewById(R.id.tvSinMetas);

        adapter = new ListaAdapter<>(
                p -> p.getNombre(),
                p -> p.getMinutos() + " / " + p.getMeta() + " min esta semana (" + Math.round(p.getCumplimiento()) + " %)",
                p -> p.getColor());
        RecyclerView recy = findViewById(R.id.recyProgreso);
        recy.setLayoutManager(new LinearLayoutManager(this));
        recy.setAdapter(adapter);

        setTitle("Inicio");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.opAsignaturas) {
            startActivity(new Intent(this, AsignaturaActivity.class));
        } else if (id == R.id.opSesiones) {
            startActivity(new Intent(this, SesionActivity.class));
        } else if (id == R.id.opConfiguracion) {
            startActivity(new Intent(this, ConfiguracionActivity.class));
        } else if (id == R.id.opCerrarSesion) {
            cerrarSesion();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPanel();
    }

    // RF16: panel diario
    private void cargarPanel() {
        long ahora = System.currentTimeMillis();
        long hoy = inicioDelDia(ahora);
        long semana = inicioDeSemana(ahora);

        repo.ejecutar(() -> new int[]{
                repo.pomodoros().minutosDesde(estudianteId, hoy),
                repo.pomodoros().pomodorosDesde(estudianteId, hoy)
        }, r -> tvHoy.setText("Hoy estudiaste " + r[0] + " min · " + r[1] + " pomodoros"),
                this::mostrarError);

        repo.ejecutar(() -> {
            List<Progreso> lista = new ArrayList<>();
            for (Asignatura a : repo.asignaturas().listar(estudianteId)) {
                Objetivo o = repo.asignaturas().obtenerObjetivo(a.getId());
                if (o == null) continue;
                int minutos = repo.pomodoros().minutosAsignaturaDesde(a.getId(), semana);
                lista.add(new Progreso(a.getNombre(), a.getColor(), minutos, o.getMetaSemanalMin(),
                        o.calcularCumplimiento(minutos)));
            }
            return lista;
        }, lista -> {
            adapter.setItems(lista);
            tvSinMetas.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        }, this::mostrarError);
    }

    private long inicioDelDia(long ms) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    // la semana empieza el lunes
    private long inicioDeSemana(long ms) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(inicioDelDia(ms));
        int dia = c.get(Calendar.DAY_OF_WEEK);      // domingo = 1 ... sábado = 7
        int diasDesdeLunes = (dia + 5) % 7;
        c.add(Calendar.DAY_OF_MONTH, -diasDesdeLunes);
        return c.getTimeInMillis();
    }

    private void cerrarSesion() {
        SesionUsuario.cerrar(this);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
