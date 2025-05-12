package com.rodrigo.lembrei.activity.transacao;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.activity.BaseActivity;
import com.rodrigo.lembrei.adapter.TransacaoAdapter;
import com.rodrigo.lembrei.data.Transacao;
import com.rodrigo.lembrei.db.DBHelper;
import com.rodrigo.lembrei.repository.CategoriaRepository;
import com.rodrigo.lembrei.repository.TransacaoRepository;
import com.rodrigo.lembrei.service.CategoriaService;
import com.rodrigo.lembrei.service.TransacaoService;
import com.rodrigo.lembrei.service.impl.CategoriaServiceImpl;
import com.rodrigo.lembrei.service.impl.TransacaoServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaTransacoesActivity extends BaseActivity implements TransacaoAdapter.OnTransacaoClickListener {
    private RecyclerView recyclerView;
    private TransacaoAdapter adapter;
    private TransacaoService transacaoService;
    private CategoriaService categoriaService;
    private TextInputEditText edtBusca;
    private List<Transacao> todasTransacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_transacoes);
        configurarBottomNavigation();

        inicializarComponentes();
        configurarServicosERecyclerView();
        configurarCampoBusca();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarTransacoes();
    }

    private void inicializarComponentes() {
        recyclerView = findViewById(R.id.recyclerViewTransacoes);
        FloatingActionButton fab = findViewById(R.id.fabAdicionarTransacao);
        fab.setOnClickListener(v -> abrirCadastroTransacao());
    }

    private void configurarServicosERecyclerView() {
        DBHelper dbHelper = new DBHelper(this);
        TransacaoRepository transacaoRepository = new TransacaoRepository(dbHelper.getWritableDatabase());
        CategoriaRepository categoriaRepository = new CategoriaRepository(dbHelper.getWritableDatabase());

        transacaoService = new TransacaoServiceImpl(transacaoRepository);
        categoriaService = new CategoriaServiceImpl(categoriaRepository);

        adapter = new TransacaoAdapter(new ArrayList<>(), categoriaService, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void abrirCadastroTransacao() {
        Intent intent = new Intent(this, CadastroTransacaoActivity.class);
        startActivity(intent);
    }

    private void carregarTransacoes() {
        todasTransacoes = transacaoService.listarPendentes();
        adapter.atualizarLista(todasTransacoes);
    }

    private void configurarCampoBusca() {
        edtBusca = findViewById(R.id.edtBusca);
        edtBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filtrarTransacoes();
            }
        });
    }

    private void filtrarTransacoes() {
        String termoBusca = edtBusca.getText().toString().toLowerCase();

        List<Transacao> transacoesFiltradas = todasTransacoes.stream()
                .filter(transacao ->
                        transacao.getTitulo().toLowerCase().contains(termoBusca))
                .collect(Collectors.toList());

        adapter.atualizarLista(transacoesFiltradas);
    }

    @Override
    public void onTransacaoClick(Transacao transacao) {
        Intent intent = new Intent(this, DetalhesTransacaoActivity.class);
        intent.putExtra("transacao_id", transacao.getId());
        startActivity(intent);
    }
}
