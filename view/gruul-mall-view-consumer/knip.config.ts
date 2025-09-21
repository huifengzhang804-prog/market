import type { KnipConfig } from 'knip'
import pages from './src/pages.json' with { type: 'json' }

type Pages = {
  pages: {
    path: string
  }[]
  subPackages: {
    root: string
    pages: {
      path: string
    }[]
  }[]
}

/**
 * knip 检查的入口页面列表 解析 pages.json 得到 uniapp 定义的所有页面列表
 * 所有页面列表来自于:
 * 1. pages.pages
 * 2. pages.subPackages
 */
const entryPagesList: string[] = ['src/main.ts', 'types/auto-imports.d.ts']
;(pages as Pages).pages.forEach((page) => {
  entryPagesList.push(`src/${page.path}.vue`)
})
;(pages as Pages).subPackages.forEach((subPackage) => {
  subPackage.pages.forEach((page) => {
    entryPagesList.push(`src/${subPackage.root}/${page.path}.vue`)
  })
})

const config: KnipConfig = {
  entry: entryPagesList,
  project: [
    'src/**/*.ts',
    'src/**/*.tsx',
    'src/**/*.js',
    '!src/asyncPackages/uqrcode/**',
    'src/**/*.jsx',
    '!src/**/*.d.ts',
    'src/**/*.vue',
    '!src/composables/*.ts',
    '!src/pluginPackage/liveModule/**/*',
    '!src/uni_modules/vk-uview-ui/**/*',
    '!src/utils/**/*',
  ],
}

export default config
