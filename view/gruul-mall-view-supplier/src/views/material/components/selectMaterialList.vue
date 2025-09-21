<script setup lang="ts">
const props = defineProps({
    tableData: {
        type: Object,
        default: () => {},
    },
    selectLists: {
        type: Object,
        default: () => {},
    },
})
const emit = defineEmits(['listLiFn'])
const ListLiFn = (li: { categoryId: string; format: string; id: string; name: string; shopId: string; size: string; url: string }) => {
    emit('listLiFn', li)
}
</script>
<template>
    <div class="ListBox">
        <div v-for="(item, index) in props.tableData" :key="index" class="ListLis" @click="ListLiFn(item)">
            <div :class="props.selectLists.includes(item) ? 'border ListLi' : 'ListLi'" style="margin-right: 0">
                <div class="ListLi__imageBox">
                    <video
                        v-if="item.format === 'mp4' || item.format === 'MP4'"
                        class="ListLi__imageBox--image"
                        autoplay
                        controls
                        loop
                        muted
                        :src="item?.url"
                    ></video>
                    <!-- <img v-else class="ListLi__imageBox--image" :src="item?.url" alt="" /> -->
                    <div v-else class="ListLi__imageBox--image" :style="{ background: `url(${item?.url}) center no-repeat` }"></div>
                </div>
                <p class="ListLi__text">{{ item.name }}</p>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(ListBox) {
    width: 1050px;
    height: 400px;
    overflow-y: scroll;
    display: flex;
    flex-wrap: wrap;
    background-color: #f7f7f7;
    padding: 10px 0 10px 10px;
}
@include b(border) {
    border: 2px solid #409eff !important;
}
@include b(ListLi) {
    width: 95px;
    height: 140px;
    margin: 0 8px 20px 0;
    cursor: pointer;
    border: 2px solid transparent;
    background-color: #fff;
    border-radius: 2px;
    @include e(imageBox) {
        width: 91px;
        height: 91px;
        overflow: hidden;
        background-color: #f9f9f9;
        @include m(image) {
            width: 100%;
            height: 100%;
            background-size: contain !important;
        }
    }
    @include e(text) {
        margin-top: 5px;
        line-height: 18px;
        padding: 2px;
        font-size: 14px;
        word-break: break-all;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
    }
}
@include b(ListLis) {
    margin-right: 8px;
    &:nth-of-type(10n) {
        margin-right: 0 !important;
    }
}
</style>
