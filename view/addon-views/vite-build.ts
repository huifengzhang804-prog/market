import fs from 'fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { spawn, type ChildProcess } from 'child_process'
import batchBuildDirsNames from './batchBuildDirsNames'
import { WebSocketServer, WebSocket } from 'ws'
import chokidar from 'chokidar'
// 直接导入 globalsAlias
import { globalsAlias } from './rollup-config'

// 类型定义
type DependencyMap = Map<string, string[]>
type PackageDependencies = Map<string, DependencyMap>

// 全局常量和工具函数
const ROOT_DIR = fileURLToPath(new URL('.', import.meta.url))
const resolve = (dir: string): string => path.resolve(ROOT_DIR, dir)
const normalizePath = (filePath: string): string => path.normalize(path.resolve(filePath))

// 创建websocket监听端口变化用于热更新
const wss = new WebSocketServer({ port: 9998, clientTracking: true })

// 存储全局状态
const packageDependencies: PackageDependencies = new Map()
const watchMap = new Map<string, chokidar.FSWatcher>()
const runningBuilds = new Map<string, ChildProcess>()
const pendingBuilds = new Map<string, string[]>() // 存储待构建的入口 <packageDir, entryNames[]>
const externalDependencies: string[] = Object.keys(globalsAlias)
const FILE_EXTENSIONS = ['.vue', '.js', '.ts', '.jsx', '.tsx']

// 存储客户端信息
interface ClientInfo {
  type: string
}

const clientConnections = new Map<WebSocket, ClientInfo>()

// 记录进程是否正在退出
let isExiting = false

// 处理WebSocket连接
wss.on('connection', (ws, req) => {
  // 获取客户端IP地址
  const clientIP = req.socket.remoteAddress || 'unknown'

  // 从URL查询参数中获取客户端类型
  let clientInfo: ClientInfo | null = null
  try {
    // 解析URL获取查询参数
    const url = new URL(req.url || '', 'http://localhost')
    const clientType = url.searchParams.get('clientType')

    if (clientType) {
      clientInfo = {
        type: clientType,
      }
      clientConnections.set(ws, clientInfo)
    }
  } catch (error) {
    console.error('❌ 解析客户端类型失败:', error)
  }

  console.log(`
🔌 新的WebSocket客户端已连接
📱 客户端信息:IP: ${clientIP}${clientInfo ? ` 类型: ${clientInfo.type}` : '未知类型'}
`)

  // 处理消息
  ws.on('message', (message) => {
    try {
      const data = JSON.parse(message.toString())
      let clientInfo = clientConnections.get(ws)

      // 如果心跳消息中包含客户端信息，更新存储的客户端信息
      if (data.clientInfo && data.clientInfo.type) {
        clientInfo = { type: data.clientInfo.type }
        clientConnections.set(ws, clientInfo)
      }

      // 处理心跳消息
      if (data.type === 'ping') {
        console.log(`💓 收到来自 ${clientInfo?.type || clientIP} 的心跳检测`)
        ws.send(JSON.stringify({ type: 'pong' }))
        return
      }

      // 处理其他消息...
    } catch (error) {
      console.error(`❌ 处理来自 ${clientIP} 的WebSocket消息时出错:`, error)
    }
  })

  // 处理连接关闭
  ws.on('close', () => {
    const clientInfo = clientConnections.get(ws)
    console.log(`🔌 WebSocket客户端已断开连接 (${clientInfo?.type || clientIP})`)
    clientConnections.delete(ws)
  })

  // 处理错误
  ws.on('error', (error) => {
    const clientInfo = clientConnections.get(ws)
    console.error(`❌ WebSocket连接错误 (${clientInfo?.type || clientIP}):`, error)
  })
})

// 专用的强制退出函数
function forceExit(exitCode = 1): void {
  console.log('强制退出进程...')

  try {
    if (process.platform === 'win32') {
      // 尝试使用taskkill命令强制终止进程及其子进程
      const { execSync } = require('child_process')
      execSync(`taskkill /pid ${process.pid} /f /t`, { stdio: 'ignore' })
    } else {
      // 在Linux/macOS上使用SIGKILL信号
      process.kill(process.pid, 'SIGKILL')
    }
  } catch (e) {
    // 最后的尝试：使用process.exit
    console.error('强制退出失败，使用process.exit()...')
    process.exit(exitCode)
  }
}

