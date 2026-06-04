package com.cecyt.pomodoro;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class CronometroActivity extends AppCompatActivity {

    // Variables de configuracion
    private boolean mantenerSplashScreen = true;
    private CountDownTimer temporizador;
    private long tiempoRestante = 1500000; // 25 minutos expresados en milisegundos
    private boolean estaCorriendo = false;

    // Declaracion de componentes de la interfaz
    private TextView textoTiempo;
    private CircularProgressIndicator barraProgreso;
    private Button botonPausar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cronometro_main);

        // Pantalla de carga visible
        splashScreen.setKeepOnScreenCondition(() -> mantenerSplashScreen);

        // Temporizador de 850 milisegundos para ocultar la pantalla de carga
        new Handler(Looper.getMainLooper()).postDelayed(() -> mantenerSplashScreen = false, 850);

        // Enlace de los elementos de la interfaz con el codigo
        textoTiempo = findViewById(R.id.tvTiempo);
        barraProgreso = findViewById(R.id.pbCronometro);
        botonPausar = findViewById(R.id.btnPausar);

        // Configuracion inicial de la barra de progreso
        barraProgreso.setMax((int) 1500000);
        barraProgreso.setProgress((int) tiempoRestante);

        // Asignacion de evento al boton principal
        botonPausar.setOnClickListener(v -> {
            if (estaCorriendo) {
                pausarCronometro();
            } else {
                iniciarCronometro();
            }
        });

        // Configuracion de la barra de navegacion inferior
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

    // Metodo para arrancar el conteo regresivo
    private void iniciarCronometro() {
        temporizador = new CountDownTimer(tiempoRestante, 1000) {
            @Override
            public void onTick(long milisegundos) {
                tiempoRestante = milisegundos;
                actualizarInterfaz();
            }

            @Override
            public void onFinish() {
                estaCorriendo = false;
                botonPausar.setText("INICIAR");
                textoTiempo.setText("00:00");
                barraProgreso.setProgress(0);
            }
        }.start();

        estaCorriendo = true;
        botonPausar.setText("PAUSAR");
    }

    // Metodo para detener el conteo sin reiniciar el tiempo
    private void pausarCronometro() {
        if (temporizador != null) {
            temporizador.cancel();
        }
        estaCorriendo = false;
        botonPausar.setText("REANUDAR");
    }

    // Metodo para calcular y mostrar los minutos y segundos exactos en la pantalla
    private void actualizarInterfaz() {
        int minutos = (int) (tiempoRestante / 1000) / 60;
        int segundos = (int) (tiempoRestante / 1000) % 60;

        String formato = String.format("%02d:%02d", minutos, segundos);
        textoTiempo.setText(formato);
        barraProgreso.setProgress((int) tiempoRestante);
    }
}