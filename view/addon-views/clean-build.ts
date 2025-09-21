/**
 * 清理构建产物
 * 该脚本将清理addon目录下的所有文件以及所有子包的public目录下的文件
 */

import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'node:url'
import allPackages from './batchBuildDirsNames'

// 获取当前文件的目录路径（ESM 中不存在 __dirname）
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// 清空指定目录下的所有文件和文件夹
function emptyDir(dirPath: string): void {
    if (!fs.existsSync(dirPath)) {
        console.log(`目录不存在: ${dirPath}`)
        return
    }

    try {
        const files = fs.readdirSync(dirPath)
        for (const file of files) {
            const filePath = path.join(dirPath, file)
            if (fs.lstatSync(filePath).isDirectory()) {
                // 递归删除子目录
                emptyDir(filePath)
                fs.rmdirSync(filePath)
            } else {
                // 删除文件
                fs.unlinkSync(filePath)
            }
        }
        console.log(`已清空目录: ${dirPath}`)
    } catch (error) {
        console.error(`清空目录失败: ${dirPath}`, error)
    }
}

// 清理addon目录
const addonDir = path.join(__dirname, 'addon')
console.log('开始清理 addon 目录...')
emptyDir(addonDir)

// 清理所有子包的public目录
console.log('\n开始清理所有子包的 public 目录...')
let successCount = 0
let failedCount = 0

allPackages.forEach((packageName, index) => {
    const publicDir = path.join(__dirname, packageName, 'public')
    const fraction = `${index + 1}/${allPackages.length}`

    try {
        if (fs.existsSync(publicDir)) {
            emptyDir(publicDir)
            console.log(`[${fraction}] ✓ ${packageName}/public`)
            successCount++
        } else {
            console.log(`[${fraction}] - ${packageName}/public (目录不存在)`)
            failedCount++
        }
    } catch (error) {
        console.log(`[${fraction}] ✗ ${packageName}/public (清理失败)`)
        console.error(error)
        failedCount++
    }
})

console.log('\n------------------------------')
console.log(`清理完成: 成功 ${successCount} 个, 失败 ${failedCount} 个`)
