<script setup lang="ts">
import { useDecorationStore } from '@/store/modules/decoration'

const nowTab = ref(0)
const $emit = defineEmits(['change'])
const $decorationStore = useDecorationStore()

const handleSelectTab = (index: number) => {
    nowTab.value = index
    $emit('change', index)
    $decorationStore.SET_ISUSERCENTER(index)
}
</script>

<template>
    <div class="editor_tab">
        <div class="left_menu">
            <div :class="['tab_item', nowTab === 0 ? 'tab_item_active' : '']" @click="handleSelectTab(0)">
                <q-icon class="tab--icon" :name="nowTab === 0 ? '#icon-zujian' : '#icon-zujianhui'"></q-icon>
                <div class="tab_item_title">组件</div>
                <div class="decorator"></div>
            </div>
            <div :class="['tab_item', nowTab === 1 ? 'tab_item_active' : '']" @click="handleSelectTab(1)">
                <q-icon class="tab--icon" :name="nowTab === 1 ? '#icon-kongjian' : '#icon-kongjianhui'"></q-icon>
                <div class="tab_item_title">控件</div>
                <div class="decorator"></div>
            </div>
            <div :class="['tab_item', nowTab === 2 ? 'tab_item_active' : '']" @click="handleSelectTab(2)">
                <q-icon class="tab--icon" :name="nowTab === 2 ? '#icon-yemian' : '#icon-yemianhui'"></q-icon>
                <div class="tab_item_title">页面</div>
                <div class="decorator"></div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.editor_tab {
    height: 100%;
    width: 75px;
    display: flex;
    flex-direction: column;
    background: #403f3e;
    .editor_tab_title {
        background: #403f3e;
        width: 75px;
        height: 65px;
    }
    .left_menu {
        width: 75px;
        background: #403f3e;
        flex: 1;
        position: relative;

        .slider—span {
            position: absolute;
            top: 0;
            right: 0;
            width: 0;
            height: 0;
            border: 7px solid transparent;
            border-right-color: #fff;
            -webkit-transition: top 0.3s;
            transition: top 0.3s;
        }
    }
    .tab_item {
        width: 75px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        cursor: pointer;
        height: 96px;
        text-align: center;
        color: #a6a6a6;
        .tab--icon {
            width: 32px;
            height: 32px;
            color: #a6a6a6;
            text-align: center;
        }

        .tab_item_title {
            color: #a6a6a6;
            font-size: 14px;
            margin-top: 3px;
        }

        .tab-bg__icon1 {
            background-position: -144px 1px;
        }

        .tab-bg__icon2 {
            background-position: -145px -42px;
        }

        .tab-bg__icon3 {
            background-position: -144px -87px;
        }
    }

    .tab-bg__icon {
        display: block;
        width: 24px;
        height: 24px;
        margin: 30px auto 12px;
        background: url(https://ma.faisys.com/image/mbg01.png?v=202003041237) no-repeat;
        position: relative;
    }

    .tab_item_active {
        position: relative;
        background: #ffffff;
        &::after {
            content: '';
            width: 10px;
            height: 10px;
            background: #403f3e;
            position: absolute;
            top: -10px;
            right: 0;
            border-radius: 0 0 10px 0;
        }
        &::before {
            content: '';
            width: 10px;
            height: 10px;
            background: #fff;
            position: absolute;
            top: -10px;
            right: 0;
        }
        .tab--icon {
            font-size: 35px;
            color: #409eff;
            text-align: center;
        }
        .tab_item_title {
            color: #409eff;
        }

        .tab-bg__icon1 {
            background-position: -99px 1px;
        }

        .tab-bg__icon2 {
            background-position: -100px -42px;
        }

        .tab-bg__icon3 {
            background-position: -99px -87px;
        }

        .decorator {
            &::before {
                content: '';
                width: 10px;
                height: 10px;
                background: #fff;
                position: absolute;
                bottom: -10px;
                right: 0;
            }
            &::after {
                content: '';
                width: 10px;
                height: 10px;
                background: #403f3e;
                position: absolute;
                bottom: -10px;
                right: 0;
                border-radius: 0 10px 0 0;
            }
        }
    }
}
</style>
