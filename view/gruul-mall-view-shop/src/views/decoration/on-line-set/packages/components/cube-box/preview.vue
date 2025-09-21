<script setup lang="ts">
import type { PropType } from 'vue'
import bannerFormData from './cubeBox'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof bannerFormData>,
        default: bannerFormData,
    },
})
const pageMarginStyle = ref({
    width: 0,
    height: 0,
    margin: 0,
})
const showCubeListWrap = ref<any[]>([])
const showCubeList = ref(false)
watch(
    $props,
    () => {
        drawCube()
    },
    {
        deep: true,
    },
)

onMounted(() => {
    if ($props.formData) {
        showCubeList.value = showTxt()
        if (showCubeList.value) {
            drawCube()
        }
    }
})

function drawCube() {
    if ($props.formData) {
        showCubeList.value = showTxt()
        const perviewLayoutWidth = 370
        const item = $props.formData
        const layoutWidth = item.layoutWidth
        const layoutHeight = item.layoutHeight
        // perviewLayoutWidth = perviewLayoutWidth - item.pageMargin * 2;
        const wrapWith = perviewLayoutWidth + item.borderWidth - item.pageMargin * 2
        const styleWidth = wrapWith / layoutWidth
        const styleHeight = layoutHeight !== 1 ? perviewLayoutWidth / layoutHeight : styleWidth
        drawCubeWrap(styleWidth, styleHeight, wrapWith)
    }
}
function showTxt() {
    return !!$props.formData.subEntry.filter((item) => !!item.img).length
}
function drawCubeWrap(divWidth: number, divHeight: number, wrapWith: number) {
    const item = $props.formData
    const subEntry = item.subEntry
    showCubeListWrap.value = []
    let maxY = 0,
        maxIndex = 0,
        maxHeght = 0
    if (subEntry.length) {
        for (let i = 0; i < subEntry.length; i++) {
            const a = subEntry[i]
            const coverDiv = {
                top: a.y * divHeight + 'px',
                left: a.x * divWidth + item.pageMargin + 'px',
                width: divWidth * a.width - item.borderWidth + 'px',
                height: divHeight * a.height - item.borderWidth + 'px',
                paddingTop: (divHeight * a.height) / 2 + 'px',
                img: a[`img`] ? a[`img`] : '',
                borderWidth: item.borderWidth / 2 + 'px',
            }
            if (maxY <= a.y) {
                maxY = a.y
                maxIndex = i
            }
            showCubeListWrap.value.push(coverDiv)
        }

        maxHeght = maxY + subEntry[maxIndex].height < item.layoutHeight ? maxY + subEntry[maxIndex].height : item.layoutHeight
        pageMarginStyle.value = {
            width: wrapWith,
            height: divHeight * maxHeght,
            margin: -item.borderWidth / 2,
        }
    }
}
</script>

<template>
    <div class="rc-design-react-preview rc-design-component-default-preview">
        <div v-if="!showCubeList" class="rc-design-component-default-preview__text">点击编辑魔方</div>
        <div v-else class="cap-cube-wrap">
            <div
                class="cap-cube"
                :style="{
                    width: pageMarginStyle.width + 'px',
                    height: pageMarginStyle.height + 'px',
                    margin: pageMarginStyle.margin + 'px',
                }"
            >
                <div
                    v-for="(item, index) in showCubeListWrap"
                    :key="index"
                    class="cap-cube__item"
                    :style="{
                        width: item.width,
                        height: item.height,
                        top: item.top,
                        left: item.left,
                        backgroundImage: `url(${item.img})`,
                        margin: item.borderWidth,
                        backgroundSize: 'contain',
                    }"
                >
                    <img v-if="item.img" class="cap-cube__table-image cap-cube__table-image--invisible" :src="item.img" />
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scope>
@import '@/assets/css/decoration/cubeBox.scss';
</style>
