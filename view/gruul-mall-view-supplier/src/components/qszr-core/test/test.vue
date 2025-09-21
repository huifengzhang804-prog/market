<script setup lang="ts">
import { ref } from 'vue'
import MTable from '@/components/qszr-core/packages/q-table/QTable'
import MTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import hasSlots from './hasSlots.vue'

const testArr = ref([
    {
        name: '111',
        age: '2222',
        sex: '1',
        img: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220507/d0d7817fbcaf470e9a58065052dace88.jpg',
    },
    {
        name: '111',
        age: '2222',
        sex: '1',
        img: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220507/d0d7817fbcaf470e9a58065052dace88.jpg',
    },
    {
        name: '111',
        age: '2222',
        sex: '1',
        img: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220507/d0d7817fbcaf470e9a58065052dace88.jpg',
    },
    {
        name: '111',
        age: '2222',
        sex: '1',
        img: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220507/d0d7817fbcaf470e9a58065052dace88.jpg',
    },
])
const checkedItem = ref([])

const get = () => {
    console.log(checkedItem.value)
}
const col = [
    {
        label: '图片',
        prop: 'img',
        customStyle: '',
        width: '430',
    },
    {
        label: '名称',
        prop: 'name',
        customStyle: '',
        width: '430',
    },
]
</script>

<template>
    <m-table :data="testArr" :columns="col">
        <template #custom="{ row }">
            <div>{{ row }}</div>
        </template>
        <m-table-column prop="img" label="名字" width="430">
            <template #default="{ row }">
                <img style="width: 100px; height: 100px" :src="row.img" />
            </template>
        </m-table-column>
        <m-table-column prop="name" label="名字" width="430">
            <template #default="{ row }">
                <div>{{ row.name }}</div>
            </template>
        </m-table-column>
        <m-table-column prop="age" label="年龄" width="430">
            <template #default="{ row }">
                <div>{{ row.age }}</div>
            </template>
        </m-table-column>
    </m-table>
    <hasSlots></hasSlots>
    <div @click="get">获取select的值</div>
</template>

<style lang="scss" scoped></style>
