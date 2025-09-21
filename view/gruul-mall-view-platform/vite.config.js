/// <reference types="vitest" />
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'
import Components from 'unplugin-vue-components/vite'
import AutoImport from 'unplugin-auto-import/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import VueSetupExtend from 'vite-plugin-vue-setup-extend'
import Inspect from 'vite-plugin-inspect'
import viteCompression from 'vite-plugin-compression'
// import { dirResolver, DirResolverHelper } from 'vite-auto-import-resolvers'
import fixEnvForVite from './plugins/fixEnvForVite'
import path from 'path'
import DefineOptions from 'unplugin-vue-define-options/vite'
import { nodeResolve } from '@rollup/plugin-node-resolve'
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

export default defineConfig(({ mode }) => {
    const env = loadEnv(mode, process.cwd(), '') || {}
    return {
        base: '/platform/',
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
                // 开发环境统一使用 vite server 代理请求 防止跨域, 这样可以在开发环境模拟 https
                '^/proxy': {
                    target: env.VITE_BASE_URL, // 后台服务器的源
                    changeOrigin: true, // 修改源
                    rewrite: (path) => path.replace(/^\/proxy/, ''),
                },
            },
        },
        test: {
            globals: true,
            environment: 'happy-dom',
            // 支持t/jsx
            transformMode: {
                web: [/.[tj]sx$/],
            },
            coverage: {
                provider: 'istanbul',
            },
        },
        define: {
            __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: true,
        },
        resolve: {
            alias: {
                vue: 'vue/dist/vue.esm-bundler.js',
                '@': pathSrc,
                '@components': path.resolve(__dirname, 'src/components'),
                '@views': path.resolve(__dirname, 'src/views'),
                '@apis': path.resolve(__dirname, 'src/apis'),
                '@utils': path.resolve(__dirname, 'src/utils'),
                '@hooks': path.resolve(__dirname, 'src/hooks'),
                '~': path.resolve(__dirname, 'node_modules'),
                '#': path.resolve(__dirname),
            },
            extensions: ['.tsx', '.js', '.ts', '.json'],
        },
        plugins: [
            vue(),
            DefineOptions(),
            VueSetupExtend(),
            vueJsx(),
            // DirResolverHelper(),
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
                    // dirResolver({
                    //     target: 'src/composables',
                    //     prefix: 'use',
                    // }),
                ],
                dirs: ['src/AutoImportCustomUse'],
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
                dirs: ['src/components'],
                dts: path.resolve(pathSrc, '../types/components.d.ts'),
            }),
            Icons({
                autoInstall: true,
            }),
            Inspect(),
            viteCompression({
                verbose: true,
                disable: false,
                threshold: 10240,
                algorithm: 'gzip',
                ext: '.gz',
            }),
            fixEnvForVite(mode),
            nodeResolve({
                extensions: ['.mjs', '.js'],
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
                // 生产环境清除console和debugger
                compress: {
                    drop_console: mode === 'prod',
                    drop_debugger: mode === 'prod',
                },
            },
        },
    }
})
