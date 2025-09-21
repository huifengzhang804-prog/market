# 前端插件项目

## 介绍

本项目是**在后端某些服务停止时,前端和该服务无关的页面仍然能正常展示**以及**在为不同用户赋予不同模块权限**的需求背景下诞生的

本项目在父项目中就是表现为一个**\<q-plugin /\>**组件:

```vue
<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { post, get } from '@/apis/http'
</script>
<template>
  <q-plugin
    :context="{
      Request: { post, get },
    }"
    name="ShopAddDiscountActive"
    service="addon-full-reduction"
  />
</template>
```

如以上就是商家端添加满减活动的页面组件,它通过 service 属性找到对应的插件项目**addon-full-reduction**,再通过 name 属性对应着插件**addon-full-reduction**下的入口文件:**ShopAddDiscountActive.vue**

总的路径就是: **/addon-full-reduction/source/entries/ShopAddDiscountActive.vue**

该路径经过

```
npm run build
```

之后将会编译在**/addon-full-reduction/public/ShopAddDiscountActive/index.umd.js**文件中,

开发环境下父项目的 q-plugin 组件会去尝试访问 **http://localhost:5173/addon-full-reduction/public/ShopAddDiscountActive/index.umd.js** 文件读取前端静态文件进行页面的渲染

## 目录结构规范

```tree
addon-views
	addon-xxxx             插件名
    ├─public                    打包目录
    |   ├─*/index.umd.js        打包生成的文件
    ├─source                    src源代码文件夹
    |  ├─entries                入口文件夹
    |  |    ├─*.vue             入口文件
    |  ├─components             组件文件夹
    |  |     ├─*.vue            组件
    |  ├─apis.ts                api接口请求文件
    |  ├─index.ts               类型声明/通用函数文件
    ├─package.json              依赖配置文件
    ├─tsconfig.json             ts配置文件
    ├─tsconfig.node.json        node配置文件
    ├─vite.config.ts            打包配置文件
    ├─batch-build.ts            单包构建脚本
    ├─batch-build-unified.ts    单入口构建脚本
    addon-othersxx         其他插件名
    ├─***
├─mixins						集成的样式
|   ├─*.scss					分模块的样式文件
├─types							全局定义类型文件
|   ├─**.d.ts					分模块定义类型文件
├─batch-build.ts				批量打包脚本(npm run build)
├─batch-build-common.ts			公共构建函数库
├─batchBuildDirsNames.ts		所有插件的入口(新增插件项目就要在此处添加一个新增的项目名称)
├─package.json					总配置文件
├─README.md						自述文档
├─rollup-config.ts				rollup忽略配置文件(父项目传进来的模块)
├─tsconfig.json					ts配置文件(勿轻易改动)
├─vite-build.ts					持续监听并自动打包的脚本(npm run build:w)
├─.eslintrc.cjs					ESLint配置文件
├─.prettierrc.cjs				Prettier配置文件

```

## 项目介绍

- addon-bargain 砍价
- addon-coupon 优惠券
- addon-distribute 分销
- gruul-mall-freight 物流( 原名称: addon-freight )
- addon-full-reduction 满减
- addon-integral 积分
- addon-ic 同城配送
- addon-live 直播
- addon-matching-treasure 套餐价
- addon-member 付费会员
- addon-rebate 消费返利
- addon-seckill 秒杀
- addon-shop-store 门店
- addon-supplier 供应商
- addon-team 拼团
- addon-template 插件模板项目( 新增的插件可以从这里复制 )

## 插件开发流程

### 1. 分开开发 😟( 3.0.0 及以上版本不推荐这种开发方式 )

- 复制插件模板项目 如 cp -r addon-template addon-xxx
- cd addon-xxx
- npm install
- 开发插件
- 打包插件 npm run build
- 调试阶段可以使用 npm run dev
- 尝试访问打包后的文件 http://localhost:5173/public/xxx/index.umd.js

