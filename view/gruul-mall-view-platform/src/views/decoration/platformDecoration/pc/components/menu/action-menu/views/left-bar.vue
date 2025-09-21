<script setup lang="ts">
import { menuItem } from '../../common'

const props = defineProps<{
    activeName: string
}>()

const emit = defineEmits(['change-menu'])

/**
 * 记录点击小菜单
 */
const locActiveName = ref('')
watch(
    () => props.activeName,
    () => {
        locActiveName.value = props.activeName
    },
    { immediate: true },
)

const enterName = ref()

const enter = (name: string) => (enterName.value = name)
</script>

<template>
    <ul class="menu" :class="{ 'menu-active': enterName }">
        <li
            v-for="(item, index) in menuItem"
            :key="item.text"
            class="menu__item"
            :class="{ active: item.text === locActiveName }"
            :index="item.text"
            @mouseenter="enter(item.text)"
            @mouseleave="enterName = ''"
            @click="emit('change-menu', item.text)"
        >
            <div>
                <q-icon svg :name="item.text === enterName || item.text === locActiveName ? item.activeIcon : item.icon" size="36px" />
                <div v-if="index !== menuItem.length - 1" class="menu__item--line" />
            </div>
        </li>
    </ul>
</template>

<style lang="scss" scoped>
@include b(menu) {
    position: absolute;
    left: 0;
    top: 0;
    width: 68px;
    height: 100%;
    padding: 0 16px;
    border-radius: 9px;
    background-color: #fff;
    z-index: 3;
    box-shadow: 1px 0px 9px 0px rgba(0, 0, 0, 0.14);
    @include e(item) {
        display: flex;
        align-items: center;
        height: 68px;
        cursor: pointer;
        position: relative;
        @include m(line) {
            width: 36px;
            height: 1px;
            position: absolute;
            bottom: 0;
            background: #ebebeb;
        }

        @include m(text) {
            white-space: nowrap;
        }
    }
}

.menu__item.active:before {
    position: absolute;
    content: '';
    width: 3px;
    height: 36px;
    left: -16px;
    background-color: #f54319;
    top: 16px;
}
</style>
