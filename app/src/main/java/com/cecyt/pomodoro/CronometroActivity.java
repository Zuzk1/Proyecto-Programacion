package com.cecyt.pomodoro;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
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

    // Controladores de animacion.
    private ObjectAnimator animacionBarra;
    private ObjectAnimator animacionTexto;
    private ObjectAnimator animacionMenuActual;

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

        // Inicializa los objetos de animacion de levitacion central.
        configurarAnimaciones();

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

        // Accede al contenedor interno de los iconos del menu.
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            int indiceSeleccionado = -1;

            if (itemId == R.id.nav_enfoque) {
                indiceSeleccionado = 0;
            } else if (itemId == R.id.nav_tareas) {
                indiceSeleccionado = 1;
            } else if (itemId == R.id.nav_estadisticas) {
                indiceSeleccionado = 2;
            }

            // Ejecuta la animacion sobre el icono correspondiente.
            if (indiceSeleccionado != -1) {
                animarIconoMenu(menuView, indiceSeleccionado);
            }

            return true;
        });

        // Fuerza la animacion en el primer elemento al iniciar la actividad.
        bottomNavigationView.setSelectedItemId(R.id.nav_enfoque);
    }

    // Define los parametros fisicos de la animacion de levitacion.
    private void configurarAnimaciones() {
        animacionBarra = ObjectAnimator.ofFloat(barraProgreso, "translationY", 0f, -30f, 0f);
        animacionBarra.setDuration(5000);
        animacionBarra.setRepeatCount(ObjectAnimator.INFINITE);

        animacionTexto = ObjectAnimator.ofFloat(textoTiempo, "translationY", 0f, -30f, 0f);
        animacionTexto.setDuration(5000);
        animacionTexto.setRepeatCount(ObjectAnimator.INFINITE);
    }

    // Gestiona la animacion de levitacion para los items del BottomNavigationView.
    private void animarIconoMenu(BottomNavigationMenuView menuView, int indiceSeleccionado) {
        // Cancela la animacion en curso si existe.
        if (animacionMenuActual != null) {
            animacionMenuActual.cancel();
        }

        // Restablece todos los iconos a su coordenada Y original.
        for (int i = 0; i < menuView.getChildCount(); i++) {
            menuView.getChildAt(i).setTranslationY(0f);
        }

        // Obtiene la vista especifica del icono tocado e inicia su levitacion.
        View vistaIcono = menuView.getChildAt(indiceSeleccionado);
        animacionMenuActual = ObjectAnimator.ofFloat(vistaIcono, "translationY", 0f, -15f, 0f);
        animacionMenuActual.setDuration(2500);
        animacionMenuActual.setRepeatCount(ObjectAnimator.INFINITE);
        animacionMenuActual.start();
    }

    // Inicia el hilo del temporizador, actualiza el icono y reanuda las animaciones.
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
                detenerYRestablecerAnimaciones();
            }
        }.start();

        estaCorriendo = true;
        botonPausar.setImageResource(android.R.drawable.ic_media_pause);

        // Condicional para iniciar o reanudar la animacion desde su punto de pausa.
        if (animacionBarra.isPaused()) {
            animacionBarra.resume();
            animacionTexto.resume();
        } else {
            animacionBarra.start();
            animacionTexto.start();
        }
    }

    // Detiene el hilo del temporizador y congela las animaciones en su estado actual.
    private void pausarCronometro() {
        if (temporizador != null) {
            temporizador.cancel();
        }
        estaCorriendo = false;
        botonPausar.setImageResource(android.R.drawable.ic_media_play);

        animacionBarra.pause();
        animacionTexto.pause();
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

    // Reinicia las variables de tiempo, actualiza la interfaz y cancela animaciones.
    private void reiniciarCronometro() {
        if (temporizador != null) {
            temporizador.cancel();
        }
        tiempoRestante = 1500000;
        estaCorriendo = false;
        botonPausar.setImageResource(android.R.drawable.ic_media_play);
        actualizarInterfaz();
        detenerYRestablecerAnimaciones();
    }

    // Calcula minutos y segundos para actualizar el texto y la barra de progreso.
    private void actualizarInterfaz() {
        int minutos = (int) (tiempoRestante / 1000) / 60;
        int segundos = (int) (tiempoRestante / 1000) % 60;

        String formato = String.format("%02d:%02d", minutos, segundos);
        textoTiempo.setText(formato);
        barraProgreso.setProgress((int) tiempoRestante);
    }

    // Finaliza los objetos animadores y devuelve las vistas a su coordenada original.
    private void detenerYRestablecerAnimaciones() {
        animacionBarra.cancel();
        animacionTexto.cancel();
        barraProgreso.setTranslationY(0f);
        textoTiempo.setTranslationY(0f);
    }
}