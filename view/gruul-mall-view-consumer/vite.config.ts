// @ts-nocheck
import { defineConfig, loadEnv } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { dirResolver, DirResolverHelper } from 'vite-auto-import-resolvers'
import AutoImport from 'unplugin-auto-import/vite'
import viteCompression from 'vite-plugin-compression'
import viteBuildmp, { argvExist } from './plugins/vite-plugin-buildmp'
import vitePreload from './plugins/vite-plugin-preload'
import path from 'path'
import modifyAssemble from './src/scripts/modify-assemble'
import vitePluginPagesPlatform from './src/scripts/vite-plugin-pages-platform'
import legacy from '@vitejs/plugin-legacy'
// 导入fs模块
import fs from 'fs'

// 项目路径中不能包含空格和中文字符
// 中文正则
const reg = /[\u4e00-\u9fa5]/
if (__dirname.includes(' ') || reg.test(__dirname)) {
  // 终止程序
  throw new Error(`项目路径:
        ${__dirname}
        中不能包含 [空格] 和 [中文]
        请修改后重试`)
}

const pathSrc = path.resolve(__dirname, 'src')
const isH5 = !argvExist('mp-weixin') && !argvExist('app')

export default defineConfig(({ mode }: any) => {
  const env = loadEnv(mode, process.cwd(), '') || {}
  return {
    base: '/h5/',
    server: {
      hmr: true,
      port: 7777,
      host: true,
      https: {
        cert: fs.readFileSync(path.join(__dirname, '/cert.crt')),
        key: fs.readFileSync(path.join(__dirname, '/cert.key')),
      },
      proxy: {
        '^/api': {
          target: env.VITE_BASE_URL, // 后台服务器的源
          changeOrigin: true, // 修改源
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
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
    plugins: [
      vitePluginPagesPlatform(),
      argvExist('mp-weixin')
        ? null
        : viteCompression({
            threshold: 10240,
          }),
      uni(),
      modifyAssemble(mode)(),
      vueJsx(),
      DirResolverHelper(),
      AutoImport({
        include: [/\.[tj]sx?$/, /\.ts$/, /\.vue$/],
        resolvers: [
          dirResolver({
            target: 'src/composables',
            include: ['useConvert', 'useMember', 'usePriceRange', 'useBottomSafe', 'useScreenHeight'],
          }),
        ],
        dts: path.resolve(pathSrc, '../types/auto-imports.d.ts'),
        eslintrc: {
          enabled: true,
          filepath: './types/.eslintrc-auto-import.json',
          globalsPropValue: true,
        },
      }),
      viteBuildmp(),
      vitePreload(),
      isH5
        ? legacy({
            targets: ['defaults', 'not IE 11'],
            renderLegacyChunks: false,
          })
        : null,
    ],
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@import "@/assets/css/global.scss";`,
        },
      },
    },
    optimizeDeps: {
      include: ['@amap/amap-jsapi-loader', '@vueuse/core', 'lodash', 'intersection-observer', 'decimal.js-light', 'qrcode-reader-vue3'],
    },
    build: {
      emptyOutDir: true,
      sourcemap: !mode === 'production',
      minify: 'terser',
      terserOptions: {
        compress: {
          drop_console: mode === 'production',
          drop_debugger: mode === 'production',
        },
      },
    },
  }
})
