<script setup lang="ts">
import type { PropType } from 'vue'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import { useDecorationStore } from '@/store/modules/decoration'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { doGetShopPages } from '@/apis/decoration'
import type { LinkSelectItem } from '../linkSelectItem'
import { DecorationPage } from '@/apis/decoration/types'

const $props = defineProps({
    link: {
        type: Object as PropType<LinkSelectItem>,
        default() {
            return {
                id: null,
                type: null,
                name: '',
                url: '',
                append: '',
            }
        },
    },
    visible: {
        type: Boolean,
        default: false,
    },
})
const $emit = defineEmits(['update:link'])
const linkSelectItem = useVModel($props, 'link', $emit)
const name = 'CustomPage'
const selectId = ref('')
const loading = ref(false)
const tableData = ref<DecorationPage[]>([])
const $decorationStore = useDecorationStore()
const { shopMode } = useShopInfoStore().getterShopInfo
const pageConfig = reactive({
    current: 1,
    size: 999,
    type: 'SHOP_CUSTOMIZED_PAGE',
    businessType: shopMode === 'COMMON' ? 'ONLINE' : 'O2O',
    endpointType: $decorationStore.getEndpointType,
})
defineExpose({
    selectId,
})

onMounted(() => {
    getPageList()
})
watch(
    linkSelectItem,
    (newVal) => {
        selectId.value = newVal.id
    },
    {
        immediate: true,
    },
)

const handleSelect = () => {
    const currentItem = tableData.value.find((item: DecorationPage) => item.id === String(selectId.value))
    console.log(currentItem)
    Object.assign(linkSelectItem.value, currentItem)
}
const onClear = () => {
    getPageList()
}
async function getPageList() {
    loading.value = true
    const { code, data } = await doGetShopPages(pageConfig)
    if (code !== 200) {
        return ElMessage.error('获取自定页面失败')
    }
    const tempArr = data.records.map((item) => {
        const { id, name, shopId } = item
        return {
            id,
            type: '3',
            name,
            url: '/basePackage/pages/customPage/CustomPage',
            append: id,
            shopId,
        }
    })
    tableData.value = tempArr
    loading.value = false
}
</script>

<template>
    <!-- 商超商品 -->
    <div>
        <el-button @click="onClear">刷新</el-button>
        <el-table v-loading="loading" :data="tableData" height="369">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column label="页面名称" prop="name"></el-table-column>
            <el-table-column label="操作" width="100px">
                <template #default="scope">
                    <el-radio v-model="selectId" :label="scope.row.id" @change="handleSelect">
                        <span></span>
                    </el-radio>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<style lang="scss" scoped></style>
