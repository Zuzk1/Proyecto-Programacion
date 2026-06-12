package com.cecyt.pomodoro;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class GestorTemas {
    private static final String PREF_NAME = "PomodoroTemas";
    private static final String CLAVE_TEMA_ACTUAL = "tema_actual";
    private static final String CLAVE_TEMAS_DESBLOQUEADOS = "temas_desbloqueados";

    private final SharedPreferences prefs;

    public enum Tema {
        AZUL_OSCURO("azul_oscuro", "Azul Oscuro", R.style.Theme_Pomodoro_Dark_Azul,
                R.color.tema_oscuro_fondo_principal, R.color.acento_azul, 0, true),
        AZUL_CLARO("azul_claro", "Azul Claro", R.style.Theme_Pomodoro_Light_Azul,
                R.color.tema_claro_fondo_principal, R.color.acento_azul, 0, true),

        NARANJA_OSCURO("naranja_oscuro", "Naranja Oscuro", R.style.Theme_Pomodoro_Dark_Naranja,
                R.color.tema_oscuro_fondo_principal, R.color.acento_naranja, 150, false),
        NARANJA_CLARO("naranja_claro", "Naranja Claro", R.style.Theme_Pomodoro_Light_Naranja,
                R.color.tema_claro_fondo_principal, R.color.acento_naranja, 150, false),

        ROJO_OSCURO("rojo_oscuro", "Rojo Oscuro", R.style.Theme_Pomodoro_Dark_Rojo,
                R.color.tema_oscuro_fondo_principal, R.color.acento_rojo, 150, false),
        ROJO_CLARO("rojo_claro", "Rojo Claro", R.style.Theme_Pomodoro_Light_Rojo,
                R.color.tema_claro_fondo_principal, R.color.acento_rojo, 150, false),

        MARINO_OSCURO("marino_oscuro", "Azul Marino Oscuro", R.style.Theme_Pomodoro_Dark_Marino,
                R.color.tema_oscuro_fondo_principal, R.color.acento_marino, 150, false),
        MARINO_CLARO("marino_claro", "Azul Marino Claro", R.style.Theme_Pomodoro_Light_Marino,
                R.color.tema_claro_fondo_principal, R.color.acento_marino, 150, false),

        MORADO_OSCURO("morado_oscuro", "Morado Oscuro", R.style.Theme_Pomodoro_Dark_Morado,
                R.color.tema_oscuro_fondo_principal, R.color.acento_morado, 150, false),
        MORADO_CLARO("morado_claro", "Morado Claro", R.style.Theme_Pomodoro_Light_Morado,
                R.color.tema_claro_fondo_principal, R.color.acento_morado, 150, false),

        VERDE_OSCURO("verde_oscuro", "Verde Oscuro", R.style.Theme_Pomodoro_Dark_Verde,
                R.color.tema_oscuro_fondo_principal, R.color.acento_verde, 150, false),
        VERDE_CLARO("verde_claro", "Verde Claro", R.style.Theme_Pomodoro_Light_Verde,
                R.color.tema_claro_fondo_principal, R.color.acento_verde, 150, false),

        ROSA_OSCURO("rosa_oscuro", "Rosa Oscuro", R.style.Theme_Pomodoro_Dark_Rosa,
                R.color.tema_oscuro_fondo_principal, R.color.acento_rosa, 150, false),
        ROSA_CLARO("rosa_claro", "Rosa Claro", R.style.Theme_Pomodoro_Light_Rosa,
                R.color.tema_claro_fondo_principal, R.color.acento_rosa, 150, false);

        public final String id;
        public final String nombre;
        public final int idEstilo;
        public final int colorFondoPreview;
        public final int colorAcentoPreview;
        public final int precio;
        public final boolean gratis;

        Tema(String id, String nombre, int idEstilo, int colorFondoPreview, int colorAcentoPreview, int precio, boolean gratis) {
            this.id = id;
            this.nombre = nombre;
            this.idEstilo = idEstilo;
            this.colorFondoPreview = colorFondoPreview;
            this.colorAcentoPreview = colorAcentoPreview;
            this.precio = precio;
            this.gratis = gratis;
        }

        public static Tema porId(String id) {
            for (Tema tema : values()) {
                if (tema.id.equals(id)) return tema;
            }
            return AZUL_OSCURO;
        }

        public boolean esClaro() {
            return id.endsWith("claro");
        }
    }

    public GestorTemas(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public Tema getTemaActual() {
        return Tema.porId(prefs.getString(CLAVE_TEMA_ACTUAL, Tema.AZUL_OSCURO.id));
    }

    public void establecerTemaActual(Tema tema) {
        prefs.edit().putString(CLAVE_TEMA_ACTUAL, tema.id).apply();
    }

    public boolean estaDesbloqueado(Tema tema) {
        if (tema.gratis) return true;
        return obtenerDesbloqueados().contains(tema.id);
    }

    public void desbloquear(Tema tema) {
        Set<String> desbloqueados = obtenerDesbloqueados();
        desbloqueados.add(tema.id);
        prefs.edit().putStringSet(CLAVE_TEMAS_DESBLOQUEADOS, desbloqueados).apply();
    }

    private Set<String> obtenerDesbloqueados() {
        return new HashSet<>(prefs.getStringSet(CLAVE_TEMAS_DESBLOQUEADOS, new HashSet<>()));
    }

    /** Aplica el tema guardado a la actividad. Debe llamarse antes de super.onCreate(). */
    public void aplicarTema(Activity actividad) {
        actividad.setTheme(getTemaActual().idEstilo);
    }
}
