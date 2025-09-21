<script setup lang="ts">
defineProps({
    content: { type: String, default: '' },
})
const $emit = defineEmits(['seeRemark'])
</script>

<template>
    <el-popover
        :disabled="!content"
        transition="fade"
        :width="content.length > 32 ? 440 : ''"
        :content="content"
        trigger="hover"
        effect="dark"
        :teleported="false"
    >
        <template #reference>
            <el-link class="remark_flag" :underline="false" style="margin-right: 12px" @click="$emit('seeRemark')">
                <q-icon name="icon-hongqi" size="20px" :color="content ? 'red' : '#C7C7C7'" />
            </el-link>
        </template>
    </el-popover>
</template>

<style scoped></style>
