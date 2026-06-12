package com.cecyt.pomodoro;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class CronometroService extends Service {

    public static final String ACCION_INICIAR = "com.cecyt.pomodoro.ACCION_INICIAR_SERVICIO";
    public static final String ACCION_DETENER = "com.cecyt.pomodoro.ACCION_DETENER_SERVICIO";
    public static final String EXTRA_TIEMPO_RESTANTE = "extra_tiempo_restante";
    public static final String EXTRA_ES_DESCANSO = "extra_es_descanso";

    private static final int ID_NOTIFICACION_PROGRESO = 1001;

    private static final String ETIQUETA_DEPURACION = "DepuracionCronometro";

    private final Handler manejador = new Handler(Looper.getMainLooper());
    private Runnable accionFinalizacion;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ETIQUETA_DEPURACION, "Servicio: onCreate");
        GestorAlertas.crearCanales(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.d(ETIQUETA_DEPURACION, "Servicio: onStartCommand con intent nulo, deteniendo");
            detenerServicio();
            return START_NOT_STICKY;
        }

        String accion = intent.getAction();
        Log.d(ETIQUETA_DEPURACION, "Servicio: onStartCommand accion=" + accion);
        if (ACCION_DETENER.equals(accion)) {
            detenerServicio();
            return START_NOT_STICKY;
        }

        long tiempoRestante = intent.getLongExtra(EXTRA_TIEMPO_RESTANTE, 0);
        boolean esDescanso = intent.getBooleanExtra(EXTRA_ES_DESCANSO, false);
        Log.d(ETIQUETA_DEPURACION, "Servicio: iniciando seguimiento, tiempoRestante=" + tiempoRestante + " esDescanso=" + esDescanso);
        iniciarSeguimiento(tiempoRestante, esDescanso);
        return START_STICKY;
    }

    private void iniciarSeguimiento(long tiempoRestante, boolean esDescanso) {
        if (accionFinalizacion != null) {
            manejador.removeCallbacks(accionFinalizacion);
        }

        long finEstimado = System.currentTimeMillis() + tiempoRestante;

        Log.d(ETIQUETA_DEPURACION, "Servicio: llamando startForeground");
        startForeground(ID_NOTIFICACION_PROGRESO, construirNotificacionProgreso(finEstimado, esDescanso));

        accionFinalizacion = () -> {
            Log.d(ETIQUETA_DEPURACION, "Servicio: tiempo agotado, notificando finalizacion");
            notificarFinalizacion(esDescanso);
            detenerServicio();
        };
        manejador.postDelayed(accionFinalizacion, tiempoRestante);
    }

    private android.app.Notification construirNotificacionProgreso(long finEstimado, boolean esDescanso) {
        Intent intentAbrirApp = new Intent(this, CronometroActivity.class);
        intentAbrirApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent intentoPendienteAbrirApp = PendingIntent.getActivity(
                this, 0, intentAbrirApp, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, GestorAlertas.CANAL_PROGRESO_ID)
                .setSmallIcon(R.drawable.ic_gato_notificacion)
                .setContentTitle(esDescanso ? "Descanso en curso" : "Sesión de enfoque en curso")
                .setContentText("Toca para volver a Miau Focus")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setUsesChronometer(true)
                .setChronometerCountDown(true)
                .setWhen(finEstimado)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(intentoPendienteAbrirApp)
                .build();
    }

    private void notificarFinalizacion(boolean esDescanso) {
        Log.d(ETIQUETA_DEPURACION, "Servicio: notificarFinalizacion, puedeDibujarSobreOtrasApps=" + Settings.canDrawOverlays(this)
                + " permisoNotificaciones=" + (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED));
        Intent intentAlerta = new Intent(this, alertaActivity.class);
        intentAlerta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        PendingIntent intentoPendienteAlerta = PendingIntent.getActivity(
                this, 1, intentAlerta, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        android.app.Notification notificacionAlarma = new NotificationCompat.Builder(this, GestorAlertas.CANAL_ALARMA_ID)
                .setSmallIcon(R.drawable.ic_gato_notificacion)
                .setContentTitle(esDescanso ? "¡El descanso terminó!" : "¡La sesión de enfoque terminó!")
                .setContentText("Toca para continuar")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(intentoPendienteAlerta, true)
                .setContentIntent(intentoPendienteAlerta)
                .setAutoCancel(true)
                .build();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(GestorAlertas.ID_NOTIFICACION_ALARMA, notificacionAlarma);
        }

        if (Settings.canDrawOverlays(this)) {
            Intent intentAlertaDirecta = new Intent(this, alertaActivity.class);
            intentAlertaDirecta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentAlertaDirecta);
        }
    }

    private void detenerServicio() {
        Log.d(ETIQUETA_DEPURACION, "Servicio: detenerServicio");
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
        Log.d(ETIQUETA_DEPURACION, "Servicio: onTaskRemoved");
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
