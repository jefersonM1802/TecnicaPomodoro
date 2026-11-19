package com.example.tecnicapomodoro.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SesionDao {

    @Insert
    long insertar(SesionEstudio sesion);

    @Delete
    void eliminar(SesionEstudio sesion);

    @Query("SELECT * FROM sesion_estudio WHERE estudianteId = :estudianteId ORDER BY fechaHora")
    List<SesionEstudio> listar(int estudianteId);
}
