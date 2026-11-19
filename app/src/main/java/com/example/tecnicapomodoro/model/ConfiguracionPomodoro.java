package com.example.tecnicapomodoro.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "configuracion_pomodoro",
        foreignKeys = @ForeignKey(entity = Estudiante.class, parentColumns = "id",
                childColumns = "estudianteId", onDelete = ForeignKey.CASCADE))
public class ConfiguracionPomodoro {
    @PrimaryKey
    private int estudianteId;
    private int minTrabajo = 25;
    private int minDescansoCorto = 5;
    private int minDescansoLargo = 15;
    private int pomodorosPorCiclo = 4;

    public static ConfiguracionPomodoro porDefecto(int estudianteId) {
        ConfiguracionPomodoro c = new ConfiguracionPomodoro();
        c.setEstudianteId(estudianteId);
        return c;
    }

    public void actualizar(int trabajo, int corto, int largo, int porCiclo) {
        minTrabajo = trabajo;
        minDescansoCorto = corto;
        minDescansoLargo = largo;
        pomodorosPorCiclo = porCiclo;
    }

    private int obtenerMinTrabajo() {
        return minTrabajo;
    }

    private int obtenerDescanso(int nroPomodoro) {
        if (nroPomodoro > 0 && nroPomodoro % pomodorosPorCiclo == 0) return minDescansoLargo;
        return minDescansoCorto;
    }

    public ConfiguracionPomodoro(int estudianteId, int minTrabajo, int minDescanso, int minDescansoLargo, int pomodorosPorCiclo) {
        this.estudianteId = estudianteId;
        this.minTrabajo = minTrabajo;
        this.minDescansoCorto = minDescanso;
        this.minDescansoLargo = minDescansoLargo;
        this.pomodorosPorCiclo = pomodorosPorCiclo;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public int getMinTrabajo() {
        return minTrabajo;
    }

    public void setMinTrabajo(int minTrabajo) {
        this.minTrabajo = minTrabajo;
    }

    public int getMinDescansoCorto() {
        return minDescansoCorto;
    }

    public void setMinDescansoCorto(int minDescanso) {
        this.minDescansoCorto = minDescanso;
    }

    public int getMinDescansoLargo() {
        return minDescansoLargo;
    }

    public void setMinDescansoLargo(int minDescansoLargo) {
        this.minDescansoLargo = minDescansoLargo;
    }

    public int getPomodorosPorCiclo() {
        return pomodorosPorCiclo;
    }

    public void setPomodorosPorCiclo(int pomodorosPorCiclo) {
        this.pomodorosPorCiclo = pomodorosPorCiclo;
    }
}
