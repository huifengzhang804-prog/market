import { defineConfig } from 'vitest/config'
import path from 'path'

export default defineConfig({
  test: {
    // vitest 启用全局模式 将常见的测试方法作为全局变量
    // 如 describe, it, expect, test 等
    globals: true,
    // 测试文件运行环境
    environment: 'node',
    coverage: {
      enabled: false,
      provider: 'v8',
      cleanOnRerun: true,
      reporter: ['html'],
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '@pluginPackage': path.resolve(__dirname, 'src/pluginPackage'),
      '@decoration': path.resolve(__dirname, 'src/decoration'),
      '@plugin': path.resolve(__dirname, 'src/pages/plugin'),
    },
  },
})
