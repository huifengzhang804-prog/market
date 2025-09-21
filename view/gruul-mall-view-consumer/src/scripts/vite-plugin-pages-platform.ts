/**
 * Vite 插件：根据平台修改 pages.json 文件
 * 在构建过程开始时自动运行，根据平台添加或移除 devTools 相关配置
 */
import type { Plugin, ResolvedConfig } from 'vite'
import fs from 'fs'
import path from 'path'
import { parseJsonWithComments } from '../utils/jsonUtils'
import prettier from 'prettier'

export default function vitePluginPagesPlatform(): Plugin {
  let config: ResolvedConfig

  return {
    name: 'vite-plugin-pages-platform',

    configResolved(resolvedConfig) {
      config = resolvedConfig
    },

    async buildStart() {
      const platform = process.env.UNI_PLATFORM || ''
      const isMP = platform === 'mp-weixin'
      const isApp = platform === 'app' || platform === 'app-plus'

      // 读取 pages.json 文件
      const pagesJsonPath = path.resolve(process.cwd(), 'src/pages.json')

      if (!fs.existsSync(pagesJsonPath)) {
        console.error('pages.json 文件不存在')
        return
      }

      try {
        const pagesJsonContent = fs.readFileSync(pagesJsonPath, 'utf8')
        // 解析 JSON，自动移除注释
        const pagesJson = parseJsonWithComments(pagesJsonContent)

        // 如果是小程序平台，移除 devTools 相关配置
        if (isMP) {
          // 过滤掉 devTools 相关的子包
          pagesJson.subPackages = pagesJson.subPackages.filter((pkg: any) => pkg.name !== 'devToolsPage')
          console.log('已移除 devTools 相关配置（小程序平台）')
        }
        // 如果是 App 平台，确保 devTools 相关配置存在
        else if (isApp) {
          // 检查是否已存在 devTools 相关配置
          const hasDevTools = pagesJson.subPackages.some((pkg: any) => pkg.name === 'devToolsPage')

          if (!hasDevTools) {
            // 添加 devTools 相关配置
            pagesJson.subPackages.push({
              root: 'devTools/page',
              name: 'devToolsPage',
              pages: [
                {
                  path: 'index',
                  style: {
                    navigationStyle: 'custom',
                    softinputMode: 'adjustResize',
                    backgroundColor: 'transparent',
                    animationDuration: 1,
                    animationType: 'none',
                    popGesture: 'none',
                    bounce: 'none',
                    titleNView: false,
                  },
                },
              ],
            })
            console.log('已添加 devTools 相关配置（App 平台）')
          }
        }

        // 使用 Prettier 格式化 JSON
        const prettierConfig = await prettier.resolveConfig(process.cwd())
        const formattedJson = await prettier.format(JSON.stringify(pagesJson), {
          ...prettierConfig,
          parser: 'json',
        })

        // 对比处理后的内容与原始内容，只有当有变化时才写回文件
        const currentContent = fs.readFileSync(pagesJsonPath, 'utf8')
        const formattedCurrentContent = await prettier.format(currentContent, {
          ...prettierConfig,
          parser: 'json',
        })

        if (formattedJson !== formattedCurrentContent) {
          fs.writeFileSync(pagesJsonPath, formattedJson, 'utf8')
          console.log('pages.json 已更新')
        } else {
          console.log('pages.json 无变化，跳过写入')
        }
      } catch (error) {
        console.error('处理 pages.json 文件时出错:', error)
      }
    },
  }
}