### 2. 批量开发 😄

- 根目录直接**`npm i`**安装依赖(子目录内无需安装)
- 直接在根目录执行**`npm run dev`**开启开发服务器
- 尝试访问打包后的文件 http://localhost:5173/addon-xxx/public/xxx/index.umd.js
- **开发过程中**:
  - 在根目录执行**`npm run build:w`**开启持续监听打包模式
  - 修改文件后会自动重新打包并热更新，无需手动刷新页面
  - 如果监听过程报错，请按照命令行给出的错误信息定位问题并修改
  - 所有`.ts`文件中的导入路径使用正常路径即可，无需添加任何后缀
- **代码格式化**:
  - 执行**`npm run fix`**进行全局代码格式化和 lint 检查
  - 注意避免不必要的字符串连接，使用模板字符串代替
- **单包开发调试**:
  - 可以在子包目录执行**`npm run build`**仅构建单个子包
  - 开发某个特定入口文件时，需要修改子包目录下的`batch-build-unified.ts`文件中的`targetFileName`值
  - 例如：`const targetFileName = 'MyEntryFile'` // 对应 source/entries/MyEntryFile.vue
- **上线过程中**:
  - 所有项目开发完毕后，在根目录执行**`npm run build`**进行批量静态打包
  - 打包结果会存放在**`addon/`**文件夹中
  - 将此文件夹交给运维/后端人员即可

## 构建模式详解

项目提供了两种主要的构建模式：**批量构建模式**和**监听构建模式**，以下是这两种模式的详细说明。

### 批量构建模式 (npm run build)

该模式适用于开发完成后，一次性构建所有插件项目。

```bash
npm run build
```

执行后，终端将显示类似以下输出：

```
> addon@3.0.2 build
> tsx ./batch-build.ts

开始执行统一构建 (多线程模式)...

准备清理所有构建产物...
清理完成，准备开始构建

[===>                                               ] 6.7% (1/15) | ✓ addon-distribute | 活跃线程: 8/8 | 队列: 6
[======>                                            ] 13.3% (2/15) | ✓ addon-matching-treasure | 活跃线程: 8/8 | 队列: 5
[==========>                                        ] 20.0% (3/15) | ✓ addon-member | 活跃线程: 8/8 | 队列: 4
[=============>                                     ] 26.7% (4/15) | ✓ addon-live | 活跃线程: 8/8 | 队列: 3
[================>                                  ] 33.3% (5/15) | ✓ addon-full-reduction | 活跃线程: 8/8 | 队列: 2
[====================>                              ] 40.0% (6/15) | ✓ gruul-mall-freight | 活跃线程: 8/8 | 队列: 1
[=======================>                           ] 46.7% (7/15) | ✓ addon-coupon | 活跃线程: 8/8 | 队列: 0
[==========================>                        ] 53.3% (8/15) | ✓ addon-bargain | 活跃线程: 7/8 | 队列: 0
[==============================>                    ] 60.0% (9/15) | ✓ addon-ic | 活跃线程: 6/8 | 队列: 0
[=================================>                 ] 66.7% (10/15) | ✓ addon-integral | 活跃线程: 5/8 | 队列: 0
[====================================>              ] 73.3% (11/15) | ✓ addon-rebate | 活跃线程: 4/8 | 队列: 0
[========================================>          ] 80.0% (12/15) | ✓ addon-team | 活跃线程: 3/8 | 队列: 0
[===========================================>       ] 86.7% (13/15) | ✓ addon-shop-store | 活跃线程: 2/8 | 队列: 0
[==============================================>    ] 93.3% (14/15) | ✓ addon-seckill | 活跃线程: 1/8 | 队列: 0
[==================================================>] 100.0% (15/15) | ✓ addon-supplier | 活跃线程: 0/8 | 队列: 0

------------------------------
构建完成: 成功 15 个, 失败 0 个, 耗时 46.92s
准备关闭工作线程池...
工作线程池已关闭，退出进程
```

