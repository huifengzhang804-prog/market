<script setup lang="ts">
import type { PropType } from 'vue'
import NavBarItem from './nav-bar-item.vue'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import NavBar from './nav-bar'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof NavBar>,
        default() {
            return NavBar
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
const notice = ref(false)
const dragStarIndex = ref(-1)

const handleAddNav = () => {
    formData.value.codeStyle = 1
    if (formData.value.menuList.length <= 4) {
        const defaultTab = {
            text: '名称',
            id: '',
            iconType: 2,
            iconPath: 'https://qiniu-app.qtshe.com/u391.png',
            selectedIconPath: 'https://qiniu-app.qtshe.com/u391.png',
            sortIndex: 0,
            isHome: false,
            /** 系统图标 */
            codeStyle: 1,
            isAdd: false, // 新增的导航是只能选择上传图片
            link: {
                id: '',
                type: 0,
                name: '',
                url: '',
                append: 0,
            },
        }
        formData.value.menuList.push(defaultTab)
        sortMenu()
    } else {
        ElMessage.warning('底部导航最多4个')
    }
}
const handleDelect = (index: number) => {
    formData.value.codeStyle = 1
    formData.value.menuList.splice(index, 1)
    sortMenu()
}

const handleSetHomeTab = (homeIndex: number) => {
    formData.value.menuList.forEach((item, index) => {
        if (homeIndex === index) {
            item.isHome = true
        } else {
            item.isHome = false
        }
    })
}
const handleDragstart = (i: number) => {
    dragStarIndex.value = i
}
const handleDragover = (e: any) => {
    e.preventDefault()
}
const handleDrop = (i: number) => {
    if (dragStarIndex.value === i) {
        return false
    }
    const temp = formData.value.menuList.splice(dragStarIndex.value, 1, formData.value.menuList[i])
    formData.value.menuList.splice(i, 1, ...temp)
}
function sortMenu() {
    formData.value.menuList.forEach((item, index) => {
        item.sortIndex = index
    })
}
</script>

<template>
    <el-form :model="formData" label-width="100px">
        <div class="setmenu__form--item" style="padding-top: 10px; position: relative">
            <label class="el-form-item__label">选择样式:</label>
            <el-radio-group v-model="formData.codeStyle">
                <el-radio :value="1">普通样式</el-radio>
                <el-radio :disabled="formData.menuList.length < 5" :value="2"
                    >中间大图样式 <i class="el-icon-warning-outline" style="cursor: pointer" @mouseover="notice = true" @mouseout="notice = false"></i
                ></el-radio>
            </el-radio-group>
            <span v-if="notice" style="position: absolute; top: 40px; left: 50px">该模式适合单数导航，最多可添加5个，最少3个</span>
        </div>
        <div class="setmenu__form--item" style="padding-top: 0px">
            <label class="el-form-item__label">添加导航:</label>
            <span style="color: #9797a1; fontsize: 12px">最多添加5个导航，鼠标拖拽调整导航顺序</span>
        </div>
        <div class="setmenu__form--item" style="padding-top: 0px">
            <label class="el-form-item__label">未选中颜色:</label>
            <el-color-picker v-model="formData.defaultColor"></el-color-picker>
        </div>
        <div class="setmenu__form--item" style="padding-top: 0px">
            <label class="el-form-item__label">已选择颜色:</label>
            <el-color-picker v-model="formData.selectColor"></el-color-picker>
        </div>
        <div class="setmenu__form--item" style="padding-top: 0px">
            <label class="el-form-item__label">底部填充色:</label>
            <el-color-picker v-model="formData.underfillColor"></el-color-picker>
        </div>
        <!-- 导航item -->
        <div
            v-for="(item, index) in formData.menuList"
            :key="index"
            :draggable="true"
            @dragstart="handleDragstart(index)"
            @dragover="handleDragover"
            @drop="handleDrop(index)"
        >
            <nav-bar-item :item-index="index" :form-data="item" @on-set-home-tab="handleSetHomeTab(index)" @on-delect="handleDelect(index)" />
        </div>
        <!-- 添加导航按钮 -->
        <el-button v-if="formData.menuList.length <= 4" style="margintop: 10px; margin-bottom: 40px" @click="handleAddNav">添加导航</el-button>
    </el-form>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/navBar.scss';
</style>
