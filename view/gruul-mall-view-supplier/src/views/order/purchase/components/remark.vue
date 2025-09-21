<template>
    <el-form>
        <el-form-item label="备注" prop="remark">
            <el-input v-model="remark" type="textarea" />
        </el-form-item>
    </el-form>
</template>

<script lang="ts" setup>
const $props = withDefaults(defineProps<{ orderNos: string[]; remark: string }>(), {
    orderNos: () => [],
    remark: '',
})
const $emit = defineEmits(['update:remark'])
const remark = computed({
    get() {
        return $props.remark
    },
    set(remarkData) {
        $emit('update:remark', remarkData)
    },
})
</script>
