package com.cecyt.pomodoro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class bienvenidaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferencias = getSharedPreferences("PreferenciasCajaFacil", MODE_PRIVATE);
        boolean primerInicio = preferencias.getBoolean("primerInicio", true);

        if (!primerInicio) {
            iniciarCronometro();
            return;
        }

        setContentView(R.layout.activity_bienvenida);

        Button btnComenzar = findViewById(R.id.btnComenzar);
        if (btnComenzar != null) {
            btnComenzar.setOnClickListener(v -> {
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putBoolean("primerInicio", false);
                editor.apply();
                iniciarCronometro();
            });
        }
    }

    private void iniciarCronometro() {
        Intent intent = new Intent(this, CronometroActivity.class);
        startActivity(intent);
        finish();
    }
}