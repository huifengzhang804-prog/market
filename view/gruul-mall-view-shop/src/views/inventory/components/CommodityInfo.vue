<template>
    <div class="commoditys">
        <div class="commoditys__left">
            <el-image style="width: 68px; height: 68px" :src="pic" fit="fill"></el-image>
        </div>
        <div class="commoditys__right">
            <div style="position: relative">
                <div v-if="flag" class="commoditys__right--name">
                    <div>
                        <el-tooltip v-if="$props.info.productName.length >= 30" effect="dark">
                            <template #content>
                                <p style="max-width: 280px">{{ $props.info.productName }}</p>
                            </template>
                            <p class="productName">
                                <span v-if="$props.info.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                                {{ $props.info.productName }}
                            </p>
                        </el-tooltip>
                        <span v-else class="productName">
                            <span v-if="$props.info.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                            {{ $props.info.productName }}
                        </span>
                    </div>
                    <QIcon v-if="$props.currentTab !== 'delted'" name="icon-bianji_o" class="edit" size="18px" color="#333" @click="handleName" />
                </div>
                <el-input v-else ref="inputRef" v-model="name" type="textarea" style="width: 280px" maxlength="35" @blur="handleBlur" />
            </div>
            <div style="position: relative">
                <div class="commoditys__right--price">
                    <span style="color: #0f40f5; cursor: pointer" @click="copyOrderNo($props.info.id)"> SPUID: {{ $props.info.id }}</span>
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { doNameUpdate } from '@/apis/good'
import type { PropType } from 'vue'
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

watch(
    () => $props.info.productName,
    (val) => {
        name.value = val
    },
)

const copyOrderNo = async (data: string) => {
    try {
        await toClipboard(data)
        ElMessage.success('复制成功')
    } catch (e) {
        ElMessage.error('复制失败')
    }
}

// 修改名称
const flag = ref(true)

const name = ref($props.info.productName)
const inputRef = ref()

const handleName = () => {
    flag.value = false
    nextTick(() => {
        inputRef.value.focus()
    })
}

const handleBlur = async () => {
    try {
        if (!name.value) {
            name.value = $props.info.name
            flag.value = true
            return
        }
        const { code } = await doNameUpdate({ id: $props.info.id, name: name.value })
        if (code === 200) {
            flag.value = true
            emit('update-name', name.value)
        }
    } catch (error) {
        return
    }
}

const pic = computed(() => {
    return $props.info.albumPics.split(',')[0]
})
</script>
<style scoped lang="scss">
@import '@/assets/css/mixins/mixins';
@include b(commoditys) {
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
        display: flex;
        height: 68px;
        flex-direction: column;
        justify-content: space-around;
        @include m(name) {
            display: flex;
            align-items: center;
            .productName {
                max-width: 218px;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
            }
            &:hover .edit {
                display: block;
            }
            .edit {
                display: none;
                height: 20px;
                width: 20px;
                cursor: pointer;
            }
        }
        @include m(price) {
            color: #ff7417;
        }
        @include m(sup) {
            width: 120px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
    }
}
</style>
