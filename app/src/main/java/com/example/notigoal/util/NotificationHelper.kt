package com.example.notigoal.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.notigoal.R

object NotificationHelper {

    private const val CHANNEL_ID = "goal_alerts"
    private const val CHANNEL_NAME = "Alertas de Gol"

    fun createNotificationChannel(context: Context) {
        // El canal de notificación solo es necesario en Android 8.0 (API 26) y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para goles y resultados importantes"
            }
            // Registra el canal en el sistema
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showGoalNotification(context: Context, teamName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Construye la notificación
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // ¡IMPORTANTE! Este debe ser tu icono
            .setContentTitle("¡GOOOOOL de $teamName!")
            .setContentText("El marcador ahora es 1-0. ¡No te pierdas el resto del partido!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // La notificación se cierra al pulsarla
            .build()

        // Muestra la notificación con un ID único basado en la hora actual
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
