package com.example.tecnicapomodoro.model;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "objetivo",
        foreignKeys = @ForeignKey(entity = Asignatura.class, parentColumns = "id",
                childColumns = "asignaturaId", onDelete = ForeignKey.CASCADE),
        indices = @Index(value = "asignaturaId", unique = true))


public class Objetivo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int asignaturaId;
    private int metaSemanalMin;

    public double calcularCumplimiento(int minutos) {
        if (metaSemanalMin <= 0) return 0;
        return minutos * 100.0 / metaSemanalMin;
    }

    public void ajustarMeta(int nuevaMeta) {
        metaSemanalMin = nuevaMeta;
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

    public int getMetaSemanalMin() {
        return metaSemanalMin;
    }

    public void setMetaSemanalMin(int metaSemanalMin) {
        this.metaSemanalMin = metaSemanalMin;
    }
}
