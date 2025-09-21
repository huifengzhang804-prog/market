<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import shopSing from './shop-sign'
import upload from '@/views/decoration/pc/components/upload/index.vue'
import qSelectDialog from '@/components/q-select-dialog/q-select-dialog.vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { PropType } from 'vue'
import type { Default } from './shop-sign'

const props = defineProps({
    formData: {
        type: Object as PropType<Default>,
        default: shopSing,
    },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

/**
 * 校验函数
 */
const validate = () => {
    const { img, type, link } = formData.value
    if (!img) {
        ElMessage.error('店铺形象中的图片为必传项！')
        return false
    }
    if (type === '自定义链接' && link && link.includes('https://')) {
        ElMessage.error('请输入正确的链接')
        return false
    }
}

const selectDialogRef = ref()
const selectLink = (link: string) => {
    if (!formData.value.type) return
    selectDialogRef.value?.open(link)
}

const confirm = (link: any) => {
    formData.value.link = link
}

defineExpose({
    formRef: { validate },
})
</script>

<template>
    <div class="shop-sign">
        <div class="switch">
            <div class="switch__item"><span class="switch__item--label">上新公告</span> <el-switch v-model="formData.newTips" /></div>
            <div class="switch__item"><span class="switch__item--label">联系客服</span> <el-switch v-model="formData.service" /></div>
            <div class="switch__item"><span class="switch__item--label">关注店铺</span> <el-switch v-model="formData.follow" /></div>
        </div>
        <div class="label">店铺形象</div>
        <div class="item">
            <!-- 上传图片 -->
            <div class="item__uploader m-r-10">
                <upload v-model="formData.img" width="508px" height="116px" />
            </div>

            <!-- 轮播图配置 -->
            <div class="item__config">
                <div class="item__config-row item__config-size">
                    <div class="item__config-row--label">建议尺寸</div>
                    <div class="item__config-row--content">1920*200</div>
                </div>

                <div class="item__config-row">
                    <div class="item__config-row--label">链接类型</div>
                    <el-select v-model="formData.type" placeholder="请选择链接类型" style="flex: 1" clearable @change="formData.link = ''">
                        <el-option value="系统链接" />
                        <el-option value="自定义链接" />
                    </el-select>
                </div>

                <div class="item__config-row">
                    <div class="item__config-row--label">链接地址</div>

                    <el-input
                        v-if="formData.type === '自定义链接'"
                        v-model="formData.link"
                        style="flex: 1"
                        placeholder="请输入以【https://】开头的链接地址"
                    />

                    <div v-else class="v-select" @click="selectLink(formData.link)">
                        <div class="v-select__wrapper" :class="{ 'v-disabled': !formData.type }">
                            <div class="v-select__wrapper-item">
                                <span v-if="!formData.link" class="v-select__wrapper-item--placeholder">请选择系统链接</span>
                                <span v-else class="v-select__wrapper-item--name">
                                    <el-tag class="m-r-6">{{ formData.link.type }}</el-tag>
                                    {{ formData.link.item.name ?? formData.link.item.productName }}
                                </span>
                            </div>
                            <div class="v-select__wrapper--suffix">
                                <el-icon><ArrowDown /></el-icon>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <q-select-dialog ref="selectDialogRef" @confirm="confirm" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(shop-sign) {
    height: 300px;
}

@include b(switch) {
    margin-bottom: 18px;
    width: 394px;
    display: flex;
    font-size: 12px;

    @include e(item) {
        color: #333;
        margin-right: 40px;
        @include m(label) {
            position: relative;
            top: 2px;
            margin-right: 8px;
        }
    }
}

@include b(label) {
    margin-bottom: 10px;
    &::before {
        content: '*';
        color: var(--el-color-danger);
        margin-right: 4px;
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

    @include e(config-row) {
        display: flex;
        justify-content: space-between;
        width: 366px;
        height: 32px;
        align-items: center;

        @include m(label) {
            width: 48px;
            font-size: 12px;
            margin-right: 6px;
        }
        @include m(content) {
            padding: 0 8px;
        }
    }

    @include e(config) {
        flex: 1;
        display: flex;
        height: 116px;
        flex-direction: column;
        justify-content: space-between;
    }

    @include e(config-size) {
        justify-content: flex-start;
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
