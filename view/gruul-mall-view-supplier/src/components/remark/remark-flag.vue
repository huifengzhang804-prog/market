<script setup lang="ts">
defineProps({
    content: { type: String, default: '' },
})
const $emit = defineEmits(['SeeRemark'])
</script>

<template>
    <el-popover :disabled="!content" trigger="hover" :content="content" :teleported="false" :width="content.length > 32 ? 440 : ''" effect="dark">
        <template #reference>
            <el-link :underline="false" style="margin-right: 12px" @click="$emit('SeeRemark')">
                <q-icon name="icon-hongqi" size="20px" :color="content ? 'red' : '#C7C7C7'" />
            </el-link>
        </template>
    </el-popover>
</template>

<style scoped></style>
