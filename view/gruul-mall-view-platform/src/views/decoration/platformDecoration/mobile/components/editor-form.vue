<script setup lang="ts">
import { useDecorationStore } from '@/store/modules/decoration/index'
import setting from '../packages/components/index/setting'
import type { ComponentItem } from '../packages/components/index/formModel'
import type { PropType } from 'vue'
const $props = defineProps({
    currentComponent: {
        type: Object as PropType<ComponentItem>,
        default() {
            return {}
        },
    },
    activePagesType: {
        type: String,
        default: '',
    },
})
const $decoration = useDecorationStore()

const activePageType = computed(() => {
    return $decoration.activePageType
})
</script>

<template>
    <div class="editor__form_wrap">
        <div class="clearfix editor__form_header">
            <span>{{ $props.currentComponent ? `${$props.currentComponent.label}` : '' }}组件设置</span>
        </div>
        <div class="editor__form_wrap_main">
            <el-scrollbar>
                <div class="pd20">
                    <transition name="el-fade-in-linear" mode="out-in">
                        <component
                            :is="setting[$props.currentComponent.value]"
                            v-if="$props.currentComponent"
                            :formData="$props.currentComponent.formData as any"
                            :activePagesType="activePagesType"
                        />
                        <div v-else class="form-tips">
                            <div class="form-tips-logo">
                                <div class="el-icon-setting"></div>
                                <p>
                                    {{ activePageType === 'userCenter' ? '当前组件,无法进行设置' : '当前未设置组件,请选择组件进行设置' }}
                                </p>
                            </div>
                        </div>
                    </transition>
                </div>
            </el-scrollbar>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/editPage.scss';
.editor__form_wrap {
    width: 100%;
    height: 100%;
    padding-bottom: 0 !important;
    display: flex;
    flex-direction: column;
    .editor__form_wrap_main {
        width: 100%;
        height: calc(100% - 56px);
        position: relative;
    }
    .editor__form_header {
        padding: 18px 20px;
        border-bottom: 1px solid #dcdfe6;
        -webkit-box-sizing: border-box;
        box-sizing: border-box;
    }
    .pd20 {
        padding: 20px;
        padding-bottom: 0;
    }
    .form-tips {
        .form-tips-logo {
            width: 435px;
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            text-align: center;
            color: #b2b2b2;
            .el-icon-setting {
                font-size: 100px;
                margin-bottom: 20px;
            }
        }
    }
}
</style>
