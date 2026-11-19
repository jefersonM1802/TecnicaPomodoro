package com.example.tecnicapomodoro.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EstudianteDao {

    @Insert
    long insertar (Estudiante estudiante);

    @Query("SELECT COUNT(*) FROM estudiante WHERE correo = :correo")
    int contarPorCorreo(String correo);

    @Query("SELECT * FROM estudiante WHERE correo = :correo AND password = :password LIMIT 1")
    Estudiante buscarPorCredenciales(String correo, String password);

    @Query("SELECT * FROM estudiante ORDER BY id")
    List<Estudiante> obtenerTodos();
}
