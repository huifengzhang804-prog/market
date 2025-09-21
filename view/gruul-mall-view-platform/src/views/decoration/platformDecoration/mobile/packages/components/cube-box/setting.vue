<script setup lang="ts">
import { useDecorationStore } from '@/store/modules/decoration'
import QUpload from '@/components/q-upload/q-upload.vue'
import { useVModel } from '@vueuse/core'
import LinkSelect from '@/components/link-select/link-select.vue'
import templateList from './template'
import bannerFormData from './cubeBox'
import type { CubeBoxFormData } from './cubeBox'
import type { PropType } from 'vue'

// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
const buttonlFn = () => {
    dialogVisible.value = true
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    const index = activeShowCubeListWrap.value
    activeItem.value[`subEntry`][index].img = val
    showCubeListWrap.value[index].img = val
}
const checkedFileLists = (val: string[]) => {
    const index = activeShowCubeListWrap.value
    activeItem.value[`subEntry`][index].img = val?.shift() || ''
    showCubeListWrap.value[index].img = val?.shift() || ''
}
// 选择素材 d

const $props = defineProps({
    formData: {
        type: Object as PropType<CubeBoxFormData>,
        default() {
            return bannerFormData
        },
    },
})
const $emit = defineEmits(['update:formData'])
const activeItem = useVModel($props, 'formData', $emit)
const $decorationStore = useDecorationStore()
// 模板选择列表
const chooseTemplate = templateList
// 展示格子
const showCubeList = ref([])
// 格子蒙层
const showCubeListWrap = ref([])
// 蒙层下标
const activeShowCubeListWrap = ref(0)
// 鼠标移动到密度上下标记录
const mouseOverSeleted = ref(null)
// 判断密度是是不是开始选取
const isSeletedMidu = ref(false)
// 开始选取时候的坐标
const coordinates = ref({
    //x 行
    rowStart: 0,
    //y  列
    columnStart: 0,
})
// 对于选中块结算单位
const ranksNumber = ref({
    rowCount: 1,
    columnCount: 1,
})
// 初始化后单个模块的宽度
const styleWidth = ref(0)
// 初始化后单个模块的高度
const styleHeight = ref(0)
const options = [
    // 尺寸属性
    {
        value: '4x4',
        label: '4x4',
    },
    {
        value: '5x5',
        label: '5x5',
    },
    {
        value: '6x6',
        label: '6x6',
    },
    {
        value: '7x7',
        label: '7x7',
    },
]
const miduValue = ref('4x4')
const activeComIndex = computed(() => {
    // 针对多个魔方处理缓存数据(当前组件下标)
    return $decorationStore.activeComIndex
})
// 切换相同组件清除缓存
watch(activeComIndex, () => {
    mouseOverSeleted.value = null
    activeShowCubeListWrap.value = 0
    showCubeListWrap.value = []
    showCubeList.value = []
    isSeletedMidu.value = false
    coordinates.value = {
        rowStart: 0,
        columnStart: 0,
    }
    ranksNumber.value = {
        rowCount: 0,
        columnCount: 0,
    }
})
watch(
    activeItem,
    () => {
        drawCube(false)
        if (activeItem.value.showMethod === 7) {
            miduValue.value = `${activeItem.value.layoutWidth}x${activeItem.value.layoutHeight}`
        }
    },
    {
        deep: true,
    },
)

onMounted(() => {
    if (activeItem.value.showMethod === 7) {
        miduValue.value = `${activeItem.value.layoutWidth}x${activeItem.value.layoutHeight}`
    }
    drawCube()
})

/**
 * 选择魔方密度转变
 */
const handleChangeMidu = () => {
    const arr = miduValue.value.split('x')
    activeItem.value[`layoutWidth`] = Number(arr[0])
    activeItem.value[`layoutHeight`] = Number(arr[1])
    activeItem.value[`subEntry`] = []
    mouseOverSeleted.value = null
    activeShowCubeListWrap.value = 0
    showCubeListWrap.value = []
    showCubeList.value = []
    isSeletedMidu.value = false
    drawCube(true)
}
/**
 * 选择模块属性
 * @param {CubeBoxFormData} item
 * @param {number} index
 */
