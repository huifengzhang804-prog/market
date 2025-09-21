import { resolve } from 'path'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import rollupOptions from '../rollup-config'
import Unocss from 'unocss/vite'
import { presetUno, presetAttributify, presetIcons } from 'unocss'
import { rules } from '../js/unocssRules'

//组件名称
// 从环境变量获取入口名称，或使用默认值
function getNameFromEnv() {
  return process.env.VITE_ENTRY_NAME || 'ShopCouponList'
}
const name = getNameFromEnv()
console.log('Building entry from env:', name)

// rollup配置
const { external, globals } = rollupOptions(name)
// https://cn.vitejs.dev/config/
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
    //输出目录
    outDir: `public/${name.startsWith('Pc') ? 'pc/' : ''}${name}`,
    //不拆分 css
    cssCodeSplit: true,
    //不复制public目录
    copyPublicDir: false,
    lib: {
      //输出的文件格式
      formats: ['umd'],
      //编译的目标文件
      //@ts-ignore
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
