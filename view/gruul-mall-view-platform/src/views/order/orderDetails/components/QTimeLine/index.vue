<script setup lang="ts"></script>

<template>
    <div class="q_time_line">
        <slot></slot>
    </div>
</template>

<style lang="scss" scoped>
.q_time_line {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
}
</style>
