package com.example.tecnicapomodoro.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ConfiguracionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void guardar(ConfiguracionPomodoro configuracion);

    @Query("SELECT * FROM configuracion_pomodoro WHERE estudianteId = :estudianteId")
    ConfiguracionPomodoro obtener(int estudianteId);
}
