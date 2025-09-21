<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import swiper, { getDefault, type Default } from './swiper'
import upload from '@/views/decoration/pc/components/upload/index.vue'
import qSelectDialog from '@/components/q-select-dialog/q-select-dialog.vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { Plus, CircleCloseFilled, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof swiper>,
        default: swiper,
    },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

/**
 * 添加
 */
const add = () => {
    formData.value.push(getDefault())
}

const activeId = ref(0) // 激活id

/**
 * 删除
 */
const del = (i: number) => {
    formData.value.splice(i, 1)
}

/**
 * 校验函数
 */
const validate = () => {
    return formData.value.every((item) => {
        if (!item.img) {
            ElMessage.error('轮播图中的图片为必传项！')
            return false
        }
        if (item.type === '自定义链接' && item.link && !item.link.includes('https://')) {
            ElMessage.error('请输入正确的链接')
            return false
        }
        return true
    })
}

const selectDialogRef = ref()
let currentIndex = -1
const selectLink = (item: Default, index: number) => {
    if (!item.type) return
    currentIndex = index
    selectDialogRef.value?.open(item.link)
}

const confirm = (link: any) => {
    formData.value[currentIndex].link = link
}

defineExpose({
    formRef: { validate },
})
</script>

<template>
    <div>
        <div class="line">
            图片建议尺寸1920*450~550；最多 10 张
            <el-button :icon="Plus" type="primary" :disabled="formData.length > 9" @click="add">添加</el-button>
        </div>

        <el-scrollbar class="dialog-scroll scroll" height="550" always>
            <vue-draggable-next :list="formData" handle=".item__icon">
                <div v-for="(item, index) in formData" :key="item.id" class="item" @mouseover="activeId = item.id" @mouseleave="activeId = 0">
                    <!-- 拖动图标 -->
                    <div class="item__icon m-r-10 cp">
                        <q-icon name="icon-svg1" size="28px" color="#999999"></q-icon>
                    </div>

                    <!-- 上传图片 -->
                    <div class="item__uploader m-r-10">
                        <upload v-model="item.img" width="454px" height="116px" />
                    </div>

                    <!-- 轮播图配置 -->
                    <div class="item__config">
                        <div class="item__row">
                            <div class="item__row--label">标题</div>
                            <el-input v-model="item.title" style="flex: 1" placeholder="请输入标题" show-word-limit :maxlength="10" />
                        </div>

                        <div class="item__row">
                            <div class="item__row--label">链接类型</div>
                            <el-select v-model="item.type" placeholder="请选择链接类型" style="flex: 1" clearable @change="item.link = ''">
                                <el-option value="系统链接" />
                                <el-option value="自定义链接" />
                            </el-select>
                        </div>

                        <div class="item__row">
                            <div class="item__row--label">链接地址</div>

                            <el-input
                                v-if="item.type === '自定义链接'"
                                v-model="item.link"
                                style="flex: 1"
                                placeholder="请输入以【https://】开头的链接地址"
                            />

                            <div v-else class="v-select" @click="selectLink(item, index)">
                                <div class="v-select__wrapper" :class="{ 'v-disabled': !item.type }">
                                    <div class="v-select__wrapper-item">
                                        <span v-if="!item.link" class="v-select__wrapper-item--placeholder">请选择系统链接</span>
                                        <span v-else class="v-select__wrapper-item--name">
                                            <el-tag class="m-r-6">{{ item.link.type }}</el-tag>
                                            {{ item.link.item.name ?? item.link.item.productName }}
                                        </span>
                                    </div>
                                    <div class="v-select__wrapper--suffix">
                                        <el-icon><ArrowDown /></el-icon>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 删除按钮 -->
                    <el-icon v-if="activeId === item.id && formData.length > 1" class="item__close" size="20" @click="del(index)">
                        <CircleCloseFilled />
                    </el-icon>
                </div>
            </vue-draggable-next>
        </el-scrollbar>

        <q-select-dialog ref="selectDialogRef" @confirm="confirm" />
    </div>
</template>

<style lang="scss" scoped>
@include b(line) {
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: #999999;
    font-size: 16px;
}

@include b(scroll) {
    :deep(.el-scrollbar__view) {
        padding-top: 12px;
    }
}

@include b(item) {
    position: relative;
    height: 136px;
    width: 910px;
    border: 1px solid #dcdae2;
    display: flex;
    align-items: center;
    padding: 10px;
    margin-bottom: 10px;
    &:hover {
        border-color: #409eff;
    }

    @include e(icon) {
        width: 44px;
        padding: 10px;
    }

    @include e(row) {
        display: flex;
        justify-content: space-between;
        width: 366px;
        align-items: center;

        @include m(label) {
            width: 48px;
            font-size: 12px;
            margin-right: 6px;
        }
    }

    @include e(config) {
        display: flex;
        height: 116px;
        flex-direction: column;
        justify-content: space-between;
    }

    @include e(close) {
        position: absolute;
        cursor: pointer;
        right: -10px;
        top: -10px;
    }
}

@include b(v-select) {
    flex: 1 1 0%;

    @include e(wrapper) {
        display: flex;
        align-items: center;
        position: relative;
        box-sizing: border-box;
        cursor: pointer;
        text-align: left;
        font-size: 14px;
        padding: 4px 12px;
        gap: 6px;
        min-height: 32px;
        line-height: 24px;
        border-radius: var(--el-border-radius-base);
        background-color: var(--el-fill-color-blank);
        transition: var(--el-transition-duration);
        box-shadow: 0 0 0 1px var(--el-border-color) inset;

        @include m(suffix) {
            display: flex;
            align-items: center;
            flex-shrink: 0;
            gap: 6px;
            color: var(--el-input-icon-color, var(--el-text-color-placeholder));
        }

        @include e(wrapper-item) {
            position: relative;
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            flex: 1;
            min-width: 0;
            gap: 6px;

            @include m(placeholder) {
                position: absolute;
                display: block;
                top: 50%;
                transform: translateY(-50%);
                width: 100%;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                color: var(--el-text-color-placeholder);
            }

            @include m(name) {
                @include utils-ellipsis;
                width: 240px;
            }
        }
    }
}

@include b(v-disabled) {
    background-color: var(--el-disabled-bg-color);
    box-shadow: 0 0 0 1px var(--el-disabled-border-color) inset;
}
</style>
