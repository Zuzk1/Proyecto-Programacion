package com.cecyt.pomodoro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class CronometroService extends Service {

    public static final String ACCION_INICIAR = "com.cecyt.pomodoro.ACCION_INICIAR_SERVICIO";
    public static final String ACCION_DETENER = "com.cecyt.pomodoro.ACCION_DETENER_SERVICIO";
    public static final String EXTRA_TIEMPO_RESTANTE = "extra_tiempo_restante";
    public static final String EXTRA_ES_DESCANSO = "extra_es_descanso";

    private static final String CANAL_PROGRESO_ID = "canal_progreso_pomodoro";
    private static final String CANAL_ALARMA_ID = "canal_alarma_pomodoro";
    private static final int NOTIF_ID_PROGRESO = 1001;
    private static final int NOTIF_ID_ALARMA = 1002;

    private final Handler manejador = new Handler(Looper.getMainLooper());
    private Runnable accionFinalizacion;

    @Override
    public void onCreate() {
        super.onCreate();
        crearCanalesNotificacion();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            detenerServicio();
            return START_NOT_STICKY;
        }

        String accion = intent.getAction();
        if (ACCION_DETENER.equals(accion)) {
            detenerServicio();
            return START_NOT_STICKY;
        }

        long tiempoRestante = intent.getLongExtra(EXTRA_TIEMPO_RESTANTE, 0);
        boolean esDescanso = intent.getBooleanExtra(EXTRA_ES_DESCANSO, false);
        iniciarSeguimiento(tiempoRestante, esDescanso);
        return START_STICKY;
    }

    private void crearCanalesNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manejadorNotificaciones = getSystemService(NotificationManager.class);

            NotificationChannel canalProgreso = new NotificationChannel(
                    CANAL_PROGRESO_ID, "Sesión en curso", NotificationManager.IMPORTANCE_LOW);
            canalProgreso.setDescription("Muestra el progreso de la sesión de enfoque o descanso");
            manejadorNotificaciones.createNotificationChannel(canalProgreso);

            NotificationChannel canalAlarma = new NotificationChannel(
                    CANAL_ALARMA_ID, "Alarma de Pomodoro", NotificationManager.IMPORTANCE_HIGH);
            canalAlarma.setDescription("Avisa cuando termina la sesión de enfoque o descanso");
            manejadorNotificaciones.createNotificationChannel(canalAlarma);
        }
    }

    private void iniciarSeguimiento(long tiempoRestante, boolean esDescanso) {
        if (accionFinalizacion != null) {
            manejador.removeCallbacks(accionFinalizacion);
        }

        long finEstimado = System.currentTimeMillis() + tiempoRestante;

        startForeground(NOTIF_ID_PROGRESO, construirNotificacionProgreso(finEstimado, esDescanso));

        accionFinalizacion = () -> {
            notificarFinalizacion(esDescanso);
            detenerServicio();
        };
        manejador.postDelayed(accionFinalizacion, tiempoRestante);
    }

    private android.app.Notification construirNotificacionProgreso(long finEstimado, boolean esDescanso) {
        Intent intentAbrirApp = new Intent(this, CronometroActivity.class);
        intentAbrirApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingAbrirApp = PendingIntent.getActivity(
                this, 0, intentAbrirApp, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CANAL_PROGRESO_ID)
                .setSmallIcon(R.drawable.ic_reloj)
                .setContentTitle(esDescanso ? "Descanso en curso" : "Sesión de enfoque en curso")
                .setContentText("Toca para volver a Miau Focus")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setUsesChronometer(true)
                .setChronometerCountDown(true)
                .setWhen(finEstimado)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingAbrirApp)
                .build();
    }

    private void notificarFinalizacion(boolean esDescanso) {
        Intent intentAlerta = new Intent(this, alertaActivity.class);
        intentAlerta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingAlerta = PendingIntent.getActivity(
                this, 1, intentAlerta, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        android.app.Notification notificacionAlarma = new NotificationCompat.Builder(this, CANAL_ALARMA_ID)
                .setSmallIcon(R.drawable.ic_reloj)
                .setContentTitle(esDescanso ? "¡El descanso terminó!" : "¡La sesión de enfoque terminó!")
                .setContentText("Toca para continuar")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingAlerta, true)
                .setContentIntent(pendingAlerta)
                .setAutoCancel(true)
                .build();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(NOTIF_ID_ALARMA, notificacionAlarma);
        }
    }

    private void detenerServicio() {
        if (accionFinalizacion != null) {
            manejador.removeCallbacks(accionFinalizacion);
            accionFinalizacion = null;
        }
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        detenerServicio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (accionFinalizacion != null) {
            manejador.removeCallbacks(accionFinalizacion);
            accionFinalizacion = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
