/*
 * 自动化构建小程序组件分包异步化插件
 */
import type { Plugin, ResolvedConfig } from 'vite'
import { exec } from 'child_process'
import { log, bold } from 'console-log-colors'

export function argvExist(argv: string) {
  return process.argv.includes(argv)
}

export default function myVitePluginVirtual(): Plugin {
  let config: ResolvedConfig | null = null
  return {
    name: 'vite-plugin-buildmp',
    apply(config, { command }) {
      return command === 'build' && !config?.build?.ssr
    },
    enforce: 'post',
    configResolved(c: ResolvedConfig) {
      config = c
    },
    closeBundle() {
      if (argvExist('mp-weixin')) {
        const isProd = argvExist('build')
        const command = isProd ? 'prod' : 'dev'
        // 判断当前环境是windows还是mac
        const isWindows = process.platform === 'win32'
        exec(`${isWindows ? 'set' : 'export'} NODE_ENV=${command} && npm run buildmp`, (error, stdout, stderr) => {
          log.white(bold('正在分包组件差量分析...'))
          if (error) {
            log.red(`执行出错: ${error}`)
            return
          }
          log.white(`stdout: ${stdout}`)
          // log.red(`stderr: ${stderr}`)
        })
      }
    },
  }
}
