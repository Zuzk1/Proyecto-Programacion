package com.cecyt.pomodoro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class GestorAlertas {

    public static final String CANAL_PROGRESO_ID = "canal_progreso_pomodoro";
    public static final String CANAL_ALARMA_ID = "canal_alarma_pomodoro";
    public static final String CANAL_BLOQUEO_ID = "canal_bloqueo_pomodoro";
    public static final int ID_NOTIFICACION_ALARMA = 1002;
    public static final int ID_NOTIFICACION_BLOQUEO = 1003;
    public static final String EXTRA_ES_INFRACCION = "extra_es_infraccion";
    private static final String PREFS_NOMBRE = "PrefsAlertas";
    private static final String CLAVE_INFRACCION_PENDIENTE = "infraccion_pendiente";

    public static void crearCanales(Context contexto) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager gestorNotificaciones = contexto.getSystemService(NotificationManager.class);

        NotificationChannel canalProgreso = new NotificationChannel(
                CANAL_PROGRESO_ID, "Sesión en curso", NotificationManager.IMPORTANCE_LOW);
        canalProgreso.setDescription("Muestra el progreso de la sesión de enfoque o descanso");
        gestorNotificaciones.createNotificationChannel(canalProgreso);

        NotificationChannel canalAlarma = new NotificationChannel(
                CANAL_ALARMA_ID, "Alarma de Pomodoro", NotificationManager.IMPORTANCE_HIGH);
        canalAlarma.setDescription("Avisa cuando termina la sesión o cuando te distraes durante el enfoque");
        canalAlarma.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
        canalAlarma.enableVibration(true);
        gestorNotificaciones.createNotificationChannel(canalAlarma);

        NotificationChannel canalBloqueo = new NotificationChannel(
                CANAL_BLOQUEO_ID, "Cronómetro con pantalla bloqueada", NotificationManager.IMPORTANCE_DEFAULT);
        canalBloqueo.setDescription("Recuerda que el cronómetro sigue corriendo mientras la pantalla está bloqueada");
        canalBloqueo.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
        gestorNotificaciones.createNotificationChannel(canalBloqueo);
    }

    public static void mostrarNotificacionBloqueo(Context contexto) {
        Intent intentAbrirApp = new Intent(contexto, CronometroActivity.class);
        intentAbrirApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent intentoPendienteAbrirApp = PendingIntent.getActivity(
                contexto, 2, intentAbrirApp, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        android.app.Notification notificacion = new NotificationCompat.Builder(contexto, CANAL_BLOQUEO_ID)
                .setSmallIcon(R.drawable.ic_gato_notificacion)
                .setContentTitle("Cronómetro en curso")
                .setContentText("Vuelve a Miau Focus")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setContentIntent(intentoPendienteAbrirApp)
                .build();

        if (ContextCompat.checkSelfPermission(contexto, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(contexto).notify(ID_NOTIFICACION_BLOQUEO, notificacion);
        }
    }

    public static void cancelarNotificacionBloqueo(Context contexto) {
        NotificationManagerCompat.from(contexto).cancel(ID_NOTIFICACION_BLOQUEO);
    }

    public static void marcarInfraccionPendiente(Context contexto) {
        contexto.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
                .edit().putBoolean(CLAVE_INFRACCION_PENDIENTE, true).apply();
    }

    public static void limpiarInfraccionPendiente(Context contexto) {
        contexto.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
                .edit().putBoolean(CLAVE_INFRACCION_PENDIENTE, false).apply();
    }

    public static boolean hayInfraccionPendiente(Context contexto) {
        return contexto.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
                .getBoolean(CLAVE_INFRACCION_PENDIENTE, false);
    }

    public static void mostrarAlertaPomodoro(Context contexto, String titulo, String mensaje) {
        Intent intentAlerta = new Intent(contexto, CronometroActivity.class);
        intentAlerta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent intentoPendienteAlerta = PendingIntent.getActivity(
                contexto, 1, intentAlerta, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        android.app.Notification notificacionAlarma = new NotificationCompat.Builder(contexto, CANAL_ALARMA_ID)
                .setSmallIcon(R.drawable.ic_gato_notificacion)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(intentoPendienteAlerta)
                .setAutoCancel(true)
                .build();

        if (ContextCompat.checkSelfPermission(contexto, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(contexto).notify(ID_NOTIFICACION_ALARMA, notificacionAlarma);
        }
    }
}
