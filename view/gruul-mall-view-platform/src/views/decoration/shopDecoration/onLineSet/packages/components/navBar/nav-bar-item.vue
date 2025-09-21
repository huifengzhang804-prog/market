<script setup lang="ts">
import type { PropType } from 'vue'
import QUpload from '@/components/q-upload/q-upload.vue'
import { useVModel } from '@vueuse/core'
import { navBarItem } from './nav-bar'
import { ElMessage } from 'element-plus'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof navBarItem>,
        default() {
            return navBarItem
        },
    },
    itemIndex: {
        type: Number,
        default: 0,
    },
})
const $emit = defineEmits(['update:formData', 'onDelect', 'onSetHomeTab'])
const formData = useVModel($props, 'formData', $emit)

const handleDeleteNav = (formData: typeof navBarItem, itemIndex: number) => {
    if (formData.isHome) {
        ElMessage.error('该导航已设为首页,不允许删除')
    } else {
        $emit('onDelect', itemIndex)
    }
}
const handleSetHomeTab = (itemIndex: number) => {
    $emit('onSetHomeTab', itemIndex)
}
</script>

<template>
    <div class="bar_item navBar__setting">
        <div class="bar_item_top">
            <span v-if="formData.isHome">
                <img class="bar_home_icon" src="@/assets/image/decoration/u325.png" />
                <span style="color: #7f7f7f; fontsize: 16px; marginleft: 5px">首页</span>
            </span>
            <!-- <div
        v-if="!formData.isHome"
        style="color:#2D8CF0;fontSize:14px;marginLeft:40px;marginTop:5px;cursor:pointer"
        @click="handleSetHomeTab(formData, itemIndex)"
      >
        设为首页
      </div>-->
        </div>
        <el-form-item label="名称" prop="name">
            <el-input v-model="formData.text" :maxlength="3"></el-input>
        </el-form-item>
        <el-form-item v-if="formData.iconType === 2" label="图片" prop="pic">
            <div style="display: flex">
                <div>
                    <q-upload v-model:src="formData.iconPath" />
                    <div style="color: #9797a1; margin-left: 12px; text-align: center">未选中</div>
                </div>
                <div style="margin-left: 10px">
                    <q-upload v-model:src="formData.selectedIconPath" />
                    <div style="color: #9797a1; margin-left: 17px; text-align: center">选中</div>
                </div>
            </div>
            <div>
                <span style="color: #9797a1">建议尺寸为44*44，支持png格式</span>
            </div>
        </el-form-item>

        <img v-if="itemIndex > 1" class="bar_item_del_icon" src="@/assets/image/decoration/del.png" @click="handleDeleteNav(formData, itemIndex)" />
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/navBar.scss';
</style>
