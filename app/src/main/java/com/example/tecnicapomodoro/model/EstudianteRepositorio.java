package com.example.tecnicapomodoro.model;

import java.util.ArrayList;
import java.util.List;

public class EstudianteRepositorio {
    private final ArrayList<Estudiante> lista = new ArrayList<>();

    public boolean existeCorreo(String correo) {
        for (Estudiante estudiante : lista) {
            if (estudiante.getCorreo().equalsIgnoreCase(correo)) {
                return true;
            }
        }
        return false;
    }

    public void guardar(Estudiante estudiante) {
        estudiante.setId(lista.size() + 1);
        lista.add(estudiante);
    }

    public List<Estudiante> obtenerTodos() {
        return lista;
    }
}

