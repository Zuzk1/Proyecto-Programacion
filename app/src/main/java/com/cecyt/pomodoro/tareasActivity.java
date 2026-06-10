package com.cecyt.pomodoro;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class tareasActivity extends BaseActivity {

    private TextView tvTareaActiva;
    private TextView tvResumen;
    private TextView tvTotalTareas;
    private RecyclerView recyclerTareas;
    private MaterialButton btnAnadirTarea;
    private MaterialButton btnCancelarRedaccion;
    private MaterialButton btnGuardarRedaccion;
    private ConstraintLayout cardRedactarTarea;
    private EditText etNuevaTareaInput;

    private SharedPreferences preferencias;
    private static final String PREFS_NAME = "PreferenciasCajaFacil";

    private ArrayList<String> listaTareas;
    private TareasAdapter adaptador;

    private boolean esEdicion = false;
    private int posicionEdicion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tareas_main);

        preferencias = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        tvTareaActiva = findViewById(R.id.tvTareaActiva);
        tvResumen = findViewById(R.id.tvResumen);
        tvTotalTareas = findViewById(R.id.tvTotalTareas);
        recyclerTareas = findViewById(R.id.recyclerTareas);
        btnAnadirTarea = findViewById(R.id.btnAnadirTarea);
        btnCancelarRedaccion = findViewById(R.id.btnCancelarRedaccion);
        btnGuardarRedaccion = findViewById(R.id.btnGuardarRedaccion);
        etNuevaTareaInput = findViewById(R.id.etNuevaTareaInput);
        cardRedactarTarea = findViewById(R.id.cardRedactarTarea);

        listaTareas = new ArrayList<>();

        recyclerTareas.setLayoutManager(new LinearLayoutManager(this));

        adaptador = new TareasAdapter(listaTareas, posicion -> mostrarOpcionesTarea(posicion));
        recyclerTareas.setAdapter(adaptador);

        cargarDatos();

        btnAnadirTarea.setOnClickListener(v -> {
            esEdicion = false;
            cardRedactarTarea.setVisibility(View.VISIBLE);
            etNuevaTareaInput.requestFocus();

            mostrarTecladoVirtual();
        });

        btnCancelarRedaccion.setOnClickListener(v -> ocultarPanelRedaccion());

        btnGuardarRedaccion.setOnClickListener(v -> {
            String tarea = etNuevaTareaInput.getText().toString().trim();

            if (tarea.isEmpty()) {
                Toast.makeText(this, "Escribe una tarea", Toast.LENGTH_SHORT).show();
                return;
            }

            if (esEdicion) {
                editarTarea(posicionEdicion, tarea);
                Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
            } else {
                agregarTarea(tarea);
                Toast.makeText(this, "Tarea agregada", Toast.LENGTH_SHORT).show();
            }

            ocultarPanelRedaccion();
        });

        configurarNavegacion(R.id.nav_tareas);
    }

    private void mostrarOpcionesTarea(int posicion) {
        String tareaSeleccionada = listaTareas.get(posicion);
        CharSequence[] opciones = {"Fijar como Tarea Activa", "Editar Texto", "Eliminar Tarea"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Qué deseas hacer con esta tarea?");
        builder.setItems(opciones, (dialog, item) -> {
            if (item == 0) {
                guardarTareaActiva(tareaSeleccionada);
                Toast.makeText(this, "Tarea fijada arriba", Toast.LENGTH_SHORT).show();
            } else if (item == 1) {
                esEdicion = true;
                posicionEdicion = posicion;
                cardRedactarTarea.setVisibility(View.VISIBLE);
                etNuevaTareaInput.setText(tareaSeleccionada);
                etNuevaTareaInput.requestFocus();

                mostrarTecladoVirtual();
            } else if (item == 2) {
                eliminarTarea(posicion);
                Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void agregarTarea(String tarea) {
        listaTareas.add(tarea);
        adaptador.notifyItemInserted(listaTareas.size() - 1);
        guardarTareaActiva(tarea);
        actualizarResumen();
        guardarDatos();
    }

    private void editarTarea(int posicion, String nuevoTexto) {
        if (posicion >= 0 && posicion < listaTareas.size()) {
            String activaActual = preferencias.getString("tarea_activa_actual", "Ninguna");
            if (activaActual.equals(listaTareas.get(posicion))) {
                guardarTareaActiva(nuevoTexto);
            }

            listaTareas.set(posicion, nuevoTexto);
            adaptador.notifyItemChanged(posicion);
            guardarDatos();
        }
    }

    private void eliminarTarea(int posicion) {
        if (posicion >= 0 && posicion < listaTareas.size()) {
            String tareaAEliminar = listaTareas.get(posicion);
            listaTareas.remove(posicion);
            adaptador.notifyItemRemoved(posicion);
            adaptador.notifyItemRangeChanged(posicion, listaTareas.size());

            String activaActual = preferencias.getString("tarea_activa_actual", "Ninguna");
            if (listaTareas.isEmpty()) {
                guardarTareaActiva("Ninguna");
            } else if (activaActual.equals(tareaAEliminar)) {
                guardarTareaActiva(listaTareas.get(listaTareas.size() - 1));
            }

            actualizarResumen();
            guardarDatos();
        }
    }

    private void actualizarResumen() {
        int total = listaTareas.size();
        if (tvResumen != null) {
            tvResumen.setText(total + " tareas registradas");
        }
        if (tvTotalTareas != null) {
            tvTotalTareas.setText(String.valueOf(total));
        }
    }

    private void ocultarPanelRedaccion() {

        ocultarTecladoVirtual();

        if (cardRedactarTarea != null) {
            cardRedactarTarea.setVisibility(View.GONE);
        }
        if (etNuevaTareaInput != null) {
            etNuevaTareaInput.setText("");
        }
        esEdicion = false;
        posicionEdicion = -1;
    }


    private void ocultarTecladoVirtual() {
        View vistaFocus = this.getCurrentFocus();
        if (vistaFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(vistaFocus.getWindowToken(), 0);
            }
        }
    }

    private void mostrarTecladoVirtual() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void guardarTareaActiva(String tarea) {
        preferencias.edit().putString("tarea_activa_actual", tarea).apply();
        if (tvTareaActiva != null) {
            tvTareaActiva.setText(tarea);
        }
    }

    private void guardarDatos() {
        SharedPreferences.Editor editor = preferencias.edit();

        int cantidadAnterior = preferencias.getInt("cantidad_tareas", 0);
        for (int i = 0; i < cantidadAnterior; i++) {
            editor.remove("tarea_" + i);
        }

        editor.putInt("cantidad_tareas", listaTareas.size());

        for (int i = 0; i < listaTareas.size(); i++) {
            editor.putString("tarea_" + i, listaTareas.get(i));
        }

        editor.apply();
    }

    private void cargarDatos() {
        String tareaActiva = preferencias.getString("tarea_activa_actual", "Ninguna");
        if (tvTareaActiva != null) {
            tvTareaActiva.setText(tareaActiva);
        }

        listaTareas.clear();
        int cantidad = preferencias.getInt("cantidad_tareas", 0);

        for (int i = 0; i < cantidad; i++) {
            String tarea = preferencias.getString("tarea_" + i, null);
            if (tarea != null) {
                listaTareas.add(tarea);
            }
        }

        adaptador.notifyDataSetChanged();
        actualizarResumen();
    }
}