/**
 * 获取子包的所有入口文件
 */
function getPackageEntries(packageDir: string): string[] {
  const entriesDir = resolve(`${packageDir}/source/entries`)
  if (!fs.existsSync(entriesDir)) {
    return []
  }

  return fs
    .readdirSync(entriesDir)
    .filter((file) => path.extname(file) === '.vue')
    .map((file) => path.basename(file, '.vue'))
}

/**
 * 检查路径是否为外部依赖（需要忽略的依赖）
 */
function isExternalDependency(importPath: string): boolean {
  if (externalDependencies.includes(importPath)) {
    return true
  }

  if (importPath.startsWith('@/')) {
    return true
  }

  return externalDependencies.some((dep) => importPath.startsWith(`${dep}/`))
}

/**
 * 查找文件的完整路径，包括处理缺失的扩展名和index文件
 */
function findFileWithExtension(filePath: string): string | null {
  // 如果有扩展名且文件存在，直接返回
  if (path.extname(filePath) && fs.existsSync(filePath) && !fs.statSync(filePath).isDirectory()) {
    return filePath
  }

  // 尝试添加各种扩展名
  for (const ext of FILE_EXTENSIONS) {
    const pathWithExt = `${filePath}${ext}`
    if (fs.existsSync(pathWithExt) && !fs.statSync(pathWithExt).isDirectory()) {
      return pathWithExt
    }
  }

  // 尝试作为目录查找index文件
  for (const ext of FILE_EXTENSIONS) {
    const indexPath = `${filePath}/index${ext}`
    if (fs.existsSync(indexPath) && !fs.statSync(indexPath).isDirectory()) {
      return indexPath
    }
  }

  return null
}

/**
 * 解析入口文件的依赖关系
 */
function analyzeEntryDependencies(packageDir: string, entryName: string): string[] {
  // 初始化依赖表
  if (!packageDependencies.has(packageDir)) {
    packageDependencies.set(packageDir, new Map<string, string[]>())
  }

  const entryFile = resolve(`${packageDir}/source/entries/${entryName}.vue`)
  if (!fs.existsSync(entryFile)) {
    packageDependencies.get(packageDir)?.set(entryName, [])
    return []
  }

  // 强制重新分析依赖关系，不使用缓存
  const processedDependencies = new Set<string>()
  const dependencies = new Set<string>()

  /**
   * 递归分析文件及其依赖的所有文件
   */
  function analyzeFileDependencies(filePath: string): void {
    if (processedDependencies.has(filePath)) {
      return
    }
    processedDependencies.add(filePath)

    // 确保文件存在且不是目录
    if (!fs.existsSync(filePath) || fs.statSync(filePath).isDirectory()) {
      return
    }

    const content = fs.readFileSync(filePath, 'utf8')
    dependencies.add(normalizePath(filePath))

    // 定义所有需要提取的导入模式
    const importPatterns = [
      // 标准导入
      { regex: /import\s+.*?from\s+['"]([^'"]+)['"]/g, type: 'standard' },
      // 相对路径导入
      { regex: /import\s+.*?from\s+['"](\.\.[^'"]+)['"]/g, type: 'relative' },
      // 动态导入
      { regex: /import\s*\(\s*['"]([^'"]+)['"]\s*\)/g, type: 'dynamic' },
      // defineAsyncComponent包装的动态导入
      { regex: /defineAsyncComponent\s*\(\s*\(\s*\)\s*=>\s*import\s*\(\s*['"]([^'"]+)['"]\s*\)\s*\)/g, type: 'asyncComponent' },
    ]

    const localDependencies = new Set<string>()

    // 处理所有导入模式
    for (const pattern of importPatterns) {
      let match: RegExpExecArray | null
      while ((match = pattern.regex.exec(content)) !== null) {
        const importPath = match[1]

        // 只处理非外部的相对路径
        if (!isExternalDependency(importPath) && (importPath.startsWith('./') || importPath.startsWith('../'))) {
          localDependencies.add(importPath)
        }
      }
    }

    // 处理并递归分析每个依赖
    localDependencies.forEach((dep) => {
      // 只处理相对路径
      if (!(dep.startsWith('./') || dep.startsWith('../'))) {
        return
      }

      const resolvedPath = path.resolve(path.dirname(filePath), dep)
      const fullPath = findFileWithExtension(resolvedPath)

      if (fullPath) {
        analyzeFileDependencies(fullPath)
      }
    })
  }

  // 从入口文件开始分析
  try {
    analyzeFileDependencies(entryFile)
    const resultDeps = [...dependencies].map((dep) => normalizePath(dep))
    packageDependencies.get(packageDir)?.set(entryName, resultDeps)
    return resultDeps
  } catch (error) {
    console.error(`❌ 分析 ${packageDir}/${entryName} 时出错:`, error instanceof Error ? error.message : String(error))
    packageDependencies.get(packageDir)?.set(entryName, [])
    return []
  }
}

