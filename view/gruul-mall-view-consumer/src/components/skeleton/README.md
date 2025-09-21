# 预加载骨架屏组件

## 概述

这是一个通用的预加载骨架屏组件库，提供了多种骨架屏样式，用于在数据加载时提供更好的用户体验。

## 组件结构

```
src/components/skeleton/
├── index.vue              # 通用骨架屏组件
├── waterfall-skeleton.vue  # 瀑布流专用骨架屏
└── README.md              # 使用说明
```

## 使用方法

### 1. 瀑布流骨架屏

```vue
<template>
  <skeleton-index type="waterfall" :count="6" :columns="2" />
</template>

<script setup>
import SkeletonIndex from '@/components/skeleton/index.vue'
</script>
```

### 2. 列表骨架屏

```vue
<template>
  <skeleton-index type="list" :count="10" />
</template>
```

### 3. 在瀑布流组件中使用

```vue
<template>
  <view class="classificate__list">
    <!-- 预加载骨架屏 -->
    <skeleton-index v-if="loading" type="waterfall" :count="skeletonCount" :columns="2" />

    <!-- 实际内容 -->
    <view v-else class="waterfall-container">
      <!-- 商品列表内容 -->
    </view>
  </view>
</template>

<script setup>
import SkeletonIndex from '@/components/skeleton/index.vue'

const props = defineProps({
  list: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

// 骨架屏数量
const skeletonCount = computed(() => {
  return Math.max(6, props.list.length || 6)
})
</script>
```

## 组件属性

### SkeletonIndex 组件

| 属性名  | 类型    | 默认值 | 说明                                                      |
| ------- | ------- | ------ | --------------------------------------------------------- |
| type    | String  | 'list' | 骨架屏类型：'waterfall'、'list'、'card'、'grid'、'custom' |
| count   | Number  | 6      | 骨架屏项目数量                                            |
| columns | Number  | 2      | 列数（仅对瀑布流有效）                                    |
| loading | Boolean | true   | 是否显示加载状态                                          |

### WaterfallSkeleton 组件

| 属性名  | 类型   | 默认值 | 说明           |
| ------- | ------ | ------ | -------------- |
| count   | Number | 6      | 骨架屏项目数量 |
| columns | Number | 2      | 瀑布流列数     |

## 样式特性

### 闪烁动画

所有骨架屏都包含闪烁动画效果，通过 CSS 动画实现：

```scss
.skeleton-shimmer {
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
    animation: shimmer 1.5s infinite;
  }
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}
```

### 响应式布局

骨架屏组件采用响应式设计，适配不同屏幕尺寸：

- 瀑布流：支持自定义列数
- 列表：自适应容器宽度
- 网格：支持多列布局

## 最佳实践

### 1. 合理设置骨架屏数量

```javascript
// 根据实际数据量动态设置
const skeletonCount = computed(() => {
  return Math.max(6, props.list.length || 6)
})
```

### 2. 控制加载状态

```javascript
// 在数据加载完成后隐藏骨架屏
const isLoading = ref(true)

const loadData = async () => {
  isLoading.value = true
  try {
    const data = await fetchData()
    goodsList.value = data
  } finally {
    isLoading.value = false
  }
}
```

### 3. 错误处理

```javascript
// 添加错误状态处理
const hasError = ref(false)

const loadData = async () => {
  try {
    isLoading.value = true
    hasError.value = false
    const data = await fetchData()
    goodsList.value = data
  } catch (error) {
    hasError.value = true
    console.error('数据加载失败:', error)
  } finally {
    isLoading.value = false
  }
}
```

## 扩展开发

### 添加新的骨架屏类型

1. 在 `index.vue` 中添加新的条件渲染
2. 添加对应的样式类
3. 更新 Props 接口定义

```vue
<template>
  <view class="skeleton-wrapper">
    <!-- 现有类型 -->
    <waterfall-skeleton v-if="type === 'waterfall'" :count="count" :columns="columns" />

    <!-- 新增类型 -->
    <view v-else-if="type === 'newType'" class="skeleton-new-type">
      <!-- 新类型的骨架屏内容 -->
    </view>
  </view>
</template>
```

### 自定义样式

可以通过 CSS 变量自定义骨架屏样式：

```scss
.skeleton-item {
  --skeleton-bg-color: #f5f5f5;
  --skeleton-animation-duration: 1.5s;

  background-color: var(--skeleton-bg-color);
  animation-duration: var(--skeleton-animation-duration);
}
```

## 注意事项

1. **性能优化**：骨架屏组件应该轻量化，避免复杂的计算和渲染
2. **可访问性**：为骨架屏添加适当的 ARIA 标签
3. **一致性**：确保骨架屏与实际内容的布局保持一致
4. **加载时间**：合理控制骨架屏显示时间，避免过长等待

## 更新日志

- v1.0.0: 初始版本，支持瀑布流和列表骨架屏
- 支持多种骨架屏类型
- 添加闪烁动画效果
- 支持响应式布局
