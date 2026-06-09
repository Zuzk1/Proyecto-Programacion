package com.cecyt.pomodoro;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Calendar;

public class GestorEstadisticas {
    private static final String PREF_NAME = "PomodoroStats";
    private SharedPreferences prefs;

    public GestorEstadisticas(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void registrarPomodoroExitoso(int minutosEnfoque, int puntosGanados) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("minutos_totales", getMinutosTotales() + minutosEnfoque);
        editor.putInt("pomodoros_completados", getPomodorosCompletados() + 1);
        editor.putInt("puntos", getPuntos() + puntosGanados);
        editor.putInt("racha_actual", getRachaActual() + 1);

        int diaSemana = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String keyDia = "pomodoros_dia_" + diaSemana;
        editor.putInt(keyDia, prefs.getInt(keyDia, 0) + 1);

        editor.apply();
    }

    public void registrarFallo() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("pomodoros_fallados", getPomodorosFallados() + 1);
        editor.putInt("racha_actual", 0);
        editor.apply();
    }
//hola
    public int getMinutosTotales() { return prefs.getInt("minutos_totales", 0); }
    public int getPomodorosCompletados() { return prefs.getInt("pomodoros_completados", 0); }
    public int getPomodorosFallados() { return prefs.getInt("pomodoros_fallados", 0); }
    public int getPuntos() { return prefs.getInt("puntos", 0); }
    public int getRachaActual() { return prefs.getInt("racha_actual", 0); }

    public int[] getDatosSemana() {
        return new int[]{
                prefs.getInt("pomodoros_dia_" + Calendar.MONDAY, 0),
                prefs.getInt("pomodoros_dia_" + Calendar.TUESDAY, 0),
                prefs.getInt("pomodoros_dia_" + Calendar.WEDNESDAY, 0),
                prefs.getInt("pomodoros_dia_" + Calendar.THURSDAY, 0),
                prefs.getInt("pomodoros_dia_" + Calendar.FRIDAY, 0),
                prefs.getInt("pomodoros_dia_" + Calendar.SATURDAY, 0),
                prefs.getInt("pomodoros_dia_" + Calendar.SUNDAY, 0)
        };
    }
}
