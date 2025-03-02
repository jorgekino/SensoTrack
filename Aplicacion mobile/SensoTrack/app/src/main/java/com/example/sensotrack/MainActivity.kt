package com.example.sensotrack

import SensorDataSyncWorker
import android.Manifest
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sensotrack.ui.theme.SensoTrackTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    // Variables de estado para controlar si los servicios están activos
    private var isLocationServiceRunning by mutableStateOf(false)
    private var isSensorServiceRunning by mutableStateOf(false)
    private var isScreenServiceRunning by mutableStateOf(false)

    // BroadcastReceiver para la pantalla (bloqueo/desbloqueo)
    private lateinit var screenReceiver: ScreenReceiver

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.SYSTEM_ALERT_WINDOW
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkPermissions()) {
            requestPermissions()
        }

        // Restaurar el estado de los servicios
        isSensorServiceRunning = loadServiceState("SENSOR_SERVICE")
        if (isSensorServiceRunning) {
            startSensorService()
        }

        isLocationServiceRunning = loadServiceState("LOCATION_SERVICE")
        if (isLocationServiceRunning) {
            startLocationService()
        }

        isScreenServiceRunning = loadServiceState("SCREEN_SERVICE")
        if (isScreenServiceRunning) {
            startScreenService()
        }

        setContent {
            SensoTrackTheme {
                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Botón para iniciar/detener el servicio de ubicación
                        Button(onClick = {
                            if (isMyServiceRunning(LocationService::class.java)) {
                                stopLocationService()
                                saveServiceState("LOCATION_SERVICE", false)
                            } else {
                                startLocationService()
                                saveServiceState("LOCATION_SERVICE", true)
                            }
                            isLocationServiceRunning = !isLocationServiceRunning
                        }) {
                            Text(text = if (isLocationServiceRunning) "Apagar Ubicación" else "Encender Ubicación")
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Botón para iniciar/detener el servicio de sensores
                        Button(onClick = {
                            if (isMyServiceRunning(SensorService::class.java)) {
                                stopSensorService()
                                saveServiceState("SENSOR_SERVICE", false)
                            } else {
                                startSensorService()
                                saveServiceState("SENSOR_SERVICE", true)
                            }
                            isSensorServiceRunning = !isSensorServiceRunning
                        }) {
                            Text(text = if (isSensorServiceRunning) "Apagar Sensores" else "Encender Sensores")
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Botón para activar/desactivar el envío cuando la pantalla se bloquea/desbloquea
                        Button(onClick = {
                            if (isScreenServiceRunning) {
                                stopScreenService()
                                saveServiceState("SCREEN_SERVICE", false)
                                ScreenReceiver.isScreenDataSendingEnabled = false
                            } else {
                                startScreenService()
                                saveServiceState("SCREEN_SERVICE", true)
                                ScreenReceiver.isScreenDataSendingEnabled = true
                            }
                            isScreenServiceRunning = !isScreenServiceRunning
                        }) {
                            Text(text = if (isScreenServiceRunning) "Apagar Envío Pantalla" else "Encender Envío Pantalla")
                        }
                    }
                }
            }
        }
        scheduleDataSyncWorker()
        // Registrar los BroadcastReceivers
        registerReceiver(locationReceiver, IntentFilter("LOCATION_UPDATE"), Context.RECEIVER_NOT_EXPORTED)
        registerReceiver(sensorReceiver, IntentFilter("SENSOR_UPDATE"), Context.RECEIVER_NOT_EXPORTED)

    }

    // Verificar si los permisos están concedidos
    private fun checkPermissions(): Boolean {
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    // Solicitar permisos en tiempo de ejecución
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions, 0)
    }

    // Iniciar el servicio de ubicación
    private fun startLocationService() {
        val locationIntent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(locationIntent)
        } else {
            startService(locationIntent)
        }
    }

    // Detener el servicio de ubicación
    private fun stopLocationService() {
        val locationIntent = Intent(this, LocationService::class.java)
        stopService(locationIntent)
    }

    // Iniciar el servicio de sensores
    private fun startSensorService() {
        val sensorIntent = Intent(this, SensorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(sensorIntent)
        } else {
            startService(sensorIntent)
        }
    }

    // Detener el servicio de sensores
    private fun stopSensorService() {
        val sensorIntent = Intent(this, SensorService::class.java)
        stopService(sensorIntent)
    }

    // Iniciar el servicio de pantalla (bloqueo/desbloqueo)
    private fun startScreenService() {
        screenReceiver = ScreenReceiver()
        val screenFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenReceiver, screenFilter)
    }

    // Detener el servicio de pantalla
    private fun stopScreenService() {
        unregisterReceiver(screenReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReceiver)
        unregisterReceiver(sensorReceiver)
    }

    // Métodos para guardar y cargar el estado de los servicios
    private fun saveServiceState(key: String, value: Boolean) {
        val sharedPref = getSharedPreferences("ServiceState", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    private fun loadServiceState(key: String): Boolean {
        val sharedPref = getSharedPreferences("ServiceState", Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, false)
    }

    // Verificar si un servicio sigue ejecutándose
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    // BroadcastReceiver para la ubicación
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Lógica cuando se recibe la actualización de ubicación
        }
    }

    // BroadcastReceiver para los sensores
    private val sensorReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Lógica cuando se recibe la actualización de sensor
        }
    }

    private fun scheduleDataSyncWorker() {
        // Configura las restricciones necesarias
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Requiere conexión a Internet
            .build()

        // Define el PeriodicWorkRequest con un intervalo válido
        val syncWorkRequest = PeriodicWorkRequestBuilder<SensorDataSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        // Encola el trabajo de manera única
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "SensorDataSyncWorker", // Nombre único para evitar duplicados
            ExistingPeriodicWorkPolicy.KEEP, // Conserva la tarea existente si ya está encolada
            syncWorkRequest
        )
    }

}
