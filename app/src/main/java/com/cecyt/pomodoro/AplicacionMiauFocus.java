package com.cecyt.pomodoro;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

public class AplicacionMiauFocus extends Application implements DefaultLifecycleObserver {

    @Override
    public void onCreate() {
        super.onCreate();
        GestorAlertas.crearCanales(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner propietario) {
        if (CronometroActivity.estaCorriendoGlobal) {
            GestorAlertas.mostrarAlertaPomodoro(this,
                    "¡Te estás distrayendo!",
                    "Regresa a tu sesión de enfoque, el cronómetro sigue corriendo");
        }
    }
}