const handleChangeTemplate = (item, index: number) => {
    mouseOverSeleted.value = null
    activeShowCubeListWrap.value = 0
    showCubeListWrap.value = []
    showCubeList.value = []
    isSeletedMidu.value = false
    activeItem.value.showMethod = index
    if (index === 7) {
        miduValue.value = '4x4'
        handleChangeMidu()
    } else {
        activeItem.value[`layoutWidth`] = item[`layoutWidth`]
        activeItem.value[`layoutHeight`] = item[`layoutHeight`]
        if (!activeItem.value[`subEntry`].length) {
            activeItem.value[`subEntry`] = item[`subEntry`]
        } else {
            const dist = item[`subEntry`].length > activeItem.value[`subEntry`].length ? activeItem.value[`subEntry`].length : item[`subEntry`].length
            for (let i = 0; i < dist; i++) {
                const item1 = item[`subEntry`][i]
                item1[`img`] = activeItem.value[`subEntry`][i][`img`] ? activeItem.value[`subEntry`][i][`img`] : ''
                item1[`link`] = activeItem.value[`subEntry`][i][`link`]
                item1[`linkName`] = activeItem.value[`subEntry`][i][`linkName`]
            }
            activeItem.value[`subEntry`] = item[`subEntry`]
        }
    }
    console.log('showCubeList', showCubeList.value)
}
/**
 * 魔方密度选择后产生后针对魔方绘制画出对应的魔方
 * @param {number} index
 */
const handleShowCubeListWrap = (index: number) => {
    activeShowCubeListWrap.value = index
}
/**
 * 点击任意方块度时候判断该方块是起始位置还是结束位置
 */
const handleSelectMidu = (index1: number, index2: number) => {
    const selected = showCubeList.value[index1][index2].selected
    if (isSeletedMidu.value && !selected) {
        return
    }
    if (selected) {
        //再次确认
        let { rowStart, columnStart } = getItemStart()
        mouseOverSeleted.value = null
        isSeletedMidu.value = false
        let rowEnd = index1
        let columnEnd = index2
        if (rowStart > rowEnd) {
            ;[rowStart, rowEnd] = [rowEnd, rowStart]
        }
        //如果开始列大于结束列，则开始列用结束列，结束列用开始列
        if (columnStart > columnEnd) {
            ;[columnStart, columnEnd] = [columnEnd, columnStart]
        }
        activeItem.value.subEntry.push({
            x: rowStart,
            y: columnStart,
            width: ranksNumber.value.rowCount,
            height: ranksNumber.value.columnCount,
            img: '',
            link: {
                id: null,
                type: null,
                name: '',
                url: '',
                append: '',
            },
            linkName: '',
        })
        showCubeList.value.forEach((item1, index3) => {
            //所有归零
            item1.forEach((item2, index4) => {
                showCubeList.value[index3][index4].start = 0
            })
        })
        showCubeList.value.forEach((t, i) => {
            if (t.length) {
                t.forEach((t1, j) => {
                    if (t1.selected === true) {
                        showCubeList.value[i][j][`finish`] = true
                    }
                })
            }
        })
        ranksNumber.value = {
            rowCount: 1,
            columnCount: 1,
        }
    } else {
        mouseOverSeleted.value = null
        showCubeList.value[index1][index2].selected = true
        showCubeList.value[index1][index2].start = 1
        isSeletedMidu.value = true
        coordinates.value = {
            rowStart: index1,
            columnStart: index2,
        }
    }
}
/**
 * 鼠标在任意方块的上滑动计算方块队列
 * @param {number} index1
 * @param {number} index2
 */