这种多线程构建模式利用了系统的多核心能力，同时可以看到实时的构建进度、已完成的包、活跃的线程数量以及队列中待处理的任务数。

### 监听构建模式 (npm run build:w)

该模式适用于开发过程中，能够监听文件变化并自动重新构建受影响的入口文件。每个入口文件构建完成后，会触发 9998 端口的 websocket 服务，在父项目中会自动读取最新的构建产物并进行页面渲染。

```bash
npm run build:w
```

执行后，终端将显示类似以下输出：

```
🔍 准备监听 15 个子包的变化...
📡 已设置 15 个子包的监听
🚀 文件监听服务已启动，等待文件变化...
```

当你修改一个文件（例如 `apis.ts`）时，系统会自动检测依赖关系并只构建受影响的入口：

```
🔄 addon-supplier/PlatformOverviewBasic 依赖于文件 apis.ts
🔄 addon-supplier/ShopConsignment 依赖于文件 apis.ts
🔨 开始构建: addon-supplier/PlatformOverviewBasic
[addon-supplier/PlatformOverviewBasic] ✓ built in 442ms
✅ 构建成功: addon-supplier/PlatformOverviewBasic
🔨 开始构建: addon-supplier/ShopConsignment
[addon-supplier/ShopConsignment] ✓ built in 567ms
✅ 构建成功: addon-supplier/ShopConsignment
```

#### 智能构建队列优化

从 3.0.2 版本开始，监听构建模式实现了智能构建队列优化功能。当你在短时间内多次修改同一文件时，系统将**自动跳过**已经排队但尚未开始的构建任务，只保留最新的构建请求。

例如，如果修改 `apis.ts` 文件影响了 10 个入口，但在第一批构建进行中（比如构建了 4 个）时你又修改了该文件，系统会：

1. 完成已经开始的 4 个构建
2. 跳过剩余 6 个旧的构建任务
3. 直接开始构建最新版本的 10 个入口

这将显示如下输出：

```
🔄 addon-supplier/PlatformOverviewBasic 依赖于文件 apis.ts
// 再次修改文件后
⏭️ 跳过 addon-supplier/PlatformPurchaseInfo 构建，已有更新的文件变化
⏭️ 跳过 addon-supplier/ShopPurchaseList 构建，已有更新的文件变化
🔨 开始构建: addon-supplier/PlatformOverviewBasic
```

这项优化可以显著减少不必要的构建次数，特别是在频繁修改共享文件（如 APIs、公共组件、工具函数等）的场景中。

## TypeScript 支持与 ESM 模块

从 3.0.2 版本开始，项目全面采用 TypeScript 进行开发，所有核心脚本文件已迁移到 TypeScript：

- `batch-build.js` → `batch-build.ts`
- `batch-build-unified.js` → `batch-build-unified.ts`
- `batchBuildDirsNames.js` → `batchBuildDirsNames.ts`
- 新增 `batch-build-common.ts` 统一管理构建函数

### ESM 模块规范

本项目采用 ESM 模块规范，需要注意以下几点：

1. 在`package.json`中已设置`"type": "module"`

2. 配置文件采用`.cjs`后缀而非`.js`，以确保 CommonJS 兼容性：

   - `.eslintrc.cjs`
   - `.prettierrc.cjs`

3. 在导入 TypeScript 文件时使用正常路径即可，无需添加任何后缀

### 构建脚本

子包中的构建脚本已更新为 TypeScript 版本，并且遵循 ESM 规范：

```typescript
// batch-build.ts
import { fileURLToPath } from 'node:url'
import path from 'path'
import { runBuildProcess } from '../batch-build-common'

// 获取当前文件的目录
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// 执行构建过程
runBuildProcess({ packageDir: __dirname })
  .then((success) => {
    if (!success) {
      process.exit(1)
    }
  })
  .catch((error) => {
    console.error('构建过程出错:', error)
    process.exit(1)
  })
```

