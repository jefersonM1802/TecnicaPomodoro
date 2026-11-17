package com.example.tecnicapomodoro.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities ={Estudiante.class}, version = 1)
public abstract class BaseDatos extends RoomDatabase {

    private static BaseDatos instancia;

    public abstract  EstudianteDao estudianteDao();

    public static BaseDatos getInstance(Context contexto){
       if (instancia == null){
           instancia = Room.databaseBuilder(contexto.getApplicationContext(),
                   BaseDatos.class, "base_datos").build();
       }
       return instancia;
       }
    }

}
