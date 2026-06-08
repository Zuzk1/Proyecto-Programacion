package com.cecyt.pomodoro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class analisisActivity extends AppCompatActivity {

    private GestorEstadisticas gestor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            EdgeToEdge.enable(this);
            setContentView(R.layout.analisis_main);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // Inicializar el gestor de base de datos local
            gestor = new GestorEstadisticas(this);

            configurarBotonVolver();
            cargarDatosReales();

            // Extraer los datos de la semana y dibujar la gráfica
            int[] datosPomodorosSemana = gestor.getDatosSemana();
            dibujarGraficaDeBarras(datosPomodorosSemana);

            // Conectar el botón de Tienda a la Actividad de Personalización
            findViewById(R.id.btnTienda).setOnClickListener(v -> {
                Intent intent = new Intent(analisisActivity.this, personalizacionActivity.class);
                startActivity(intent);
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar la pantalla: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarDatosReales() {
        int minutosTotales = gestor.getMinutosTotales();
        int completados = gestor.getPomodorosCompletados();
        int fallos = gestor.getPomodorosFallados();
        int racha = gestor.getRachaActual();
        int puntos = gestor.getPuntos();

        // Formatear Tiempo Total (Horas y Minutos)
        int horas = minutosTotales / 60;
        int minutosRestantes = minutosTotales % 60;
        String textoTiempo = "Tiempo de Enfoque Total: " + horas + " hrs " + minutosRestantes + " min";
        ((TextView) findViewById(R.id.tvTiempoTotal)).setText(textoTiempo);

        // Imprimir Pomodoros y Puntos
        ((TextView) findViewById(R.id.tvPomodorosCompletados)).setText("Pomodoros Completados: " + completados);
        ((TextView) findViewById(R.id.tvPuntos)).setText(puntos + " Puntos");

        // Formatear Racha (Gris + Rojo)
        TextView tvRacha = findViewById(R.id.tvRachaActual);
        String textoRacha = "Racha Actual: " + racha + " Días (¡Pérdida en caso de Fallo!)";
        SpannableString spannableRacha = new SpannableString(textoRacha);
        int inicioRojoRacha = textoRacha.indexOf("(");

        if (inicioRojoRacha != -1) {
            spannableRacha.setSpan(new ForegroundColorSpan(Color.parseColor("#A0A0A0")), 0, inicioRojoRacha, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableRacha.setSpan(new ForegroundColorSpan(Color.parseColor("#E57373")), inicioRojoRacha, textoRacha.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvRacha.setText(spannableRacha);

        // Calcular y Formatear Tasa de Fallos
        float porcentajeFallo = 0;
        int intentosTotales = completados + fallos;
        if (intentosTotales > 0) {
            porcentajeFallo = ((float) fallos / intentosTotales) * 100;
        }

        TextView tvFallos = findViewById(R.id.tvTasaFallos);
        String stringPorcentaje = String.format("%.0f%%", porcentajeFallo);
        String textoFallos = "Tasa de Fallos: " + stringPorcentaje + " · Bloques Abandonados (" + fallos + ")";
        SpannableString spannableFallos = new SpannableString(textoFallos);
        int inicioRojoFallos = textoFallos.indexOf("·");

        if (inicioRojoFallos != -1) {
            spannableFallos.setSpan(new ForegroundColorSpan(Color.parseColor("#A0A0A0")), 0, inicioRojoFallos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableFallos.setSpan(new ForegroundColorSpan(Color.parseColor("#E57373")), inicioRojoFallos, textoFallos.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvFallos.setText(spannableFallos);
    }

    private void configurarBotonVolver() {
        ImageView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());
    }

    private void dibujarGraficaDeBarras(int[] datos) {
        // Validación de seguridad para que nunca explote si la base de datos está vacía
        if (datos == null || datos.length == 0) {
            datos = new int[]{0, 0, 0, 0, 0, 0, 0};
        }

        LinearLayout llContenedorBarras = findViewById(R.id.llContenedorBarras);
        llContenedorBarras.removeAllViews();

        String[] dias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};

        int MAX_VALOR_EJE_Y = 8;
        for (int dato : datos) {
            if (dato > MAX_VALOR_EJE_Y) MAX_VALOR_EJE_Y = dato + 2;
        }

        for (int i = 0; i < datos.length && i < dias.length; i++) {
            LinearLayout columna = new LinearLayout(this);
            columna.setOrientation(LinearLayout.VERTICAL);
            columna.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            columna.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

            LinearLayout areaBarra = new LinearLayout(this);
            areaBarra.setOrientation(LinearLayout.VERTICAL);
            areaBarra.setWeightSum(MAX_VALOR_EJE_Y);
            LinearLayout.LayoutParams paramsAreaBarra = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
            paramsAreaBarra.bottomMargin = 8;
            areaBarra.setLayoutParams(paramsAreaBarra);

            float vacio = MAX_VALOR_EJE_Y - datos[i];
            View espaciador = new View(this);
            espaciador.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, vacio));

            View barra = new View(this);
            barra.setBackgroundResource(R.drawable.fondo_barra);
            LinearLayout.LayoutParams paramsBarra = new LinearLayout.LayoutParams(
                    (int) (24 * getResources().getDisplayMetrics().density), 0, datos[i]);
            paramsBarra.gravity = Gravity.CENTER_HORIZONTAL;
            barra.setLayoutParams(paramsBarra);

            areaBarra.addView(espaciador);
            areaBarra.addView(barra);

            TextView tvDia = new TextView(this);
            tvDia.setText(dias[i]);
            tvDia.setTextColor(Color.parseColor("#A0A0A0"));
            tvDia.setTextSize(12f);
            tvDia.setGravity(Gravity.CENTER);

            columna.addView(areaBarra);
            columna.addView(tvDia);
            llContenedorBarras.addView(columna);
        }
    }
}