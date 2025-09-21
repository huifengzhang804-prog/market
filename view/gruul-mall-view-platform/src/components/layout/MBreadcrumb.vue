<template>
    <div class="breadcrumb_container">
        <el-breadcrumb separator="/" class="m__breadcrumb">
            <el-breadcrumb-item
                v-for="(item, i) of breadcrumb"
                :key="i"
                :class="['m__breadcrumb--item', { 'm__breadcrumb--last': i === breadcrumb.length - 1 }]"
                :to="i !== breadcrumb.length - 1 && i !== 0 ? { path: item.path } : ''"
                >{{ item.title }}
            </el-breadcrumb-item>
        </el-breadcrumb>
    </div>
</template>
<script lang="ts" setup>
import { useRoute } from 'vue-router'

const route = useRoute()
const breadcrumb = computed(() => {
    return route.matched
        .filter((route) => !!route.meta.title)
        .map((route) => {
            return {
                title: route.meta.title,
                path: route.path,
            }
        })
})
</script>
<style lang="scss" scoped>
.breadcrumb_container {
    display: flex;
    align-items: center;
    background-color: $bg-grey;
    height: 40px;
    flex-shrink: 0;
}
.m__breadcrumb {
    --el-text-color-regular: #999;
    line-height: 24px;
    font-weight: bold;
    .m__breadcrumb--last {
        :deep(.el-breadcrumb__inner) {
            font-weight: bold !important;
            color: #666;
            &:hover {
                color: #666;
            }
        }
    }
}
.msg-bell {
    width: 20px;
}
:deep(.el-badge__content, .is-fixed) {
    background-color: transparent;
    border: transparent;
    color: #f72020;
    font-weight: bold;
    font-size: 14px;
    padding-left: 5px;
    border: transparent;
}
:deep(.el-breadcrumb) {
    font-size: 13px;
}
</style>
