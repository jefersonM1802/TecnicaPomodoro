package com.example.tecnicapomodoro.model;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EstudianteRepositorio {

    public interface Resultado {
        void onExito();
        void onError(String mensaje);
    }

    public interface Listado {
        void onListo(List<Estudiante> estudiantes);
    }

    public interface Sesion {
        void onEncontrado(Estudiante estudiante);
        void onNoEncontrado();
    }

    private final EstudianteDao dao;
    private final ExecutorService hilo = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());

    public EstudianteRepositorio(Context contexto) {
        dao = BaseDatos.getInstance(contexto).estudianteDao();
    }

    public void registrar(Estudiante estudiante, Resultado resultado) {
        hilo.execute(() -> {
            if (dao.contarPorCorreo(estudiante.getCorreo()) > 0) {
                principal.post(() -> resultado.onError("Este correo ya esta registrado"));
                return;
            }
            try {
                dao.insertar(estudiante);
                principal.post(() -> resultado.onExito());
            } catch (SQLiteConstraintException e) {
                principal.post(() -> resultado.onError("Este correo ya esta registrado"));
            }
        });
    }

    public void obtenerTodos(Listado listado) {
        hilo.execute(() -> {
            List<Estudiante> datos = dao.obtenerTodos();
            principal.post(() -> listado.onListo(datos));
        });
    }

    public void iniciarSesion(String correo, String password, Sesion sesion) {
        hilo.execute(() -> {
            Estudiante estudiante = dao.buscarPorCredenciales(correo, password);
            if (estudiante == null) {
                principal.post(() -> sesion.onNoEncontrado());
            } else {
                principal.post(() -> sesion.onEncontrado(estudiante));
            }
        });
    }
}