package com.example.notigoal

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.notigoal.di.AppContainer
import com.example.notigoal.di.DefaultAppContainer
import com.example.notigoal.util.worker.GoalCheckWorker
import java.util.concurrent.TimeUnit

/**
 * Clase Application personalizada para inicializar componentes globales.
 */
class NotiGoalApplication : Application() {

    /**
     * Contenedor de dependencias utilizado por el resto de la aplicación.
     * Se inicializa una vez en [onCreate].
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

        val goalCheckRequest = PeriodicWorkRequestBuilder<GoalCheckWorker>(
            15, TimeUnit.MINUTES // Ejecutar cada 15 minutos
        )
            .addTag("goal_check_worker_tag") // Un tag para identificar nuestro Worker
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "GoalCheckWorker", // Nombre único para el trabajo
            ExistingPeriodicWorkPolicy.KEEP, // Mantener el trabajo existente si ya está en cola
            goalCheckRequest
        )
    }
}
