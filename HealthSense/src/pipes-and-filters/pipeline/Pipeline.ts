import { EventEmitter } from 'events'
import { IQueue } from '../queues-provider/IQueue'

type FilterFunction<T> = (input: T) => Promise<T>

interface RetryConfig {
  attempts: number
  delay: number // delay en milisegundos
}

export class Pipeline<T> extends EventEmitter {
  private filters: FilterFunction<T>[]
  private filterQueues: {
    filter: FilterFunction<T>
    queue: IQueue<T>
    retryConfig: RetryConfig
  }[]

  constructor(
    filters: FilterFunction<T>[],
    queueFactory: (name: string) => IQueue<T>,
    private retryConfig: RetryConfig = { attempts: 3, delay: 1000 },
  ) {
    super()
    this.filters = filters
    this.filterQueues = []
    this.setupQueues(queueFactory)
  }

  private setupQueues(queueFactory: (name: string) => IQueue<T>): void {
    this.filters.forEach((filter, index) => {
      const queueName = `filter-queue-${index}`
      const filterQueue = queueFactory(queueName)
      this.filterQueues.push({
        filter,
        queue: filterQueue,
        retryConfig: this.retryConfig,
      })
      filterQueue.process(async (data: T) => {
        await this.processWithRetry(filter, data, index)
      })
    })
  }

  private async processWithRetry(
    filter: FilterFunction<T>,
    data: T,
    currentFilterIndex: number,
  ) {
    let attempts = 0
    const { attempts: maxAttempts, delay } = this.retryConfig

    while (attempts < maxAttempts) {
      try {
        const filteredData = await filter(data)
        this.enqueueNextFilter(currentFilterIndex, filteredData)
        return
      } catch (error) {
        attempts++
        if (attempts >= maxAttempts) {
          this.emit('error', error, data)
          return
        }
        await new Promise((resolve) => setTimeout(resolve, delay))
      }
    }
  }

  private enqueueNextFilter(currentFilterIndex: number, data: T): void {
    const nextFilter = this.filterQueues[currentFilterIndex + 1]
    if (nextFilter) {
      nextFilter.queue.add(data)
    } else {
      this.emit('final', data)
    }
  }

  public async processInput(input: T): Promise<void> {
    if (this.filterQueues.length > 0) {
      await this.filterQueues[0].queue.add(input)
    }
  }
}
