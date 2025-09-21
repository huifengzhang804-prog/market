<script setup lang="ts">
import { ElMessage } from 'element-plus'
import qSelectGoods from '../q-select-goods/q-select-goods.vue'
import qSelectActivity from '../q-select-activity/q-select-activity.vue'
import qSelectText from '../q-select-text/q-select-text.vue'

const visible = ref(false)

const emit = defineEmits(['confirm'])

const selected = ref<any[]>([])
const link = ref<any>({})
const activeName = ref('商品')

/**
 * 打开弹框
 */
const open = (payload?: { type: string; item: any }) => {
    if (payload) {
        activeName.value = payload.type
        if (activeArr.includes(payload.type)) {
            selected.value = [payload.item]
        } else {
            link.value = payload.item
        }
    }
    visible.value = true
}

/**
 * 关闭的回调
 */
const closed = () => {
    activeName.value = '商品'
    selected.value = []
    link.value = {}
}
/**
 * 点击确定
 */
const activeArr = ['商品']
const confirm = () => {
    if (!link.value.id && !selected.value.length) return ElMessage.error('未选中链接！')

    let val = link.value
    if (selectedType === '商品') val = selected.value[0]

    emit('confirm', { type: selectedType, item: val })
    visible.value = false
}

let selectedType = ''
/**
 * 选择商品或店铺
 */
const selectedGoods = (value: any, type: string) => {
    link.value = {}
    selectedType = type
    selected.value = value
}

/**
 * 选择页面
 */
const selectedPage = (value: any, type: string) => {
    selectedType = type
    link.value = value
    selected.value = []
}

defineExpose({
    open,
})
</script>

<template>
    <el-dialog v-model="visible" title="链接选择器" width="804px" append-to-body destroy-on-close class="q-select-dialog" @closed="closed">
        <el-tabs v-model="activeName" class="tabs">
            <el-tab-pane name="商品">
                <template #label>
                    <div class="label">商品</div>
                </template>
                <q-select-goods :selected-goods="selected" :goods-max="1" @update:selected-goods="(val) => selectedGoods(val, '商品')" />
            </el-tab-pane>

            <el-tab-pane name="活动页面">
                <template #label>
                    <div class="label">活动页面</div>
                </template>
                <q-select-activity :link="link" @update:link="(val) => selectedPage(val, '活动页面')" />
            </el-tab-pane>

            <el-tab-pane name="图文页面">
                <template #label>
                    <div class="label">图文页面</div>
                </template>
                <q-select-text :link="link" @update:link="(val) => selectedPage(val, '图文页面')" />
            </el-tab-pane>
        </el-tabs>

        <!-- 底部 -->
        <template #footer>
            <div class="center__dialog-footer">
                <el-button @click="visible = false">取消</el-button>
                <el-button type="primary" @click="confirm"> 确定 </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(tabs) {
    --el-text-color-primary: #979797;
    :deep(.el-tabs__header) {
        padding: 0 10px;
    }

    @include b(el-tab-pane) {
        padding: 0 22px;
    }
}

@include b(label) {
    padding: 0 10px;
}
</style>

<style lang="scss">
@include b(q-select-dialog) {
    padding: 0;

    @include b(el-dialog__header) {
        padding: 20px;
        padding-bottom: 10px;
    }
    @include b(el-dialog__title) {
        font-size: 16px;
        font-weight: 400;
    }

    @include b(el-dialog__body) {
        padding: 0px;
    }

    .el-dialog__footer {
        padding: 12px 20px;
    }
}
</style>
