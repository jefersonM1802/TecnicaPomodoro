package com.example.tecnicapomodoro.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "asignatura",
        foreignKeys = @ForeignKey(entity = Estudiante.class, parentColumns = "id",
                childColumns = "estudianteId", onDelete = ForeignKey.CASCADE),
        indices = @Index("estudianteId"))
public class Asignatura {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int estudianteId;
    private String nombre;
    private String tipo;
    private String color;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void asignarObjetivo(Objetivo objetivo) {
        objetivo.setAsignaturaId(id);
    }
}