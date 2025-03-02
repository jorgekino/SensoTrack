import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sensotrack.database.SensorDatabaseHelper

class SensorDataSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dbHelper = SensorDatabaseHelper(applicationContext)
        val sensorDataList = dbHelper.getAllSensorData() // Obtiene los datos de la base de datos

        if (sensorDataList.isEmpty()) {
            return Result.success() // Si no hay datos, termina la tarea
        }

        // Convierte los datos de la base de datos en una lista de SensorRequest
        val sensorRequestList = sensorDataList.mapNotNull { sensorData ->
            try {
                SensorRequest(
                    id = (sensorData["id"] as? Int) ?: return@mapNotNull null,
                    sensorType = (sensorData["sensorType"] as? String) ?: "UNKNOWN",
                    latitude = (sensorData["latitude"] as? Double) ?: 0.0,
                    longitude = (sensorData["longitude"] as? Double) ?: 0.0,
                    lightValue = (sensorData["lightValue"] as? Number)?.toInt() ?: 0,
                    timestamp = (sensorData["timestamp"] as? String) ?: "1970-01-01 00:00:00.000",
                    action = sensorData["action"] as? String
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null // Ignorar registros con errores
            }
        }

        return try {
            // Envía la lista completa a través de Retrofit
            RetrofitClient.apiService.sendSensorData(sensorRequestList)

            // Si el envío es exitoso, elimina los registros de la base de datos
            sensorDataList.forEach { sensorData ->
                dbHelper.deleteSensorData(sensorData["id"] as Int)
            }

            Result.success() // Éxito
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry() // Reintentar en caso de error
        }
    }
}
