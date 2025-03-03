import { SensorData } from '../models/SensorData'

export const SensorService = {

  getSensor: async (sensorId: number) => {
    let sensor = null;
    try {
      sensor = await SensorData.findByPk(sensorId)
    } catch (error) {
      console.log(error)
    }
    return sensor
  },
}
