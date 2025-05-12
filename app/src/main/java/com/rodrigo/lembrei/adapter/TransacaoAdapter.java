package com.rodrigo.lembrei.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.data.Categoria;
import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;
import com.rodrigo.lembrei.service.CategoriaService;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransacaoAdapter extends RecyclerView.Adapter<TransacaoAdapter.TransacaoViewHolder> {
    private List<Transacao> transacoes;
    private final CategoriaService categoriaService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final OnTransacaoClickListener listener;

    public interface OnTransacaoClickListener {
        void onTransacaoClick(Transacao transacao);
    }

    public TransacaoAdapter(List<Transacao> transacoes, CategoriaService categoriaService,
                            OnTransacaoClickListener listener) {
        this.transacoes = transacoes;
        this.categoriaService = categoriaService;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transacao, parent, false);
        return new TransacaoViewHolder(itemView);
    }

    public void adicionarItens(List<Transacao> novosItens) {
        int posicaoInicio = transacoes.size();
        transacoes.addAll(novosItens);
        notifyItemRangeInserted(posicaoInicio, novosItens.size());
    }

    public void limparLista() {
        transacoes.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull TransacaoViewHolder holder, int position) {
        Transacao transacao = transacoes.get(position);
        holder.bind(transacao);
    }

    @Override
    public int getItemCount() {
        return transacoes.size();
    }

    public void atualizarLista(List<Transacao> novasTransacoes) {
        this.transacoes = novasTransacoes;
        notifyDataSetChanged();
    }

    class TransacaoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitulo;
        private final TextView tvCategoria;
        private final TextView tvVencimento;
        private final TextView tvValor;
        private final TextView tvTipo;

        public TransacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvVencimento = itemView.findViewById(R.id.tvVencimento);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvTipo = itemView.findViewById(R.id.tvTipo);
        }

        public void bind(Transacao transacao) {
            tvTitulo.setText(transacao.getTitulo());

            tvTipo.setText(transacao.getTipo() == TipoTransacao.PAGAR ? "Pagar" : "Receber");

            Categoria categoria = categoriaService.buscarPorId(transacao.getCategoriaId());
            tvCategoria.setText(categoria != null ? categoria.getNome() : "");

            tvVencimento.setText("Vencimento: " + transacao.getDataVencimento().format(dateFormatter));

            String valorFormatado = String.format("R$ %.2f", transacao.getValor());
            tvValor.setText(valorFormatado);

            itemView.setOnClickListener(v -> listener.onTransacaoClick(transacao));
        }
    }
}
