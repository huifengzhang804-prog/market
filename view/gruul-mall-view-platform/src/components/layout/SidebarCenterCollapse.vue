<script setup lang="ts">
import { ArrowLeftBold } from '@element-plus/icons-vue'

interface Props {
    isActive: boolean
}

withDefaults(defineProps<Props>(), {
    isActive: false,
})

const emit = defineEmits<{
    (e: 'toggleClick'): void
}>()

const toggleClick = () => {
    emit('toggleClick')
}
</script>

<template>
    <div class="center-collapse" @click="toggleClick">
        <ArrowLeftBold :style="{ width: '16px', height: '16px', transform: isActive ? 'none' : 'rotateY(180deg)' }" />
    </div>
</template>

<style lang="scss" scoped>
.center-collapse {
    position: absolute;
    top: 50%;
    right: 5px;
    z-index: 10;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 14px;
    height: 44px;
    cursor: pointer;
    border: 1px solid rgba(0, 0, 0, 0.1);
    border-radius: 10px;
    box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.06);
    background: rgb(255, 255, 255);
    transform: translate(12px, -50%);
    transition: 0.2s transform ease-in-out !important;
    &:hover {
        border: 1px solid rgba(85, 92, 253, 0.8);
        color: rgba(85, 92, 253, 0.8);
    }
}
</style>
