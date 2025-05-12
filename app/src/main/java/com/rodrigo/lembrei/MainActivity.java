package com.rodrigo.lembrei;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.rodrigo.lembrei.service.VerificadorTransacoesWorker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        agendarVerificacaoTransacoes();
    }

    private void agendarVerificacaoTransacoes() {
        PeriodicWorkRequest verificacaoRequest =
                new PeriodicWorkRequest.Builder(VerificadorTransacoesWorker.class,
                        1, TimeUnit.DAYS)
                        .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        "verificacao_transacoes",
                        ExistingPeriodicWorkPolicy.KEEP,
                        verificacaoRequest
                );
    }
}