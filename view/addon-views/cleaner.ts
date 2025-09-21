/**
 * 清理构建产物的工具模块
 */

import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'node:url'

// 获取当前文件的目录路径
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

/**
 * 安全地清空或删除目录
 * @param dirPath 要清空或删除的目录路径
 * @param options 选项
 * @returns 操作是否成功
 */
export function cleanDirectory(dirPath: string, options: { createIfNotExist?: boolean } = {}): boolean {
  const { createIfNotExist = false } = options

  try {
    // 检查目录是否存在
    if (!fs.existsSync(dirPath)) {
      if (createIfNotExist) {
        fs.mkdirSync(dirPath, { recursive: true })
        return true
      }
      return true
    }

    // 使用 fs.rmSync 一次性删除目录及其所有内容
    fs.rmSync(dirPath, { recursive: true, force: true })

    // 如果需要，重新创建空目录
    if (createIfNotExist) {
      fs.mkdirSync(dirPath, { recursive: true })
    }

    return true
  } catch (error) {
    console.error(`清理目录时出错 ${dirPath}:`, error)
    return false
  }
}

/**
 * 清理特定子包的构建产物
 * @param packageName 子包名称
 * @returns 是否清理成功
 */
export function cleanPackageBuildOutput(packageName: string): boolean {
  try {
    const packageDir = path.join(__dirname, packageName)
    const publicDir = path.join(packageDir, 'public')
    const addonTargetDir = path.join(__dirname, 'addon', packageName)

    let success = true

    // 清理子包的 public 目录（构建输出目录）
    if (fs.existsSync(publicDir)) {
      const publicCleaned = cleanDirectory(publicDir, { createIfNotExist: true })
      if (!publicCleaned) {
        success = false
      }
    }

    // 清理 addon 目录中对应的子包目录
    if (fs.existsSync(addonTargetDir)) {
      const addonCleaned = cleanDirectory(addonTargetDir, { createIfNotExist: true })
      if (!addonCleaned) {
        success = false
      }
    }

    return success
  } catch (error) {
    console.error(`清理 ${packageName} 构建产物时出错:`, error)
    return false
  }
}

/**
 * 清理所有构建产物
 * @param packageNames 子包名称数组
 * @returns 是否清理成功
 */
export function cleanAllBuildOutput(packageNames: string[]): boolean {
  try {
    // 清理 addon 根目录（但保留目录本身）
    const addonDir = path.join(__dirname, 'addon')
    if (fs.existsSync(addonDir)) {
      // 读取目录内容并删除
      const items = fs.readdirSync(addonDir)
      for (const item of items) {
        const itemPath = path.join(addonDir, item)
        fs.rmSync(itemPath, { recursive: true, force: true })
      }
    } else {
      // 创建 addon 目录
      fs.mkdirSync(addonDir, { recursive: true })
    }

    // 清理所有子包的产物
    let success = true
    packageNames.forEach((packageName) => {
      const packageCleaned = cleanPackageBuildOutput(packageName)
      if (!packageCleaned) success = false
    })

    return success
  } catch (error) {
    console.error('清理构建产物时出错:', error)
    return false
  }
}
