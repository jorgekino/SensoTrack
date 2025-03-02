package com.example.sensotrack

import SensorData
import SensorRequest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.sensotrack.database.SensorDatabaseHelper

class SensorService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    var lastLightValue: Float? = null
    val lightThreshold = 200f // Umbral mínimo de cambio significativo

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        // Iniciar el servicio en primer plano
        startForegroundService()

        // Inicializar SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundService() {
        val channelId = "SensorServiceChannel"
        val channelName = "Sensor Service"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Sensores Activos")
            .setContentText("La aplicación está recopilando datos de sensores.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(2, notification)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lightValue = event.values[0]

            // Solo guardar los datos si el cambio es mayor que el umbral
            if (lastLightValue == null || Math.abs(lightValue - lastLightValue!!) > lightThreshold) {
                lastLightValue = lightValue

                // Crear formato de fecha yyyy-MM-dd HH:mm:ss.SSS
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                val timestamp = sdf.format(System.currentTimeMillis())
                val sensorType = "LIGHT"

                // Crear el objeto SensorData para almacenar en SQLite
                val dbHelper = SensorDatabaseHelper(applicationContext)

                // Guardar los datos en la base de datos SQLite
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val result = dbHelper.insertSensorData(
                            sensorType = sensorType,
                            action = null,       // No aplica para el sensor de luz
                            latitude = null,     // No aplica para el sensor de luz
                            longitude = null,    // No aplica para el sensor de luz
                            lightValue = lightValue,
                            timestamp = timestamp
                        )
                        Log.d("SensorService", "Datos insertados en la BD con ID: $result")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        // Desregistrar el sensor cuando el servicio sea destruido
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No hacer nada
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