/**
 * 获取依赖指定文件的所有入口
 */
function getEntriesDependingOnFile(packageDir: string, filePath: string): string[] {
  const entries = getPackageEntries(packageDir)
  const affectedEntries: string[] = []
  const normalizedFilePath = normalizePath(filePath)

  // 如果是入口文件本身，只返回该入口
  const entriesDir = resolve(`${packageDir}/source/entries`)
  if (normalizedFilePath.startsWith(entriesDir)) {
    const filename = path.basename(normalizedFilePath)
    if (path.extname(filename) === '.vue') {
      const entryName = path.basename(filename, '.vue')
      if (entries.includes(entryName)) {
        console.log(`检测到入口文件变化: ${entryName}`)
        return [entryName]
      }
    }
  }

  // 清除依赖缓存并重新分析所有入口的依赖
  // 这样可以确保新增或删除的import语句能够正确反映在依赖关系中
  const packageDepMap = packageDependencies.get(packageDir)
  if (packageDepMap) {
    // 只清除与当前文件相关的入口依赖缓存
    for (const [entry, deps] of packageDepMap.entries()) {
      if (deps.some((dep) => normalizePath(dep) === normalizedFilePath)) {
        // 移除缓存，强制重新分析
        packageDepMap.delete(entry)
      }
    }
  }

  // 确保依赖分析已完成
  entries.forEach((entry) => analyzeEntryDependencies(packageDir, entry))

  const dependencies = packageDependencies.get(packageDir)
  if (!dependencies) return []

  // 查找受影响的入口
  for (const [entry, deps] of dependencies.entries()) {
    const hasExactMatch = deps.some((dep) => normalizePath(dep) === normalizedFilePath)
    if (hasExactMatch) {
      console.log(`🔄 ${packageDir}/${entry} 依赖于文件 ${path.basename(filePath)}`)
      affectedEntries.push(entry)
    }
  }

  return affectedEntries
}

/**
 * 更新构建队列，移除旧的待构建任务
 */
function updateBuildQueue(packageDir: string, newEntries: string[]): void {
  // 清除之前的待构建任务
  pendingBuilds.set(packageDir, newEntries)
}

/**
 * 构建特定入口
 */
async function buildEntry(packageDir: string, entryName: string): Promise<string | void> {
  // 如果进程正在退出，不启动新的构建
  if (isExiting) {
    console.log(`⏭️ 跳过 ${packageDir}/${entryName} 构建，进程正在退出`)
    return
  }

  const buildKey = `${packageDir}:${entryName}`

  // 检查此入口是否仍在待构建队列中
  const pendingEntriesForPackage = pendingBuilds.get(packageDir) || []
  if (!pendingEntriesForPackage.includes(entryName)) {
    console.log(`⏭️ 跳过 ${packageDir}/${entryName} 构建，已有更新的文件变化`)
    return
  }

  // 如果已经在构建中，跳过
  if (runningBuilds.has(buildKey)) {
    console.log(`⏳ ${buildKey} 已经在构建中，跳过...`)
    return
  }

  // 从待构建队列中移除
  pendingBuilds.set(
    packageDir,
    pendingEntriesForPackage.filter((entry) => entry !== entryName),
  )

  console.log(`🔨 开始构建: ${packageDir}/${entryName}`)

  const buildProcess = spawn('npx', ['vite', 'build'], {
    cwd: resolve(packageDir),
    stdio: 'pipe',
    env: { ...process.env, VITE_ENTRY_NAME: entryName, NODE_ENV: 'production' },
    shell: true,
  })

  runningBuilds.set(buildKey, buildProcess)

  // 设置输出处理
  buildProcess.stdout?.on('data', (data) => {
    const chunk = data.toString()
    // 只输出错误信息和构建结果信息
    if (chunk.includes('error') || chunk.includes('Error') || chunk.includes('built in')) {
      process.stdout.write(`[${packageDir}/${entryName}] ${chunk}`)
    }
  })

  buildProcess.stderr?.on('data', (data) => {
    process.stderr.write(`[${packageDir}/${entryName}] ${data.toString()}`)
  })

  return new Promise((resolve, reject) => {
    buildProcess.on('close', (code) => {
      runningBuilds.delete(buildKey)

      // 如果进程正在退出，不发送WebSocket通知
      if (isExiting) {
        resolve(entryName)
        return
      }

      if (code === 0) {
        console.log(`✅ 构建成功: ${packageDir}/${entryName}`)

        // 通知所有连接的客户端
        wss.clients.forEach((client) => {
          if (client.readyState === WebSocket.OPEN) {
            client.send(
              JSON.stringify({
                type: 'file-change',
                service: packageDir,
                name: entryName,
              }),
            )
          }
        })

        resolve(entryName)
      } else {
        console.error(`❌ 构建失败: ${packageDir}/${entryName}`)
        reject(new Error(`构建失败: ${packageDir}/${entryName}, 退出码 ${code}`))
      }
    })
  })
}

