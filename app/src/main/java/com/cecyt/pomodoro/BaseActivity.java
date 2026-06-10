package com.cecyt.pomodoro;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;
    private ObjectAnimator animacionMenuActual;
    private int idMenuActual = 0;

    protected void configurarNavegacion(int idItemSeleccionado) {
        this.idMenuActual = idItemSeleccionado;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView == null) return;

        bottomNavigationView.getMenu().findItem(idItemSeleccionado).setChecked(true);

        bottomNavigationView.post(() -> {
            ViewGroup menuView = (ViewGroup) bottomNavigationView.getChildAt(0);
            int indice = obtenerIndicePorId(idItemSeleccionado);
            if (indice != -1) animarIconoMenu(menuView, indice);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == idItemSeleccionado) {
                return true;
            }

            Intent intent = null;

            if (itemId == R.id.nav_enfoque) {
                intent = new Intent(this, CronometroActivity.class);
            } else if (itemId == R.id.nav_tareas) {
                intent = new Intent(this, tareasActivity.class);
            } else if (itemId == R.id.nav_estadisticas) {
                intent = new Intent(this, analisisActivity.class);
            }

            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }

            return false;
        });

        animarIconosToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null && idMenuActual != 0) {
            bottomNavigationView.getMenu().findItem(idMenuActual).setChecked(true);
            bottomNavigationView.post(() -> {
                ViewGroup menuView = (ViewGroup) bottomNavigationView.getChildAt(0);
                int indice = obtenerIndicePorId(idMenuActual);
                if (indice != -1) animarIconoMenu(menuView, indice);
            });
        }
    }

    private void animarIconosToolbar() {
        View ivEscudo = findViewById(R.id.ivEscudo);
        View ivConfig = findViewById(R.id.ivConfigTiempo);
        if (ivEscudo != null) iniciarPulsoToolbar(ivEscudo, 3800, 0);
        if (ivConfig != null) iniciarPulsoToolbar(ivConfig, 3800, 700);
    }

    private void iniciarPulsoToolbar(View vista, long duracion, long delay) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(vista, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(vista, "scaleY", 1f, 1.2f, 1f);
        scaleX.setDuration(duracion);
        scaleY.setDuration(duracion);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        AccelerateDecelerateInterpolator interp = new AccelerateDecelerateInterpolator();
        scaleX.setInterpolator(interp);
        scaleY.setInterpolator(interp);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setStartDelay(delay);
        set.start();
    }

    private int obtenerIndicePorId(int id) {
        if (id == R.id.nav_enfoque) return 0;
        if (id == R.id.nav_tareas) return 1;
        if (id == R.id.nav_estadisticas) return 2;
        return -1;
    }
//ola
    private void animarIconoMenu(ViewGroup menuView, int indiceSeleccionado) {
        if (animacionMenuActual != null) animacionMenuActual.cancel();
        for (int i = 0; i < menuView.getChildCount(); i++) {
            View child = menuView.getChildAt(i);
            if (child != null) child.setTranslationY(0f);
        }
        View vistaIcono = menuView.getChildAt(indiceSeleccionado);
        if (vistaIcono != null) {
            animacionMenuActual = ObjectAnimator.ofFloat(vistaIcono, "translationY", 0f, -15f, 0f);
            animacionMenuActual.setDuration(2500);
            animacionMenuActual.setRepeatCount(ObjectAnimator.INFINITE);
            animacionMenuActual.start();
        }
    }
}