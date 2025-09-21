<script setup lang="ts">
import type { PropType } from 'vue'
import type { UserCenterMenuItem } from '../user-center'

const $props = defineProps({
    gridMenu: {
        type: Array as PropType<UserCenterMenuItem[]>,
        default() {
            return []
        },
    },
})
</script>

<template>
    <div class="user__menu--grid">
        <div class="grid">
            <div class="grid__name">我的工具</div>
            <div class="grid__wrap">
                <div v-for="gridItem in $props.gridMenu" v-show="gridItem.showMenu" :key="gridItem.menuName" class="grid__navigator">
                    <img class="grid__navigator--image" :src="gridItem.menuIconUrl" />
                    <div class="grid__navigator--right">
                        <span class="grid__navigator--name">{{ gridItem.menuName }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.grid {
    background-color: #ffffff;
    margin-bottom: 15px;
    border-radius: 5px;

    .grid__name {
        font-size: 14px;
        color: #45403c;
        padding: 15px 0 0 10px;
        font-weight: bolder;
    }

    .grid__wrap {
        @include flex(flex-start);
        flex-wrap: wrap;

        .grid__navigator {
            @include flex();
            flex-direction: column;
            flex: 0 0 25%;
            padding: 15px 0;

            .grid__navigator--image {
                width: 28px;
                height: 28px;
            }

            .grid__navigator--right {
                text-align: center;
                padding-top: 15px;
                display: flex;
                flex-direction: column;
                justify-content: center;

                .grid__navigator--name {
                    font-size: 13px;
                    color: #45403c;
                }

                .grid__navigator--text {
                    font-size: 14px;
                    color: #b2b2b2;
                }
            }
        }
    }
}
</style>
