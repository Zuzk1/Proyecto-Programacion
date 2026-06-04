package com.cecyt.pomodoro;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class CronometroActivity extends AppCompatActivity {

    private boolean mantenerSplashScreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cronometro_main);

        //Pantalla de carga visible
        splashScreen.setKeepOnScreenCondition(() -> mantenerSplashScreen);

        //Temporizador de 1000 milisegundos para ocultar la pantalla de carga
        new Handler(Looper.getMainLooper()).postDelayed(() -> mantenerSplashScreen = false, 850);
    }
}