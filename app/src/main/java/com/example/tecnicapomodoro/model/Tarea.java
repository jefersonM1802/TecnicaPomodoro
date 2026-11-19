package com.example.tecnicapomodoro.model;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tarea",
        foreignKeys = @ForeignKey(entity = Asignatura.class, parentColumns = "id",
                childColumns = "asignaturaId", onDelete = ForeignKey.CASCADE),
        indices = @Index("asignaturaId"))
public class Tarea {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int asignaturaId;
    private String titulo;
    private int pomodorosEstimados;
    private String estado = "pendiente";
    private int minutosRegistrados;

    public Tarea(int id, int asignaturaId, String titulo, int pomodorosEstimados, String estado, int minutosRegistrados) {
        this.id = id;
        this.asignaturaId = asignaturaId;
        this.titulo = titulo;
        this.pomodorosEstimados = pomodorosEstimados;
        this.estado = estado;
        this.minutosRegistrados = minutosRegistrados;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(int asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getPomodorosEstimados() {
        return pomodorosEstimados;
    }

    public void setPomodorosEstimados(int pomodorosEstimados) {
        this.pomodorosEstimados = pomodorosEstimados;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getMinutosRegistrados() {
        return minutosRegistrados;
    }

    public void setMinutosRegistrados(int minutosRegistrados) {
        this.minutosRegistrados = minutosRegistrados;
    }

    public void registrarTiempo(int minutos) {
        minutosRegistrados += minutos;
        if (estado.equals("pendiente")) estado = "en curso";
    }

    public void completar() {
        estado = "completada";
    }

    private int tiempoTotal() {
        return minutosRegistrados;
    }
}
