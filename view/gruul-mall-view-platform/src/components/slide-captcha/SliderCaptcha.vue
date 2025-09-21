<template>
    <el-dialog v-model="params.isShow" :show-close="false" :width="captchaRequest.bgImageWidth * props.scale" class="signDialog">
        <div
            :style="{
                height: captchaRequest.bgImageHeight * props.scale + 'px',
                width: captchaRequest.bgImageWidth * props.scale + 'px',
                backgroundImage: 'url(' + params.captcha.bgImage + ')',
                backgroundSize: '100% 100%',
            }"
            style="position: relative"
        >
            <el-image
                :src="params.captcha.sliderImage"
                :style="{
                    left: sliderImageLeft + 'px',
                    height: captchaRequest.sliderImageHeight * props.scale + 'px',
                    width: captchaRequest.sliderImageWidth * props.scale + 'px',
                }"
                style="color: #4ceed6; border: #4cd964; position: absolute"
                @mousedown="down"
            />
        </div>
        <el-progress
            v-if="captchaRequest.bgImageWidth && captchaRequest.bgImageWidth > 0"
            :duration="0"
            :percentage="((sliderImageLeft / props.scale) * 100) / (captchaRequest.bgImageWidth - captchaRequest.sliderImageWidth)"
            :show-text="false"
            color="#4cd964"
        />
    </el-dialog>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import DateUtil from '@/utils/date'
import { ImageCaptchaTrack, CaptchaResponse, TrackType, CaptchaRequest } from './Captcha'
import { ElMessage } from 'element-plus'

const props = defineProps({
    modelValue: {
        type: Boolean,
        required: true,
    },
    scale: {
        type: Number,
        default: 2,
    },
    smsType: {
        type: String,
        default: null,
    },
    getForm: {
        type: Function as PropType<() => any>,
        required: true,
    },
    doSubmit: {
        type: Function as PropType<(request: CaptchaRequest<any>) => Promise<any>>,
        required: true,
    },
    captchaList: {
        type: Object as PropType<CaptchaResponse>,
        required: true,
    },
})
const emits = defineEmits(['update:modelValue', 'success'])
const sliderImageLeft = computed(() => {
    const trackList = captchaRequest.trackList
    const length = trackList.length
    if (!trackList || length <= 0) {
        return 0
    }
    const min = 0
    const max = (captchaRequest.bgImageWidth - captchaRequest.sliderImageWidth) * props.scale
    const left = trackList[length - 1].x * props.scale
    return left <= min ? min : left >= max ? max : left
})

const params = reactive({
    isShow: true,
    sliderValue: 0,
    captcha: {
        id: '',
        startX: 0,
        startY: 0,
        bgImage: '',
        sliderImage: '',
    },
})

const captchaRequest = reactive<ImageCaptchaTrack>({
    bgImageWidth: 0,
    bgImageHeight: 0,
    sliderImageWidth: 0,
    sliderImageHeight: 0,
    startSlidingTime: '',
    endSlidingTime: '',
    startTime: new Date(),
    trackList: [],
})
const refresh = async (data: CaptchaResponse) => {
    initCaptcha(data)
}
watch(
    () => props.modelValue,
    (value) => {
        params.isShow = value
        if (value) {
            refresh(props.captchaList)
        }
    },
    { immediate: true },
)
watch(
    () => params.isShow,
    (value) => emits('update:modelValue', value),
)

const initCaptcha = ({ id, captcha }: CaptchaResponse) => {
    params.sliderValue = 0
    params.captcha.id = id
    params.captcha.startX = 0
    params.captcha.startY = 0
    params.captcha.bgImage = captcha.backgroundImage
    params.captcha.sliderImage = captcha.templateImage
    captchaRequest.startTime = new Date()
    captchaRequest.bgImageWidth = captcha.backgroundImageWidth / props.scale
    captchaRequest.bgImageHeight = captcha.backgroundImageHeight / props.scale
    captchaRequest.sliderImageWidth = captcha.templateImageWidth / props.scale
    captchaRequest.sliderImageHeight = captcha.templateImageHeight / props.scale
    captchaRequest.startSlidingTime = new DateUtil().getYMDHMSs(captchaRequest.startTime)
    captchaRequest.trackList = []
}
const down = (event: any) => {
    const target = event.target
    params.captcha.startX = event.clientX
    params.captcha.startY = event.clientY
    captchaRequest.trackList = []
    captchaRequest.trackList.push({
        x: 0,
        y: 0,
        type: TrackType.DOWN,
        t: new Date().getTime() - captchaRequest.startTime.getTime(),
    })
    target.ondragstart = () => false
    target.onmousemove = moving
    target.onmouseup = up
}
const moving = (event: any) => {
    captchaRequest.trackList.push({
        x: event.clientX - params.captcha.startX,
        y: event.clientY - params.captcha.startY,
        type: TrackType.MOVE,
        t: new Date().getTime() - captchaRequest.startTime.getTime(),
    })
}
const up = (event: any) => {
    const target = event.target
    target.onmousemove = null
    target.onmouseup = null
    captchaRequest.endSlidingTime = new DateUtil().getYMDHMSs(new Date())
    captchaRequest.trackList.push({
        x: event.clientX - params.captcha.startX,
        y: event.clientY - params.captcha.startY,
        type: TrackType.UP,
        t: new Date().getTime() - captchaRequest.startTime.getTime(),
    })
    props.doSubmit(HavesmsType()).then((response) => {
        const { code, msg } = response
        if (code === 200) {
            emits('success', response)
            return
        }
        if (msg) {
            ElMessage.error(msg)
        }
        refresh(props.captchaList)
        return
    })
}
const HavesmsType = () => {
    if (props.smsType) {
        return {
            form: props.getForm(),
            id: params.captcha.id,
            captchaTrack: captchaRequest,
            smsType: props.smsType,
        } as CaptchaRequest<any>
    }
    return {
        form: props.getForm(),
        id: params.captcha.id,
        captchaTrack: captchaRequest,
    } as CaptchaRequest<any>
}
</script>

<style lang="scss" scoped></style>
<style>
.signDialog.el-dialog {
    padding: 0;
}

.signDialog .el-dialog__header {
    padding: 0;
    margin: 0;
}

.signDialog .el-dialog__body {
    padding: 0;
}
</style>
