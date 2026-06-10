package com.cecyt.pomodoro;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import androidx.core.splashscreen.SplashScreen;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class CronometroActivity extends BaseActivity {

    private boolean mantenerSplashScreen = true;
    private boolean alertaYaLanzada = false;
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

    private ValueAnimator ralentizador;

    private Handler handlerCiclos = new Handler(Looper.getMainLooper());
    private Runnable runnableCiclos;
    private static final long INTERVALO_ANIMACION_CICLOS = 10000;

    private long startTimeBase;
    private long tiempoFinEstimado;

    @Override
    protected void onPause() {
        super.onPause();
        if (estaCorriendo && !alertaYaLanzada) {
            alertaYaLanzada = true;
            startActivity(new android.content.Intent(this, alertaActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        alertaYaLanzada = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerCiclos.removeCallbacks(runnableCiclos);
    }

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
        animarCirculos();

        botonPausar.setOnClickListener(v -> {
            if (estaCorriendo) {
                pausarCronometro();
            } else {
                iniciarCronometro();
            }
        });

        botonRenunciar.setOnClickListener(v -> mostrarDialogoAdvertencia());

        configurarNavegacion(R.id.nav_enfoque);
    }

    private void habilitarNavegacion(boolean habilitar) {
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setEnabled(habilitar);
        }
    }

    private void animarCirculos() {
        View circleTopLeft = findViewById(R.id.circleTopLeft);
        View circleBottomRight = findViewById(R.id.circleBottomRight);
        if (circleTopLeft != null) iniciarPulso(circleTopLeft, 4500, 0);
        if (circleBottomRight != null) iniciarPulso(circleBottomRight, 5500, 1500);
    }

    private void iniciarPulso(View vista, long duracion, long delay) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(vista, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(vista, "scaleY", 1f, 1.2f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(vista, "alpha", 0.13f, 0.28f, 0.13f);

        scaleX.setDuration(duracion);
        scaleY.setDuration(duracion);
        alpha.setDuration(duracion);

        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        alpha.setRepeatCount(ObjectAnimator.INFINITE);

        AccelerateDecelerateInterpolator interp = new AccelerateDecelerateInterpolator();
        scaleX.setInterpolator(interp);
        scaleY.setInterpolator(interp);
        alpha.setInterpolator(interp);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, alpha);
        set.setStartDelay(delay);
        set.start();
    }

    private void iniciarCicloAnimacionCiclos() {

        final long sessionLength = barraProgreso.getMax();
        final long elapsed = sessionLength - tiempoRestante;
        startTimeBase = SystemClock.elapsedRealtime() - elapsed;

        runnableCiclos = new Runnable() {
            @Override
            public void run() {
                long now = SystemClock.elapsedRealtime();
                long elapsedReal = now - startTimeBase;

                animarCiclosEnCascada();

                long nextTarget = ((elapsedReal / INTERVALO_ANIMACION_CICLOS) + 1) * INTERVALO_ANIMACION_CICLOS;
                long delayNext = nextTarget - elapsedReal;
                if (delayNext <= 0) delayNext = INTERVALO_ANIMACION_CICLOS;

                handlerCiclos.postDelayed(this, delayNext);
            }
        };

        long now = SystemClock.elapsedRealtime();
        long elapsedNow = now - startTimeBase;
        long nextTargetFirst = ((elapsedNow / INTERVALO_ANIMACION_CICLOS) + 1) * INTERVALO_ANIMACION_CICLOS;
        long delayFirst = nextTargetFirst - elapsedNow;
        if (delayFirst <= 0) delayFirst = INTERVALO_ANIMACION_CICLOS;
        handlerCiclos.postDelayed(runnableCiclos, delayFirst);
    }

    private void animarCiclosEnCascada() {
        TextView[] ciclos = {tvCiclo1, tvCiclo2, tvCiclo3, tvCiclo4};
        long delayBase = 0;

        for (TextView ciclo : ciclos) {
            if (ciclo == null) continue;
            long delay = delayBase;

            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(ciclo, "scaleX", 1f, 1.35f);
            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(ciclo, "scaleY", 1f, 1.35f);
            ObjectAnimator translateUp = ObjectAnimator.ofFloat(ciclo, "translationY", 0f, -18f);

            scaleUpX.setDuration(350);
            scaleUpY.setDuration(350);
            translateUp.setDuration(350);
            translateUp.setInterpolator(new OvershootInterpolator(2f));

            AnimatorSet subida = new AnimatorSet();
            subida.playTogether(scaleUpX, scaleUpY, translateUp);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ciclo, "scaleX", 1.35f, 1f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ciclo, "scaleY", 1.35f, 1f);
            ObjectAnimator translateDown = ObjectAnimator.ofFloat(ciclo, "translationY", -18f, 0f);

            scaleDownX.setDuration(400);
            scaleDownY.setDuration(400);
            translateDown.setDuration(400);

            AnimatorSet bajada = new AnimatorSet();
            bajada.playTogether(scaleDownX, scaleDownY, translateDown);

            AnimatorSet brinco = new AnimatorSet();
            brinco.playSequentially(subida, bajada);
            brinco.setStartDelay(delay);
            brinco.start();

            delayBase += 200;
        }
    }

    private void configurarAnimaciones() {
        animacionBarra = ObjectAnimator.ofFloat(barraProgreso, "translationY", 0f, -30f, 0f);
        animacionBarra.setDuration(5000);
        animacionBarra.setRepeatCount(ObjectAnimator.INFINITE);

        animacionTexto = ObjectAnimator.ofFloat(textoTiempo, "translationY", 0f, -30f, 0f);
        animacionTexto.setDuration(5000);
        animacionTexto.setRepeatCount(ObjectAnimator.INFINITE);
    }

    private void iniciarCronometro() {
        if (ralentizador != null && ralentizador.isRunning()) {
            ralentizador.cancel();
        }

        gatoAnimado.setSpeed(1f);

        handlerCiclos.removeCallbacks(runnableCiclos);
        iniciarCicloAnimacionCiclos();
        habilitarNavegacion(false);

        tiempoFinEstimado = SystemClock.elapsedRealtime() + tiempoRestante;

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
        gatoAnimado.resumeAnimation();

        if (animacionBarra.isPaused()) {
            animacionBarra.resume();
            animacionTexto.resume();
        } else {
            animacionBarra.start();
            animacionTexto.start();
        }
    }

    private void pausarCronometro() {
        handlerCiclos.removeCallbacks(runnableCiclos);
        habilitarNavegacion(true);

        if (temporizador != null) {
            temporizador.cancel();
        }

        tiempoRestante = tiempoFinEstimado - SystemClock.elapsedRealtime();
        if (tiempoRestante < 0) tiempoRestante = 0;

        estaCorriendo = false;
        botonPausar.setImageResource(android.R.drawable.ic_media_play);

        if (ralentizador != null && ralentizador.isRunning()) {
            ralentizador.cancel();
        }

        ralentizador = ValueAnimator.ofFloat(gatoAnimado.getSpeed(), 0f);
        ralentizador.setDuration(600);
        ralentizador.addUpdateListener(anim -> gatoAnimado.setSpeed((float) anim.getAnimatedValue()));
        ralentizador.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                gatoAnimado.pauseAnimation();
            }
        });
        ralentizador.start();

        animacionBarra.pause();
        animacionTexto.pause();
    }

    private void procesarFinDeCiclo() {
        handlerCiclos.removeCallbacks(runnableCiclos);
        habilitarNavegacion(true);

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
        handlerCiclos.removeCallbacks(runnableCiclos);
        habilitarNavegacion(true);

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
        textoTiempo.setText(String.format("%02d:%02d", minutos, segundos));
        barraProgreso.setProgress((int) tiempoRestante);
    }

    private void detenerYRestablecerAnimaciones() {
        animacionBarra.cancel();
        animacionTexto.cancel();
        barraProgreso.setTranslationY(0f);
        textoTiempo.setTranslationY(0f);
    }
}