package com.example.tecnicapomodoro.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppRepositorio {
    public interface AlTerminar<T> {
        void onResultado(T resultado);
    }

    public interface AlFallar {
        void onError(String mensaje);
    }

    private static ExecutorService hilo = Executors.newSingleThreadExecutor();
    private static Handler principal = new Handler(Looper.getMainLooper());

    private BaseDatos db;

    public AppRepositorio(Context contexto) {
        db = BaseDatos.getInstance(contexto);
    }

    public AsignaturaDao asignaturas() { return db.asignaturaDao(); }
    public TareaDao tareas() { return db.tareaDao(); }
    public PomodoroDao pomodoros() { return db.pomodoroDao(); }
    public SesionDao sesiones() { return db.sesionDao(); }
    public ConfiguracionDao configuracion() { return db.configuracionDao(); }

    public <T> void ejecutar(Callable<T> operacion, AlTerminar<T> exito, AlFallar error) {
        hilo.execute(() -> {
            try {
                T resultado = operacion.call();
                principal.post(() -> {
                    if (exito != null) exito.onResultado(resultado);
                });
            } catch (Exception e) {
                Log.e("AppRepositorio", "Error", e);
                String mensaje = e.getMessage() != null ? e.getMessage() : "Ocurrió un error con los datos";
                principal.post(() -> {
                    if (error != null) error.onError(mensaje);
                });
            }
        });
    }
}
