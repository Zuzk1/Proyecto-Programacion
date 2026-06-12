package com.cecyt.pomodoro;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
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
//holaaA
    public int getMinutosTotales() { return prefs.getInt("minutos_totales", 0); }
    public int getPomodorosCompletados() { return prefs.getInt("pomodoros_completados", 0); }
    public int getPomodorosFallados() { return prefs.getInt("pomodoros_fallados", 0); }
    public int getPuntos() { return prefs.getInt("puntos", 0); }
    public int getRachaActual() { return prefs.getInt("racha_actual", 0); }

    public void registrarActividadCompletada(String nombreTarea) {
        SharedPreferences.Editor editor = prefs.edit();
        int total = prefs.getInt("actividades_total", 0);
        editor.putString("actividad_" + total, nombreTarea);
        editor.putInt("actividades_total", total + 1);
        editor.apply();
    }

    public ArrayList<String> getActividadesRecientes(int maxCantidad) {
        ArrayList<String> actividades = new ArrayList<>();
        int total = prefs.getInt("actividades_total", 0);
        int inicio = Math.max(0, total - maxCantidad);

        for (int i = total - 1; i >= inicio; i--) {
            String actividad = prefs.getString("actividad_" + i, null);
            if (actividad != null) {
                actividades.add(actividad);
            }
        }

        return actividades;
    }

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
