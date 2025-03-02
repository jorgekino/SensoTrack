package com.example.sensotrack

import SensorRequest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.sensotrack.database.SensorDatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class LocationService : Service(), LocationListener {

    private var locationManager: LocationManager? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "Servicio de ubicación iniciado")
        // Iniciar el servicio en primer plano
        startForegroundService()
        // Inicializar LocationManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L, // Intervalo de tiempo en milisegundos
                1000f,    // Distancia mínima entre actualizaciones en metros
                this
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundService() {
        val channelId = "LocationServiceChannel"
        val channelName = "Location Service"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Ubicación Activa")
            .setContentText("La aplicación está recopilando datos de ubicación.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val timestamp = sdf.format(System.currentTimeMillis())
        val sensorType = "LOCATION"

        // Crear el mapa dinámico para 'sensorData'
        val sensorData: Map<String, Any> = mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "timestamp" to timestamp,
            "sensorType" to sensorType
        )

        // Enviar los datos al backend usando Retrofit
        //CoroutineScope(Dispatchers.IO).launch {
        //    try {
                // Envía el objeto como una lista con un único elemento
        //        RetrofitClient.apiService.sendSensorData(listOf(SensorRequest(sensorData)))
        //    } catch (e: Exception) {
        //        e.printStackTrace()
        //    }
        //}


        // Crear el objeto SensorData para almacenar en SQLite
        val dbHelper = SensorDatabaseHelper(applicationContext)

        // Guardar los datos en la base de datos SQLite
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = dbHelper.insertSensorData(
                    sensorType = sensorType,
                    action = null,
                    latitude = latitude,
                    longitude = longitude,
                    lightValue = 0.0f,
                    timestamp = timestamp
                )
                Log.d("SensorService", "Datos insertados en la BD con ID: $result")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopLocationUpdates() {
        try {
            Log.d("LocationService", "Deteniendo actualizaciones de ubicación")
            locationManager?.removeUpdates(this)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LocationService", "Servicio de ubicación destruido")
        stopLocationUpdates()

        // Eliminamos el reinicio automático
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
