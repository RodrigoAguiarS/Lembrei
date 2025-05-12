package com.rodrigo.lembrei.service;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rodrigo.lembrei.data.NotificacaoManager;
import com.rodrigo.lembrei.data.Transacao;
import com.rodrigo.lembrei.db.DBHelper;
import com.rodrigo.lembrei.repository.TransacaoRepository;
import com.rodrigo.lembrei.service.impl.TransacaoServiceImpl;

import java.util.List;

public class VerificadorTransacoesWorker extends Worker {
    private final TransacaoService transacaoService;
    private final NotificacaoManager notificacaoManager;

    public VerificadorTransacoesWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        DBHelper dbHelper = new DBHelper(context);
        TransacaoRepository repository = new TransacaoRepository(dbHelper.getWritableDatabase());
        this.transacaoService = new TransacaoServiceImpl(repository);
        this.notificacaoManager = new NotificacaoManager(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        List<Transacao> transacoes = transacaoService.listarTransacoesProximasVencimento();

        for (Transacao transacao : transacoes) {
            if (!transacao.isPago()) {
                notificacaoManager.criarNotificacaoTransacao(transacao);
            }
        }

        return Result.success();
    }
}