package com.example.tecnicapomodoro.model;

public class Progreso {

    private String nombre;
    private String color;
    private int minutos;
    private int meta;
    private double cumplimiento;

    public Progreso(String nombre, String color, int minutos, int meta, double cumplimiento) {
        this.nombre = nombre;
        this.color = color;
        this.minutos = minutos;
        this.meta = meta;
        this.cumplimiento = cumplimiento;
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

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public int getMeta() {
        return meta;
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    public double getCumplimiento() {
        return cumplimiento;
    }

    public void setCumplimiento(double cumplimiento) {
        this.cumplimiento = cumplimiento;
    }
}
