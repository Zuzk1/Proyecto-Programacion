package com.cecyt.pomodoro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;

public class bienvenidaActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout dotsContainer;
    private MaterialButton btnComenzar;
    private SharedPreferences preferencias;

    private View[] dots;
    private final int TOTAL_PAGINAS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencias = getSharedPreferences("PreferenciasCajaFacil", MODE_PRIVATE);
        boolean primerInicio = preferencias.getBoolean("primerInicio", true);

        if (!primerInicio) {
            iniciarCronometro();
            return;
        }

        setContentView(R.layout.activity_bienvenida);

        viewPager = findViewById(R.id.viewPager);
        dotsContainer = findViewById(R.id.dotsContainer);
        btnComenzar = findViewById(R.id.btnComenzar);

        viewPager.setAdapter(new OnboardingAdapterActivity());

        configurarDots();
        actualizarDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                actualizarDots(position);
                if (position == TOTAL_PAGINAS - 1) {
                    btnComenzar.setText("COMENZAR");
                } else {
                    btnComenzar.setText("SIGUIENTE");
                }
            }
        });

        btnComenzar.setOnClickListener(v -> {
            int paginaActual = viewPager.getCurrentItem();
            if (paginaActual < TOTAL_PAGINAS - 1) {
                viewPager.setCurrentItem(paginaActual + 1);
            } else {
                preferencias.edit().putBoolean("primerInicio", false).apply();
                iniciarCronometro();
            }
        });
    }

    private void configurarDots() {
        dots = new View[TOTAL_PAGINAS];
        int margen = (int) (6 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < TOTAL_PAGINAS; i++) {
            dots[i] = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (6 * getResources().getDisplayMetrics().density),
                    (int) (6 * getResources().getDisplayMetrics().density)
            );
            params.setMargins(margen, 0, margen, 0);
            dots[i].setLayoutParams(params);
            dots[i].setBackgroundResource(R.drawable.puntos_inactivos_chiquitos);
            dotsContainer.addView(dots[i]);
        }
    }

    private void actualizarDots(int posicionActiva) {
        float density = getResources().getDisplayMetrics().density;
        int margen = (int) (6 * density);

        for (int i = 0; i < TOTAL_PAGINAS; i++) {
            if (i == posicionActiva) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        (int) (20 * density), (int) (6 * density)
                );
                params.setMargins(margen, 0, margen, 0);
                dots[i].setLayoutParams(params);
                dots[i].setBackgroundResource(R.drawable.punto_largo);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        (int) (6 * density), (int) (6 * density)
                );
                params.setMargins(margen, 0, margen, 0);
                dots[i].setLayoutParams(params);
                dots[i].setBackgroundResource(R.drawable.puntos_inactivos_chiquitos);
            }
        }
    }

    private void iniciarCronometro() {
        startActivity(new Intent(this, CronometroActivity.class));
        finish();
    }
}