package com.example.tecnicapomodoro.model;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PomodoroDao {

    @Insert
    long insertar(Pomodoro pomodoro);

    @Query("SELECT COALESCE(SUM(p.minutos), 0) FROM pomodoro p " +
            "INNER JOIN tarea t ON t.id = p.tareaId " +
            "INNER JOIN asignatura a ON a.id = t.asignaturaId " +
            "WHERE a.estudianteId = :estudianteId AND p.fin >= :desde")
    int minutosDesde(int estudianteId, long desde);

    @Query("SELECT COUNT(*) FROM pomodoro p " +
            "INNER JOIN tarea t ON t.id = p.tareaId " +
            "INNER JOIN asignatura a ON a.id = t.asignaturaId " +
            "WHERE a.estudianteId = :estudianteId AND p.manual = 0 AND p.fin >= :desde")
    int pomodorosDesde(int estudianteId, long desde);

    @Query("SELECT COALESCE(SUM(p.minutos), 0) FROM pomodoro p " +
            "INNER JOIN tarea t ON t.id = p.tareaId " +
            "WHERE t.asignaturaId = :asignaturaId AND p.fin >= :desde")
    int minutosAsignaturaDesde(int asignaturaId, long desde);
}
