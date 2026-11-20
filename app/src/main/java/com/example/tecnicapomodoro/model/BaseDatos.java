package com.example.tecnicapomodoro.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {
        Estudiante.class,
        Asignatura.class,
        Objetivo.class,
        Tarea.class,
        Pomodoro.class,
        SesionEstudio.class,
        ConfiguracionPomodoro.class
}, version = 3, exportSchema = false)
public abstract class BaseDatos extends RoomDatabase {

    private static BaseDatos instancia;

    public abstract EstudianteDao estudianteDao();
    public abstract AsignaturaDao asignaturaDao();
    public abstract TareaDao tareaDao();
    public abstract PomodoroDao pomodoroDao();
    public abstract SesionDao sesionDao();
    public abstract ConfiguracionDao configuracionDao();

    public static synchronized BaseDatos getInstance(Context contexto) {
        if (instancia == null) {
            instancia = Room.databaseBuilder(contexto.getApplicationContext(),
                            BaseDatos.class, "base_datos")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instancia;
    }
}