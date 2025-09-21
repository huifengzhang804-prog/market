<script setup lang="ts">
defineProps({
    content: { type: String, default: '' },
})
const $emit = defineEmits(['SeeRemark'])
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
            <el-link class="remark_flag" :underline="false" @click="$emit('SeeRemark')">
                <i class="iconfont icon-hongqi" :style="{ color: content ? '#F54319' : '#C7C7C7', fontSize: '18px' }"></i>
            </el-link>
        </template>
    </el-popover>
</template>
