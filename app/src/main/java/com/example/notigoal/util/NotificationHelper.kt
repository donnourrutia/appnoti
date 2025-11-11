package com.example.notigoal.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.notigoal.MainActivity
import com.example.notigoal.R

object NotificationHelper {

    private const val CHANNEL_ID = "goal_alerts"
    private const val CHANNEL_NAME = "Alertas de Gol"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para goles y resultados importantes"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Función actualizada para mostrar una notificación de gol con más detalles
    fun showGoalNotification(
        context: Context,
        homeTeamName: String,
        awayTeamName: String,
        score: String,
        minute: String?,
        matchId: Int = -1 // Añadir matchId para navegación futura
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val minuteText = minute?.let { " (Min. $it)" } ?: ""
        val title = "¡GOOOOOL! $homeTeamName vs $awayTeamName"
        val contentText = "¡Marcador: $score$minuteText!"

        // Crear un Intent para abrir la MainActivity cuando se toque la notificación
        // Aquí podríamos añadir extras para navegar a una pantalla específica del partido
        val intent = Intent(context, MainActivity::class.java).apply {
            // Si matchId es válido, lo añadimos para la navegación profunda
            if (matchId != -1) {
                putExtra("matchId", matchId)
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de que este sea tu icono
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Establece el intent al hacer clic
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText)) // Para notificaciones más largas
            .build()

        notificationManager.notify(matchId, notification) // Usamos el matchId como ID de notificación
    }
}
