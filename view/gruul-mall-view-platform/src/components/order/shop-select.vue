<script lang="ts" setup>
import { PropType } from 'vue'
import { doGetShopList } from '@apis/shops'
import type { ShopType } from '@views/order/types'

const props = defineProps({
    modelValue: {
        type: Object as PropType<Long | null>,
        default: () => null,
    },
})

const shopType = ref<string>('')
const shopSearchList = ref<ShopType[]>([])
const emits = defineEmits(['update:modelValue', 'change'])

const shopTypes = [
    {
        value: '',
        label: '全部',
    },
    {
        value: 'SELF_OWNED',
        label: '自营',
    },
    {
        value: 'PREFERRED',
        label: '优选',
    },
    {
        value: 'ORDINARY',
        label: '普通',
    },
]
const shopSearchRemote = async (query: string) => {
    if (!query) {
        shopSearchList.value = []
        return []
    }
    const param = { name: query, current: 1, size: 999, shopType: '' }
    if (shopType.value) {
        param.shopType = shopType.value
    }
    const { data } = (await doGetShopList(param)) as any
    shopSearchList.value =
        data?.records?.map((v: ShopType) => {
            return {
                ...v,
                label: v.name,
                value: v.id,
            }
        }) || []
    return shopSearchList.value
}
const keywords = ref()
const focusFlag = reactive({
    keywords: '',
    // changed: false,
    selected: false,
})
const fetchSuggestions = async (queryString: string, callback: (suggestions: any[]) => void) => {
    callback(await shopSearchRemote(queryString))
}

const update = (shop?: ShopType | undefined) => {
    let val = null
    const selected = !!shop?.id
    focusFlag.selected = selected
    if (selected) {
        val = shop.id
    }
    emits('update:modelValue', val)
    emits('change', val)
}

const onChanged = () => {
    focusFlag.selected = false
}

const onFocus = () => {
    focusFlag.keywords = keywords.value
}
const onBlur = () => {
    if (focusFlag.selected) {
        return
    }
    keywords.value = ''
    shopType.value = ''
    update()
}

watch(
    () => props.modelValue,
    (val) => {
        if (!val) {
            keywords.value = ''
        }
    },
)
</script>

<template>
    <el-autocomplete
        v-model="keywords"
        :debounce="850"
        :fetch-suggestions="
            (queryString, callback) => {
                shopSearchRemote(queryString).then((data) => callback(data))
            }
        "
        :trigger-on-focus="false"
        class="autocomplete-with-select"
        clearable
        placeholder="请选择店铺"
        value-key="name"
        @blur="onBlur"
        @change="onChanged"
        @clear="update"
        @focus="onFocus"
        @select="(item: Record<string, any>) => update(item as ShopType)"
    >
        <template #prepend>
            <el-select v-model="shopType" clearable placeholder="类型" style="width: 75px">
                <el-option v-for="item in shopTypes" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
        </template>

        <template #default="{ item }">
            <div>{{ item.name }}</div>
        </template>
    </el-autocomplete>
</template>

<style lang="scss" scoped>
.autocomplete-with-select {
    :deep(.el-input-group__prepend) {
        background-color: var(--el-fill-color-blank);
    }
}
</style>
<style lang="scss" scoped></style>
