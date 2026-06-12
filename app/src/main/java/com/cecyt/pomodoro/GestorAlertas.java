package com.cecyt.pomodoro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class GestorAlertas {

    public static final String CANAL_PROGRESO_ID = "canal_progreso_pomodoro";
    public static final String CANAL_ALARMA_ID = "canal_alarma_pomodoro";
    public static final int ID_NOTIFICACION_ALARMA = 1002;
    public static final String EXTRA_ES_INFRACCION = "extra_es_infraccion";

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
    }

    public static void mostrarAlertaPomodoro(Context contexto, String titulo, String mensaje) {
        Intent intentAlerta = new Intent(contexto, alertaActivity.class);
        intentAlerta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentAlerta.putExtra(EXTRA_ES_INFRACCION, true);
        PendingIntent intentoPendienteAlerta = PendingIntent.getActivity(
                contexto, 1, intentAlerta, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        android.app.Notification notificacionAlarma = new NotificationCompat.Builder(contexto, CANAL_ALARMA_ID)
                .setSmallIcon(R.drawable.ic_gato_notificacion)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(intentoPendienteAlerta, true)
                .setContentIntent(intentoPendienteAlerta)
                .setAutoCancel(true)
                .build();

        if (ContextCompat.checkSelfPermission(contexto, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(contexto).notify(ID_NOTIFICACION_ALARMA, notificacionAlarma);
        }

        if (Settings.canDrawOverlays(contexto)) {
            Intent intentAlertaDirecta = new Intent(contexto, alertaActivity.class);
            intentAlertaDirecta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentAlertaDirecta.putExtra(EXTRA_ES_INFRACCION, true);
            contexto.startActivity(intentAlertaDirecta);
        }
    }
}
