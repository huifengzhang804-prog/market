import { Worker } from 'worker_threads'
import os from 'os'

// 定义任务类型
export interface Task<T> {
  data: any
  callback: (err: Error | null, result?: T) => void
}

export class WorkerPool {
  private workers: Worker[] = []
  private queue: Task<any>[] = []
  private workerPath: string
  private numWorkers: number
  private activeWorkers: number = 0
  private workerStatus: Map<Worker, boolean> = new Map()
  private workerTasks: Map<Worker, Task<any>> = new Map() // 跟踪每个工作线程的当前任务
  private isClosing: boolean = false

  /**
   * 创建一个新的工作线程池
   * @param workerPath 工作线程脚本的路径
   * @param numWorkers 工作线程数量，默认为CPU核心数
   */
  constructor(workerPath: string, numWorkers?: number) {
    this.workerPath = workerPath
    // 设置工作线程数量，默认为CPU核心数(最多8个)
    this.numWorkers = Math.min(numWorkers || os.cpus().length, 8)
  }

  /**
   * 初始化工作线程池
   */
  init(): void {
    // 创建指定数量的工作线程
    for (let i = 0; i < this.numWorkers; i++) {
      this.addNewWorker()
    }
  }

  /**
   * 添加一个新的工作线程到池中
   */
  private addNewWorker(): void {
    const worker = new Worker(this.workerPath)
    this.workerStatus.set(worker, false)

    worker.on('message', (result) => {
      // 如果正在关闭，则忽略结果
      if (this.isClosing) return

      // 工作线程完成任务，更新状态
      this.workerStatus.set(worker, false)
      this.activeWorkers--

      // 获取当前任务并调用其回调
      const task = this.workerTasks.get(worker)
      if (task) {
        task.callback(null, result)
        this.workerTasks.delete(worker)
      }

      // 如果还有任务，继续处理
      if (this.queue.length > 0) {
        const nextTask = this.queue.shift()!
        this.assignTask(worker, nextTask)
      }
    })

    worker.on('error', (err) => {
      // 如果正在关闭，则忽略错误
      if (this.isClosing) return

      console.error('Worker error:', err)

      // 从池中移除出错的工作线程
      this.workers = this.workers.filter((w) => w !== worker)
      this.workerStatus.delete(worker)

      // 获取当前任务并通知失败
      const task = this.workerTasks.get(worker)
      if (task) {
        task.callback(err)
        this.workerTasks.delete(worker)
      }

      // 减少活跃工作线程计数
      if (this.workerStatus.get(worker)) {
        this.activeWorkers--
      }

      // 创建新工作线程替代失败的工作线程
      this.addNewWorker()
    })

    this.workers.push(worker)
  }

  /**
   * 分配任务给工作线程
   */
  private assignTask(worker: Worker, task: Task<any>): void {
    this.workerStatus.set(worker, true)
    this.activeWorkers++
    this.workerTasks.set(worker, task)
    worker.postMessage(task.data)
  }

  /**
   * 提交一个任务到工作线程池
   */
  runTask<T>(data: any): Promise<T> {
    return new Promise((resolve, reject) => {
      // 如果正在关闭，则拒绝新任务
      if (this.isClosing) {
        reject(new Error('工作线程池正在关闭，无法提交新任务'))
        return
      }

      const task: Task<T> = {
        data,
        callback: (err, result) => {
          if (err) reject(err)
          else resolve(result as T)
        },
      }

      // 寻找空闲的工作线程
      const idleWorker = this.workers.find((worker) => !this.workerStatus.get(worker))

      if (idleWorker) {
        // 有空闲工作线程，直接分配任务
        this.assignTask(idleWorker, task)
      } else {
        // 没有空闲工作线程，将任务加入队列
        this.queue.push(task)
      }
    })
  }

  /**
   * 关闭所有工作线程
   */
  close(): Promise<void> {
    // 标记为正在关闭
    this.isClosing = true

    // 清空队列并通知等待的任务
    while (this.queue.length > 0) {
      const task = this.queue.shift()
      if (task) {
        task.callback(new Error('工作线程池已关闭，任务被取消'))
      }
    }

    return new Promise<void>((resolve) => {
      // 如果没有工作线程，直接返回
      if (this.workers.length === 0) {
        return resolve()
      }

      // 强制退出超时设置
      const forceExitTimeout = setTimeout(() => {
        console.warn('关闭工作线程池超时，强制关闭')
        resolve()
      }, 5000)

      // 计算已终止的工作线程数量
      let terminatedCount = 0

      // 为每个工作线程设置监听器，以便在终止时更新计数
      for (const worker of this.workers) {
        try {
          // 移除所有现有的监听器
          worker.removeAllListeners('message')
          worker.removeAllListeners('error')

          // 添加一次性退出监听器
          worker.once('exit', () => {
            terminatedCount++
            // 当所有工作线程都终止时，解析Promise
            if (terminatedCount === this.workers.length) {
              clearTimeout(forceExitTimeout)
              resolve()
            }
          })

          // 尝试发送终止命令
          try {
            worker.postMessage({ type: 'TERMINATE' })
          } catch (err) {
            // 忽略错误
          }

          // 设置个别工作线程超时终止
          setTimeout(() => {
            try {
              worker.terminate()
            } catch (err) {
              // 忽略错误
            }
          }, 100)
        } catch (err) {
          // 如果发生错误，直接增加计数
          terminatedCount++
          if (terminatedCount === this.workers.length) {
            clearTimeout(forceExitTimeout)
            resolve()
          }
        }
      }
    })
  }

  /**
   * 获取当前活跃的工作线程数量
   */
  getActiveWorkers(): number {
    return this.activeWorkers
  }

  /**
   * 获取当前队列中的任务数量
   */
  getQueueLength(): number {
    return this.queue.length
  }

  /**
   * 获取当前工作线程总数
   */
  getTotalWorkers(): number {
    return this.workers.length
  }
}
