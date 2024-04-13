package com.example.gerenciador_de_tarefas.helpers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.gerenciador_de_tarefas.R;

public class LembreteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            String titulo = intent.getStringExtra("tituloTarefa");
            int idNotificacao = (int) System.currentTimeMillis();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ID_CANAL_TAREFAS")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Tarefa Vence Hoje")
                    .setContentText(titulo)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(idNotificacao, builder.build());
        } else {
            Log.d("LembreteReceiver", "Permissão para enviar notificações não concedida");

            Toast.makeText(context, "Permissão para enviar notificações não está disponível. Por favor, habilite para receber lembretes.",
                    Toast.LENGTH_LONG).show();        }
    }
}