const handleOnMouseOver = (index1: number, index2: number) => {
    showCubeList.value.forEach((item1, index3) => {
        item1.forEach((item2, index4) => {
            if (!item2.finish) {
                showCubeList.value[index3][index4].selected = false
            }
        })
    })
    if (isSeletedMidu.value) {
        //鼠标移动
        let rowStart, columnStart
        showCubeList.value.forEach((item1, index3) => {
            item1.forEach((item2, index4) => {
                if (item2.start) {
                    rowStart = index3
                    columnStart = index4
                }
            })
        })
        let rowEnd = index1
        let columnEnd = index2
        //如果开始行大于结束行，则开始行用结束行，结束行用开始行
        if (rowStart > rowEnd) {
            ;[rowStart, rowEnd] = [rowEnd, rowStart]
        }
        //如果开始列大于结束列，则开始列用结束列，结束列用开始列
        if (columnStart > columnEnd) {
            ;[columnStart, columnEnd] = [columnEnd, columnStart]
        }

        let rowCount = 0 //总行数
        const columnCount = [] //总列数
        let isAdd = true
        for (let forRowStart = rowStart; forRowStart <= rowEnd; forRowStart++) {
            rowCount++
            //遍历列
            for (let i = columnStart; i <= columnEnd; i++) {
                //当前行列坐标
                // const currentCoordinates = forRowStart + ":" + i;
                //检测当前的模块是否被占用，如果被占用，那么整个都不能选择
                if (showCubeList.value[forRowStart][i].finish) {
                    isAdd = false
                    break
                }
                if (!inArray(i, columnCount)) columnCount.push(i)
                showCubeList.value[forRowStart][i].selected = true
            }
        }
        if (!isAdd) {
            let rowStart1, columnStart1
            showCubeList.value.forEach((item1, index1) => {
                item1.forEach((item2, index2) => {
                    if (!item2.finish) {
                        showCubeList.value[index1][index2].selected = false
                    }
                    if (item2.start) {
                        rowStart1 = index1
                        columnStart1 = index2
                    }
                })
            })
            showCubeList.value[rowStart1][columnStart1].selected = true
        }
        ranksNumber.value = {
            rowCount: rowCount,
            columnCount: columnCount.length,
        }
    }
}
/**
 * 点击魔方块的showCubeListWrap下标
 * @param {number} index
 */
const handleOnMouseOverSeleted = (index: number) => {
    mouseOverSeleted.value = index
}
/**
 * 监听该模快是否被删除
 */
const handleDelSelected = (i: number) => {
    const subEntry = activeItem.value[`subEntry`]
    showCubeList.value[subEntry[i].x][subEntry[i].y].selected = false
    showCubeList.value[subEntry[i].x][subEntry[i].y].finish = 0
    showCubeList.value[subEntry[i].x][subEntry[i].y].start = 0
    for (let j = 1; j < subEntry[i].width; j++) {
        showCubeList.value[subEntry[i].x + j][subEntry[i].y].selected = false
        showCubeList.value[subEntry[i].x + j][subEntry[i].y].finish = 0
        showCubeList.value[subEntry[i].x][subEntry[i].y].start = 0
    }
    for (let l = 1; l < subEntry[i].height; l++) {
        showCubeList.value[subEntry[i].x][subEntry[i].y + l].selected = false
        showCubeList.value[subEntry[i].x][subEntry[i].y + l].finish = 0
        showCubeList.value[subEntry[i].x][subEntry[i].y].start = 0
    }
    showCubeList.value.forEach((item1, index3) => {
        //清除选中的
        item1.forEach((item2, index4) => {
            if (!item2.finish) {
                showCubeList.value[index3][index4].selected = false
            }
            showCubeList.value[index3][index4].start = 0
            showCubeList.value[index3][index4].finish = 0
        })
    })
    mouseOverSeleted.value = null
    isSeletedMidu.value = false
    activeShowCubeListWrap.value = 0
    ranksNumber.value = {
        rowCount: 1,
        columnCount: 1,
    }
    activeItem.value[`subEntry`].splice(i, 1)
}
function getItemStart() {
    let rowStart, columnStart
    showCubeList.value.forEach((item1, index3) => {
        item1.forEach((item2, index4) => {
            if (item2.start) {
                rowStart = index3
                columnStart = index4
            }
        })
    })
    return { rowStart, columnStart }
}
/**
 * 魔方密度选择后产生相对应魔方数据
 * @param {boolean} isChangeMidu
 */
