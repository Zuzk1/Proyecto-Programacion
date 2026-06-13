package com.cecyt.pomodoro;

import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

public class configuracionesAppActivity extends BaseActivity {

    private NumberPicker npTrabajo;
    private NumberPicker npDescansoCorto;
    private NumberPicker npDescansoLargo;
    private MaterialSwitch switchOpcionesPredeterminadas;
    private MaterialSwitch switchDescansoLargo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones_app);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        npTrabajo = findViewById(R.id.npTrabajo);
        npDescansoCorto = findViewById(R.id.npDescansoCorto);
        npDescansoLargo = findViewById(R.id.npDescansoLargo);
        switchOpcionesPredeterminadas = findViewById(R.id.switchOpcionesPredeterminadas);
        switchDescansoLargo = findViewById(R.id.switchDescansoLargo);

        npTrabajo.setMinValue(1);
        npTrabajo.setMaxValue(60);

        npDescansoCorto.setMinValue(1);
        npDescansoCorto.setMaxValue(20);

        npDescansoLargo.setMinValue(1);
        npDescansoLargo.setMaxValue(60);

        GestorConfiguracion gestorConfiguracion = new GestorConfiguracion(this);
        npTrabajo.setValue(gestorConfiguracion.getMinutosTrabajo());
        npDescansoCorto.setValue(gestorConfiguracion.getMinutosDescansoCorto());
        npDescansoLargo.setValue(gestorConfiguracion.getMinutosDescansoLargo());

        switchDescansoLargo.setChecked(gestorConfiguracion.isDescansoLargoHabilitado());
        switchDescansoLargo.setOnCheckedChangeListener((buttonView, isChecked) -> actualizarEstadoDescansoLargo());

        switchOpcionesPredeterminadas.setChecked(gestorConfiguracion.isOpcionesPredeterminadasHabilitadas());
        aplicarEstadoOpcionesPredeterminadas(switchOpcionesPredeterminadas.isChecked());
        switchOpcionesPredeterminadas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                npTrabajo.setValue(GestorConfiguracion.MINUTOS_TRABAJO_PREDETERMINADO);
                npDescansoCorto.setValue(GestorConfiguracion.MINUTOS_DESCANSO_CORTO_PREDETERMINADO);
                npDescansoLargo.setValue(GestorConfiguracion.MINUTOS_DESCANSO_LARGO_PREDETERMINADO);
            }
            aplicarEstadoOpcionesPredeterminadas(isChecked);
        });

        findViewById(R.id.btnGuardarConfig).setOnClickListener(v -> {
            gestorConfiguracion.setOpcionesPredeterminadasHabilitadas(switchOpcionesPredeterminadas.isChecked());
            gestorConfiguracion.setDescansoLargoHabilitado(switchDescansoLargo.isChecked());
            gestorConfiguracion.guardar(npTrabajo.getValue(), npDescansoCorto.getValue(), npDescansoLargo.getValue());
            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.ivAtras).setOnClickListener(v -> finish());
    }

    private void aplicarEstadoOpcionesPredeterminadas(boolean habilitadas) {
        npTrabajo.setEnabled(!habilitadas);
        npDescansoCorto.setEnabled(!habilitadas);
        actualizarEstadoDescansoLargo();
    }

    private void actualizarEstadoDescansoLargo() {
        boolean predeterminadasActivas = switchOpcionesPredeterminadas.isChecked();
        npDescansoLargo.setEnabled(switchDescansoLargo.isChecked() && !predeterminadasActivas);
    }
}
