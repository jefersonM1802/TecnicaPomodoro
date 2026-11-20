package com.example.tecnicapomodoro.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "pomodoro",
        foreignKeys = @ForeignKey(entity = Tarea.class, parentColumns = "id",
                childColumns = "tareaId", onDelete = ForeignKey.CASCADE),
        indices = @Index("tareaId"))
public class Pomodoro {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int tareaId;
    private long inicio;
    private long fin;
    private int minutos;
    private boolean manual;

    public static Pomodoro crear(int tareaId, long inicio, long fin, int minutos, boolean manual) {
        Pomodoro p = new Pomodoro();
        p.setTareaId(tareaId);
        p.setInicio(inicio);
        p.setFin(fin);
        p.setMinutos(minutos);
        p.setManual(manual);
        return p;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTareaId() {
        return tareaId;
    }

    public void setTareaId(int tareaId) {
        this.tareaId = tareaId;
    }

    public long getInicio() {
        return inicio;
    }

    public void setInicio(long inicio) {
        this.inicio = inicio;
    }

    public long getFin() {
        return fin;
    }

    public void setFin(long fin) {
        this.fin = fin;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }
}
