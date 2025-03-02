import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Definir la interfaz de API
interface ApiService {
    @POST("/sensorData")
    suspend fun sendSensorData(@Body request: List<SensorRequest>)
}

// Configuración de Retrofit
object RetrofitClient {
    private const val BASE_URL = "https://d16jus92zqzkzr.cloudfront.net/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// Modelo de datos dinámico
data class SensorRequest(
    val id: Int,
    val sensorType: String,
    val latitude: Double,
    val longitude: Double,
    val lightValue: Int,
    val timestamp: String,
    val action: String? = null // Opcional, solo para sensores tipo SCREEN
)

