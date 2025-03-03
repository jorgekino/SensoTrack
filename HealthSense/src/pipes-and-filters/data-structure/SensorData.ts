export interface SensorData {
  sensorId?: number; 
  timestamp: string; 
  sensorType: string; // (e.g., LIGHT, LOCATION, SCREEN)
  lightValue?: number; 
  latitude?: number; 
  longitude?: number; 
  action?: string; 
  [key: string]: any // Propiedades extras que se quieran agregar a futuro
}
