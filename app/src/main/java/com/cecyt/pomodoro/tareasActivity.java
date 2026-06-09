package com.cecyt.pomodoro;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class tareasActivity extends AppCompatActivity {

    private TextView tvTareaActiva;
    private RecyclerView recyclerTareas;
    private MaterialButton btnAnadirTarea;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        setContentView(R.layout.tareas_main);


        if (getWindow() != null && getWindow().getDecorView() != null) {
            ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

// pinche madre
        tvTareaActiva = findViewById(R.id.tvTareaActiva);
        recyclerTareas = findViewById(R.id.recyclerTareas);
        btnAnadirTarea = findViewById(R.id.btnAnadirTarea);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        if (recyclerTareas != null) {
            recyclerTareas.setLayoutManager(new LinearLayoutManager(this));
        }

        if (btnAnadirTarea != null) {
            btnAnadirTarea.setOnClickListener(v -> {
                Toast.makeText(tareasActivity.this, "¡Presionaste Añadir Tarea!", Toast.LENGTH_SHORT).show();
            });
        }

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                Toast.makeText(tareasActivity.this, "Sección: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        if (tvTareaActiva != null) {
            tvTareaActiva.setText("Ninguna");
        }
    }
}