package com.cecyt.pomodoro;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

public class ajustesGeneralesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes_generales);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.ivAtras).setOnClickListener(v -> finish());

        findViewById(R.id.btnAjustesNotificaciones).setOnClickListener(v -> {
            Intent intentAjustes = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intentAjustes.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                intentAjustes = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intentAjustes.setData(Uri.parse("package:" + getPackageName()));
            }
            startActivity(intentAjustes);
        });

        MaterialSwitch switchAlertaDistraccion = findViewById(R.id.switchAlertaDistraccion);
        GestorConfiguracion gestorConfiguracion = new GestorConfiguracion(this);
        switchAlertaDistraccion.setChecked(gestorConfiguracion.isAlertasDistraccionHabilitadas());
        switchAlertaDistraccion.setOnCheckedChangeListener((buttonView, isChecked) ->
                gestorConfiguracion.setAlertasDistraccionHabilitadas(isChecked));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (redirigidoAAlerta) {
            return;
        }
        actualizarEstadoNotificaciones();
    }

    private void actualizarEstadoNotificaciones() {
        boolean notificacionesActivas = NotificationManagerCompat.from(this).areNotificationsEnabled();
        android.widget.TextView tvEstado = findViewById(R.id.tvEstadoNotificaciones);
        tvEstado.setText(notificacionesActivas
                ? "Las notificaciones están activadas"
                : "Las notificaciones están desactivadas, actívalas si quieres recibir avisos");
    }
}
