/**
 * 统一构建脚本 - 支持单次构建和监听模式
 *
 * 使用方法：
 * 1. 单次构建: npm run build    - 使用 isWatchMode = false
 * 2. 监听构建: npm run build:w  - 使用 isWatchMode = true
 *
 * 修改 targetFileName 变量来指定要构建的入口
 */

import fs from 'fs'
import path from 'path'
import { execSync } from 'child_process'
import { fileURLToPath } from 'node:url'

// 获取当前文件的目录
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// ======================================================================
// 配置区域 - 根据需要修改
// ======================================================================
const targetFileName = 'AddIntegralGoods' // 要构建的入口文件名
const isWatchMode = process.argv.includes('--watch') // 是否为监听模式
// ======================================================================

// 获取入口文件列表
function getEntryFiles(): string[] {
  const entriesPath = path.resolve(__dirname, 'source/entries')
  return fs
    .readdirSync(entriesPath)
    .filter((file) => file.endsWith('.vue'))
    .map((file) => file.split('.vue')[0])
}

// 递归复制文件夹
function copyFolderSync(source: string, target: string): void {
  // 确保目标文件夹存在
  if (!fs.existsSync(target)) {
    fs.mkdirSync(target, { recursive: true })
  }

  // 读取源文件夹内容
  const files = fs.readdirSync(source)

  // 复制每个文件/文件夹
  for (const file of files) {
    const sourcePath = path.join(source, file)
    const targetPath = path.join(target, file)

    // 判断是文件还是文件夹
    if (fs.statSync(sourcePath).isDirectory()) {
      // 递归复制子文件夹
      copyFolderSync(sourcePath, targetPath)
    } else {
      // 复制文件
      fs.copyFileSync(sourcePath, targetPath)
    }
  }
}

// 复制构建结果到addon目录
function copyToAddon(): void {
  const folderName = path.basename(__dirname)
  const sourcePath = path.resolve(__dirname, 'public')
  const addonDir = path.resolve(__dirname, '../addon')
  const targetFolder = path.join(addonDir, folderName)

  // 检查源目录是否存在
  if (!fs.existsSync(sourcePath)) {
    console.error(`错误: 源目录不存在: ${sourcePath}`)
    return
  }

  // 检查并清理现有目录
  if (fs.existsSync(addonDir)) {
    if (fs.existsSync(path.join(addonDir, folderName))) {
      fs.rmSync(path.join(addonDir, folderName), {
        recursive: true,
      })
    }
  } else {
    // 创建addon目录
    fs.mkdirSync(addonDir, { recursive: true })
  }

  // 创建新目录并复制文件
  fs.mkdirSync(targetFolder, { recursive: true })
  copyFolderSync(sourcePath, targetFolder)

  console.log(`已复制构建结果到 ${targetFolder}`)
}

// 构建函数
function build(): void {
  // 获取所有可用的入口文件
  const availableEntries = getEntryFiles()
  console.log(`可用的入口文件: ${availableEntries.join(', ')}`)
  console.log(`当前选择的入口文件: ${targetFileName}`)

  // 检查指定的入口文件是否存在
  if (!availableEntries.includes(targetFileName)) {
    console.error(`错误: 入口文件 '${targetFileName}' 不存在`)
    console.log(`可用的入口文件: ${availableEntries.join(', ')}`)
    console.log('请修改 batch-build-unified.ts 文件中的 targetFileName 变量值')
    process.exit(1)
  }

  try {
    // 使用环境变量传递入口文件名
    process.env.VITE_ENTRY_NAME = targetFileName

    // 构建模式提示
    const modeDesc = isWatchMode ? '监听模式' : '单次构建'
    console.log(`开始${modeDesc}构建 ${targetFileName}`)

    if (isWatchMode) {
      console.log('按 Ctrl+C 停止监听')
    }

    // 构建命令
    const buildCmd = isWatchMode ? 'npx vite build --watch' : 'npx vite build'

    // 执行构建命令
    execSync(buildCmd, {
      stdio: 'inherit',
      env: process.env,
    })

    // 在监听模式下，下面的代码不会执行，因为execSync会一直运行
    if (!isWatchMode) {
      console.log(`${targetFileName}构建完成`)
      // 复制构建结果到addon目录
      copyToAddon()
      console.log(`\n构建完成: ${targetFileName}`)
    }
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : String(error)
    console.error('构建过程中出错:', errorMessage)
    process.exit(1)
  }
}

// 主函数
function main(): void {
  try {
    build()
  } catch (error) {
    console.error('执行过程中出错:', error)
    process.exit(1)
  }
}

// 执行主函数
main()
