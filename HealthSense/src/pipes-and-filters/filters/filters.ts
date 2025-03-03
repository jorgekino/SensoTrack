import { SensorData } from '../data-structure/SensorData'
import { SensorData as SensorDataModel } from '../../models/SensorData';

export const validateSensor = async (data: SensorData): Promise<SensorData> => {

  const sensorId = typeof data.sensorId === 'string' ? parseInt(data.sensorId, 10) : (data.sensorId || 1);

  data.sensorId = sensorId;

  return data;
};

export const validateSensorData = async (
  data: SensorData,
): Promise<SensorData> => {
  const sensorId = typeof data.sensorId === 'string' ? parseInt(data.sensorId, 10) : (data.sensorId || 1);

  let isValid = false;

  switch (data.sensorType) {
    case 'LIGHT':
      isValid = typeof data.lightValue === 'number' && data.lightValue >= 0;
      break;

    case 'LOCATION':
      isValid =
        typeof data.latitude === 'number' &&
        typeof data.longitude === 'number' &&
        data.latitude >= -90 &&
        data.latitude <= 90 &&
        data.longitude >= -180 &&
        data.longitude <= 180;
      break;

    case 'SCREEN':
      isValid = typeof data.action === 'string' && data.action.length > 0;
      break;

    default:
      console.warn(`Unknown sensor type: ${data.sensorType}`);
      isValid = false;
  }

  return {
    ...data, 
    isValid,
  }
}

export const saveSensorData = async (
  data: SensorData,
): Promise<SensorData> => {
  if (!data.isValid) {
    console.warn('Data is not valid, skipping save:', data);
    return data;
  }

  try {
    const savedData = await SensorDataModel.create({
      sensorId: data.sensorId,
      timestamp: data.timestamp,
      sensorType: data.sensorType,
      lightValue: data.lightValue,
      latitude: data.latitude,
      longitude: data.longitude,
      action: data.action,
    });

    return {
      ...data,
      id: savedData,
    };
  } catch (error) {
    console.error('Error saving SensorData:', error);
    throw new Error('Failed to save SensorData');
  }
}

