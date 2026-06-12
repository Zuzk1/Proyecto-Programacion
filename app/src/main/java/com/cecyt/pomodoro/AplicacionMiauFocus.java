package com.cecyt.pomodoro;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

public class AplicacionMiauFocus extends Application implements DefaultLifecycleObserver {

    private boolean pantallaEncendida = true;

    @Override
    public void onCreate() {
        super.onCreate();
        GestorAlertas.crearCanales(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        IntentFilter filtroPantalla = new IntentFilter();
        filtroPantalla.addAction(Intent.ACTION_SCREEN_OFF);
        filtroPantalla.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pantallaEncendida = Intent.ACTION_SCREEN_ON.equals(intent.getAction());
                if (CronometroActivity.estaCorriendoGlobal) {
                    if (pantallaEncendida) {
                        GestorAlertas.cancelarNotificacionBloqueo(context);
                    } else {
                        GestorAlertas.mostrarNotificacionBloqueo(context);
                    }
                }
            }
        }, filtroPantalla);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner propietario) {
        if (CronometroActivity.estaCorriendoGlobal && pantallaEncendida) {
            GestorAlertas.marcarInfraccionPendiente(this);
            new GestorEstadisticas(this).registrarFallo();

            Intent intentPausar = new Intent(this, CronometroService.class);
            intentPausar.setAction(CronometroService.ACCION_PAUSAR);
            ContextCompat.startForegroundService(this, intentPausar);

            GestorAlertas.mostrarAlertaPomodoro(this,
                    "¡Te estás distrayendo!",
                    "Regresa a tu sesión de enfoque, el cronómetro sigue corriendo");
        }
    }
}
