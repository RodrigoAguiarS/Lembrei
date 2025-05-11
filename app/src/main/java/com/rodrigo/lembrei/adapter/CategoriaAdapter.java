package com.rodrigo.lembrei.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.data.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {
    private List<Categoria> categorias;
    private OnItemClickListener listener;
    private OnEditClickListener editListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(Categoria categoria);
    }

    public interface OnEditClickListener {
        void onEditClick(Categoria categoria);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Categoria categoria);
    }

    public CategoriaAdapter() {
        this.categorias = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editListener = listener;
    }
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);
        holder.bind(categoria);
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public void atualizarLista(List<Categoria> novaLista) {
        this.categorias = novaLista;
        notifyDataSetChanged();
    }

    class CategoriaViewHolder extends RecyclerView.ViewHolder {
        private TextView txtIcone;
        private TextView txtNomeCategoria;
        private View viewCorCategoria;
        private ImageButton btnEditarCategoria;
        private ImageButton btnDeletarCategoria;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIcone = itemView.findViewById(R.id.txtIcone);
            txtNomeCategoria = itemView.findViewById(R.id.txtNomeCategoria);
            viewCorCategoria = itemView.findViewById(R.id.viewCorCategoria);
            btnEditarCategoria = itemView.findViewById(R.id.btnEditarCategoria);
            btnDeletarCategoria = itemView.findViewById(R.id.btnDeletarCategoria);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(categorias.get(position));
                }
            });

            btnEditarCategoria.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && editListener != null) {
                    editListener.onEditClick(categorias.get(position));
                }
            });

            btnDeletarCategoria.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(categorias.get(position));
                }
            });
        }

        public void bind(Categoria categoria) {
            txtIcone.setText(categoria.getIcone());
            txtIcone.setTextColor(Color.parseColor(categoria.getCorHex()));
            txtNomeCategoria.setText(categoria.getNome());
            viewCorCategoria.setBackgroundColor(Color.parseColor(categoria.getCorHex()));
        }
    }
}