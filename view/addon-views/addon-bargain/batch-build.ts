/**
 * 批量构建脚本 - 构建所有入口文件
 * 此脚本引用根目录下的通用构建脚本
 */

import { fileURLToPath } from 'node:url'
import path from 'path'
import { runBuildProcess } from '../batch-build-common'

// 获取当前文件的目录
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// 执行构建过程
runBuildProcess({ packageDir: __dirname })
  .then((success) => {
    if (!success) {
      process.exit(1)
    }
  })
  .catch((error) => {
    console.error('构建过程出错:', error)
    process.exit(1)
  })
