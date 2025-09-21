/**
 * 通用构建脚本 - 提供共享的构建功能
 * 这个脚本会被所有子包的批量构建脚本引用
 */

import fs from 'fs'
import path from 'path'
import { execSync } from 'child_process'

/**
 * 获取子包中的入口文件列表
 * @param packageDir - 子包目录路径
 * @returns - 入口文件名称数组
 */
export function getEntryFiles(packageDir: string): string[] {
    const entriesDir = path.join(packageDir, 'source/entries')

    if (!fs.existsSync(entriesDir)) {
        console.error(`错误: 入口目录不存在: ${entriesDir}`)
        return []
    }

    try {
        // 获取所有文件并过滤掉非.vue文件
        return fs
            .readdirSync(entriesDir)
            .filter((file) => file.endsWith('.vue'))
            .map((file) => file.replace('.vue', ''))
    } catch (error) {
        console.error(`获取入口文件时出错:`, error)
        return []
    }
}

/**
 * 复制构建结果到addon目录
 * @param packageDir - 子包目录路径
 * @returns - 是否复制成功
 */
export function copyToAddon(packageDir: string): boolean {
    const packageName = path.basename(packageDir)
    const sourceDir = path.join(packageDir, 'public')
    const targetDir = path.join(packageDir, '..', 'addon', packageName)

    if (!fs.existsSync(sourceDir)) {
        console.error(`错误: 构建结果目录不存在: ${sourceDir}`)
        return false
    }

    try {
        // 确保目标目录存在，如果已存在则清空
        if (fs.existsSync(targetDir)) {
            // 递归删除目录内容
            fs.rmSync(targetDir, { recursive: true, force: true })
            console.log(`已清空目标目录: ${targetDir}`)
        }

        // 创建目标目录
        fs.mkdirSync(targetDir, { recursive: true })

        // 复制文件
        const copyFiles = (source: string, target: string): void => {
            const files = fs.readdirSync(source)

            for (const file of files) {
                const sourcePath = path.join(source, file)
                const targetPath = path.join(target, file)
                const stat = fs.statSync(sourcePath)

                if (stat.isDirectory()) {
                    fs.mkdirSync(targetPath, { recursive: true })
                    copyFiles(sourcePath, targetPath)
                } else {
                    fs.copyFileSync(sourcePath, targetPath)
                }
            }
        }

        copyFiles(sourceDir, targetDir)
        console.log(`已成功复制构建结果到: ${targetDir}`)
        return true
    } catch (error) {
        console.error(`复制构建结果时出错:`, error)
        return false
    }
}

/**
 * 构建单个入口文件
 * @param fileName - 入口文件名称
 * @param packageDir - 子包目录路径
 * @returns - 构建是否成功
 */
export function buildEntry(fileName: string, packageDir: string): boolean {
    console.log(`开始构建入口: ${fileName}`)

    try {
        // 使用本地安装的vite
        execSync(`npx vite build`, {
            cwd: packageDir,
            stdio: 'inherit',
            env: {
                ...process.env,
                VITE_ENTRY_NAME: fileName,
                NODE_ENV: 'production',
            },
        })

        console.log(`入口 ${fileName} 构建成功`)
        return true
    } catch (error) {
        const errorMessage = error instanceof Error ? error.message : String(error)
        console.error(`入口 ${fileName} 构建失败:`, errorMessage)
        return false
    }
}

/**
 * 运行整个构建过程
 * @param options - 构建选项
 * @returns - 构建过程是否全部成功
 */
export interface BuildOptions {
    packageDir: string
}

export async function runBuildProcess({ packageDir }: BuildOptions): Promise<boolean> {
    console.log(`开始构建子包: ${path.basename(packageDir)}`)

    // 获取入口文件列表
    const entryFiles = getEntryFiles(packageDir)
    if (entryFiles.length === 0) {
        console.error('错误: 没有找到可构建的入口文件')
        return false
    }

    console.log(`找到 ${entryFiles.length} 个入口文件: ${entryFiles.join(', ')}`)

    // 记录构建结果
    let successCount = 0
    let failedCount = 0
    const failedEntries: string[] = []

    // 逐个构建入口文件
    for (const entry of entryFiles) {
        const success = buildEntry(entry, packageDir)

        if (success) {
            successCount++
        } else {
            failedCount++
            failedEntries.push(entry)
        }
    }

    // 如果所有入口都构建成功，复制结果到addon目录
    let copySuccess = true
    if (failedCount === 0) {
        copySuccess = copyToAddon(packageDir)
    }

    // 输出构建汇总
    console.log('\n构建结果汇总:')
    console.log(`成功构建: ${successCount} 个入口`)
    console.log(`失败构建: ${failedCount} 个入口`)

    if (failedCount > 0) {
        console.log('构建失败的入口:')
        failedEntries.forEach((entry) => console.log(`- ${entry}`))
    }

    if (!copySuccess) {
        console.log('警告: 构建结果复制失败')
    }

    // 整个过程成功的条件: 所有入口构建成功且复制成功
    return failedCount === 0 && copySuccess
}
