package com.cecyt.pomodoro;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class CronometroActivity extends AppCompatActivity {

    // Control de estado de la actividad y tiempo.
    private boolean mantenerSplashScreen = true;
    private CountDownTimer temporizador;
    private long tiempoRestante = 1500000; // 25 minutos.
    private boolean estaCorriendo = false;

    // Vistas de la interfaz.
    private TextView textoTiempo;
    private CircularProgressIndicator barraProgreso;
    private FloatingActionButton botonPausar;
    private FloatingActionButton botonRenunciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inicializa pantalla de carga.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cronometro_main);

        // Mantiene la pantalla de carga activa por 850ms.
        splashScreen.setKeepOnScreenCondition(() -> mantenerSplashScreen);
        new Handler(Looper.getMainLooper()).postDelayed(() -> mantenerSplashScreen = false, 850);

        // Enlaza variables con los IDs del XML.
        textoTiempo = findViewById(R.id.tvTiempo);
        barraProgreso = findViewById(R.id.pbCronometro);
        botonPausar = findViewById(R.id.btnPausar);
        botonRenunciar = findViewById(R.id.btnRenunciar);

        // Configura limite y estado inicial de la barra.
        barraProgreso.setMax(1500000);
        barraProgreso.setProgress((int) tiempoRestante);

        // Asigna funcion de alternancia al boton de pausa/reproduccion.
        botonPausar.setOnClickListener(v -> {
            if (estaCorriendo) {
                pausarCronometro();
            } else {
                iniciarCronometro();
            }
        });

        // Asigna funcion de confirmacion al boton de renuncia.
        botonRenunciar.setOnClickListener(v -> mostrarDialogoAdvertencia());

        // Configura acciones de la barra de navegacion inferior.
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_enfoque) {
                return true;
            } else if (itemId == R.id.nav_tareas) {
                return true;
            } else if (itemId == R.id.nav_estadisticas) {
                return true;
            }
            return false;
        });
    }

    // Inicia el hilo del temporizador y actualiza el icono a pausa.
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
                botonPausar.setImageResource(android.R.drawable.ic_media_play);
                textoTiempo.setText("00:00");
                barraProgreso.setProgress(0);
            }
        }.start();

        estaCorriendo = true;
        botonPausar.setImageResource(android.R.drawable.ic_media_pause);
    }

    // Detiene el hilo del temporizador y actualiza el icono a reproduccion.
    private void pausarCronometro() {
        if (temporizador != null) {
            temporizador.cancel();
        }
        estaCorriendo = false;
        botonPausar.setImageResource(android.R.drawable.ic_media_play);
    }

    // Despliega cuadro de dialogo para confirmar la interrupcion del Pomodoro.
    private void mostrarDialogoAdvertencia() {
        new AlertDialog.Builder(this)
                .setTitle("Advertencia")
                .setMessage("¿Estás seguro de renunciar a la sesión? Se aplicará el castigo correspondiente.")
                .setPositiveButton("Renunciar", (dialog, which) -> reiniciarCronometro())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Reinicia las variables de tiempo y el estado visual al valor por defecto.
    private void reiniciarCronometro() {
        if (temporizador != null) {
            temporizador.cancel();
        }
        tiempoRestante = 1500000;
        estaCorriendo = false;
        botonPausar.setImageResource(android.R.drawable.ic_media_play);
        actualizarInterfaz();
    }

    // Calcula minutos y segundos para actualizar el texto y la barra de progreso.
    private void actualizarInterfaz() {
        int minutos = (int) (tiempoRestante / 1000) / 60;
        int segundos = (int) (tiempoRestante / 1000) % 60;

        String formato = String.format("%02d:%02d", minutos, segundos);
        textoTiempo.setText(formato);
        barraProgreso.setProgress((int) tiempoRestante);
    }
}