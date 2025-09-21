<script setup lang="ts">
import { cloneDeep } from 'lodash-es'
import type { ShopList } from '@/components/q-select-shop/type'

const visible = ref(false)

const cancel = () => {
    visible.value = false
}

const emit = defineEmits(['confirm'])

/**
 * 点击确定
 */
const selectGoodsRef = ref()
const confirm = () => {
    visible.value = false

    emit('confirm', selectedShop.value)
}

/**
 * 打开选择商品弹框
 */
const selectedShop = ref<ShopList[]>([])
const title = ref('选择商品')
const open = (shop: ShopList[], val?: string) => {
    selectedShop.value = cloneDeep(shop)
    title.value = val || '选择商品'
    visible.value = true
}

defineExpose({ open, selectGoodsRef })
</script>

<template>
    <el-dialog v-model="visible" :title="title" width="804px" append-to-body destroy-on-close>
        <q-select-shop ref="selectGoodsRef" v-model:selected-shop="selectedShop" :shop-max="6" />

        <!-- 底部 -->
        <template #footer>
            <div class="center__dialog-footer">
                <el-button @click="cancel">取消</el-button>
                <el-button type="primary" @click="confirm"> 确定 </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped></style>
