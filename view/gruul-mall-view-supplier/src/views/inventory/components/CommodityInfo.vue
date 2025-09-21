<template>
    <div class="commodity">
        <div class="commodity__left">
            <el-image :src="pic" fit="fill" style="width: 68px; height: 68px"></el-image>
        </div>
        <div class="commodity__right">
            <div style="display: flex; align-items: center">
                <div v-if="flag" class="productName">
                    <div>
                        <el-tooltip v-if="$props.info.productName.length >= 30" :content="$props.info.productName" effect="dark">
                            <span class="commodity__right--name">
                                <span v-if="$props.info.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                                {{ $props.info.productName }}
                            </span>
                        </el-tooltip>
                        <span v-else class="commodity__right--name">
                            <span v-if="$props.info.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                            {{ $props.info.productName }}
                        </span>
                    </div>
                    <QIcon v-if="$props.currentTab !== 'delted'" name="icon-bianji_o" class="edit" size="18px" @click="handleName" />
                </div>
                <!-- 多行文本输入框  -->
                <el-input v-else ref="inputRef" v-model="name" type="textarea" maxlength="35" @blur="handleBlur" />
            </div>
            <div style="position: relative; margin-top: 8px">
                <span style="color: #0f40f5; cursor: pointer" @click="copyOrderNo($props.info.id)"> SPUID:{{ $props.info.id }}</span>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { doNameUpdate } from '@/apis/good'
import type { PropType } from 'vue'
import { computed, nextTick, ref } from 'vue'
import { ApiCommodityType } from '@/views/goods/types'
import { ElMessage } from 'element-plus'
import useClipboard from 'vue-clipboard3'

const { toClipboard } = useClipboard()
const $props = defineProps({
    info: {
        type: Object as PropType<ApiCommodityType>,
        default: null,
    },
    currentTab: {
        type: String as PropType<'' | 'SELL_ON' | 'SELL_OFF' | 'PLATFORM_SELL_OFF' | 'delted'>,
        default: '',
    },
})
const emit = defineEmits(['update-name'])
const copyOrderNo = async (data: string) => {
    try {
        await toClipboard(data)
        ElMessage.success('复制成功')
    } catch (e) {
        ElMessage.error('复制失败')
    }
}

watch(
    () => $props.info.productName,
    (val) => {
        name.value = val
    },
)

// 修改名称
const flag = ref(true)

const name = ref($props.info.productName)
const inputRef = ref()
watch(
    () => flag.value,
    (val) => {
        if (!val) {
            nextTick(() => {
                inputRef.value.focus()
            })
        }
    },
)

const handleBlur = async () => {
    try {
        const curName = name.value
        if (!curName || curName === $props.info.productName) {
            flag.value = true
            return
        }
        const { code } = await doNameUpdate({ id: $props.info.id, name: curName })
        if (code === 200) {
            flag.value = true
            emit('update-name', curName)
        }
    } catch (error) {
        return
    }
}

const handleName = () => {
    name.value = $props.info.productName
    flag.value = false
    nextTick(() => {
        inputRef.value.focus()
    })
}

const pic = computed(() => {
    return $props.info.albumPics.split(',')[0]
})
</script>
<style lang="scss">
@import '@/assets/css/mixins/mixins';

@include b(commodity) {
    height: 83px;
    @include flex();
    text-align: left;
    @include e(left) {
        width: 68px;
        height: 68px;
        margin-right: 10px;
    }
    @include e(right) {
        margin-right: 10px;
        height: 68px;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        flex: 1;
        @include m(name) {
            max-width: 217px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
        }
        @include m(price) {
            color: #ff7417;
            width: 130px;
        }
        @include m(sup) {
            width: 120px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
    }
}
.productName {
    display: flex;
    align-items: center;
    &:hover .edit {
        display: block;
    }
    .edit {
        display: none;
        margin-left: 5px;
        cursor: pointer;
    }
}
</style>