## 开发规范

#### 1. TS 类型完善

对于插件中没有的自定义模块,是由父组件(父级项目: 平台端/商家端/供应商端/PC 商城)传进来的.

所以需要在**rollup-config.ts**文件中配置忽略打包:

```typescript
const globalsAlias: Record<string, string> = {
  '@/components/element-plus/el-table/ElTableEmpty/index.vue': 'ElTableEmpty',
}
```

比如我现在有一个父项目通过 q-plugin 调用如下:

```vue
<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
</script>

<template>
  <q-plugin
    :context="{
      ElTableEmpty,
    }"
    name="ShopInvoiceHeader"
    service="addon-supplier"
  />
</template>
```

该父项目向**addon-supplier**插件项目传递了一个**\<ElTableEmpty /\>**组件,

插件中接收:

```vue
<script setup lang="ts">
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
</script>

<template>
  <el-table :data="InvoiceHeadelist">
    <template #empty> <ElTableEmpty /> </template>
  </el-table>
</template>
```

此时会在 **@/components/element-plus/el-table/ElTableEmpty/index.vue** 处 TS 飘红:找不到 **@/components/element-plus/el-table/ElTableEmpty/index.vue** 这个模块的类型声明

所以需要在插件项目根目录添加类型声明文件: **types\shims-vue.d.ts** 如下:

```typescript
declare module '*.vue' {
  import { defineComponent } from 'vue'
  const component: ReturnType<typeof defineComponent>
  export default component
}
```

该文件表示所有.vue 结尾的文件全部声明类型为 **typeof defineComponent**

除了.vue 的文件需要做类型声明之外,凡是父项目传进来的模块,全部都要在 /types 目录下做出类型声明文件

除此之外需要在根目录的 **tsconfig.json** 文件中配置读取该模块的目录:

```json
{
  "compilerOptions": {
    "paths": {
      "@/模块名称路径": ["./types/@/模块名称路径.d.ts"]
    }
  }
}
```

#### 2. scss 规范

为了兼容最新的 scss 版本,在进行 scss 的嵌套写法时,需要注意,**所有的父级 css 属性必须==顶格==写**:

```scss
// bad ⬇️😟
@include b(com) {
  @include flex;
  @include e(pic) {
    width: 62px;
    height: 62px;
  }
  width: 113px;
  font-size: 14px;
}
// good ⬇️😄
@include b(com) {
  width: 113px;
  @include flex;
  font-size: 14px;
  @include e(pic) {
    width: 62px;
    height: 62px;
  }
}
```

#### 3. 包引入规范

​ 由于 **element-plus** 包是不参与最终打包结果的,其包名位于`rollup-config.ts`的忽略列表中,**==所以在本项目页面中所有地方都不要直接引入 element 的相关组件! 除非父项目将这个组件传进来了!==** 例如:

```vue
// bad ⬇️😟 // 父项目没有传进来 // 本项目直接引入:
<script lang="ts" setup>
import { ElForm, ElButton, ElMessageBox, ElMessage, *** } from 'element-plus'
</script>

// good ⬇️😄 // 父项目:
<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
</script>
<template>
  <q-plugin
    :context="{
      ElementPlus: { ElMessageBox, ElMessage },
    }"
    name="PlatformDistribute"
    service="addon-distribute"
  />
</template>
// 本项目: import { ElMessageBox, ElMessage } from 'element-plus'
```

#### 4. ESLint 和 Prettier 配置

项目使用 ESLint 和 Prettier 进行代码风格检查和格式化。配置文件已更新为 CommonJS 模块格式：

- `.eslintrc.cjs` - ESLint 配置
- `.prettierrc.cjs` - Prettier 配置

执行以下命令进行代码格式化：

```bash
npm run fix
```

这将对所有子包中的代码进行格式化和基本错误修复。
