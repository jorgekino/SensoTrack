import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SensorDataDao {

    @Insert
    suspend fun insert(sensorData: SensorData)

    @Query("SELECT * FROM sensor_data")
    suspend fun getAllSensorData(): List<SensorData>

    @Query("DELETE FROM sensor_data")
    suspend fun clearAllData()
}
