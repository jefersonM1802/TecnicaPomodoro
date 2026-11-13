package com.example.tecnicapomodoro.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tecnicapomodoro.R;
import com.example.tecnicapomodoro.model.Estudiante;

import java.util.List;

public class EstudianteAdapter extends RecyclerView.Adapter<EstudianteAdapter.ViewHolder> {
    private final List<Estudiante> estudiantes;

    public EstudianteAdapter(List<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_estudiante, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Estudiante estudiante = estudiantes.get(position);
        holder.tvNombre.setText(estudiante.getNombre());
        holder.tvCorreo.setText(estudiante.getCorreo());
    }

    @Override
    public int getItemCount() {
        return estudiantes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvCorreo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEstudiante);
            tvCorreo = itemView.findViewById(R.id.tvCorreoEstudiante);
        }
    }
}
