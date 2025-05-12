package com.rodrigo.lembrei.activity.transacao;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.data.Categoria;
import com.rodrigo.lembrei.data.Frequencia;
import com.rodrigo.lembrei.data.TipoTransacao;
import com.rodrigo.lembrei.data.Transacao;
import com.rodrigo.lembrei.db.DBHelper;
import com.rodrigo.lembrei.repository.CategoriaRepository;
import com.rodrigo.lembrei.repository.TransacaoRepository;
import com.rodrigo.lembrei.service.CategoriaService;
import com.rodrigo.lembrei.service.TransacaoService;
import com.rodrigo.lembrei.service.impl.CategoriaServiceImpl;
import com.rodrigo.lembrei.service.impl.TransacaoServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.List;

public class EditarTransacaoActivity extends AppCompatActivity {
    private TextInputEditText edtTitulo, edtValor, edtDataVencimento, edtObservacoes;
    private RadioGroup rgTipo;
    private RadioButton rbPagar, rbReceber;
    private Spinner spnFrequencia, spnCategoria;
    private Button btnSalvar;
    private TransacaoService transacaoService;
    private CategoriaService categoriaService;
    private Transacao transacao;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_transacao);

        Long transacaoId = getIntent().getLongExtra("transacao_id", -1);
        if (transacaoId == -1) {
            Toast.makeText(this, "Transação não encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inicializarComponentes();
        configurarServicos();
        configurarSpinners();
        configurarDatePicker();
        carregarTransacao(transacaoId);
        configurarBotaoSalvar();
    }

    private void inicializarComponentes() {
        edtTitulo = findViewById(R.id.edtTitulo);
        edtValor = findViewById(R.id.edtValor);
        edtDataVencimento = findViewById(R.id.edtDataVencimento);
        edtObservacoes = findViewById(R.id.edtObservacoes);
        rgTipo = findViewById(R.id.rgTipo);
        rbPagar = findViewById(R.id.rbPagar);
        rbReceber = findViewById(R.id.rbReceber);
        spnFrequencia = findViewById(R.id.spnFrequencia);
        spnCategoria = findViewById(R.id.spnCategoria);
        btnSalvar = findViewById(R.id.btnSalvar);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Editar Transação");
    }

    private void configurarServicos() {
        DBHelper dbHelper = new DBHelper(this);
        TransacaoRepository transacaoRepository = new TransacaoRepository(dbHelper.getWritableDatabase());
        CategoriaRepository categoriaRepository = new CategoriaRepository(dbHelper.getWritableDatabase());

        transacaoService = new TransacaoServiceImpl(transacaoRepository);
        categoriaService = new CategoriaServiceImpl(categoriaRepository);
    }

    private void configurarSpinners() {
        ArrayAdapter<Frequencia> frequenciaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Frequencia.values());
        frequenciaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFrequencia.setAdapter(frequenciaAdapter);

        List<Categoria> categorias = categoriaService.listarTodas();
        ArrayAdapter<Categoria> categoriaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategoria.setAdapter(categoriaAdapter);
    }

    private void configurarDatePicker() {
        edtDataVencimento.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, ano, mes, dia) -> {
                        LocalDate data = LocalDate.of(ano, mes + 1, dia);
                        edtDataVencimento.setText(data.format(dateFormatter));
                    },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    private void carregarTransacao(Long id) {
        transacao = transacaoService.buscarPorId(id);
        if (transacao == null) {
            Toast.makeText(this, "Transação não encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        edtTitulo.setText(transacao.getTitulo());
        edtValor.setText(String.format("%.2f", transacao.getValor()));
        edtDataVencimento.setText(transacao.getDataVencimento().format(dateFormatter));
        edtObservacoes.setText(transacao.getObservacoes());

        if (transacao.getTipo() == TipoTransacao.PAGAR) {
            rbPagar.setChecked(true);
        } else {
            rbReceber.setChecked(true);
        }

        for (int i = 0; i < spnFrequencia.getCount(); i++) {
            if (spnFrequencia.getItemAtPosition(i).equals(transacao.getFrequencia())) {
                spnFrequencia.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < spnCategoria.getCount(); i++) {
            Categoria categoria = (Categoria) spnCategoria.getItemAtPosition(i);
            if (categoria.getId().equals(transacao.getCategoriaId())) {
                spnCategoria.setSelection(i);
                break;
            }
        }
    }

    private void configurarBotaoSalvar() {
        btnSalvar.setOnClickListener(v -> salvarTransacao());
    }

    private void salvarTransacao() {
        try {
            String titulo = edtTitulo.getText() != null ? edtTitulo.getText().toString().trim() : "";
            String valorTexto = edtValor.getText() != null ? edtValor.getText().toString().trim() : "";
            String dataVencimentoTexto = edtDataVencimento.getText() != null ?
                    edtDataVencimento.getText().toString().trim() : "";
            String observacoes = edtObservacoes.getText() != null ?
                    edtObservacoes.getText().toString().trim() : "";

            // Validações
            if (titulo.isEmpty()) {
                edtTitulo.setError("Título obrigatório");
                return;
            }

            if (valorTexto.isEmpty()) {
                edtValor.setError("Valor obrigatório");
                return;
            }

            if (dataVencimentoTexto.isEmpty()) {
                edtDataVencimento.setError("Data obrigatória");
                return;
            }

            Categoria categoriaSelecionada = (Categoria) spnCategoria.getSelectedItem();
            if (categoriaSelecionada == null) {
                Toast.makeText(this, "Selecione uma categoria", Toast.LENGTH_SHORT).show();
                return;
            }

            transacao.setTitulo(titulo);
            transacao.setTipo(rgTipo.getCheckedRadioButtonId() == R.id.rbPagar ?
                    TipoTransacao.PAGAR : TipoTransacao.RECEBER);
            transacao.setValor(new BigDecimal(valorTexto));
            transacao.setDataVencimento(LocalDate.parse(dataVencimentoTexto, dateFormatter));
            transacao.setCategoriaId(categoriaSelecionada.getId());
            transacao.setFrequencia((Frequencia) spnFrequencia.getSelectedItem());
            transacao.setObservacoes(observacoes);

            transacaoService.atualizar(transacao);
            Toast.makeText(this, "Transação atualizada com sucesso!", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            edtValor.setError("Valor inválido");
        } catch (DateTimeParseException e) {
            edtDataVencimento.setError("Data inválida");
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}