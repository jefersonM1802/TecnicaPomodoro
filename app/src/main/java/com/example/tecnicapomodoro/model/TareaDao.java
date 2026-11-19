package com.example.tecnicapomodoro.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TareaDao {


    @Insert
    long insertar(Tarea tarea);

    @Update
    void actualizar(Tarea tarea);

    @Delete
    void eliminar(Tarea tarea);

    @Query("SELECT * FROM tarea WHERE asignaturaId = :asignaturaId ORDER BY id DESC")
    List<Tarea> listarPorAsignatura(int asignaturaId);

    @Query("SELECT * FROM tarea WHERE id = :id")
    Tarea buscar(int id);

    @Query("SELECT t.* FROM tarea t INNER JOIN asignatura a ON a.id = t.asignaturaId " +
            "WHERE a.estudianteId = :estudianteId AND a.archivada = 0 AND t.estado != 'completada' " +
            "ORDER BY t.titulo")
    List<Tarea> listarPendientesDe(int estudianteId);
}
