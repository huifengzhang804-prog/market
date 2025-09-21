<script setup lang="ts">
import type { PropType } from 'vue'
import LinkSelect from '@/components/link-select/link-select.vue'
import QUpload from '@/components/q-upload/q-upload.vue'
import { useVModel } from '@vueuse/core'
import { navBarItem } from './nav-bar'
import { ElMessage } from 'element-plus'

// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
const buttonlFn = (val: string) => {
    dialogVisible.value = true
    parameterId.value = val
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    if (parameterId.value === 'iconPath') formData.value.iconPath = val
    else if (parameterId.value === 'selectedIconPath') formData.value.selectedIconPath = val
}
const checkedFileLists = (val: string[]) => {
    if (parameterId.value === 'iconPath') formData.value.iconPath = val?.shift() || ''
    else if (parameterId.value === 'selectedIconPath') formData.value.selectedIconPath = val?.shift() || ''
}
// 选择素材 d

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
// const link = ref({
//     id: '',
//     type: 0,
//     name: '',
//     url: '',
//     append: '',
// })
// watch(link, (newVal) => {
//     formData.value.linkUrl = newVal.url
//     formData.value.linkName = newVal.name
//     formData.value.name = newVal.append
//     formData.value.type = newVal.type
//     formData.value.id = newVal.id
//     if (!formData.value.isAdd) {
//         formData.value.iconType = 2
//     }
// })

onMounted(() => {
    // link.value = {
    //     id: formData.value.id,
    //     type: formData.value.type,
    //     name: formData.value.linkName,
    //     url: formData.value.linkUrl,
    //     append: formData.value.name,
    // }
})

const handleDeleteNav = (formData, itemIndex: number) => {
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
            <el-input v-model="formData.text" :maxlength="4"></el-input>
        </el-form-item>
        <el-form-item v-if="itemIndex > 1" label="链接" prop="link">
            <link-select get-from="bottomNav" :inner="true" :link="formData.link" :no-pro-tab="true" :limit-pro-tab="false"></link-select>
            <!-- <div>
                <span v-if="formData.link.name" style="color: #9797a1">{{ formData.link.name }}</span>
            </div> -->
        </el-form-item>

        <el-form-item v-if="formData.iconType === 2" label="图片" prop="pic">
            <div style="display: flex">
                <!-- <div>
                    <q-upload v-model:src="formData.iconPath" />
                    <span style="color: #9797a1; marginleft: 12px">未选中</span>
                </div> -->
                <div>
                    <img :src="formData.iconPath" alt="" class="selectMaterialStyle" @click="buttonlFn('iconPath')" />
                    <span style="color: #9797a1; margin-left: 12px">未选中</span>
                </div>

                <div style="margin-left: 10px">
                    <img :src="formData.selectedIconPath" alt="" class="selectMaterialStyle" @click="buttonlFn('selectedIconPath')" />
                    <span style="color: #9797a1; margin-left: 12px">选中</span>
                </div>
            </div>
            <div>
                <span style="color: #9797a1">建议尺寸为44*44，支持png格式</span>
            </div>
        </el-form-item>
        <img v-if="itemIndex > 1" class="bar_item_del_icon" src="@/assets/image/decoration/del.png" @click="handleDeleteNav(formData, itemIndex)" />
        <!-- 选择素材 e -->
        <selectMaterial
            :dialog-visible="dialogVisible"
            :upload-files="1"
            @select-material-fn="selectMaterialFn"
            @cropped-file-change="croppedFileChange"
            @checked-file-lists="checkedFileLists"
        />
        <!-- 选择素材 d -->
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/navBar.scss';
@include b(selectMaterialStyle) {
    width: 60px;
    height: 60px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    @include e(span) {
        color: #3088f0;
        font-size: 14px;
    }
}
</style>
