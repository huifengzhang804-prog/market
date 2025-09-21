<script lang="ts" setup>
import * as Vue from 'vue'
import plugin from './plugin'
import { storeToRefs } from 'pinia'
import { useDevListenerStore } from '@/store/modules/devListener'

enum LoadStatus {
  /**
   * 加载中
   */
  LOADING,
  /**
   * 加载成功
   */
  SUCCESS,
  /**
   * 加载失败
   */
  ERROR,
}

const loadStatus = ref(LoadStatus.LOADING)
const currentComponentRef = ref<any>(null)
/**
 * 远程组件
 */
const CurrentComponent = Vue.shallowRef()

const props = defineProps({
  /**
   * @deprecated
   */
  devUrl: {
    type: String,
    required: false,
    default: null,
  },
  /**
   * 组件渲染上下文
   */
  context: {
    type: Object,
    default: () => ({}),
  },
  /**
   * 服务接口名称
   */
  service: {
    type: String,
    required: true,
  },
  /**
   * 挂载到全局的组件名称
   */
  name: {
    type: String,
    default: 'value',
  },
  /**
   * 挂载到全局的上下文名称
   */
  contextName: {
    type: String,
    default: '',
  },
  /**
   * 传递到远程组件的属性
   */
  properties: {
    type: Object,
    default: () => ({}),
  },
  /**
   *  未加载到数据立即隐藏
   */
  hideOnMiss: {
    type: Boolean,
    default: false,
  },
})

Vue.onMounted(() =>
  loadRemoteComponent()
    .then((value) => {
      CurrentComponent.value = value
      loadStatus.value = value ? LoadStatus.SUCCESS : LoadStatus.ERROR
    })
    .catch((e) => {
      console.log(e)
      loadStatus.value = LoadStatus.ERROR
    }),
)
// 是否开发环境
const isDev = import.meta.env.MODE === 'dev'

/**
 * 通过远程url 加载远程组件
 */
function loadRemoteComponent() {
  console.log(`${isDev ? import.meta.env.VITE_PLUGIN_URL : import.meta.env.VITE_BASE_URL}${props.service}/public/${props.name}/index.umd.js`)
  //准备上下文
  return renderScript(
    // `${(isDev && props.devUrl) || import.meta.env.VITE_BASE_URL + props.service}/public/${props.name}/index.umd.js`,
    // 摒弃 devUrl 方式单独加载插件 连接插件总入口
    // 开发环境就加载 http://127.0.0.1:5173/ 插件总入口服务 生产环境直接加载后端服务
    `${isDev ? import.meta.env.VITE_PLUGIN_URL : import.meta.env.VITE_BASE_URL}${props.service}/public/pc/${props.name}/index.umd.js`,
    props.name,
    props.contextName || `${props.name}Context`,
    props.context,
  )
}

function renderScript(url: string, resultName: string, contextName?: string, context = {}) {
  contextName = contextName || `${resultName}Context`
  // script类型

  let script: HTMLScriptElement | null = null
  //渲染组件
  return new Promise((resolve, reject) => {
    let value
    if ((value = plugin.tryGet(url))) {
      resolve(value)
      return true
    }
    //@ts-ignore
    window[contextName] = { Vue, ...context }
    script = document.createElement('script')
    script.onload = () => {
      if ((value = plugin.get(url))) {
        resolve(value)
        return
      }
      //@ts-ignore
      value = window[resultName]
      resolve(plugin.set(url, value))
    }
    script.onerror = (event, source, lineno, colno, error) => {
      if ((value = plugin.get(url))) {
        resolve(value)
        return
      }
      reject({ event, error })
    }
    script.src = url
    document.body.appendChild(script)
  }).finally(() => {
    if (script) {
      document.body.removeChild(script)
    }
    plugin.remove(url).then((result) => {
      if (result) {
        return
      }
      //删除上下文
      //@ts-ignore
      delete window[contextName]
      //删除组件
      //@ts-ignore
      delete window[resultName]
    })
  })
}

const reloadComponents = (fn?: () => void) => {
  loadRemoteComponent()
    .then((value) => {
      CurrentComponent.value = value
    })
    .catch((e) => {
      loadStatus.value = LoadStatus.ERROR
    })
    .finally(() => {
      if (fn) fn()
    })
}

if (isDev) {
  watch(
    () => useDevListenerStore().getReloadState,
    () => {
      if (useDevListenerStore().getReloadPath === `${props.service}/${props.name}`) {
        reloadComponents()
      }
    },
  )
}

defineExpose({
  currentComponentRef,
})
</script>

<template>
  <el-skeleton v-show="loadStatus === LoadStatus.LOADING" :rows="6" />
  <current-component v-if="loadStatus === LoadStatus.SUCCESS" ref="currentComponentRef" :properties="props.properties" />
  <el-empty v-show="!hideOnMiss && loadStatus === LoadStatus.ERROR" description="未获取到远程组件" />
</template>

<style lang="scss" scoped></style>
