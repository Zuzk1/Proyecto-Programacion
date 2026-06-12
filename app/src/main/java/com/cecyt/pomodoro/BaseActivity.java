package com.cecyt.pomodoro;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;
    private ObjectAnimator animacionMenuActual;
    private View vistaIconoMenuActual;
    private int idMenuActual = 0;

    protected void configurarNavegacion(int idItemSeleccionado) {
        this.idMenuActual = idItemSeleccionado;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView == null) return;

        aplicarInsetsBarrasSistema();

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
                final Intent intentDestino = intent;
                detenerAnimacionIconoMenuSuave();

                bottomNavigationView.postDelayed(() -> {
                    intentDestino.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intentDestino);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }, 180);
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

    private void aplicarInsetsBarrasSistema() {
        View contenido = findViewById(android.R.id.content);
        View appBar = findViewById(R.id.appBar);

        ViewCompat.setOnApplyWindowInsetsListener(contenido, (vista, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            if (appBar != null) {
                appBar.setPadding(appBar.getPaddingLeft(), barras.top, appBar.getPaddingRight(), appBar.getPaddingBottom());
            }

            bottomNavigationView.setPadding(bottomNavigationView.getPaddingLeft(), bottomNavigationView.getPaddingTop(),
                    bottomNavigationView.getPaddingRight(), barras.bottom);

            return insets;
        });
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

    private void animarIconoMenu(ViewGroup menuView, int indiceSeleccionado) {
        detenerAnimacionIconoMenuSuave();

        View vistaIcono = menuView.getChildAt(indiceSeleccionado);
        if (vistaIcono != null) {
            iniciarAnimacionIconoMenu(vistaIcono);
        }
    }

    private void iniciarAnimacionIconoMenu(View vistaIcono) {
        vistaIconoMenuActual = vistaIcono;
        animacionMenuActual = ObjectAnimator.ofFloat(vistaIcono, "translationY", 0f, -15f, 0f);
        animacionMenuActual.setDuration(2500);
        animacionMenuActual.setRepeatCount(ObjectAnimator.INFINITE);
        animacionMenuActual.start();
    }

    private void detenerAnimacionIconoMenuSuave() {
        if (animacionMenuActual != null) {
            animacionMenuActual.cancel();
            animacionMenuActual = null;
        }

        if (vistaIconoMenuActual != null) {
            ObjectAnimator detener = ObjectAnimator.ofFloat(vistaIconoMenuActual, "translationY", vistaIconoMenuActual.getTranslationY(), 0f);
            detener.setDuration(300);
            detener.setInterpolator(new AccelerateDecelerateInterpolator());
            detener.start();
        }
    }

    protected void pausarAnimacionMenuSuave() {
        detenerAnimacionIconoMenuSuave();
    }

    protected void reanudarAnimacionMenuSuave() {
        if (vistaIconoMenuActual != null) {
            iniciarAnimacionIconoMenu(vistaIconoMenuActual);
        }
    }
}