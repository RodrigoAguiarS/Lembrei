package com.rodrigo.lembrei.activity.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.activity.BaseActivity;
import com.rodrigo.lembrei.activity.categoria.CadastroCategoriaActivity;
import com.rodrigo.lembrei.activity.transacao.CadastroTransacaoActivity;
import com.rodrigo.lembrei.activity.transacao.DetalhesTransacaoActivity;
import com.rodrigo.lembrei.adapter.TransacaoAdapter;
import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;
import com.rodrigo.lembrei.db.DBHelper;
import com.rodrigo.lembrei.repository.CategoriaRepository;
import com.rodrigo.lembrei.repository.TransacaoRepository;
import com.rodrigo.lembrei.service.CategoriaService;
import com.rodrigo.lembrei.service.TransacaoService;
import com.rodrigo.lembrei.service.impl.TransacaoServiceImpl;
import com.rodrigo.lembrei.service.impl.CategoriaServiceImpl;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends BaseActivity implements TransacaoAdapter.OnTransacaoClickListener {
    private BarChart barChart;
    private RecyclerView recyclerViewProximas;
    private TextView tvItensVencidos;
    private TransacaoService transacaoService;
    private CategoriaService categoriaService;
    private TransacaoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        configurarBottomNavigation();
        inicializarComponentes();
        configurarServicos();
        configurarRecyclerView();
        carregarDados();
        configureToolbar();
    }

    private void inicializarComponentes() {
        barChart = findViewById(R.id.barChart);
        recyclerViewProximas = findViewById(R.id.recyclerViewProximas);
        tvItensVencidos = findViewById(R.id.tvItensVencidos);
    }

    private void configurarServicos() {
        DBHelper dbHelper = new DBHelper(this);
        TransacaoRepository repository = new TransacaoRepository(dbHelper.getWritableDatabase());
        transacaoService = new TransacaoServiceImpl(repository);
        categoriaService = new CategoriaServiceImpl(new CategoriaRepository(dbHelper.getWritableDatabase()));
    }

    private void configurarRecyclerView() {
        adapter = new TransacaoAdapter(new ArrayList<>(), categoriaService, this);
        recyclerViewProximas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProximas.setAdapter(adapter);
    }

    private void carregarDados() {
        carregarGrafico();
        carregarProximasTransacoes();
        carregarItensVencidos();
    }

    private void carregarGrafico() {
        ArrayList<BarEntry> entradas = new ArrayList<>();

        BigDecimal totalPagar = transacaoService.calcularTotalMes(TipoTransacao.PAGAR);
        BigDecimal totalReceber = transacaoService.calcularTotalMes(TipoTransacao.RECEBER);

        entradas.add(new BarEntry(0f, totalPagar.floatValue()));
        entradas.add(new BarEntry(1f, totalReceber.floatValue()));

        BarDataSet dataSet = new BarDataSet(entradas, "Valores");
        dataSet.setColors(Color.RED, Color.GREEN);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"A Pagar", "A Receber"}));
        xAxis.setTextSize(12f);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setExtraOffsets(10f, 10f, 10f, 10f);

        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);

        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void carregarProximasTransacoes() {
        List<Transacao> proximasTransacoes = transacaoService.listarProximasTransacoes();
        adapter.atualizarLista(proximasTransacoes);
    }

    private void carregarItensVencidos() {
        List<Transacao> itensVencidos = transacaoService.listarItensVencidos();
        StringBuilder mensagem = new StringBuilder();

        for (Transacao transacao : itensVencidos) {
            mensagem.append(transacao.getTitulo())
                    .append(" - Vencimento: ")
                    .append(transacao.getDataVencimento().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .append("\n");
        }

        if (mensagem.length() > 0) {
            tvItensVencidos.setText(mensagem.toString());
        } else {
            tvItensVencidos.setText("Não há itens vencidos");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_add_transacao) {
            Intent intent = new Intent(this, CadastroTransacaoActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_add_categoria) {
            Intent intent = new Intent(this, CadastroCategoriaActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onTransacaoClick(Transacao transacao) {
        Intent intent = new Intent(this, DetalhesTransacaoActivity.class);
        intent.putExtra("transacao_id", transacao.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }
}
