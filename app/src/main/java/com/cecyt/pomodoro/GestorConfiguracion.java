package com.cecyt.pomodoro;

import android.content.Context;
import android.content.SharedPreferences;

public class GestorConfiguracion {

    private static final String PREF_NAME = "PomodoroConfiguracion";
    private static final String CLAVE_MINUTOS_TRABAJO = "minutos_trabajo";
    private static final String CLAVE_MINUTOS_DESCANSO_CORTO = "minutos_descanso_corto";
    private static final String CLAVE_MINUTOS_DESCANSO_LARGO = "minutos_descanso_largo";

    public static final int MINUTOS_TRABAJO_PREDETERMINADO = 25;
    public static final int MINUTOS_DESCANSO_CORTO_PREDETERMINADO = 5;
    public static final int MINUTOS_DESCANSO_LARGO_PREDETERMINADO = 15;

    private final SharedPreferences prefs;

    public GestorConfiguracion(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getMinutosTrabajo() {
        return prefs.getInt(CLAVE_MINUTOS_TRABAJO, MINUTOS_TRABAJO_PREDETERMINADO);
    }

    public int getMinutosDescansoCorto() {
        return prefs.getInt(CLAVE_MINUTOS_DESCANSO_CORTO, MINUTOS_DESCANSO_CORTO_PREDETERMINADO);
    }

    public int getMinutosDescansoLargo() {
        return prefs.getInt(CLAVE_MINUTOS_DESCANSO_LARGO, MINUTOS_DESCANSO_LARGO_PREDETERMINADO);
    }

    public void guardar(int minutosTrabajo, int minutosDescansoCorto, int minutosDescansoLargo) {
        prefs.edit()
                .putInt(CLAVE_MINUTOS_TRABAJO, minutosTrabajo)
                .putInt(CLAVE_MINUTOS_DESCANSO_CORTO, minutosDescansoCorto)
                .putInt(CLAVE_MINUTOS_DESCANSO_LARGO, minutosDescansoLargo)
                .apply();
    }
}
