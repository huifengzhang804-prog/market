import { parentPort } from 'worker_threads'
import fs from 'fs'
import path from 'path'
import { execSync } from 'child_process'
import { cleanPackageBuildOutput } from './cleaner'

// 确保有parentPort对象，因为这是工作线程和主线程通信的必要条件
if (!parentPort) {
  throw new Error('这个脚本必须作为Worker线程运行')
}

/**
 * 构建单个子包
 * @param packageName 子包名称
 * @param packageDir 子包目录路径
 * @returns 构建结果
 */
function buildPackage(packageName: string, packageDir: string) {
  const buildScriptTs = path.join(packageDir, 'batch-build.ts')

  // 检查目录是否存在
  if (!fs.existsSync(packageDir)) {
    return {
      success: false,
      error: '目录不存在',
    }
  }

  // 检查构建脚本是否存在
  if (!fs.existsSync(buildScriptTs)) {
    return {
      success: false,
      error: 'batch-build.ts 脚本不存在',
    }
  }

  try {
    // 在构建前，使用可靠的清理函数清空目标目录
    cleanPackageBuildOutput(packageName)

    // 执行子包的构建脚本
    const buildCommand = `cd "${packageDir}" && npx tsx batch-build.ts`

    const output = execSync(buildCommand, {
      stdio: 'pipe',
      env: {
        ...process.env,
        VITE_ENTRY_NAME: '',
        NODE_ENV: 'production',
      },
      maxBuffer: 10 * 1024 * 1024, // 增加缓冲区大小到10MB，避免输出太多导致溢出
    }).toString()

    return {
      success: true,
      output,
    }
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : String(error),
    }
  }
}

// 处理主线程消息
parentPort.on('message', (data) => {
  // 检查是否是终止信号
  if (data && data.type === 'TERMINATE') {
    process.exit(0)
    return
  }

  try {
    const { packageName, packageDir } = data
    const result = buildPackage(packageName, packageDir)
    parentPort?.postMessage(result)
  } catch (error) {
    parentPort?.postMessage({
      success: false,
      error: error instanceof Error ? error.message : '未知错误',
    })
  }
})

// 处理进程信号
process.on('SIGTERM', () => process.exit(0))
process.on('SIGINT', () => process.exit(0))

// 处理未捕获的异常
process.on('uncaughtException', (error) => {
  console.error('未捕获的异常:', error)
  process.exit(1)
})

process.on('unhandledRejection', (reason) => {
  console.error('未处理的Promise拒绝:', reason)
  process.exit(1)
})