/**
 * 为单个子包创建文件监听
 */
function setupPackageWatcher(packageDir: string): chokidar.FSWatcher | undefined {
  const sourceDir = resolve(`${packageDir}/source`)

  // 确保目录存在
  if (!fs.existsSync(sourceDir)) {
    console.warn(`⚠️ 警告: 子包目录 ${packageDir}/source 不存在，跳过监听`)
    return
  }

  // 预分析所有入口的依赖
  getPackageEntries(packageDir).forEach((entry) => analyzeEntryDependencies(packageDir, entry))

  // 设置文件监听
  const watcher = chokidar.watch(sourceDir, {
    ignoreInitial: true,
    ignorePermissionErrors: true,
    ignored: ['**/node_modules/**', '**/dist/**', '**/public/**'],
    awaitWriteFinish: {
      stabilityThreshold: 300,
      pollInterval: 100,
    },
  })

  // 处理文件变化
  watcher.on('all', async (event, filePath) => {
    const normalizedPath = path.resolve(filePath)

    // 如果文件被修改或创建，可能导入依赖发生变化，需要清除受影响文件的依赖缓存
    if (event === 'change' || event === 'add' || event === 'unlink') {
      // 获取当前文件所在的目录下的所有文件，它们可能引用了当前文件
      const fileDir = path.dirname(normalizedPath)
      const packageDepMap = packageDependencies.get(packageDir)

      if (packageDepMap) {
        // 清除可能引用了此文件的入口依赖缓存
        for (const [entry, deps] of packageDepMap.entries()) {
          // 如果依赖中有文件在同一目录，可能引用关系发生了变化，需要重新分析
          if (deps.some((dep) => path.dirname(dep) === fileDir)) {
            packageDepMap.delete(entry)
          }
        }
      }
    }

    // 获取受影响的入口
    const affectedEntries = getEntriesDependingOnFile(packageDir, normalizedPath)

    if (affectedEntries.length === 0) return

    // 更新构建队列，取消之前未开始的构建
    updateBuildQueue(packageDir, affectedEntries)

    // 构建受影响的入口
    for (const entry of affectedEntries) {
      try {
        await buildEntry(packageDir, entry)
      } catch (error) {
        console.error(`❌ 构建 ${packageDir}/${entry} 时出错:`, error instanceof Error ? error.message : String(error))
      }
    }
  })

  return watcher
}

/**
 * 初始化所有子包的监听
 */
function initWatchers(): void {
  console.log(`🔍 准备监听 ${batchBuildDirsNames.length} 个子包的变化...`)

  // 过滤存在的子包并设置监听
  const validPackages = batchBuildDirsNames.filter((dir) => fs.existsSync(resolve(dir)))

  validPackages.forEach((packageDir) => {
    const watcher = setupPackageWatcher(packageDir)
    if (watcher) {
      watchMap.set(packageDir, watcher)
    }
  })

  console.log(`📡 已设置 ${watchMap.size} 个子包的监听`)
  console.log('🚀 文件监听服务已启动，等待文件变化...')
}

/**
 * 关闭所有文件监听器
 */
