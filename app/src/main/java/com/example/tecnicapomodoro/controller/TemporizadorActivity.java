package com.example.tecnicapomodoro.controller;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tecnicapomodoro.R;
import com.example.tecnicapomodoro.model.AppRepositorio;
import com.example.tecnicapomodoro.model.ConfiguracionPomodoro;
import com.example.tecnicapomodoro.model.Pomodoro;
import com.example.tecnicapomodoro.model.SesionUsuario;
import com.example.tecnicapomodoro.model.Tarea;
import com.example.tecnicapomodoro.view.UiUtil;

import java.util.Locale;

public class TemporizadorActivity extends AppCompatActivity {

    private static String canal = "pomodoro";

    private AppRepositorio repo;
    private int tareaId;
    private ConfiguracionPomodoro config;

    private CountDownTimer timer;
    private long restanteMs;
    private long inicioTrabajo;
    private boolean enTrabajo = true;
    private boolean pausado = false;
    private int completados = 0;

    private TextView tvTarea, tvFase, tvTiempo, tvCompletados;
    private Button btnIniciar, btnPausar, btnReanudar, btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporizador);
        UiUtil.ajustarBordes(findViewById(R.id.raiz));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setTitle("Pomodoro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        repo = new AppRepositorio(this);
        tareaId = getIntent().getIntExtra("tareaId", -1);

        tvTarea = findViewById(R.id.tvTareaTimer);
        tvFase = findViewById(R.id.tvFase);
        tvTiempo = findViewById(R.id.tvTiempo);
        tvCompletados = findViewById(R.id.tvCompletados);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnPausar = findViewById(R.id.btnPausar);
        btnReanudar = findViewById(R.id.btnReanudar);
        btnCancelar = findViewById(R.id.btnCancelar);

        btnIniciar.setOnClickListener(v -> iniciar());
        btnPausar.setOnClickListener(v -> pausar());
        btnReanudar.setOnClickListener(v -> reanudar());
        btnCancelar.setOnClickListener(v -> cancelar());
        actualizarBotones();

        crearCanal();
        pedirPermisoNotificaciones();

        int estudianteId = SesionUsuario.id(this);
        repo.ejecutar(() -> repo.configuracion().obtener(estudianteId), c -> {
            config = (c != null) ? c : ConfiguracionPomodoro.porDefecto(estudianteId);
            restanteMs = config.obtenerMinTrabajo() * 60_000L;
            tvTiempo.setText(formatoTiempo(restanteMs));
            actualizarBotones();
        }, this::mostrarError);

        repo.ejecutar(() -> repo.tareas().buscar(tareaId),
                t -> tvTarea.setText(t != null ? t.getTitulo() : ""), this::mostrarError);
    }

    // RF07: iniciar
    private void iniciar() {
        if (config == null) return;
        enTrabajo = true;
        arrancarFase();
    }

    private void arrancarFase() {
        int minutos;
        if (enTrabajo) {
            minutos = config.obtenerMinTrabajo();
            inicioTrabajo = System.currentTimeMillis();
            tvFase.setText("Trabajo");
        } else {
            minutos = config.obtenerDescanso(completados);
            tvFase.setText(minutos == config.getMinDescansoLargo() ? "Descanso largo" : "Descanso corto");
        }
        correr(minutos * 60_000L);
    }

    private void correr(long ms) {
        pausado = false;
        timer = new CountDownTimer(ms, 1000) {
            @Override
            public void onTick(long restante) {
                restanteMs = restante;
                tvTiempo.setText(formatoTiempo(restante));
            }

            @Override
            public void onFinish() {
                timer = null;
                tvTiempo.setText(formatoTiempo(0));
                terminarFase();
            }
        }.start();
        actualizarBotones();
    }

    // RF07: pausar y reanudar
    private void pausar() {
        if (timer == null) return;
        timer.cancel();
        timer = null;
        pausado = true;
        actualizarBotones();
    }

    private void reanudar() {
        if (pausado) correr(restanteMs);
    }

    // RF07: cancelar (el tiempo no se registra)
    private void cancelar() {
        if (timer != null) timer.cancel();
        timer = null;
        pausado = false;
        enTrabajo = true;
        tvFase.setText("Trabajo");
        restanteMs = config.obtenerMinTrabajo() * 60_000L;
        tvTiempo.setText(formatoTiempo(restanteMs));
        actualizarBotones();
        Toast.makeText(this, "Pomodoro cancelado: el tiempo no se registra", Toast.LENGTH_SHORT).show();
    }

    // RF08: alternancia automática trabajo / descanso
    private void terminarFase() {
        if (enTrabajo) {
            completados++;
            tvCompletados.setText("Pomodoros completados: " + completados);
            registrarPomodoro();
            int descanso = config.obtenerDescanso(completados);
            notificar("¡Pomodoro completado!", "Toma un descanso de " + descanso + " min");
            enTrabajo = false;
        } else {
            notificar("Descanso terminado", "Comienza el siguiente pomodoro");
            enTrabajo = true;
        }
        arrancarFase();
    }

    // RF09: registro automático del tiempo
    private void registrarPomodoro() {
        int minutos = config.obtenerMinTrabajo();
        long inicio = inicioTrabajo;
        long fin = System.currentTimeMillis();
        repo.ejecutar(() -> {
            Tarea t = repo.tareas().buscar(tareaId);
            if (t == null) throw new Exception("La tarea ya no existe");
            repo.pomodoros().insertar(Pomodoro.crear(tareaId, inicio, fin, minutos, false));
            t.registrarTiempo(minutos);
            repo.tareas().actualizar(t);
            return t;
        }, t -> Toast.makeText(this, "Se registraron " + minutos + " min en " + t.getTitulo(),
                Toast.LENGTH_SHORT).show(), this::mostrarError);
    }

    private void actualizarBotones() {
        boolean corriendo = timer != null;
        btnIniciar.setEnabled(config != null && !corriendo && !pausado);
        btnPausar.setEnabled(corriendo);
        btnReanudar.setEnabled(pausado);
        btnCancelar.setEnabled(corriendo || pausado);
    }

    private void crearCanal() {
        NotificationChannel nuevoCanal = new NotificationChannel(canal, "Pomodoro",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(nuevoCanal);
    }

    private boolean sinPermisoNotificaciones() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void pedirPermisoNotificaciones() {
        if (sinPermisoNotificaciones()) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    private void notificar(String titulo, String texto) {
        Toast.makeText(this, titulo, Toast.LENGTH_SHORT).show();
        if (sinPermisoNotificaciones()) return;
        Notification n = new Notification.Builder(this, canal)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setAutoCancel(true)
                .build();
        getSystemService(NotificationManager.class).notify(1, n);
    }

    private String formatoTiempo(long ms) {
        long seg = (ms + 999) / 1000;
        return String.format(Locale.US, "%02d:%02d", seg / 60, seg % 60);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
