import { BullQueueAdapter } from '../queues-provider/BullQueueAdapter'
import { RabbitMQQueueAdapter } from '../queues-provider/RabbitQueueAdapter'
import { IQueue } from '../queues-provider/IQueue'

export class QueueFactory {
  static getQueueFactory<T>(queueName: string): (name: string) => IQueue<T> {
    switch (queueName) {
      case 'BULL':
        return (name: string) => new BullQueueAdapter<T>(name)
      case 'RABBITMQ':
        return (name: string) => new RabbitMQQueueAdapter<T>(name)
      default:
        throw new Error(`Unsupported queue type: ${queueName}`)
    }
  }
}
