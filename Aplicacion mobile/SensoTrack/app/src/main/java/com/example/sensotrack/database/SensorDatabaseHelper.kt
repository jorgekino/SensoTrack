package com.example.sensotrack.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class SensorDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "sensor_data.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "sensor_data"
        const val COLUMN_ID = "id"
        const val COLUMN_SENSOR_TYPE = "sensorType"
        const val COLUMN_ACTION = "action"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_LIGHT_VALUE = "lightValue"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SENSOR_TYPE TEXT,
                $COLUMN_ACTION TEXT,
                $COLUMN_LATITUDE REAL,
                $COLUMN_LONGITUDE REAL,
                $COLUMN_LIGHT_VALUE REAL,
                $COLUMN_TIMESTAMP TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Método para insertar datos en la base de datos
    fun insertSensorData(sensorType: String, action: String?, latitude: Double?, longitude: Double?, lightValue: Float, timestamp: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SENSOR_TYPE, sensorType)
            put(COLUMN_ACTION, action)
            put(COLUMN_LATITUDE, latitude)
            put(COLUMN_LONGITUDE, longitude)
            put(COLUMN_LIGHT_VALUE, lightValue)
            put(COLUMN_TIMESTAMP, timestamp)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllSensorData(): List<Map<String, Any>> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        val sensorDataList = mutableListOf<Map<String, Any>>()

        if (cursor.moveToFirst()) {
            do {
                val sensorData = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    "sensorType" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENSOR_TYPE)),
                    "action" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTION)),
                    "latitude" to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                    "longitude" to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                    "lightValue" to cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_LIGHT_VALUE)),
                    "timestamp" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                )
                sensorDataList.add(sensorData)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sensorDataList
    }

    fun deleteSensorData(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

}
