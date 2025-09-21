<script setup lang="ts">
import WECHAT from '@/views/decoration/chooseType/components/WECHAT.vue'
import H5 from '@/views/decoration/chooseType/components/H5.vue'
import PC from '@/views/decoration/chooseType/components/PC.vue'
import ChromeTab from '@/components/chrome-tab/index.vue'

const templateType = ref<'PLATFORM' | 'SHOP'>('PLATFORM')
const tabPaneSet = [
    { label: '平台端装修', name: 'PLATFORM' },
    { label: '店铺模板装修', name: 'SHOP' },
]

/**
 * function
 */
const changeTab = (value: any) => {
    if (value) templateType.value = value
}
</script>

<template>
    <chrome-tab :tab-list="tabPaneSet" :value="templateType" @handle-tabs="changeTab" />
    <div v-if="templateType === 'PLATFORM'" class="box">
        <WECHAT :template-type="templateType" />
        <H5 :template-type="templateType" />
        <PC />
    </div>
    <div v-if="templateType === 'SHOP'" class="box">
        <div style="display: flex; align-items: center; padding-bottom: 20px; border-bottom: 1px dashed #bbb">
            <div style="margin-right: 30px">线上业务</div>
            <WECHAT :template-type="templateType" :is-o2o="false" />
            <H5 :template-type="templateType" :is-o2o="false" />
        </div>
        <div style="display: flex; align-items: center; padding-top: 20px">
            <div style="margin-right: 30px">O2O业务</div>
            <WECHAT :template-type="templateType" :is-o2o="true" />
            <H5 :template-type="templateType" :is-o2o="true" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
a {
    margin-right: 50px;
    width: 146px;
    display: inline-block;
}

:deep() {
    .choose-card {
        margin-right: 55px;
        width: 146px;
        height: 82px;
        line-height: 82px;
        text-align: center;
    }
}
.box {
    padding: 20px 15px;
}
</style>
