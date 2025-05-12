package com.rodrigo.lembrei.activity.transacao;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.activity.BaseActivity;
import com.rodrigo.lembrei.adapter.TransacaoAdapter;
import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;
import com.rodrigo.lembrei.data.TransacaoFiltro;
import com.rodrigo.lembrei.db.DBHelper;
import com.rodrigo.lembrei.repository.CategoriaRepository;
import com.rodrigo.lembrei.repository.TransacaoRepository;
import com.rodrigo.lembrei.service.CategoriaService;
import com.rodrigo.lembrei.service.TransacaoService;
import com.rodrigo.lembrei.service.impl.CategoriaServiceImpl;
import com.rodrigo.lembrei.service.impl.TransacaoServiceImpl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaTransacoesActivity extends BaseActivity implements TransacaoAdapter.OnTransacaoClickListener {

    private static final int ITENS_POR_PAGINA = 6;
    private int paginaAtual = 1;
    private boolean carregandoMais = false;
    private boolean temMaisItens = true;
    private TransacaoFiltro filtroAtual;
    private RecyclerView recyclerView;
    private TransacaoAdapter adapter;
    private TransacaoService transacaoService;
    private CategoriaService categoriaService;
    private AutoCompleteTextView spinnerTipo;
    private AutoCompleteTextView spinnerStatus;
    private TextInputEditText editValorMin;
    private TextInputEditText editValorMax;
    private TextInputEditText edtDataInicial;
    private TextInputEditText edtDataFinal;
    private TextInputEditText edtBusca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_transacoes);

        configurarBottomNavigation();
        filtroAtual = new TransacaoFiltro();

        inicializarComponentes();
        configurarServicosERecyclerView();
        configurarFiltros();
        configurarScrollListener();
        configurarCampoBusca();
        carregarTransacoes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarTransacoes();
    }

    private void inicializarComponentes() {
        recyclerView = findViewById(R.id.recyclerViewTransacoes);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        editValorMin = findViewById(R.id.editValorMin);
        editValorMax = findViewById(R.id.editValorMax);
        edtDataInicial = findViewById(R.id.edtDataInicial);
        edtDataFinal = findViewById(R.id.edtDataFinal);
        edtBusca = findViewById(R.id.edtBusca);
        FloatingActionButton fab = findViewById(R.id.fabAdicionarTransacao);
        fab.setOnClickListener(v -> abrirCadastroTransacao());

        findViewById(R.id.btnLimparFiltros).setOnClickListener(v -> limparFiltros());
        findViewById(R.id.btnAplicarFiltros).setOnClickListener(v -> aplicarFiltros());
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

    private void configurarFiltros() {
        String[] tiposTransacao = {"Todos", "Pagar", "Receber"};
        String[] statusTransacao = {"Todos", "Pendentes", "Pagos", "Atrasados"};

        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, tiposTransacao);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, statusTransacao);

        spinnerTipo.setAdapter(tipoAdapter);
        spinnerStatus.setAdapter(statusAdapter);

        configurarDatePicker(edtDataInicial);
        configurarDatePicker(edtDataFinal);
    }

    private void configurarDatePicker(TextInputEditText editText) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String data = String.format(Locale.getDefault(), "%02d/%02d/%d",
                                dayOfMonth, month + 1, year);
                        editText.setText(data);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void configurarScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItens = layoutManager.getItemCount();
                int ultimoItemVisivel = layoutManager.findLastVisibleItemPosition();

                if (!carregandoMais && temMaisItens && ultimoItemVisivel >= totalItens - 5) {
                    carregarMaisItens();
                }
            }
        });
    }

    private void configurarCampoBusca() {
        edtBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filtroAtual.setTextoBusca(s.toString());
                reiniciarListagem();
            }
        });
    }

    private void aplicarFiltros() {
        filtroAtual = new TransacaoFiltro();

        if (spinnerTipo.getText().toString().equals("Pagar")) {
            filtroAtual.setTipo(TipoTransacao.PAGAR);
        } else if (spinnerTipo.getText().toString().equals("Receber")) {
            filtroAtual.setTipo(TipoTransacao.RECEBER);
        }

        String statusSelecionado = spinnerStatus.getText().toString();
        switch (statusSelecionado) {
            case "Pendentes":
                filtroAtual.setPago(false);
                break;
            case "Pagos":
                filtroAtual.setPago(true);
                break;
            case "Atrasados":
                filtroAtual.setAtrasado(true);
                break;
        }

        try {
            if (!editValorMin.getText().toString().isEmpty()) {
                filtroAtual.setValorMinimo(new BigDecimal(editValorMin.getText().toString()));
            }
            if (!editValorMax.getText().toString().isEmpty()) {
                filtroAtual.setValorMaximo(new BigDecimal(editValorMax.getText().toString()));
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            if (!edtDataInicial.getText().toString().isEmpty()) {
                Date dataInicial = sdf.parse(edtDataInicial.getText().toString());
                filtroAtual.setDataInicial(dataInicial.toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate());
            }
            if (!edtDataFinal.getText().toString().isEmpty()) {
                Date dataFinal = sdf.parse(edtDataFinal.getText().toString());
                filtroAtual.setDataFinal(dataFinal.toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate());
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Data inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        filtroAtual.setTextoBusca(edtBusca.getText().toString());

        reiniciarListagem();
    }

    private void reiniciarListagem() {
        paginaAtual = 1;
        temMaisItens = true;
        carregarTransacoes();
    }

    private void carregarMaisItens() {
        carregandoMais = true;
        paginaAtual++;
        List<Transacao> novasTransacoes = transacaoService.buscarTransacoesPaginadas(
                filtroAtual, paginaAtual, ITENS_POR_PAGINA);

        if (novasTransacoes.isEmpty()) {
            temMaisItens = false;
        } else {
            adapter.adicionarItens(novasTransacoes);
        }
        carregandoMais = false;
    }

    private void carregarTransacoes() {
        List<Transacao> transacoes = transacaoService.buscarTransacoesPaginadas(
                filtroAtual, paginaAtual, ITENS_POR_PAGINA);

        if (paginaAtual == 1) {
            adapter.limparLista();
        }

        if (transacoes.isEmpty()) {
            temMaisItens = false;
        } else {
            adapter.adicionarItens(transacoes);
        }
    }

    private void abrirCadastroTransacao() {
        Intent intent = new Intent(this, CadastroTransacaoActivity.class);
        startActivity(intent);
    }

    private void limparFiltros() {
        // Limpa os campos
        spinnerTipo.setText("Todos", false);
        spinnerStatus.setText("Todos", false);
        editValorMin.setText("");
        editValorMax.setText("");
        edtDataInicial.setText("");
        edtDataFinal.setText("");
        edtBusca.setText("");

        filtroAtual = new TransacaoFiltro();

        reiniciarListagem();

        Toast.makeText(this, "Filtros limpos", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransacaoClick(Transacao transacao) {
        Intent intent = new Intent(this, DetalhesTransacaoActivity.class);
        intent.putExtra("transacao_id", transacao.getId());
        startActivity(intent);
    }
}