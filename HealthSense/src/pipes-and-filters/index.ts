import { SensorData } from './data-structure/SensorData'
import { saveSensorData, validateSensor, validateSensorData } from './filters/filters'
import { Pipeline } from './pipeline/Pipeline'
import { QueueFactory } from './pipeline/QueueFactory'

const queueFactory = QueueFactory.getQueueFactory<SensorData>(
  process.env.QUEUE_TYPE || 'BULL',
)

const retryConfig = { attempts: 1, delay: 1000 }

const pipeline = new Pipeline<SensorData>(
  [
    validateSensor,
    validateSensorData,
    saveSensorData,
  ],
  queueFactory,
  retryConfig,
)

export const processSensorData = (async (dataCollection: SensorData[]) => {
  try {
    for (const data of dataCollection) {
      try {
        pipeline.processInput(data);
      } catch (error) {
        console.error('Error processing individual data:', data, error);
      }
    }
  } catch (error) {
    console.error('Error processing data collection:', error);
  }
})