function drawCube(isChangeMidu = false) {
    if (activeItem.value) {
        // this.formData = Object.assign({}, this.activeItem);
        const perviewLayoutWidth = 322
        // if(this.showCubeList)
        if (showCubeList.value.length && activeItem.value.showMethod === 7 && !isChangeMidu) {
            //拦截密度选择截取
        } else {
            showCubeList.value = []
            const item = activeItem.value
            const layoutWidth = item.layoutWidth
            const layoutHeight = item.layoutHeight
            //画魔方
            const drawStyleWidth = Math.ceil(perviewLayoutWidth / layoutWidth)
            const drawStyleHeight = layoutHeight !== 1 ? Math.ceil(perviewLayoutWidth / layoutHeight) : drawStyleWidth
            for (let i = 0; i < layoutWidth; i++) {
                const ul = []
                for (let j = 0; j < layoutHeight; j++) {
                    const li = {
                        style: {
                            width: drawStyleWidth + 'px',
                            height: drawStyleHeight + 'px',
                        },
                        coordinates: i + ':' + j,
                        selected: false,
                        finish: 0,
                        start: 0,
                    }
                    ul.push(li)
                }
                showCubeList.value.push(ul)
            }
            styleWidth.value = drawStyleWidth
            styleHeight.value = drawStyleHeight
        }
        drawCubeWrap(styleWidth.value, styleHeight.value)
    }
}
/**
 * 魔方密度选择后产生后针对对应魔方数据绘画出对应的魔方
 * @param {number} divWidth
 * @param {number} divHeight
 */
function drawCubeWrap(divWidth: number, divHeight: number) {
    const item = activeItem.value
    const subEntry = item.subEntry
    const showMethod = activeItem.value.showMethod
    showCubeListWrap.value = []
    if (subEntry.length) {
        for (let i = 0; i < subEntry.length; i++) {
            const coverDiv = {
                top: subEntry[i].y * divHeight + 'px',
                left: subEntry[i].x * divWidth + 'px',
                width: divWidth * subEntry[i].width + 'px',
                height: divHeight * subEntry[i].height + 'px',
                paddingTop: (divHeight * subEntry[i].height) / 2 + 'px',
                img: subEntry[`img`] ? subEntry[`img`] : '',
            }
            showCubeList.value[subEntry[i].x][subEntry[i].y].selected = true
            showCubeList.value[subEntry[i].x][subEntry[i].y].finish = 1
            for (let j = 1; j < subEntry[i].width; j++) {
                showCubeList.value[subEntry[i].x + j][subEntry[i].y].selected = true
                showCubeList.value[subEntry[i].x + j][subEntry[i].y].finish = 1
            }
            for (let l = 1; l < subEntry[i].height; l++) {
                showCubeList.value[subEntry[i].x][subEntry[i].y + l].selected = true
                showCubeList.value[subEntry[i].x][subEntry[i].y + l].finish = 1
            }
            if (showMethod === 0 || showMethod === 1 || showMethod === 2) {
                coverDiv[`text`] = '宽度' + Math.ceil(750 / item.layoutWidth) + '像素'
            } else {
                coverDiv[`text`] =
                    Math.ceil((750 / item.layoutWidth) * subEntry[i].width) +
                    '*' +
                    Math.ceil((750 / item.layoutHeight) * subEntry[i].height) +
                    '或同等比例'
            }
            showCubeListWrap.value.push(coverDiv)
        }
    }
}
function inArray(elem, arr) {
    let i = 0
    const n = arr.length
    for (; i < n; ++i) if (arr[i] === elem) return true
    return false
}
async function handleChange(imgSrc: string) {
    const index = activeShowCubeListWrap.value
    activeItem.value[`subEntry`][index].img = imgSrc
    showCubeListWrap.value[index].img = imgSrc
}
</script>

