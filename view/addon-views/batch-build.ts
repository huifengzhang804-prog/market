/**
 * 统一批量构建脚本
 * 该脚本将调用所有子包的批量构建脚本进行构建
 */

import path from 'path'
import { fileURLToPath } from 'node:url'
import allPackages from './batchBuildDirsNames'
import { WorkerPool } from './worker-pool'
import { cleanAllBuildOutput } from './cleaner'

// 获取当前文件的目录路径（ESM 中不存在 __dirname）
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// 记录构建结果
let successCount = 0
let failedCount = 0
const failedPackages: { name: string; error: string }[] = []

// 开始构建流程
console.log('开始执行统一构建 (多线程模式)...\n')

// 在开始构建前，先清理所有构建产物
console.log('准备清理所有构建产物...')
const cleanResult = cleanAllBuildOutput(allPackages)
if (!cleanResult) {
  console.warn('警告: 清理构建产物时遇到一些问题，但将继续构建')
} else {
  console.log('清理完成，准备开始构建\n')
}

// 记录开始时间
const startTime = Date.now()

// 计算总包数
const totalPackages = allPackages.length

// 创建进度跟踪对象
const progress = {
  completed: 0,
  total: totalPackages,
  getProgressBar: () => {
    const percent = ((progress.completed / progress.total) * 100).toFixed(1)
    const progressBar = '='.repeat(Math.floor(Number(percent) / 2)) + '>' + ' '.repeat(50 - Math.floor(Number(percent) / 2))
    const fraction = `${progress.completed}/${progress.total}`
    return { progressBar, percent, fraction }
  },
}

// 创建工作线程池，最大8个线程
const workerPool = new WorkerPool(path.join(__dirname, 'build-worker.ts'))

// 初始化工作线程池
workerPool.init()

// 设置强制超时退出，确保程序不会挂起
const maxBuildTimeMinutes = 15 // 最长允许构建15分钟
const forceExitTimeout = setTimeout(() => {
  console.error(`\n构建超时！已经运行了 ${maxBuildTimeMinutes} 分钟，强制退出`)
  process.exit(1)
}, maxBuildTimeMinutes * 60 * 1000)

// 处理未捕获异常和Promise拒绝
process.on('uncaughtException', (err) => {
  console.error('发生未捕获的异常:', err)
  clearTimeout(forceExitTimeout)

  console.log('准备关闭工作线程池...')
  workerPool
    .close()
    .then(() => {
      console.log('工作线程池已关闭，退出进程')
      process.exit(1)
    })
    .catch(() => process.exit(1))
})

process.on('unhandledRejection', (reason) => {
  console.error('发生未处理的Promise拒绝:', reason)
  clearTimeout(forceExitTimeout)

  console.log('准备关闭工作线程池...')
  workerPool
    .close()
    .then(() => {
      console.log('工作线程池已关闭，退出进程')
      process.exit(1)
    })
    .catch(() => process.exit(1))
})

// 处理每个包的构建结果
const handleBuildResult = (packageName: string, result: any) => {
  progress.completed++
  const { progressBar, percent, fraction } = progress.getProgressBar()
  const activeWorkers = workerPool.getActiveWorkers()
  const queueLength = workerPool.getQueueLength()
  const totalWorkers = workerPool.getTotalWorkers()

  // 检查结果是否有效
  if (!result) {
    console.log(`[${progressBar}] ${percent}% (${fraction}) | ✗ ${packageName} | 活跃线程: ${activeWorkers}/${totalWorkers} | 队列: ${queueLength}`)
    failedCount++
    failedPackages.push({ name: packageName, error: '工作线程未返回结果' })
    return
  }

  if (result.success) {
    console.log(`[${progressBar}] ${percent}% (${fraction}) | ✓ ${packageName} | 活跃线程: ${activeWorkers}/${totalWorkers} | 队列: ${queueLength}`)
    successCount++
  } else {
    const errorMessage = result.error || '未知错误'
    console.log(`[${progressBar}] ${percent}% (${fraction}) | ✗ ${packageName} | 活跃线程: ${activeWorkers}/${totalWorkers} | 队列: ${queueLength}`)
    failedCount++
    failedPackages.push({ name: packageName, error: errorMessage })
  }
}

// 创建所有构建任务的Promise数组
const buildPromises = allPackages.map((packageName) => {
  const packageDir = path.join(__dirname, packageName)

  // 将每个包的构建任务提交到工作线程池
  return workerPool
    .runTask<{ success: boolean; error?: string; output?: string }>({
      packageName,
      packageDir,
    })
    .then((result) => {
      handleBuildResult(packageName, result)
      return result
    })
    .catch((error) => {
      console.error(`处理 ${packageName} 时发生错误:`, error)
      handleBuildResult(packageName, { success: false, error: error.message })
      return { success: false, error: error.message }
    })
})

// 等待所有构建任务完成
Promise.all(buildPromises)
  .then(() => {
    // 计算耗时
    const endTime = Date.now()
    const duration = ((endTime - startTime) / 1000).toFixed(2)

    // 输出构建结果
    console.log('\n------------------------------')
    console.log(`构建完成: 成功 ${successCount} 个, 失败 ${failedCount} 个, 耗时 ${duration}s`)

    if (failedCount > 0) {
      console.log('\n构建失败的包:')
      failedPackages.forEach((pkg) => {
        console.log(`- ${pkg.name}: ${pkg.error}`)
      })
    }

    // 清除强制退出超时
    clearTimeout(forceExitTimeout)

    console.log('准备关闭工作线程池...')

    // 设置强制退出保障
    setTimeout(() => {
      console.log('强制退出进程')
      process.exit(0)
    }, 3000)

    // 关闭工作线程池
    return workerPool.close()
  })
  .then(() => {
    console.log('工作线程池已关闭，退出进程')
    process.exit(0)
  })
  .catch((error) => {
    console.error('构建过程发生错误:', error)

    // 清除强制退出超时
    clearTimeout(forceExitTimeout)

    // 确保关闭工作线程池
    console.log('准备关闭工作线程池...')

    // 设置强制退出保障
    setTimeout(() => {
      console.log('强制退出进程')
      process.exit(1)
    }, 3000)

    // 关闭工作线程池
    workerPool
      .close()
      .then(() => {
        console.log('工作线程池已关闭，退出进程')
        process.exit(1)
      })
      .catch((err) => {
        console.error('关闭工作线程池时出错:', err)
        process.exit(1)
      })
  })
