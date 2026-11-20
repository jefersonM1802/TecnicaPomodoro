package com.example.tecnicapomodoro.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AsignaturaDao {
    @Insert
    long insertar(Asignatura asignatura);

    @Update
    void actualizar(Asignatura asignatura);

    @Delete
    void eliminar(Asignatura asignatura);

    @Query("SELECT * FROM asignatura WHERE estudianteId = :estudianteId ORDER BY nombre")
    List<Asignatura> listar(int estudianteId);

    @Query("SELECT COUNT(*) FROM asignatura WHERE estudianteId = :estudianteId " +
            "AND LOWER(nombre) = LOWER(:nombre) AND id != :idExcluir")
    int contarPorNombre(int estudianteId, String nombre, int idExcluir);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void guardarObjetivo(Objetivo objetivo);

    @Query("SELECT * FROM objetivo WHERE asignaturaId = :asignaturaId LIMIT 1")
    Objetivo obtenerObjetivo(int asignaturaId);
}
