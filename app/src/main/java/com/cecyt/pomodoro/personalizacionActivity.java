package com.cecyt.pomodoro;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.NumberPicker;


public class personalizacionActivity extends AppCompatActivity {

    private NumberPicker npTrabajo;
    private NumberPicker npDescansoCorto;
    private NumberPicker npDescansoLargo;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.personalizacion_main);

        // 1. Vinculación de componentes con el XML
        npTrabajo = findViewById(R.id.npTrabajo);
        npDescansoCorto = findViewById(R.id.npDescansoCorto);
        npDescansoLargo = findViewById(R.id.npDescansoLargo);
        btnGuardar = findViewById(R.id.btnGuardarConfig);

        // 2. Configuración del selector para bloques de Trabajo (1 a 60 minutos)
        npTrabajo.setMinValue(1);
        npTrabajo.setMaxValue(60);
        npTrabajo.setValue(25); // Valor por defecto (25 mins)

        // 3. Configuración del selector para Descanso Corto (1 a 20 minutos)
        npDescansoCorto.setMinValue(1);
        npDescansoCorto.setMaxValue(20);
        npDescansoCorto.setValue(5); // Valor por defecto (5 mins)

        // 4. Configuración del selector para Descanso Largo (1 a 60 minutos)
        npDescansoLargo.setMinValue(1);
        npDescansoLargo.setMaxValue(60);
        npDescansoLargo.setValue(15); // Valor por defecto (15 mins)

        // 5. Acción del botón para guardar y regresar a la pantalla del cronómetro
        btnGuardar.setOnClickListener(v -> {
            // En pasos posteriores de su proyecto aquí guardarán los valores con SharedPreferences

            // Regresar a la pantalla principal
            Intent intent = new Intent(personalizacionActivity.this, CronometroActivity.class);
            startActivity(intent);
            finish(); // Finaliza esta actividad para no saturar el historial de pantallas
        });
    }
}