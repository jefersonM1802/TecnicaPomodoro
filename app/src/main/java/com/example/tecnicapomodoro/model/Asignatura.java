package com.example.tecnicapomodoro.model;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "asignatura"),
    foreignKeys = @ForeignKey(entity = Estudiante.class, parentColumns = "id", childColumns = "estudianteId", onDelete = ForeignKey.CASCADE),
    indices = @Index("estudianteId"))

public class Asignatura {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int estudianteId;
    private String nombre;
    private String tipo;

    private String color;

    public Asignatura(int id, String nombre, String color) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public void asignarObjetivo(Objetivo objetivo) {
        objetivo.asignaturaId = id;
    }

    public void archivar() {
        archivada = true;
    }

    public void desarchivar() {
        archivada = false;
    }
}
