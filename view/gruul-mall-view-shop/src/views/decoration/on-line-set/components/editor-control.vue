<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { useDecorationStore } from '@/store/modules/decoration'
import { defaultNavBarData } from '@/views/decoration/on-line-set/packages/components/navBar/nav-bar'
import { doGetNotPageInfo } from '@/apis/decoration'

const controlList = ref([defaultNavBarData])
const controlInfo = ref({ id: '' })
const $emit = defineEmits(['change'])
const $decorationStore = useDecorationStore()

initNav()

const handleAddComponent = () => {
    const item = controlList.value[0]
    const obj = {
        icon: item.icon,
        value: item.value,
        label: '底部导航',
    }
    $emit('change', {
        ...obj,
        id: controlInfo.value.id,
        formData: item.formData,
    })
    // 设置标识取消删除按钮
    $decorationStore.SET_ACTIVE_PAGE_TYPE('control')
}
async function initNav() {
    const { data, code } = await doGetNotPageInfo($decorationStore.getterDecType, 'TABBAR')
    if (code !== 200) return ElMessage.error('获取控件失败')
    if (data && data.properties) {
        controlInfo.value = data
        controlList.value = data.properties
    }
}
</script>

<template>
    <div class="editor_control_wrap">
        <div class="editor_control_wrap_main">
            <el-scrollbar style="height: 100%">
                <div class="checklist_item">
                    <div>底部导航</div>
                    <img class="edit_icon" src="@/assets/images/decoration/u1141.png" @click="handleAddComponent" />
                </div>
            </el-scrollbar>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/editControl.scss';
@import '@/assets/css/decoration/editPage.scss';
.editor_control_wrap {
    width: 100%;
}
</style>
