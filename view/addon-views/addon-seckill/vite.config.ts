import { resolve } from 'path'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import rollupOptions from '../rollup-config'
import Unocss from 'unocss/vite'
import { presetUno, presetAttributify, presetIcons } from 'unocss'
import { rules } from '../js/unocssRules'

// 从环境变量获取name值
function getNameFromEnv() {
  // 从环境变量获取name值
  const envName = process.env.VITE_ENTRY_NAME

  if (envName) {
    console.log(`Building entry from env: ${envName}`)
    return envName
  }

  // 如果没有指定环境变量，则使用默认值
  return 'ShopSeckillList'
}

// 获取name值
const name = getNameFromEnv()
console.log(`Building entry: ${name}`)

const { external, globals } = rollupOptions(name)

export default defineConfig({
  plugins: [
    vue(),
    // @ts-ignore
    Unocss({
      presets: [presetUno(), presetAttributify(), presetIcons()],
      rules,
    }),
  ],
  build: {
    //插件文件输出目录
    outDir: `public/${name.startsWith('Pc') ? 'pc/' : ''}${name}`,
    //不拆分 css
    cssCodeSplit: true,
    //不复制public目录
    copyPublicDir: false,
    lib: {
      //输出的文件格式
      formats: ['umd'],
      //编译的目标文件
      entry: resolve(__dirname, `source/entries/${name}.vue`),
      //挂载到全局对象的里的属性名
      name: name,
      //输出的文件名称
      fileName: (format) => `index.${format}.js`,
    },
    //排除的依赖配置 与其挂载到全局对象的别名
    rollupOptions: {
      external,
      output: {
        globals,
      },
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@import "../mixins/mixins.scss";`,
      },
    },
  },
})
