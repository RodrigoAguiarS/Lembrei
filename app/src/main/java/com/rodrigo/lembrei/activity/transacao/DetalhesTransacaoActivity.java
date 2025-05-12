package com.rodrigo.lembrei.activity.transacao;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.activity.BaseActivity;
import com.rodrigo.lembrei.activity.dashboard.DashboardActivity;
import com.rodrigo.lembrei.data.Categoria;
import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;
import com.rodrigo.lembrei.db.DBHelper;
import com.rodrigo.lembrei.repository.CategoriaRepository;
import com.rodrigo.lembrei.repository.TransacaoRepository;
import com.rodrigo.lembrei.service.CategoriaService;
import com.rodrigo.lembrei.service.TransacaoService;
import com.rodrigo.lembrei.service.impl.CategoriaServiceImpl;
import com.rodrigo.lembrei.service.impl.TransacaoServiceImpl;

import java.time.format.DateTimeFormatter;

public class DetalhesTransacaoActivity extends BaseActivity {
    private TextView tvTitulo, tvValor, tvTipo, tvCategoria, tvDataVencimento,
            tvFrequencia, tvObservacoes, tvStatus;
    private MaterialButton btnMarcarPago, btnEditar, btnApagar;
    private TransacaoService transacaoService;
    private CategoriaService categoriaService;
    private Transacao transacao;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_transacao);
        configurarBottomNavigation();

        Long transacaoId = getIntent().getLongExtra("transacao_id", -1);
        if (transacaoId == -1) {
            Toast.makeText(this, "Transação não encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inicializarComponentes();
        configurarServicos();
        carregarTransacao(transacaoId);
        configurarBotoes();
    }

    private void inicializarComponentes() {
        tvTitulo = findViewById(R.id.tvTitulo);
        tvValor = findViewById(R.id.tvValor);
        tvTipo = findViewById(R.id.tvTipo);
        tvCategoria = findViewById(R.id.tvCategoria);
        tvDataVencimento = findViewById(R.id.tvDataVencimento);
        tvFrequencia = findViewById(R.id.tvFrequencia);
        tvObservacoes = findViewById(R.id.tvObservacoes);
        tvStatus = findViewById(R.id.tvStatus);
        btnMarcarPago = findViewById(R.id.btnMarcarPago);
        btnEditar = findViewById(R.id.btnEditar);
        btnApagar = findViewById(R.id.btnApagar);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configurarServicos() {
        DBHelper dbHelper = new DBHelper(this);
        TransacaoRepository transacaoRepository = new TransacaoRepository(dbHelper.getWritableDatabase());
        CategoriaRepository categoriaRepository = new CategoriaRepository(dbHelper.getWritableDatabase());

        transacaoService = new TransacaoServiceImpl(transacaoRepository);
        categoriaService = new CategoriaServiceImpl(categoriaRepository);
    }

    private void carregarTransacao(Long id) {
        transacao = transacaoService.buscarPorId(id);
        if (transacao == null) {
            Toast.makeText(this, "Transação não encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Categoria categoria = categoriaService.buscarPorId(transacao.getCategoriaId());

        tvTitulo.setText(transacao.getTitulo());
        tvValor.setText(String.format("R$ %.2f", transacao.getValor()));
        tvTipo.setText("Tipo: " + (transacao.getTipo() == TipoTransacao.PAGAR ? "A Pagar" : "A Receber"));
        tvCategoria.setText("Categoria: " + (categoria != null ? categoria.getNome() : "N/A"));
        tvDataVencimento.setText("Vencimento: " + transacao.getDataVencimento().format(dateFormatter));
        tvFrequencia.setText("Frequência: " + transacao.getFrequencia().toString());
        tvObservacoes.setText("Observações: " + (transacao.getObservacoes() != null ? transacao.getObservacoes() : ""));

        atualizarStatus();
    }

    private void atualizarStatus() {
        tvStatus.setText("Status: " + (transacao.isPago() ? "Pago" : "Pendente"));
        btnMarcarPago.setText(transacao.isPago() ? "Reabrir" : "Marcar como " +
                (transacao.getTipo() == TipoTransacao.PAGAR ? "Pago" : "Recebido"));
    }

    private void configurarBotoes() {
        btnMarcarPago.setOnClickListener(v -> {
            transacao.setPago(!transacao.isPago());
            transacaoService.marcarComoPago(transacao);
            atualizarStatus();
            Toast.makeText(this, "Status atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarTransacaoActivity.class);
            intent.putExtra("transacao_id", transacao.getId());
            startActivity(intent);
            finish();
        });

        btnApagar.setOnClickListener(v -> confirmarExclusao());
    }

    private void confirmarExclusao() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Transação")
                .setMessage("Deseja realmente excluir esta transação?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    transacaoService.deletar(transacao.getId());
                    Toast.makeText(this, "Transação excluída com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
