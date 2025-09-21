<script setup lang="ts">
import { Sort, Switch, ZoomIn, RefreshRight, ZoomOut, ArrowLeft, ArrowRight, ArrowUp, ArrowDown, RefreshLeft } from '@element-plus/icons-vue'
import VueCropper from 'vue-cropperjs'
import { useVModel } from '@vueuse/core'
import uuid from '@/utils/uuid'
import { IMAGETYPE } from '@/components/q-upload/upload'
import 'cropperjs/dist/cropper.css'

const $props = defineProps({
    cropperSrc: { type: String, default: '' },
    cropperShow: {
        type: Boolean,
        default: false,
    },
    appendToBody: {
        type: Boolean,
        default: false,
    },
    type: {
        type: String as PropType<keyof typeof IMAGETYPE>,
        default: 'image/png',
    },
})
const $emit = defineEmits(['upload-img', 'update:cropperShow', 'close'])
const cropImg = ref('')
const cropperShow = useVModel($props, 'cropperShow', $emit)
const CropperRef = ref()
const FlipY = ref()
const FlipX = ref()

/**
 * 设置翻转
 */
const flipX = () => {
    const dom = FlipX.value
    let scale = dom.getAttribute('data-scale')
    scale = scale ? -scale : -1
    CropperRef.value.scaleX(scale)
    dom.setAttribute('data-scale', scale)
}
/**
 * 设置翻转
 */
const flipY = () => {
    const dom = FlipY.value
    let scale = dom.getAttribute('data-scale')
    scale = scale ? -scale : -1
    CropperRef.value.scaleY(scale)
    dom.setAttribute('data-scale', scale)
}
/**
 * 移动图片
 * @param {*} offsetX
 * @param {*} offsetY
 */
const move = (offsetX: number, offsetY: number) => {
    CropperRef.value.move(offsetX, offsetY)
}
/**
 * 重置
 */
const reset = () => {
    CropperRef.value.reset()
}
/**
 * 角度旋转
 * @param {*} deg 角度
 */
const rotate = (deg: number) => {
    CropperRef.value.rotate(deg)
}
/**
 * 图片放大
 * @param {*} percent
 */
const zoom = (percent: number) => {
    CropperRef.value.relativeZoom(percent)
}

/**
 * 处理完成
 */
const handleFinish = () => {
    const type = $props.cropperSrc.substring($props.cropperSrc.lastIndexOf('.') + 1)
    // 获取截图的base64 数据
    cropImg.value = CropperRef.value.getCroppedCanvas().toDataURL($props.type)
    function dataURLtoFile(dataurl: string, filename: string) {
        let arr = dataurl.split(',')
        let mime = arr[0].match(/:(.*?);/)![1]
        let suffix = mime.split('/')[1]
        let bstr = window.atob(arr[1])
        let n = bstr.length
        let u8arr = new Uint8Array(n)
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n)
        }
        return new File([u8arr], `${filename}.${suffix}`, {
            type: mime,
        })
        //将base64转换为文件
    }
    const file = dataURLtoFile(cropImg.value, uuid(10))
    $emit('upload-img', file)
    cropperShow.value = false
}
const handleCloseDialog = () => {
    $emit('close')
    cropperShow.value = false
}
</script>
<template>
    <el-dialog v-model="cropperShow" width="700px" title="图片裁剪" :append-to-body="appendToBody" @close="handleCloseDialog">
        {{ $props.cropperSrc }}
        <div class="cropper-container">
            <div class="cropper-container__content">
                <section class="cropper-container__content--area">
                    <div class="img-cropper">
                        <div style="width: 400px">
                            <VueCropper
                                ref="CropperRef"
                                :ontainer-style="{ width: '200px', height: '400px' }"
                                :auto-crop-area="400"
                                :output-type="$props.type.split('/')[1]"
                                overflow-hidden
                                :src="$props.cropperSrc"
                                preview=".preview"
                                background
                            />
                        </div>
                        <el-button-group class="cropper-container__btnGroup">
                            <el-button size="" :icon="ZoomIn" @click="zoom(0.2)" />
                            <el-button size="" :icon="ZoomOut" @click="zoom(-0.2)" />
                            <el-button size="" :icon="ArrowLeft" @click="move(-10, 0)" />
                            <el-button size="" :icon="ArrowRight" @click="move(10, 0)" />
                            <el-button size="" :icon="ArrowUp" @click="move(0, -10)" />
                            <el-button size="" :icon="ArrowDown" @click="move(0, 10)" />
                            <el-button size="" :icon="Switch" class="flipX" @click="flipX" />
                            <el-button size="" :icon="Sort" @click="flipY" />
                            <a ref="FlipX" href="#" class="inita" />
                            <a ref="FlipY" href="#" class="inita" />
                            <el-button size="" :icon="RefreshLeft" @click="rotate(90)" />
                            <el-button size="" :icon="RefreshRight" @click="rotate(-90)" />
                        </el-button-group>
                    </div>
                </section>
                <!-- 预览框 s  -->
                <section class="preview-area">
                    <p>预览</p>
                    <div class="preview"></div>
                </section>
            </div>
        </div>

        <template #footer>
            <span class="dialog-footer">
                <el-button size="" @click="reset">重置</el-button>
                <el-button size="" @click="handleCloseDialog">取消</el-button>
                <el-button size="" type="primary" @click="handleFinish">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>
<style scoped lang="scss">
@import 'cropperjs/dist/cropper.css';

@include b(dialog-footer) {
    display: flex;
    justify-content: end;
    padding-right: 20px;
}
@include b(cropper-container) {
    width: 100%;
    @include e(content) {
        display: flex;
        justify-content: space-around;
    }
    @include e(btnGroup) {
        width: 400px;
        display: flex;
        justify-content: center;
        margin-top: 20px;
    }
}
@include b(preview-area) {
    width: 30%;
    & p {
        font-size: 1.25rem;
        color: #000;
        font-weight: 700;
        margin: 0;
        margin-bottom: 1rem;
    }
    & p:last-of-type {
        margin-top: 1rem;
    }
}
@include b(preview) {
    width: 100%;
    height: calc(372px * (9 / 16));
    overflow: hidden;
}
:deep(.cropper-hide) {
    height: 0 !important;
}
</style>
