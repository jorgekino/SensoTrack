package com.example.sensotrack

import SensorRequest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.sensotrack.database.SensorDatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ScreenReceiver : BroadcastReceiver() {

    companion object {
        // Variable global para controlar si el envío de datos está activado
        var isScreenDataSendingEnabled: Boolean = false
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!isScreenDataSendingEnabled) return // Si el envío de datos está desactivado, no hacer nada

        when (intent?.action) {
            Intent.ACTION_SCREEN_OFF -> {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                val timestamp = sdf.format(System.currentTimeMillis())
                val sensorType = "SCREEN"

                // Crear el objeto SensorData para almacenar en SQLite
                context?.let {
                    val dbHelper = SensorDatabaseHelper(it)

                    // Guardar los datos en la base de datos SQLite
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val result = dbHelper.insertSensorData(
                                sensorType = sensorType,
                                action = "ACTION_SCREEN_OFF",
                                latitude = null,
                                longitude = null,
                                lightValue = 0.0f,
                                timestamp = timestamp
                            )
                            Log.d("SensorService", "Datos insertados en la BD con ID: $result")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            Intent.ACTION_SCREEN_ON -> {
                val sensorType = "SCREEN"
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                val timestamp = sdf.format(System.currentTimeMillis())

                // Crear el objeto SensorData para almacenar en SQLite
                context?.let {
                    val dbHelper = SensorDatabaseHelper(it)

                    // Guardar los datos en la base de datos SQLite
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val result = dbHelper.insertSensorData(
                                sensorType = sensorType,
                                action = "ACTION_SCREEN_ON",
                                latitude = null,
                                longitude = null,
                                lightValue = 0.0f,
                                timestamp = timestamp
                            )
                            Log.d("SensorService", "Datos insertados en la BD con ID: $result")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            Intent.ACTION_USER_PRESENT -> {
                val sensorType = "SCREEN"
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                val timestamp = sdf.format(System.currentTimeMillis())
                val lightValue = 0.0f


                // Crear el objeto SensorData para almacenar en SQLite
                context?.let {
                    val dbHelper = SensorDatabaseHelper(it)

                    // Guardar los datos en la base de datos SQLite
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val result = dbHelper.insertSensorData(
                                sensorType = sensorType,
                                action = "ACTION_USER_PRESENT",
                                latitude = null,
                                longitude = null,
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
    }
}
