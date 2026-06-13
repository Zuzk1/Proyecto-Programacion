package com.cecyt.pomodoro;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

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
        if (CronometroActivity.estaCorriendoGlobal && pantallaEncendida
                && new GestorConfiguracion(this).isAlertasDistraccionHabilitadas()) {
            GestorAlertas.marcarInfraccionPendiente(this);

            long tiempoRestante = CronometroActivity.tiempoFinEstimadoGlobal - SystemClock.elapsedRealtime();
            if (tiempoRestante < 0) tiempoRestante = 0;

            GestorConfiguracion gestorConfiguracion = new GestorConfiguracion(this);
            long duracionEtapaActual;
            if (CronometroActivity.esDescansoGlobal) {
                duracionEtapaActual = CronometroActivity.cicloActualGlobal == 1
                        ? gestorConfiguracion.getMinutosDescansoLargo() * 60000L
                        : gestorConfiguracion.getMinutosDescansoCorto() * 60000L;
            } else {
                duracionEtapaActual = gestorConfiguracion.getMinutosTrabajo() * 60000L;
            }
            long transcurridoEtapaActual = duracionEtapaActual - tiempoRestante;
            boolean etapaYaCompletada = CronometroActivity.cicloActualGlobal > 1;
            boolean menosDeDosMinutos = transcurridoEtapaActual < CronometroActivity.UMBRAL_SANCION;
            if (!etapaYaCompletada && !menosDeDosMinutos) {
                new GestorEstadisticas(this).registrarFallo();
            }

            GestorAlertas.guardarEstadoCronometro(this, tiempoRestante,
                    CronometroActivity.esDescansoGlobal, CronometroActivity.cicloActualGlobal,
                    CronometroActivity.tituloGlobal);

            Intent intentPausar = new Intent(this, CronometroService.class);
            intentPausar.setAction(CronometroService.ACCION_PAUSAR);
            ContextCompat.startForegroundService(this, intentPausar);

            GestorAlertas.mostrarAlertaPomodoro(this,
                    "¡Te estás distrayendo!",
                    "Tu cronómetro está en pausa. Vuelve a Miau Focus para continuar");
        }
    }
}
