package com.cecyt.pomodoro;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class alertaActivity extends AppCompatActivity {

    private CountDownTimer contador;
    private Button btnSilenciar;
    private View viewGlowTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setFinishOnTouchOutside(false);

        setContentView(R.layout.alerta_main);

        btnSilenciar = findViewById(R.id.btnSilenciar);
        viewGlowTop = findViewById(R.id.viewGlowTop);

        animarAura();

        btnSilenciar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                iniciarContador();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (contador != null) {
                    contador.cancel();
                    contador = null;
                }
                btnSilenciar.setText("MANTÉN 5s");
            }
            return true;
        });
    }

    private void animarAura() {
        ObjectAnimator animacionEscalaX = ObjectAnimator.ofFloat(viewGlowTop, "scaleX", 1.0f, 1.15f);
        ObjectAnimator animacionEscalaY = ObjectAnimator.ofFloat(viewGlowTop, "scaleY", 1.0f, 1.15f);
        ObjectAnimator animacionAlfa = ObjectAnimator.ofFloat(viewGlowTop, "alpha", 0.6f, 0.9f);

        animacionEscalaX.setDuration(1000);
        animacionEscalaX.setRepeatCount(ObjectAnimator.INFINITE);
        animacionEscalaX.setRepeatMode(ObjectAnimator.REVERSE);

        animacionEscalaY.setDuration(1000);
        animacionEscalaY.setRepeatCount(ObjectAnimator.INFINITE);
        animacionEscalaY.setRepeatMode(ObjectAnimator.REVERSE);

        animacionAlfa.setDuration(1000);
        animacionAlfa.setRepeatCount(ObjectAnimator.INFINITE);
        animacionAlfa.setRepeatMode(ObjectAnimator.REVERSE);

        animacionEscalaX.start();
        animacionEscalaY.start();
        animacionAlfa.start();
    }

    private void iniciarContador() {
        if (contador != null) {
            contador.cancel();
        }

        contador = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long segundosRestantes = millisUntilFinished / 1000;
                btnSilenciar.setText("CANCELANDO... " + segundosRestantes + "s");
            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contador != null) {
            contador.cancel();
            contador = null;
        }
    }
}