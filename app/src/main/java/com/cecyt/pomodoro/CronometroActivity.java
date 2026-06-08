package com.cecyt.pomodoro;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class CronometroActivity extends AppCompatActivity {

    private boolean mantenerSplashScreen = true;
    private CountDownTimer temporizador;

    private final long TIEMPO_ENFOQUE = 1500000;
    private final long TIEMPO_DESCANSO_CORTO = 300000;
    private final long TIEMPO_DESCANSO_LARGO = 900000;

    private long tiempoRestante = TIEMPO_ENFOQUE;
    private boolean estaCorriendo = false;
    private boolean esDescanso = false;
    private int cicloActual = 1;

    private TextView textoTiempo;
    private TextView tvTitulo;
    private CircularProgressIndicator barraProgreso;
    private FloatingActionButton botonPausar;
    private FloatingActionButton botonRenunciar;
    private LottieAnimationView gatoAnimado;

    private TextView tvCiclo1, tvCiclo2, tvCiclo3, tvCiclo4;

    private ObjectAnimator animacionBarra;
    private ObjectAnimator animacionTexto;
    private ObjectAnimator animacionMenuActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cronometro_main);

        splashScreen.setKeepOnScreenCondition(() -> mantenerSplashScreen);
        new Handler(Looper.getMainLooper()).postDelayed(() -> mantenerSplashScreen = false, 850);

        textoTiempo = findViewById(R.id.tvTiempo);
        tvTitulo = findViewById(R.id.tvTitulo);
        barraProgreso = findViewById(R.id.pbCronometro);
        botonPausar = findViewById(R.id.btnPausar);
        botonRenunciar = findViewById(R.id.btnRenunciar);
        gatoAnimado = findViewById(R.id.lottieGato);

        tvCiclo1 = findViewById(R.id.tvCiclo1);
        tvCiclo2 = findViewById(R.id.tvCiclo2);
        tvCiclo3 = findViewById(R.id.tvCiclo3);
        tvCiclo4 = findViewById(R.id.tvCiclo4);

        barraProgreso.setMax((int) TIEMPO_ENFOQUE);
        barraProgreso.setProgress((int) tiempoRestante);

        configurarAnimaciones();

        botonPausar.setOnClickListener(v -> {
            if (estaCorriendo) {
                pausarCronometro();
            } else {
                iniciarCronometro();
            }
        });

        botonRenunciar.setOnClickListener(v -> mostrarDialogoAdvertencia());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            int indiceSeleccionado = -1;

            if (itemId == R.id.nav_enfoque) {
                indiceSeleccionado = 0;
                // Como ya estás en la pantalla de enfoque (Cronómetro), aquí no necesitas un Intent.

            } else if (itemId == R.id.nav_tareas) {
                indiceSeleccionado = 1;
                // Orden para ir a Tareas
                Intent intentTareas = new Intent(CronometroActivity.this, tareasActivity.class);
                startActivity(intentTareas);

            } else if (itemId == R.id.nav_estadisticas) {
                indiceSeleccionado = 2;
                // Orden para ir a Estadísticas (Análisis)
                Intent intentEstadisticas = new Intent(CronometroActivity.this, analisisActivity.class);
                startActivity(intentEstadisticas);
            }

            if (indiceSeleccionado != -1) {
                animarIconoMenu(menuView, indiceSeleccionado);
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_enfoque);
    }

    private void configurarAnimaciones() {
        animacionBarra = ObjectAnimator.ofFloat(barraProgreso, "translationY", 0f, -30f, 0f);
        animacionBarra.setDuration(5000);
        animacionBarra.setRepeatCount(ObjectAnimator.INFINITE);

        animacionTexto = ObjectAnimator.ofFloat(textoTiempo, "translationY", 0f, -30f, 0f);
        animacionTexto.setDuration(5000);
        animacionTexto.setRepeatCount(ObjectAnimator.INFINITE);
    }

    private void animarIconoMenu(BottomNavigationMenuView menuView, int indiceSeleccionado) {
        if (animacionMenuActual != null) {
            animacionMenuActual.cancel();
        }

        for (int i = 0; i < menuView.getChildCount(); i++) {
            menuView.getChildAt(i).setTranslationY(0f);
        }

        View vistaIcono = menuView.getChildAt(indiceSeleccionado);
        animacionMenuActual = ObjectAnimator.ofFloat(vistaIcono, "translationY", 0f, -15f, 0f);
        animacionMenuActual.setDuration(2500);
        animacionMenuActual.setRepeatCount(ObjectAnimator.INFINITE);
        animacionMenuActual.start();
    }

    private void iniciarCronometro() {
        temporizador = new CountDownTimer(tiempoRestante, 1000) {
            @Override
            public void onTick(long milisegundos) {
                tiempoRestante = milisegundos;
                actualizarInterfaz();
            }

            @Override
            public void onFinish() {
                procesarFinDeCiclo();
            }
        }.start();

        estaCorriendo = true;
        botonPausar.setImageResource(android.R.drawable.ic_media_pause);
        gatoAnimado.playAnimation();

        if (animacionBarra.isPaused()) {
            animacionBarra.resume();
            animacionTexto.resume();
        } else {
            animacionBarra.start();
            animacionTexto.start();
        }
    }

    private void pausarCronometro() {
        if (temporizador != null) {
            temporizador.cancel();
        }
        estaCorriendo = false;
        botonPausar.setImageResource(android.R.drawable.ic_media_play);
        gatoAnimado.pauseAnimation();

        animacionBarra.pause();
        animacionTexto.pause();
    }

    private void procesarFinDeCiclo() {
        estaCorriendo = false;
        botonPausar.setImageResource(android.R.drawable.ic_media_play);
        detenerYRestablecerAnimaciones();
        gatoAnimado.pauseAnimation();

        if (!esDescanso) {
            actualizarIndicadorVisual(cicloActual);
            cicloActual++;

            if (cicloActual > 4) {
                tiempoRestante = TIEMPO_DESCANSO_LARGO;
                tvTitulo.setText("DESCANSO LARGO");
                cicloActual = 1;
                restaurarIndicadoresGlobales();
            } else {
                tiempoRestante = TIEMPO_DESCANSO_CORTO;
                tvTitulo.setText("DESCANSO CORTO");
            }
            esDescanso = true;
            barraProgreso.setIndicatorColor(Color.parseColor("#4CAF50"));
        } else {
            tiempoRestante = TIEMPO_ENFOQUE;
            tvTitulo.setText("SESIÓN DE ENFOQUE");
            esDescanso = false;
            barraProgreso.setIndicatorColor(Color.parseColor("#FFFFFF"));
            configurarIndicadorActual(cicloActual);
        }

        barraProgreso.setMax((int) tiempoRestante);
        actualizarInterfaz();
    }

    private void actualizarIndicadorVisual(int ciclo) {
        TextView tvObjetivo = obtenerTextViewPorCiclo(ciclo);
        if (tvObjetivo != null) {
            tvObjetivo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#555555")));
            tvObjetivo.setTextColor(Color.parseColor("#A0A0A0"));
        }
    }

    private void configurarIndicadorActual(int ciclo) {
        TextView tvObjetivo = obtenerTextViewPorCiclo(ciclo);
        if (tvObjetivo != null) {
            tvObjetivo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            tvObjetivo.setTextColor(Color.parseColor("#000000"));
        }
    }

    private TextView obtenerTextViewPorCiclo(int ciclo) {
        switch (ciclo) {
            case 1: return tvCiclo1;
            case 2: return tvCiclo2;
            case 3: return tvCiclo3;
            case 4: return tvCiclo4;
            default: return null;
        }
    }

    private void restaurarIndicadoresGlobales() {
        int colorOscuro = Color.parseColor("#333333");
        int colorBlanco = Color.parseColor("#FFFFFF");

        tvCiclo1.setBackgroundTintList(ColorStateList.valueOf(colorOscuro));
        tvCiclo1.setTextColor(colorBlanco);
        tvCiclo2.setBackgroundTintList(ColorStateList.valueOf(colorOscuro));
        tvCiclo2.setTextColor(colorBlanco);
        tvCiclo3.setBackgroundTintList(ColorStateList.valueOf(colorOscuro));
        tvCiclo3.setTextColor(colorBlanco);
        tvCiclo4.setBackgroundTintList(ColorStateList.valueOf(colorOscuro));
        tvCiclo4.setTextColor(colorBlanco);
    }

    private void mostrarDialogoAdvertencia() {
        new AlertDialog.Builder(this)
                .setTitle("Advertencia")
                .setMessage("¿Estás seguro de renunciar a la sesión? Se aplicará el castigo correspondiente.")
                .setPositiveButton("Renunciar", (dialog, which) -> reiniciarCronometro())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void reiniciarCronometro() {
        if (temporizador != null) {
            temporizador.cancel();
        }
        tiempoRestante = TIEMPO_ENFOQUE;
        estaCorriendo = false;
        esDescanso = false;
        cicloActual = 1;

        botonPausar.setImageResource(android.R.drawable.ic_media_play);
        gatoAnimado.pauseAnimation();
        gatoAnimado.setProgress(0);

        tvTitulo.setText("SESIÓN DE ENFOQUE");
        barraProgreso.setIndicatorColor(Color.parseColor("#FFFFFF"));

        barraProgreso.setMax((int) TIEMPO_ENFOQUE);
        restaurarIndicadoresGlobales();
        configurarIndicadorActual(cicloActual);

        actualizarInterfaz();
        detenerYRestablecerAnimaciones();
    }

    private void actualizarInterfaz() {
        int minutos = (int) (tiempoRestante / 1000) / 60;
        int segundos = (int) (tiempoRestante / 1000) % 60;

        String formato = String.format("%02d:%02d", minutos, segundos);
        textoTiempo.setText(formato);
        barraProgreso.setProgress((int) tiempoRestante);
    }

    private void detenerYRestablecerAnimaciones() {
        animacionBarra.cancel();
        animacionTexto.cancel();
        barraProgreso.setTranslationY(0f);
        textoTiempo.setTranslationY(0f);
    }
}