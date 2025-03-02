import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_data")
data class SensorData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sensorType: String,         // Tipo de sensor (SCREEN, LOCATION, etc.)
    val action: String?,            // Acción, si es aplicable (ejemplo: ACTION_USER_PRESENT para la pantalla)
    val latitude: Double?,          // Coordenadas de ubicación, si aplica
    val longitude: Double?,
    val lightValue: Float?,         // Valor de luz, si aplica
    val timestamp: String           // Marca de tiempo de la recolección del dato
)
