package com.rodrigo.lembrei.activity.transacao;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.activity.BaseActivity;
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

public class CadastroTransacaoActivity extends BaseActivity {
    private TextInputEditText edtTitulo, edtTotalParcelas, edtValor, edtDataVencimento, edtObservacoes;
    private RadioGroup rgTipo;
    private CheckBox cbParcelado;
    private TextInputLayout layoutTotalParcelas;
    private Spinner spnFrequencia;
    private Spinner spnCategoria;
    private Button btnSalvar;
    private TransacaoService transacaoService;
    private CategoriaService categoriaService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_transacao);
        configurarBottomNavigation();

        inicializarComponentes();
        configurarServicos();
        configurarSpinnerFrequencia();
        configurarDatePicker();
        configurarBotaoSalvar();
        configurarParcelamento();
        configurarSpinnerCategoria();
    }

    private void inicializarComponentes() {
        edtTitulo = findViewById(R.id.edtTitulo);
        edtValor = findViewById(R.id.edtValor);
        edtDataVencimento = findViewById(R.id.edtDataVencimento);
        edtObservacoes = findViewById(R.id.edtObservacoes);
        rgTipo = findViewById(R.id.rgTipo);
        spnFrequencia = findViewById(R.id.spnFrequencia);
        spnCategoria = findViewById(R.id.spnCategoria);
        btnSalvar = findViewById(R.id.btnSalvar);
        cbParcelado = findViewById(R.id.cbParcelado);
        layoutTotalParcelas = findViewById(R.id.layoutTotalParcelas);
        edtTotalParcelas = findViewById(R.id.edtTotalParcelas);
    }

    private void configurarServicos() {
        DBHelper dbHelper = new DBHelper(this);
        TransacaoRepository repository = new TransacaoRepository(dbHelper.getWritableDatabase());
        transacaoService = new TransacaoServiceImpl(repository);
        CategoriaRepository categoriaRepository = new CategoriaRepository(dbHelper.getWritableDatabase());
        categoriaService = new CategoriaServiceImpl(categoriaRepository);
    }

    private void configurarSpinnerFrequencia() {
        ArrayAdapter<Frequencia> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Frequencia.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFrequencia.setAdapter(adapter);
    }

    private void configurarSpinnerCategoria() {
        List<Categoria> categorias = categoriaService.listarTodas();
        ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategoria.setAdapter(adapter);
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

    private void configurarParcelamento() {
        cbParcelado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutTotalParcelas.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                edtTotalParcelas.setText("");
            }
        });
    }

    private void configurarBotaoSalvar() {
        btnSalvar.setOnClickListener(v -> salvarTransacao());
    }

    private void salvarTransacao() {
        try {
            String titulo = edtTitulo.getText() != null ? edtTitulo.getText().toString().trim() : "";
            String valorTexto = edtValor.getText() != null ? edtValor.getText().toString().trim() : "";
            String dataVencimentoTexto = edtDataVencimento.getText() != null ? edtDataVencimento.getText().toString().trim() : "";
            String observacoes = edtObservacoes.getText() != null ? edtObservacoes.getText().toString().trim() : "";

            // Validações obrigatórias
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

            int radioId = rgTipo.getCheckedRadioButtonId();
            if (radioId == -1) {
                Toast.makeText(this, "Selecione o tipo da transação", Toast.LENGTH_SHORT).show();
                return;
            }

            Categoria categoriaSelecionada = (Categoria) spnCategoria.getSelectedItem();
            if (categoriaSelecionada == null) {
                Toast.makeText(this, "Selecione uma categoria", Toast.LENGTH_SHORT).show();
                return;
            }

            BigDecimal valorTotal;
            try {
                valorTotal = new BigDecimal(valorTexto);
            } catch (NumberFormatException e) {
                edtValor.setError("Valor inválido");
                return;
            }

            LocalDate dataVencimentoInicial;
            try {
                dataVencimentoInicial = LocalDate.parse(dataVencimentoTexto, dateFormatter);
            } catch (DateTimeParseException e) {
                edtDataVencimento.setError("Data inválida");
                return;
            }

            if (cbParcelado.isChecked()) {
                String totalParcelasTexto = edtTotalParcelas.getText() != null ?
                        edtTotalParcelas.getText().toString().trim() : "";

                if (totalParcelasTexto.isEmpty()) {
                    edtTotalParcelas.setError("Total de parcelas obrigatório");
                    return;
                }

                int totalParcelas = Integer.parseInt(totalParcelasTexto);
                if (totalParcelas <= 0) {
                    edtTotalParcelas.setError("Total de parcelas deve ser maior que zero");
                    return;
                }

                BigDecimal valorParcela = valorTotal.divide(
                        new BigDecimal(totalParcelas),
                        2,
                        BigDecimal.ROUND_HALF_UP
                );

                // Cria uma transação para cada parcela
                for (int numeroParcela = 1; numeroParcela <= totalParcelas; numeroParcela++) {
                    Transacao transacao = new Transacao();

                    transacao.setTitulo(titulo + " (" + numeroParcela + "/" + totalParcelas + ")");
                    transacao.setTipo(radioId == R.id.rbPagar ? TipoTransacao.PAGAR : TipoTransacao.RECEBER);
                    transacao.setValor(valorParcela);
                    transacao.setCategoriaId(categoriaSelecionada.getId());
                    transacao.setFrequencia((Frequencia) spnFrequencia.getSelectedItem());
                    transacao.setObservacoes(observacoes);

                    transacao.setParcelado(true);
                    transacao.setTotalParcelas(totalParcelas);
                    transacao.setNumeroParcela(numeroParcela);

                    LocalDate dataVencimentoParcela = dataVencimentoInicial.plusMonths(numeroParcela - 1);
                    transacao.setDataVencimento(dataVencimentoParcela);

                    transacaoService.salvar(transacao);
                }

                Toast.makeText(this,
                        "Transação parcelada salva com sucesso: " + totalParcelas + " parcelas",
                        Toast.LENGTH_SHORT).show();
            } else {
                Transacao transacao = new Transacao();
                transacao.setTitulo(titulo);
                transacao.setTipo(radioId == R.id.rbPagar ? TipoTransacao.PAGAR : TipoTransacao.RECEBER);
                transacao.setValor(valorTotal);
                transacao.setDataVencimento(dataVencimentoInicial);
                transacao.setCategoriaId(categoriaSelecionada.getId());
                transacao.setFrequencia((Frequencia) spnFrequencia.getSelectedItem());
                transacao.setObservacoes(observacoes);
                transacao.setParcelado(false);
                transacao.setTotalParcelas(1);
                transacao.setNumeroParcela(1);

                transacaoService.salvar(transacao);
                Toast.makeText(this, "Transação salva com sucesso!", Toast.LENGTH_SHORT).show();
            }

            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
