package com.cecyt.pomodoro;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class alertaActivity extends AppCompatActivity {
    private CountDownTimer contador;
    private Button btnSilenciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alerta_main);

        btnSilenciar = findViewById(R.id.btnSilenciar);

        btnSilenciar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                iniciarContador();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (contador != null) contador.cancel();
            }
            return true;
        });
    }

    private void iniciarContador() {
        contador = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }
}