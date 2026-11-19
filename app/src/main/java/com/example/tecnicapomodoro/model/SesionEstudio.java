package com.example.tecnicapomodoro.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sesion_estudio",
        foreignKeys = @ForeignKey(entity = Tarea.class, parentColumns = "id",
                childColumns = "tareaId", onDelete = ForeignKey.CASCADE),
        indices = @Index("tareaId"))
public class SesionEstudio {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int estudianteId;
    private int tareaId;
    private String tareaTitulo;
    private long fechaHora;
    private int duracionMin;

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

    public int getTareaId() {
        return tareaId;
    }

    public void setTareaId(int tareaId) {
        this.tareaId = tareaId;
    }

    public String getTareaTitulo() {
        return tareaTitulo;
    }

    public void setTareaTitulo(String tareaTitulo) {
        this.tareaTitulo = tareaTitulo;
    }

    public long getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(long fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(int duracionMin) {
        this.duracionMin = duracionMin;
    }
}
