package com.cecyt.pomodoro;

import android.os.Bundle;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class personalizacionActivity extends BaseActivity {

    private TextView tvPuntosDisponibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalizacion_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvPuntosDisponibles = findViewById(R.id.tvPuntosDisponibles);
        actualizarPuntos();

        RecyclerView rvTemas = findViewById(R.id.rvTemas);
        rvTemas.setAdapter(new TemasAdapter(this, () -> recreate()));

        findViewById(R.id.ivAtras).setOnClickListener(v -> finish());
    }

    private void actualizarPuntos() {
        float puntos = new GestorEstadisticas(this).getPuntos();
        tvPuntosDisponibles.setText(GestorEstadisticas.formatearPuntos(puntos) + " Puntos");
    }
}
