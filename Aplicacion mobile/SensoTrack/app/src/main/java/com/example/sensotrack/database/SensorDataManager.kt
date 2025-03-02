package com.example.sensotrack.database

import android.content.ContentValues
import android.content.Context

class SensorDataManager(context: Context) {

    private val dbHelper = SensorDatabaseHelper(context)

    fun insertSensorData(sensorType: String, action: String?, latitude: Double?, longitude: Double?, lightValue: Float?, timestamp: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(SensorDatabaseHelper.COLUMN_SENSOR_TYPE, sensorType)
            put(SensorDatabaseHelper.COLUMN_ACTION, action)
            put(SensorDatabaseHelper.COLUMN_LATITUDE, latitude)
            put(SensorDatabaseHelper.COLUMN_LONGITUDE, longitude)
            put(SensorDatabaseHelper.COLUMN_LIGHT_VALUE, lightValue)
            put(SensorDatabaseHelper.COLUMN_TIMESTAMP, timestamp)
        }
        db.insert(SensorDatabaseHelper.TABLE_NAME, null, values)
        db.close()
    }
}
