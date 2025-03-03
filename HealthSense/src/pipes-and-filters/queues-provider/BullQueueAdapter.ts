import Bull, { Job, Queue as BullQueue } from 'bull'
import { IQueue } from './IQueue'

export class BullQueueAdapter<T> implements IQueue<T> {
  private queue: BullQueue

  constructor(queueName: string) {
    this.queue = new Bull(queueName, {
      redis: {
        host: process.env.REDIS_HOST,
        port: parseInt(process.env.REDIS_PORT || '19149', 10),
        password: process.env.REDIS_PASSWORD,
      },
    });
  }

  async add(data: T): Promise<void> {
    await this.queue.add(data)
  }

  process(callback: (data: T) => Promise<void>): void {
    this.queue.process(async (job: Job) => {
      await callback(job.data)
    })
  }
}
