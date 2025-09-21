/**
 * 脚本用于只对最近git更改的文件运行eslint修复
 * 用法:
 *  - 默认模式: node fix-changed-files.js
 *  - 检查最近提交: node fix-changed-files.js --last-commit
 *  - 检查最近N次提交: node fix-changed-files.js --commits=N
 *  - 检查指定文件: node fix-changed-files.js file1.js file2.js
 */
import { execSync, ExecSyncOptions } from 'child_process'
import { existsSync } from 'fs'
import path from 'path'

// 设置执行命令的选项，解决Windows下的乱码问题
const execOptions: ExecSyncOptions = {
  stdio: 'pipe', // 使用pipe而不是inherit，这样我们可以自己处理输出
  encoding: 'utf8' as BufferEncoding, // 强制UTF-8编码
  maxBuffer: 1024 * 1024 * 10, // 增加缓冲区大小，避免大输出截断
}

// 自定义的执行命令函数，处理输出并避免乱码
function safeExec(command: string): string {
  try {
    return execSync(command, execOptions).toString().trim()
  } catch (error: any) {
    if (error.stdout) {
      console.error(`命令执行出错: ${command}`)
      console.error(`错误信息: ${error.message}`)
      return error.stdout.toString().trim()
    }
    return ''
  }
}

// 处理命令行参数
const args: string[] = process.argv.slice(2)
const checkLastCommit: boolean = args.includes('--last-commit')
const commitsArg: string | undefined = args.find((arg) => arg.startsWith('--commits='))
const commitsCount: number = commitsArg ? parseInt(commitsArg.split('=')[1], 10) : 0
// 过滤掉选项参数，剩下的可能是文件路径
const filePaths: string[] = args.filter((arg) => !arg.startsWith('--'))

try {
  let files: string[] = []

  // 如果提供了文件路径（由lint-staged调用时），直接使用这些文件
  if (filePaths.length > 0) {
    files = filePaths.filter((file) => existsSync(file)).map((file) => path.resolve(file))

    console.log(`使用lint-staged提供的文件列表...`)
  } else {
    // 获取所有类型的修改文件（未暂存+已暂存+与远程分支的差异）
    let gitOutput: string = ''

    // 如果指定了检查最近提交
    if (checkLastCommit) {
      try {
        const lastCommitFiles: string = safeExec('git diff --name-only --diff-filter=ACMR HEAD~1 HEAD')
        if (lastCommitFiles) gitOutput += lastCommitFiles + '\n'
        console.log('正在检查最近一次提交的文件...')
      } catch (e: any) {
        console.log('检查最近提交时出错:', e.message)
      }
    }
    // 如果指定了检查最近N次提交
    else if (commitsCount > 0) {
      try {
        const recentCommitFiles: string = safeExec(`git diff --name-only --diff-filter=ACMR HEAD~${commitsCount} HEAD`)
        if (recentCommitFiles) gitOutput += recentCommitFiles + '\n'
        console.log(`正在检查最近 ${commitsCount} 次提交的文件...`)
      } catch (e: any) {
        console.log(`检查最近 ${commitsCount} 次提交时出错:`, e.message)
      }
    }
    // 默认检查工作区和暂存区
    else {
      // 检查工作区未暂存的修改
      try {
        const unstaged: string = safeExec('git diff --name-only --diff-filter=ACMR')
        if (unstaged) gitOutput += unstaged + '\n'
      } catch (e: any) {
        console.log('检查未暂存修改时出错:', e.message)
      }

      // 检查暂存区的修改
      try {
        const staged: string = safeExec('git diff --name-only --staged --diff-filter=ACMR')
        if (staged) gitOutput += staged + '\n'
      } catch (e: any) {
        console.log('检查已暂存修改时出错:', e.message)
      }

      // 检查与远程分支的差异（如果有远程分支）
      try {
        const currentBranch: string = safeExec('git rev-parse --abbrev-ref HEAD')
        // 使用try-catch方式获取上游分支，避免错误信息
        let remoteBranch: string = ''
        try {
          remoteBranch = safeExec(`git rev-parse --abbrev-ref ${currentBranch}@{upstream}`)
        } catch (e) {
          // 忽略错误，可能没有上游分支
        }

        if (remoteBranch) {
          const remoteDiff: string = safeExec(`git diff --name-only --diff-filter=ACMR ${remoteBranch}...HEAD`)
          if (remoteDiff) gitOutput += remoteDiff + '\n'
        }
      } catch (e) {
        // 忽略错误，可能没有上游分支
      }
    }

    if (!gitOutput) {
      console.log('没有检测到修改的文件')
      process.exit(0)
    }

    // 去重
    const allFiles: string[] = [...new Set(gitOutput.split('\n').filter(Boolean))]

    // 过滤出需要检查的文件，排除scripts目录的文件
    const fileRegex: RegExp = /\.(js|jsx|ts|tsx|vue|mjs|cjs)$/
    files = allFiles
      .filter((file) => fileRegex.test(file))
      .filter((file) => existsSync(file))
      .map((file) => path.resolve(file))
  }

  if (files.length === 0) {
    console.log('没有需要修复的前端文件')
    process.exit(0)
  }

  console.log(`正在对 ${files.length} 个文件进行ESLint修复...`)
  console.log(files.join('\n'))

  // 执行eslint修复，不再需要手动忽略scripts目录
  const eslintCommand: string = `eslint --fix --cache --no-warn-ignored ${files.map((file) => `"${file}"`).join(' ')}`
  // 对于ESLint命令，使用inherit以便看到输出结果
  execSync(eslintCommand, { stdio: 'inherit' })

  console.log('ESLint修复完成!')
} catch (error: any) {
  console.error('运行过程中出错:', error.message)
  process.exit(1)
}