async function closeAllWatchers(): Promise<void> {
  if (watchMap.size === 0) return

  console.log('正在关闭文件监听器...')

  const watcherClosePromises = Array.from(watchMap.entries()).map(
    ([packageDir, watcher]) =>
      new Promise<void>((resolve) => {
        try {
          watcher.removeAllListeners()
          watcher.close()
        } catch (err) {
          // 忽略关闭错误
        }
        resolve()
      }),
  )

  // 设置最大等待时间为1秒
  await Promise.race([Promise.all(watcherClosePromises), new Promise((resolve) => setTimeout(resolve, 1000))])

  watchMap.clear()
  console.log('文件监听器已关闭')
}

/**
 * 终止所有构建进程
 */
async function terminateAllBuildProcesses(): Promise<void> {
  const runningProcesses = [...runningBuilds.entries()]
  if (runningProcesses.length === 0) return

  console.log(`正在终止 ${runningProcesses.length} 个构建进程...`)

  const killPromises = runningProcesses.map(([buildKey, proc]) => {
    return new Promise<void>((resolve) => {
      try {
        proc.removeAllListeners()
        proc.kill('SIGTERM')

        // 100ms后强制结束未终止的进程
        setTimeout(() => {
          try {
            if (runningBuilds.has(buildKey)) {
              proc.kill('SIGKILL')
              runningBuilds.delete(buildKey)
            }
          } catch {
            // 忽略错误
          }
          resolve()
        }, 100)
      } catch {
        resolve() // 忽略错误并继续
      }
    })
  })

  // 最多等待1秒
  await Promise.race([Promise.all(killPromises), new Promise((resolve) => setTimeout(resolve, 1000))])

  runningBuilds.clear()
  console.log('所有构建进程已终止')
}

/**
 * 关闭WebSocket连接和服务器
 */
async function closeWebSocketServer(): Promise<void> {
  // 1. 关闭客户端连接
  if (wss.clients.size > 0) {
    console.log(`正在关闭 ${wss.clients.size} 个WebSocket连接...`)
    for (const client of wss.clients) {
      try {
        client.terminate()
      } catch {
        // 忽略错误
      }
    }
    console.log('所有WebSocket连接已关闭')
  }

  // 2. 关闭WebSocket服务器
  console.log('正在关闭WebSocket服务器...')
  const closed = await Promise.race([
    new Promise<boolean>((resolve) => {
      wss.close(() => {
        console.log('WebSocket服务器已关闭')
        resolve(true)
      })
    }),
    new Promise<boolean>((resolve) => {
      setTimeout(() => {
        console.log('WebSocket服务器关闭超时')
        resolve(false)
      }, 1000)
    }),
  ])

  if (!closed) {
    console.log('WebSocket服务器可能未完全关闭')
  }
}

/**
 * 优雅退出的主函数
 */
async function gracefulShutdown(exitCode: number = 0): Promise<void> {
  // 防止重复执行
  if (isExiting) return
  isExiting = true

  console.log('正在进行优雅退出...')

  try {
    // 阻止新的构建请求
    pendingBuilds.clear()

    // 按顺序关闭各种资源
    await closeAllWatchers()
    await terminateAllBuildProcesses()
    await closeWebSocketServer()

    // 清理其他资源
    packageDependencies.clear()

    console.log('所有资源已清理完毕，准备退出...')
  } catch (error) {
    console.error('退出过程中发生错误:', error)
  } finally {
    // 尝试体面退出
    console.log('正在退出进程...')
    process.exit(exitCode)

    // 如果进程未能退出，3秒后强制终止
    setTimeout(() => forceExit(exitCode), 3000)
  }
}

/**
 * 设置信号处理程序
 */
function setupGracefulShutdown(): void {
  // 处理Ctrl+C等终止信号
  process.on('SIGINT', () => gracefulShutdown(0))
  process.on('SIGTERM', () => gracefulShutdown(0))

  // 处理未捕获的异常
  process.on('uncaughtException', (error) => {
    console.error('未捕获的异常:', error)
    gracefulShutdown(1)
  })

  // 处理未处理的Promise拒绝
  process.on('unhandledRejection', (reason) => {
    console.error('未处理的Promise拒绝:', reason)
    // 不退出进程，只记录错误
  })
}

// 启动程序
setupGracefulShutdown()
initWatchers()