<template>
    <div clas="zent-design-editor-item">
        <div class="zent-design-editor__control-group">
            <div class="zent-design-editor__control-group-container">
                <div class="zent-design-editor__control-group-label zent-design-editor__control-group-label--top" title="选择模版">模版</div>
                <div class="zent-design-editor__control-group-control">
                    <div>
                        <div
                            v-for="(item, index4) in chooseTemplate"
                            :key="`template${index4}`"
                            class="rc-design-select-templates"
                            :class="{ active: index4 === formData.showMethod }"
                            :data-length="index4 !== 7 ? item[`subEntry`].length : 0"
                            @click="handleChangeTemplate(item, index4)"
                        >
                            <div class="rc-design-select-templates__image-block">
                                <img :src="item.img" alt="temp" />
                            </div>
                            <div class="rc-design-select-templates__title">
                                {{ item.title }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="formData.showMethod === 7" class="zent-design-editor__control-group">
            <div class="zent-design-editor__control-group-container">
                <div class="zent-design-editor__control-group-label">
                    <!-- react-text: 2624 -->
                    密度:
                    <!-- /react-text -->
                </div>
                <div class="zent-design-editor__control-group-control">
                    <div class="zent-popover-wrapper zent-select" style="display: inline-block">
                        <el-select v-model="miduValue" placeholder="请选择" @change="handleChangeMidu">
                            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </div>
                </div>
            </div>
        </div>
        <div class="zent-design-editor__control-group">
            <div class="zent-design-editor__control-group-container">
                <div class="zent-design-editor__control-group-label zent-design-editor__control-group-label--top">
                    <!-- react-text: 2907 -->
                    布局:
                    <!-- /react-text -->
                </div>
                <div class="zent-design-editor__control-group-control">
                    <div class="rc-design-component-cube clearfix">
                        <ul v-for="(item, index1) in showCubeList" :key="index1" class="cube-row">
                            <li
                                v-for="(item1, index2) in item"
                                :ref="`cube-item-${index1}-${index2}`"
                                :key="`cube-item-${index2}`"
                                class="cube-item"
                                :class="{ 'item-selected': item1.selected }"
                                :style="{
                                    width: item1.style.width,
                                    height: item1.style.height,
                                }"
                                :data-coordinates="item.coordinates"
                                :data-ref="`cube-item-${index1}-${index2}`"
                                :data-selected="item1.selected ? 1 : 0"
                                :data-start="item1.start"
                                @click.stop="handleSelectMidu(index1, index2)"
                                @mouseover="handleOnMouseOver(index1, index2)"
                            >
                                <span v-if="!item1.selected" class="plus-icon" :style="{ 'line-height': item1.style.height }">+</span>
                            </li>
                        </ul>
                        <div
                            v-for="(item, index3) in showCubeListWrap"
                            :key="`cube-selected-${index3}`"
                            class="cube-selected"
                            :class="{
                                'cube-selected-click': activeShowCubeListWrap === index3,
                            }"
                            :style="{
                                width: item.width,
                                height: item.height,
                                top: item.top,
                                left: item.left,
                            }"
                            @click.stop="handleShowCubeListWrap(index3)"
                            @mouseover.stop="handleOnMouseOverSeleted(index3)"
                        >
                            <el-icon
                                v-if="activeItem.showMethod === 7 && mouseOverSeleted === index3 && activeShowCubeListWrap === index3"
                                class="rc-design-editor-card-item-delete el-icon-error"
                                @click.stop="handleDelSelected(index3)"
                                ><i-ep-circleClose
                            /></el-icon>
                            <div v-if="!activeItem[`subEntry`][index3].img" class="cube-selected-text">
                                {{ item.text }}
                            </div>
                            <img v-else :src="activeItem[`subEntry`][index3].img" :style="{ width: item.width, height: item.height }" />
                            <div
                                v-if="activeShowCubeListWrap === index3 && activeItem[`subEntry`][index3].img"
                                class="iconfont iconshangpinxiangqing-baozhuang iconshangpinxiangqing-baozhuang-2"
                            ></div>
                        </div>
                    </div>
                    <div class="zent-design-editor__control-group-help-desc">
                        {{ formData.showMethod === 7 ? '选定布局区域，在下方添加图片，建议添加比例一致的图片' : '选定布局区域，在下方添加图片' }}
                    </div>
                </div>
            </div>
        </div>
        <div class="zent-design-editor__control-group">
            <div class="zent-design-editor__control-group-container">
                <div class="zent-design-editor__control-group-label">间隙:</div>
                <div class="zent-design-editor__control-group-control">
                    <el-slider v-model="activeItem.borderWidth" show-input class="zent-slider" :max="30"></el-slider>
                </div>
            </div>
        </div>
        <div class="zent-design-editor__control-group">
            <div class="zent-design-editor__control-group-container">
                <div class="zent-design-editor__control-group-label">边距:</div>
                <div class="zent-design-editor__control-group-control">
                    <el-slider v-model="activeItem.pageMargin" show-input class="zent-slider" :max="30"></el-slider>
                </div>
            </div>
        </div>
        <div v-if="activeItem.subEntry.length > 0" class="rc-design-editor-card-item">
            <div class="rc-design-component-editor_subentry-item clearfix">
                <!-- <div class="rc-design-common-choice-image-component image-editor">
      
        </div>-->
                <div class="subentry-item-editor-form-content">
                    <div class="zent-design-editor__control-group subentry-control-group">
                        <div class="zent-design-editor__control-group-container flex" style="align-items: flex-start">
                            <div class="zent-design-editor__control-group-label">图片：</div>
                            <div class="zent-design-editor__control-group-control" style="flex-grow: 0">
                                <!-- <q-upload :width="60" :height="60" :src="activeItem[`subEntry`][activeShowCubeListWrap].img" @change="handleChange" /> -->

                                <div
                                    v-if="!activeItem[`subEntry`][activeShowCubeListWrap].img"
                                    class="selectMaterialStyle"
                                    style="width: 60px; height: 60px"
                                    @click="buttonlFn"
                                >
                                    <span class="selectMaterialStyle__span">+ </span>
                                </div>
                                <img
                                    v-else
                                    alt=""
                                    class="selectMaterialStyle"
                                    style="width: 60px; height: 60px"
                                    :src="activeItem[`subEntry`][activeShowCubeListWrap].img"
                                    @click="buttonlFn"
                                />
                            </div>
                        </div>
                        <label class="zent-design-editor__control-group-container flex" style="align-items: flex-start">
                            <div class="zent-design-editor__control-group-label">链接：</div>
                            <div class="zent-design-editor__control-group-control">
                                <div class="rc-choose-link-menu">
                                    <div class="zent-popover-wrapper rc-choose-link-menu-popover-wrapper" style="display: inline-block">
                                        <div class="rc-choose-link-menu-trigger">
                                            <LinkSelect :link="activeItem[`subEntry`][activeShowCubeListWrap].link" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </label>
                    </div>
                </div>
            </div>
        </div>
        <!-- 选择素材 e -->
        <selectMaterial
            :dialog-visible="dialogVisible"
            :upload-files="1"
            @select-material-fn="selectMaterialFn"
            @cropped-file-change="croppedFileChange"
            @checked-file-lists="checkedFileLists"
        />
        <!-- 选择素材 d -->
    </div>
</template>

<style lang="scss" scope>
@import '@/assets/css/decoration/cubeBox.scss';
@include b(selectMaterialStyle) {
    width: 60px;
    height: 60px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    @include e(span) {
        color: #999;
        font-size: 14px;
        line-height: 24px;
    }
}
</style>
