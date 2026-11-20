package com.example.tecnicapomodoro.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tecnicapomodoro.R;

import java.util.ArrayList;
import java.util.List;

/** Adapter genérico: sirve para asignaturas, tareas, sesiones y el panel. */
public class ListaAdapter<T> extends RecyclerView.Adapter<ListaAdapter.Fila> {

    public interface Texto<T> {
        String de(T item);
    }

    public interface Accion<T> {
        void en(T item);
    }

    private List<T> items = new ArrayList<>();
    private Texto<T> titulo;
    private Texto<T> detalle;
    private Texto<T> color;
    private Accion<T> alTocar;
    private Accion<T> alMantener;

    public ListaAdapter(Texto<T> titulo, Texto<T> detalle, Texto<T> color) {
        this.titulo = titulo;
        this.detalle = detalle;
        this.color = color;
    }

    public void alTocar(Accion<T> accion) {
        alTocar = accion;
    }

    public void alMantener(Accion<T> accion) {
        alMantener = accion;
    }

    public void setItems(List<T> nuevos) {
        items = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fila onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista, parent, false);
        return new Fila(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Fila holder, int position) {
        T item = items.get(position);
        holder.tvTitulo.setText(titulo.de(item));
        holder.tvDetalle.setText(detalle.de(item));
        holder.vColor.setBackgroundColor(UiUtil.color(color != null ? color.de(item) : null));
        holder.itemView.setOnClickListener(v -> {
            if (alTocar != null) alTocar.en(item);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (alMantener == null) return false;
            alMantener.en(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class Fila extends RecyclerView.ViewHolder {
        private View vColor;
        private TextView tvTitulo;
        private TextView tvDetalle;

        public Fila(@NonNull View v) {
            super(v);
            vColor = v.findViewById(R.id.vColor);
            tvTitulo = v.findViewById(R.id.tvTitulo);
            tvDetalle = v.findViewById(R.id.tvDetalle);
        }
    }
}