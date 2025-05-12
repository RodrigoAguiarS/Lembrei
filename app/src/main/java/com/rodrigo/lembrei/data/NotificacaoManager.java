package com.rodrigo.lembrei.data;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.rodrigo.lembrei.R;
import com.rodrigo.lembrei.activity.transacao.DetalhesTransacaoActivity;

import java.time.format.DateTimeFormatter;

public class NotificacaoManager {
    private static final String CHANNEL_ID = "LEMBREI_CHANNEL";
    private static final int NOTIFICATION_ID = 1;
    private final Context context;
    private final NotificationManager notificationManager;

    public NotificacaoManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        criarCanalNotificacao();
    }

    private void criarCanalNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Lembretes de Transações",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificações de transações próximas ao vencimento");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void criarNotificacaoTransacao(Transacao transacao) {
        Intent intent = new Intent(context, DetalhesTransacaoActivity.class);
        intent.putExtra("transacao_id", transacao.getId());

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                transacao.getId().intValue(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notification_add_24)
                .setContentTitle("Transação Próxima")
                .setContentText(transacao.getTitulo() + " - Vence em: " +
                        transacao.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(transacao.getId().intValue(), builder.build());
    }
}
