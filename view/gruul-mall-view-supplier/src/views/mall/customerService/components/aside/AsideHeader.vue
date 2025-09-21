<template>
    <el-header class="cust_service_search_header">
        <el-input
            v-model="searchKeyword"
            class="cust_service_search_input"
            placeholder="请输入查询内容"
            :prefix-icon="Search"
            clearable
            @keyup.enter="inputChange"
            @clear="inputChange"
            @blur="inputBlur"
            @focus="inputFocus"
            @input="onInput"
        />
    </el-header>
</template>
<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
import { ref } from 'vue'

const emits = defineEmits(['keywordChange', 'searchFocus'])
const searchKeyword = ref('')

const onInput = (value: string) => {
    if (!value) {
        inputChange()
    }
}

const inputChange = () => {
    const keyword = searchKeyword.value
    emits('keywordChange', keyword)
}
const inputBlur = () => {
    emits('searchFocus', false)
}
const inputFocus = () => {
    emits('searchFocus', true)
}
</script>
<style scoped lang="scss">
.cust_service_search_header {
    display: flex;
    align-items: center;
    padding: 0;
    border: none;
    height: 60px;
    border-bottom: 1px solid $cust_service_border_color;
    .cust_service_search_input {
        height: 100%;
        border: none;
        :deep(.el-input__wrapper) {
            border-radius: unset;
            border: none;
            box-shadow: none;
        }
        :deep(.el-input__inner) {
            border: none !important;
            box-shadow: none !important;
            &:focus {
                outline: none;
                border-color: transparent;
            }
        }
    }
}
</style>
