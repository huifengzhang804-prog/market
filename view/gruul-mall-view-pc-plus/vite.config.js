import { defineConfig } from 'vite'
import Unocss from 'unocss/vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'
import Components from 'unplugin-vue-components/vite'
import AutoImport from 'unplugin-auto-import/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import VueSetupExtend from 'vite-plugin-vue-setup-extend'
import Inspect from 'vite-plugin-inspect'
import { presetAttributify, presetIcons, presetWind3 } from 'unocss'
import path from 'path'
import { rules } from './src/assets/js/unocssRules'
import fs from 'fs'

const optimizeDepsElementPlusIncludes = ['element-plus/es']
fs.readdirSync('node_modules/element-plus/es/components').forEach((dirname) => {
  try {
    fs.accessSync(`node_modules/element-plus/es/components/${dirname}/style/css.mjs`)
    optimizeDepsElementPlusIncludes.push(`element-plus/es/components/${dirname}/style/css`)
  } catch (error) {
    return ''
  }
})

const pathSrc = path.resolve(__dirname, 'src')
export default defineConfig({
  base: '/pc/',
  optimizeDeps: {
    include: optimizeDepsElementPlusIncludes,
  },
  server: {
    port: 8000,
    host: true,
    // https: {
    //     cert: fs.readFileSync(path.join(__dirname, '/cert.crt')),
    //     key: fs.readFileSync(path.join(__dirname, '/cert.key')),
    // },
    proxy: {
      '/api': {
        target: 'https://pro.bgniao.cn/',
        changeOrigin: true,
      },
      '^/proxy': {
        target: 'https://premall.superprism.cn/api/', // 后台服务器的源
        changeOrigin: true, // 修改源
        rewrite: (path) => path.replace(/^\/proxy/, ''),
      },
    },
  },
  resolve: {
    alias: {
      vue: 'vue/dist/vue.esm-bundler.js',
      '@': pathSrc,
      comps: path.resolve(__dirname, 'src/components'),
      views: path.resolve(__dirname, 'src/views'),
      apis: path.resolve(__dirname, 'src/apis'),
      utils: path.resolve(__dirname, 'src/utils'),
      '~': path.resolve(__dirname, 'node_modules'),
    },
    extensions: ['.tsx', '.js', '.ts', '.json'],
  },
  plugins: [
    vue(),
    VueSetupExtend(),
    vueJsx(),
    AutoImport({
      include: [/\.[tj]sx?$/, /\.ts$/, /\.vue$/],
      // 自动导入 Vue 相关函数，
      imports: ['vue', 'vue-router'],
      // 自动导入 Element Plus 相关函数，如：ElMessage, ElMessageBox... (带样式)
      resolvers: [
        ElementPlusResolver(),
        IconsResolver({
          prefix: 'Icon',
        }),
      ],
      dirs: ['src/composables'],
      dts: path.resolve(pathSrc, '../types/auto-imports.d.ts'),
      eslintrc: {
        enabled: true,
        filepath: './types/.eslintrc-auto-import.json',
        globalsPropValue: true,
      },
    }),
    Components({
      resolvers: [
        // 自动注册图标组件
        IconsResolver({
          enabledCollections: ['ep'],
        }),
        // 自动导入 Element Plus 组件
        ElementPlusResolver(),
      ],
      dts: path.resolve(pathSrc, '../types/components.d.ts'),
    }),
    Icons({
      autoInstall: true,
    }),
    Inspect(),
    Unocss({
      presets: [
        presetAttributify({
          // 设置attributify模式的配置
          prefix: '', // 前缀为空
          strict: false, // 非严格模式
          nonValuedAttribute: true, // 支持无值属性
          ignoreAttributes: [], // 不忽略任何属性
        }),
        presetWind3(),
        presetIcons(),
      ],
      rules,
      preflights: [],
    }),
  ],
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@import "@/assets/css/global.scss";`,
      },
    },
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            return id.toString().split('node_modules/')[1].split('/')[0].toString()
          }
        },
        //入口文件输出配置
        entryFileNames: `assets/js/[name]-[hash].js`,
        //代码分割后的文件输出配置
        chunkFileNames: `assets/js/[name]-[hash].js`,
        //静态资源输出配置
        assetFileNames(assetInfo) {
          //css文件单独输出到css文件夹
          if (assetInfo.name.endsWith('.css')) {
            return `assets/css/[name]-[hash].css`
          }
          //图片文件单独输出到img文件夹
          else if (['.png', '.jpg', '.jpeg', '.gif', '.svg', '.webp'].some((ext) => assetInfo.name.endsWith(ext))) {
            return `assets/img/[name]-[hash].[ext]`
          }
          //字体文件单独输出到font文件夹
          else if (['.woff2', '.woff', '.ttf'].some((ext) => assetInfo.name.endsWith(ext))) {
            return `assets/font/[name]-[hash].[ext]`
          }
          //媒体文件单独输出到media文件夹
          else if (['.mp3', '.mp4', '.avi'].some((ext) => assetInfo.name.endsWith(ext))) {
            return `assets/media/[name]-[hash].[ext]`
          }
          //其他资源输出到assets文件夹
          else {
            return `assets/[name]-[hash].[ext]`
          }
        },
      },
    },
    minify: 'terser',
    chunkSizeWarningLimit: 1000, // 提高超大静态资源警告大小
    terserOptions: {
      // 清除console和debugger
      compress: {
        drop_console: process.env.NODE_ENV === 'production',
        drop_debugger: process.env.NODE_ENV === 'production',
      },
    },
  },
})
