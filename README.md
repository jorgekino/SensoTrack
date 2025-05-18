# SensoTrack

SensoTrack es una aplicación móvil Android desarrollada en Kotlin que permite capturar y sincronizar datos de sensores y localización del dispositivo. Está diseñada como parte de una solución de digital phenotyping, integrándose con un backend para el análisis de datos mediante Power BI.

---

## 📱 Aplicación móvil

La aplicación móvil realiza las siguientes funciones principales:

- Registro de datos del sensor de luz.
- Registro de eventos de pantalla (encendido/apagado).
- Captura periódica de la ubicación GPS.
- Envío de datos al backend mediante solicitudes HTTPS.
- Sincronización de datos mediante `WorkManager`.

---

## 📂 Estructura del proyecto

```
Aplicacion mobile/
└── SensoTrack/
    ├── app/
    │   ├── src/main/java/com/example/sensotrack/
    │   │   ├── SensorService.kt
    │   │   ├── LocationService.kt
    │   │   ├── SensorDataSyncWorker.kt
    │   │   ├── database/
    │   │   │   ├── SensorDataManager.kt
    │   │   │   └── SensorDatabaseHelper.kt
    │   ├── AndroidManifest.xml
    ├── build.gradle.kts
```

---

## 🧱 Tecnologías utilizadas

- **Lenguaje:** Kotlin
- **Framework:** Android SDK
- **Base de datos local:** SQLite
- **Sincronización:** WorkManager
- **Red:** Retrofit con protocolo HTTPS
- **Backend:** Node.js (no incluido en este repositorio)
- **Base de datos remota:** MySQL (desplegada en Azure)
- **Reportes:** Power BI

---

## 🚀 Requisitos de instalación

- Android Studio Giraffe o superior
- SDK de Android 8.0 (API 26) o superior
- Gradle Kotlin DSL
- Conexión a Internet para sincronización de datos

---

## ⚙️ Configuración

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/jorgekino/SensoTrack.git
   ```

2. Abrir con Android Studio.

3. Configurar las URLs del backend en `RetrofitClient.kt`.

4. Ejecutar la app en un emulador o dispositivo físico Android.

---

## 🔒 Seguridad

- Toda comunicación entre la app y el backend se realiza mediante HTTPS.
- Los datos sensibles se almacenan de forma local en una base SQLite y se eliminan una vez sincronizados.

---

## 📈 Visualización de datos

Los datos sincronizados pueden ser consultados desde Power BI conectándose al backend y/o base de datos remota.

---

## 📄 Licencia

Este proyecto es de uso académico. Para uso comercial, contactarse con los desarrolladores.
