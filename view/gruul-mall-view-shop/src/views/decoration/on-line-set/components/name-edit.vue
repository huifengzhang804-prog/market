<script setup lang="ts">
import type { PropType } from 'vue'
import { useDecorationStore } from '@/store/modules/decoration'
import type { SubmitForm } from '../types'
import { ElMessage } from 'element-plus'

const $props = defineProps({
    name: {
        type: String,
        default: '自定义页面',
    },
    item: {
        type: Object as PropType<SubmitForm>,
        default() {
            return {}
        },
    },
})
const $emit = defineEmits(['change'])
const textareaInput = ref()
const $decorationStore = useDecorationStore()
const inputVisible = ref(false)
const editName = ref('')
const activePage = computed(() => {
    return $decorationStore.activePage
})

const handleShowInput = () => {
    inputVisible.value = true
    editName.value = $props.name
    // this.$nextTick(() => {
    //     const textareaInput = this.$refs.textareaInput as HTMLFormElement
    //     textareaInput.$refs.textarea.focus()
    // })
    nextTick(() => {
        textareaInput.value.textarea.focus()
    })
}
const handleHideInput = () => {
    inputVisible.value = false
    if ($props.name === editName.value) {
        return
    }
    if (editName.value !== '') {
        $emit('change', { newName: editName.value, id: $props.item.id })
    } else {
        ElMessage.warning('请填写商品名')
    }
}
</script>

<template>
    <div class="goodNameEdit">
        <span v-if="!inputVisible" class="goodNameEdit__text" @click="handleShowInput">
            {{ $props.name }}
            <el-icon style="color: #2d8cf0" :class="{ active: activePage && activePage.id === item.id }"><i-ep-editPen /></el-icon>
        </span>
        <el-input
            v-if="inputVisible"
            ref="textareaInput"
            v-model="editName"
            type="textarea"
            :rows="2"
            style="width: 180px"
            maxlength="30"
            placeholder="请输入商品名称"
            @blur="handleHideInput"
        ></el-input>
    </div>
</template>

<style lang="scss" scoped>
.goodNameEdit {
    width: 170px;
    &__text {
        cursor: pointer;
    }
    i {
        margin-left: 4px;
        display: none;
    }
    &:hover {
        i {
            display: inline-block;
        }
    }
}
.active {
    display: inline-block !important;
}
</style>
