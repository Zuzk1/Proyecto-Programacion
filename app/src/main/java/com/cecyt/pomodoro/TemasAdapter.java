package com.cecyt.pomodoro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class TemasAdapter extends RecyclerView.Adapter<TemasAdapter.TemaViewHolder> {

    public interface OnTemaSeleccionadoListener {
        void onTemaCambiado();
    }

    private final Context context;
    private final GestorTemas gestorTemas;
    private final GestorEstadisticas gestorEstadisticas;
    private final OnTemaSeleccionadoListener listener;
    private final GestorTemas.Tema[] temas = GestorTemas.Tema.values();

    public TemasAdapter(Context context, OnTemaSeleccionadoListener listener) {
        this.context = context;
        this.gestorTemas = new GestorTemas(context);
        this.gestorEstadisticas = new GestorEstadisticas(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TemaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_tema, parent, false);
        return new TemaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TemaViewHolder holder, int position) {
        GestorTemas.Tema tema = temas[position];

        holder.viewFondoPreview.setBackgroundColor(context.getColor(tema.colorFondoPreview));
        holder.viewAcentoPreview.setBackgroundColor(context.getColor(tema.colorAcentoPreview));
        holder.tvNombreTema.setText(tema.nombre);

        boolean esActual = tema == gestorTemas.getTemaActual();
        boolean desbloqueado = gestorTemas.estaDesbloqueado(tema);

        if (esActual) {
            holder.tvEstadoTema.setText("Seleccionado");
            holder.cardTema.setStrokeColor(TemaUtils.resolverColor(context, R.attr.themeAcento));
        } else if (desbloqueado) {
            holder.tvEstadoTema.setText("Toca para aplicar");
            holder.cardTema.setStrokeColor(context.getColor(android.R.color.transparent));
        } else {
            holder.tvEstadoTema.setText(tema.precio + " puntos");
            holder.cardTema.setStrokeColor(context.getColor(android.R.color.transparent));
        }

        holder.itemView.setOnClickListener(v -> {
            if (esActual) return;

            if (desbloqueado) {
                gestorTemas.establecerTemaActual(tema);
                listener.onTemaCambiado();
            } else if (gestorEstadisticas.gastarPuntos(tema.precio)) {
                gestorTemas.desbloquear(tema);
                gestorTemas.establecerTemaActual(tema);
                listener.onTemaCambiado();
            } else {
                Toast.makeText(context, "No tienes suficientes puntos 🐱", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return temas.length;
    }

    static class TemaViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardTema;
        View viewFondoPreview;
        View viewAcentoPreview;
        TextView tvNombreTema;
        TextView tvEstadoTema;

        TemaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTema = itemView.findViewById(R.id.cardTema);
            viewFondoPreview = itemView.findViewById(R.id.viewFondoPreview);
            viewAcentoPreview = itemView.findViewById(R.id.viewAcentoPreview);
            tvNombreTema = itemView.findViewById(R.id.tvNombreTema);
            tvEstadoTema = itemView.findViewById(R.id.tvEstadoTema);
        }
    }
}
