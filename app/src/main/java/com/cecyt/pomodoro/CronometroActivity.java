package com.cecyt.pomodoro;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_enfoque) {
                // Logica para mostrar la pantalla principal del cronometro
                return true;
            } else if (itemId == R.id.nav_tareas) {
                // Logica para reemplazar la vista con el fragmento de tareas
                return true;
            } else if (itemId == R.id.nav_estadisticas) {
                // Logica para reemplazar la vista con el fragmento de estadisticas
                return true;
            }

            return false;
        });
    }
}