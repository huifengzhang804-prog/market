<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import { cloneDeep } from 'lodash-es'

interface RegionList {
    label: string
    value: string
    isCheck?: boolean
    isDisable?: boolean
    isIndeterminate?: boolean
    children: RegionList[]
}
interface ChooseAreaItem {
    upperName: string
    length: number
    lowerName: string[]
}

const $props = defineProps({
    show: {
        type: Boolean,
        default: false,
    },
    checkboxData: {
        type: Array as PropType<RegionList[]>,
        default() {
            return []
        },
    },
})
const $emit = defineEmits(['update:show', 'change'])
const dialogType = useVModel($props, 'show', $emit)
// 全选标识
const allChooseType = ref(false)
// 全选disable标识
const allDisType = ref(false)
// 全选indeterminate标识
const allIndeterminateType = ref(false)

const copyCheckboxData = ref<RegionList[]>([])
watch(dialogType, (newVal) => {
    if (newVal) {
        copyCheckboxData.value = cloneDeep($props.checkboxData)
        // 半选中
        copyCheckboxData.value.forEach((item) => {
            item.isIndeterminate = isUpperLevelCheck(item)
        })
        // 全选
        checkAllStatus()
    }
})

/**
 * 省选中
 * @param {RegionList} e
 */
const handleChangeProvince = (e: RegionList) => {
    e.isIndeterminate = false
    checkBelowLevel(e.children, e.isCheck!)
    checkAllStatus()
}
/**
 * 市选中
 * @param {RegionList} province
 * @param {RegionList} city
 */
const handleChangeCity = (province: RegionList, city: RegionList) => {
    const allCheckType = isUpperLevelAllCheck(province)
    const someCheckType = isUpperLevelCheck(province)
    province.isIndeterminate = someCheckType
    province.isCheck = allCheckType
    checkBelowLevel(city.children, province.isCheck)
    checkAllStatus()
}
/**
 * 关闭弹窗重置checklist
 */
const handleHideDialog = () => {}
/**
 * 确认
 */
const handleSure = () => {
    $emit('change', getCheckItem())
    dialogType.value = false
}
const handleChangeAll = (e: boolean) => {
    checkAll(copyCheckboxData.value, e)
    checkAllStatus()
}

/**
 * 全选
 */
function checkAll(data: RegionList[], type: boolean) {
    let temp = []
    for (let i = 0; i < data.length; i++) {
        if (!data[i].isDisable) {
            data[i].isCheck = type
            if (data[i].children && data[i].children.length > 0) {
                data[i].isIndeterminate = false
                data[i].children = checkAll(data[i].children, type)
            }
        }
        temp.push(data[i])
    }
    return temp
}

/**
 * 获取已选中省市区
 * @param {RegionList} data
 */
function getCheckItem() {
    let tempArr = []
    for (let i = 0; i < copyCheckboxData.value.length; i++) {
        // 未选中
        if (!copyCheckboxData.value[i].isCheck && !copyCheckboxData.value[i].isIndeterminate) continue
        let tempObj = {
            upperName: copyCheckboxData.value[i].label,
            length: copyCheckboxData.value[i].children.length,
            lowerName: [] as string[],
        }
        for (let j = 0; j < copyCheckboxData.value[i].children.length; j++) {
            if (copyCheckboxData.value[i].children[j].isCheck && !copyCheckboxData.value[i].children[j].isDisable) {
                tempObj.lowerName.push(copyCheckboxData.value[i].children[j].label)
            }
        }
        if (tempObj.lowerName.length > 0) {
            tempArr.push(tempObj)
        }
    }
    console.log('tempArr', tempArr)

    return tempArr
}

/**
 * check当前全选状态
 */
function checkAllStatus() {
    const checkedProvinceLength = copyCheckboxData.value.filter((item) => {
        return item.isCheck
    }).length
    const indeterProvinceLength = copyCheckboxData.value.filter((item) => {
        return item.isIndeterminate
    }).length
    if (!checkedProvinceLength && !indeterProvinceLength) {
        allIndeterminateType.value = false
        // allDisType.value = false
        allChooseType.value = false
    } else if (indeterProvinceLength > 0 || (checkedProvinceLength > 0 && checkedProvinceLength !== copyCheckboxData.value.length)) {
        allIndeterminateType.value = true
        // allDisType.value = false
        allChooseType.value = false
    } else if (indeterProvinceLength === 0 && checkedProvinceLength === copyCheckboxData.value.length) {
        // allDisType.value = true
        allIndeterminateType.value = false
        allChooseType.value = true
    }
}
/**
 * 当前层级以下选择
 */
function checkBelowLevel(currentLevel: RegionList[], type: boolean) {
    for (let i = 0; i < currentLevel.length; i++) {
        currentLevel[i].isCheck = type
        if (currentLevel[i].children && currentLevel[i].children.length) {
            checkBelowLevel(currentLevel[i].children, type)
        }
    }
}
/**
 * 判断上级是否部分选择
 * @param {RegionList} e
 */
function isUpperLevelCheck(e: RegionList) {
    const checkArr = e.children.filter((item) => item.isCheck)
    if (!checkArr.length) return false
    return checkArr.length !== e.children.length
}
/**
 * 判断上级是否全选
 * @param {RegionList} e
 */
function isUpperLevelAllCheck(e: RegionList) {
    const checkArr = e.children.filter((item) => item.isCheck)
    return Boolean(checkArr.length === e.children.length)
}

function isAllDisable(province: RegionList) {
    return province.isDisable && province.children.every((item) => item.isDisable)
}
</script>

<template>
    <el-dialog v-model="dialogType" @close="handleHideDialog">
        <el-checkbox
            v-for="(province, index) in copyCheckboxData"
            :key="index"
            v-model="province.isCheck"
            :indeterminate="province.isIndeterminate"
            :disabled="isAllDisable(province)"
            style="width: 140px"
            @change="handleChangeProvince(province)"
        >
            {{ province.label }}
            <el-popover placement="right" :width="160" trigger="hover">
                <template #reference>
                    <el-icon><i-ep-arrowRight /></el-icon>
                </template>
                <template #default>
                    <div style="gap: 50px; height: 100px; overflow-y: auto">
                        <el-checkbox
                            v-for="(city, idx) in province.children"
                            :key="idx"
                            v-model="city.isCheck"
                            :label="city.label"
                            :disabled="city.isDisable"
                            @change="handleChangeCity(province, city)"
                            >{{ city.label }}</el-checkbox
                        >
                    </div>
                </template>
            </el-popover>
        </el-checkbox>
        <el-checkbox v-model="allChooseType" :disabled="allDisType" :indeterminate="allIndeterminateType" @change="(e)=>handleChangeAll(e as boolean)"
            >全选</el-checkbox
        >
        <template #footer>
            <span>
                <el-button type="" @click="dialogType = false">取消</el-button>
                <el-button type="primary" @click="handleSure">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped></style>
