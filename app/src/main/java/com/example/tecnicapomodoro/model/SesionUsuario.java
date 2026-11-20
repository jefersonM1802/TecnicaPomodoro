package com.example.tecnicapomodoro.model;



import android.content.Context;
import android.content.SharedPreferences;
public class SesionUsuario {
    private static String archivo = "sesion_usuario";

    public static void guardar(Context c, int id, String nombre) {
        prefs(c).edit().putInt("id", id).putString("nombre", nombre).apply();
    }

    public static int id(Context c) {
        return prefs(c).getInt("id", -1);
    }

    public static String nombre(Context c) {
        return prefs(c).getString("nombre", "");
    }

    public static void cerrar(Context c) {
        prefs(c).edit().clear().apply();
    }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(archivo, Context.MODE_PRIVATE);
    }
}
