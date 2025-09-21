<template>
    <div class="content_container">
        <div class="content">
            <div class="cont_left">
                <div class="img_box">
                    <el-carousel
                        v-if="advertisementForm.showFlag"
                        ref="carousel"
                        indicator-position="none"
                        arrow="always"
                        height="542px"
                        style="width: 250px; position: relative"
                        :autoplay="false"
                    >
                        <el-carousel-item v-for="(item, index) in advertisementForm.imageList" :key="index">
                            <img :src="item?.url" alt="" style="width: 100%; height: 100%" />
                        </el-carousel-item>
                    </el-carousel>
                    <QIcon name="icon-xiala" class="nextBtnLeft" size="24px" @click="prev"></QIcon>
                    <QIcon name="icon-xiala" class="nextBtnRight" size="24px" @click="next"></QIcon>
                </div>
            </div>
            <div class="cont_right">
                <el-form
                    ref="openScreenFrom"
                    :model="advertisementForm"
                    label-width="90px"
                    label-position="left"
                    :rules="rules"
                    style="max-width: 1150px"
                >
                    <el-form-item label="是否开启" prop="showFlag">
                        <el-radio-group v-model="advertisementForm.showFlag">
                            <el-radio :value="true">开启</el-radio>
                            <el-radio :value="false">关闭</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <div v-if="advertisementForm.showFlag">
                        <el-form-item label="展示时间" prop="displayTime" style="width: 400px">
                            <el-date-picker
                                v-model="advertisementForm.displayTime"
                                type="datetimerange"
                                range-separator="-"
                                start-placeholder="开始时间"
                                end-placeholder="结束时间"
                                format="YYYY/MM/DD HH:mm"
                                value-format="YYYY-MM-DD HH:mm"
                                size="default"
                                :disabled-date="disabledDate"
                                @change="handleDateTimeChange"
                            />
                        </el-form-item>
                        <el-form-item label="跳过方式" prop="skipWay">
                            <el-radio-group v-model="advertisementForm.skipWay" style="display: flex; flex-direction: column; align-items: start">
                                <el-radio value="NO_SKIP" style="margin-bottom: 10px">不跳过</el-radio>
                                <el-radio :value="flagAutomaticallySkip">
                                    <div class="skipWay">
                                        观看&ensp;
                                        <el-input
                                            v-model="advertisementForm.skipSecond"
                                            style="width: 102px"
                                            class="no-spinner"
                                            @input="handleInputNumber(advertisementForm.skipSecond, 'skipSecond')"
                                        >
                                            <template #append>秒</template>
                                        </el-input>
                                        &ensp;后, &ensp;
                                        <el-select v-model="flagAutomaticallySkip" style="width: 110px" @change="changeAutomaticallySkip">
                                            <el-option
                                                v-for="item in flagAutomaticallySkipList"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value"
                                            >
                                            </el-option>
                                        </el-select>
                                    </div>
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="展示频次" prop="showFrequency">
                            <el-radio-group
                                v-model="advertisementForm.showFrequency"
                                style="display: flex; flex-direction: column; align-items: start"
                            >
                                <el-radio value="ONLY_ONE" style="margin-bottom: 10px">每人仅展示一次</el-radio>
                                <el-radio value="EVERY_ENTER" style="margin-bottom: 10px">每次进入</el-radio>
                                <el-radio value="FEW_TIMES_BEFORE_DAY" style="margin-bottom: 10px">
                                    <div class="skipWay">
                                        每日的前&ensp;
                                        <el-input
                                            v-model="advertisementForm.times"
                                            style="width: 102px"
                                            class="no-spinner"
                                            @input="handleInputNumber(advertisementForm.times, 'times')"
                                        >
                                            <template #append>次</template>
                                        </el-input>
                                        &ensp;展示
                                    </div>
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="广告图片" prop="imageList">
                            <div>
                                <div style="margin-bottom: 15px">
                                    <el-button
                                        :disabled="advertisementForm.imageList.length === 5"
                                        type="primary"
                                        @click="buttonlFnHost(advertisementForm.imageList.length, false)"
                                        >添加</el-button
                                    >
                                    <span style="font-size: 12px; color: #999; margin-left: 5px"
                                        >建议尺寸：750*1334等比图片，建议大小不超过1M，最多5张，鼠标拖拽调整广告顺序</span
                                    >
                                </div>
                                <div style="display: flex">
                                    <div
                                        v-if="advertisementForm.imageList.length !== 5"
                                        class="selectMaterialStyle"
                                        @click="buttonlFnHost(advertisementForm.imageList.length, false)"
                                    >
                                        <el-icon style="font-size: 30px; font-weight: bold; color: #d8d8d8"><Plus /></el-icon>
                                    </div>
                                    <VueDraggableNext v-model="advertisementForm.imageList" style="display: flex">
                                        <div
                                            v-for="(item, index) in advertisementForm.imageList"
                                            :key="index"
                                            style="position: relative; margin-right: 20px"
                                            class="img_box"
                                        >
                                            <img
                                                :src="advertisementForm.imageList[index]?.url"
                                                alt=""
                                                class="selectMaterialStyle"
                                                :style="{ border: imgIndex === index ? '2px solid #555cfd' : '' }"
                                            />
                                            <div class="mask" @click="choice(index)">
                                                <el-button class="imgClose" circle @click.prevent.stop="delImgHandle(index)">
                                                    <QIcon name="icon-cuowu" size="20px" />
                                                </el-button>
                                                <el-button
                                                    type="primary"
                                                    style="background-color: #fff; color: #555cfd; border: none; margin-left: -20%"
                                                    @click.prevent.stop="buttonlFnHost(index, true)"
                                                    >替换</el-button
                                                >
                                            </div>
                                        </div>
                                    </VueDraggableNext>
                                </div>
                            </div>
                        </el-form-item>
                        <div v-if="advertisementForm.imageList.length > 0" style="position: relative">
                            <div
                                v-for="(val, ind) in advertisementForm.imageList"
                                :key="ind"
                                :style="{
                                    position: 'absolute',
                                    left: imgIndex === 4 ? '650px' : imgIndex * 160 + 'px',
                                }"
                            >
                                <el-form-item v-if="ind === imgIndex" label="展示时长" prop="showTime" label-width="78px" style="margin-left: 85px">
                                    <el-input
                                        v-model="val.showTime"
                                        placeholder="1-60秒"
                                        style="width: 310px"
                                        class="no-spinner"
                                        @input="handleInput"
                                    >
                                        <template #append>秒</template>
                                    </el-input>
                                </el-form-item>
                                <el-form-item v-if="ind === imgIndex" label="链接" label-width="40px" style="margin-left: 85px">
                                    <link-select
                                        v-model:link="val.link"
                                        style="width: 340px"
                                        :customizedProTab="$decorationStore.getEndpointType === 'WECHAT_MINI_APP'"
                                        :isH5="false"
                                        @update-value="updateValue"
                                    />
                                </el-form-item>
                            </div>
                        </div>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
    <div class="foot">
        <el-button type="primary" @click="submitForm">保存</el-button>
    </div>
    <selectMaterial
        :dialog-visible="dialogVisiblesHost"
        :upload-files="uploadQuantity ? 1 : 5 - advertisementForm.imageList.length"
        @select-material-fn="selectMaterialFnHost"
        @cropped-file-change="croppedFileChangeHost"
        @checked-file-lists="checkedFileListsHost"
    />
</template>
<script setup lang="ts">
import { AdvertisementFormType } from './types'
import type { FormRules } from 'element-plus'
import type { FormInstance } from 'element-plus'
import selectMaterial from '@/views/material/selectMaterial.vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { Plus } from '@element-plus/icons-vue'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { doGetOpenAdvertisement, upDateOpenAdvertisement } from '@/apis/decoration'
import LinkSelect from '@/components/link-select/link-select.vue'
import { ElMessage } from 'element-plus'

// 展示时间的默认时间
const startTime = new Date()
startTime.setMinutes(new Date().getMinutes() + 10)
let year = startTime.getFullYear()
let month = String(startTime.getMonth() + 1).padStart(2, '0') // 月份从0开始，所以需要+1
let day = String(startTime.getDate()).padStart(2, '0')
let hours = String(startTime.getHours()).padStart(2, '0')
let minutes = String(startTime.getMinutes()).padStart(2, '0')
let startTimes = `${year}-${month}-${day} ${hours}:${minutes}`

const endTime = new Date()
endTime.setDate(new Date().getDate() + 10)
let years = endTime.getFullYear()
let months = String(endTime.getMonth() + 1).padStart(2, '0') // 月份从0开始，所以需要+1
let days = String(endTime.getDate()).padStart(2, '0')
let hourss = String(endTime.getHours()).padStart(2, '0')
let minutess = String(endTime.getMinutes()).padStart(2, '0')
let endTimes = `${years}-${months}-${days} ${hourss}:${minutess}`

const $decorationStore = useDecorationStore()
const carousel = ref<any>(null)
const openScreenFrom = ref<FormInstance>()
const advertisementForm = ref<AdvertisementFormType>({
    showFlag: true,
    startTime: startTimes,
    endTime: endTimes,
    skipWay: 'NO_SKIP',
    skipSecond: '',
    showFrequency: 'ONLY_ONE',
    times: '',
    endPoint: $decorationStore.getEndpointType,
    displayTime: [startTimes, endTimes],
    imageList: [],
})
const uploadQuantity = ref(false)
const flagAutomaticallySkip = ref('MANUAL_SKIP_AFTER_SECONDS')
const flagAutomaticallySkipList = ref([
    { label: '手动跳过', value: 'MANUAL_SKIP_AFTER_SECONDS' },
    { label: '自动跳过', value: 'AUTO_SKIP_AFTER_SECONDS' },
])
const imgIndex = ref<number>(0)
const validateTimes = (rule: any, value: any, callback: any) => {
    return value === 'FEW_TIMES_BEFORE_DAY' && Number(advertisementForm.value.times) <= 0 ? callback(new Error('请输入每日前展示的次数')) : callback()
}
const validateSkipWay = (rule: any, value: any, callback: any) => {
    return value !== 'NO_SKIP' && Number(advertisementForm.value.skipSecond) <= 0 ? callback(new Error('请输入观看秒数')) : callback()
}
const chengeFormFlag = ref(false)
const validateImgList = (rule: any, value: any, callback: any) => {
    return !value.length ? callback(new Error('请选择广告图片')) : callback()
}
const validateImgListShowTime = (rule: any, value: any, callback: any) => {
    return !advertisementForm.value.imageList.every((v) => v.showTime) ? callback(new Error('请输入展示时长')) : callback()
}
const rules = reactive<FormRules<AdvertisementFormType>>({
    showFlag: [{ required: true, message: '', trigger: 'change' }],
    displayTime: [{ required: true, message: '请选择展示时间', trigger: 'change' }],
    showFrequency: [{ required: true, validator: validateTimes, trigger: 'change' }],
    skipWay: [{ required: true, validator: validateSkipWay, trigger: 'change' }],
    imageList: [{ required: true, validator: validateImgList, trigger: 'change' }],
    showTime: [{ required: true, validator: validateImgListShowTime, trigger: 'change' }],
})

watch(
    () => advertisementForm.value,
    (newVal, oldVal) => {
        if (JSON.stringify(newVal) === JSON.stringify(oldVal)) {
            chengeFormFlag.value = true
        }
    },
    { deep: true },
)
const updateValue = () => {
    chengeFormFlag.value = true
    console.log('测试李星宇')
}

/**
 * 根据终端查询广告信息
 */
const init = async () => {
    const { code, data } = await doGetOpenAdvertisement(advertisementForm.value.endPoint)
    if (code === 200) {
        advertisementForm.value = {
            ...data,
            displayTime: [data.startTime, data.endTime],
            imageList: data.imageList.map((v: any) => {
                return {
                    ...v,
                    link: JSON.parse(v.link)
                        ? JSON.parse(v.link)
                        : {
                              id: null,
                              type: null,
                              name: '',
                              url: '',
                              append: '',
                          },
                }
            }),
        } as AdvertisementFormType
        if (data.skipWay !== 'NO_SKIP') {
            flagAutomaticallySkip.value = data.skipWay
        }
    }
}
init()

/**
 * 上一张
 */
const prev = () => {
    carousel.value.prev()
}
/**
 * 下一张
 */
const next = () => {
    carousel.value.next()
}

/**
 * 修改时间
 * @param value
 */
const handleDateTimeChange = (value: any) => {
    if (!value) {
        advertisementForm.value.displayTime = []
    }
    if (value && value.length === 2) {
        const start = new Date(value[0]) as any
        const end = new Date(value[1]) as any
        if (start.getTime() <= new Date().getTime() - 60 * 1000) {
            ElMessage.error('开始时间必须大于当前时间')
            advertisementForm.value.displayTime = []
        } else if (end - start <= 60 * 59 * 1000) {
            ElMessage.error('结束时间必须大于开始时间1小时')
            advertisementForm.value.displayTime = []
        } else {
            const [start, end] = value
            advertisementForm.value.startTime = start
            advertisementForm.value.endTime = end
        }
    }
}

/**
 * 时间选择范围
 */
const disabledDate = (time: Date) => {
    return time.getTime() < Date.now() - 8.64e7
}
/**
 * 手动跳过/自动跳过方法
 */
const changeAutomaticallySkip = (value: string) => {
    flagAutomaticallySkip.value = value
    advertisementForm.value.skipWay = value
}

// 选择素材
const dialogVisiblesHost = ref(false)
const selectMaterialFnHost = (val: boolean) => {
    if (advertisementForm.value.imageList.length > 5) return
    dialogVisiblesHost.value = val
}
const indexs = ref<number>(-1)
let num = 0
const buttonlFnHost = (val: number, flag: boolean) => {
    dialogVisiblesHost.value = true
    indexs.value = val
    num++
    uploadQuantity.value = flag
}

//  裁剪后返回的单个素材
const croppedFileChangeHost = (val: string) => {
    if (num === 0)
        advertisementForm.value.imageList = [
            {
                url: val,
                showTime: 1,
                link: {
                    id: '',
                    type: 0,
                    name: '',
                    url: '',
                    append: '',
                },
            },
        ]
    else advertisementForm.value.imageList[indexs.value as any].url = val
}

// 选中素材返回的素材合集
const checkedFileListsHost = (val: string[]) => {
    if (num === 0) {
        advertisementForm.value.imageList[indexs.value].url = val[0]
    } else if (Array.isArray(val)) {
        if (uploadQuantity.value === true) {
            advertisementForm.value.imageList[indexs.value as any].url = val[0]
        } else {
            val.forEach((item) => {
                let newObj = {
                    url: item,
                    showTime: 1,
                    link: {
                        id: '',
                        type: 0,
                        name: '',
                        url: '',
                        append: '',
                    },
                }
                advertisementForm.value.imageList.push(newObj)
            })
        }
    } else {
        advertisementForm.value.imageList[indexs.value as any].url = val
    }
}

/**
 * 删除商品主图
 * @param {number} index
 */
const delImgHandle = (index: number) => {
    advertisementForm.value.imageList.splice(index, 1)
}

/**
 * 最大只能输入60
 */
const handleInput = (value: string) => {
    const number = Number(value)
    if (isNaN(number) || number < 0 || number > 60) {
        advertisementForm.value.imageList[imgIndex.value].showTime = 1
    } else {
        advertisementForm.value.imageList[imgIndex.value].showTime = number
    }
}

/**
 * 最大只能输入正整数
 */
const handleInputNumber = (value: string, key: string) => {
    const number = Number(value)
    if (isNaN(number) || number < 0) {
        ;(advertisementForm.value as any)[key] = ''
    } else {
        ;(advertisementForm.value as any)[key] = number
    }
}

/**
 * 选择图片
 */
const choice = (index: number) => {
    imgIndex.value = index
}

/**
 * 保存
 */
const submitForm = async () => {
    await openScreenFrom.value!.validate((valid) => {
        const findIndex = advertisementForm.value.imageList.findIndex((v) => !v.showTime)
        imgIndex.value = findIndex === -1 ? 0 : findIndex
        if (valid && chengeFormFlag.value) {
            advertisementForm.value.imageList.forEach((v: any) => {
                v.link = JSON.stringify(v.link)
            })
            upDateOpenAdvertisement(advertisementForm.value).then((res) => {
                if (res.code === 200) {
                    ElMessage.success('保存成功')
                    init()
                    chengeFormFlag.value = false
                } else {
                    ElMessage.error('保存失败')
                    advertisementForm.value.imageList.forEach((v: any) => {
                        v.link = v.link
                            ? JSON.parse(v.link)
                            : {
                                  id: null,
                                  type: null,
                                  name: '',
                                  url: '',
                                  append: '',
                              }
                    })
                }
            })
        }
    })
}
</script>

<style lang="scss" scoped>
.content_container {
    padding: 30px 30px 0 30px;
    background-color: #fff;
    .content {
        height: calc(100vh - 128px);
        background-color: #fff;
        display: flex;
        .cont_left {
            width: 32%;
            display: flex;
            justify-content: center;
            align-items: center;
            .img_box {
                width: 250px;
                height: 542px;
                background-color: rgb(243, 243, 243);
                position: relative;
                .nextBtnLeft,
                .nextBtnRight {
                    position: absolute;
                    top: 48%;
                    color: rgb(134, 144, 156);
                }
                .nextBtnLeft {
                    left: -60px;
                    transform: rotate(90deg);
                }
                .nextBtnRight {
                    right: -60px;
                    transform: rotate(270deg);
                }
            }
        }
        .cont_right {
            flex: 1;
            overflow-y: scroll;
            padding-bottom: 10px;
            .selectMaterialStyle {
                width: 140px;
                height: 303px;
                background-color: #f6f6fa;
                border-radius: 4px;
                display: flex;
                justify-content: center;
                align-items: center;
                margin-right: 20px;
            }
            .mask {
                position: absolute;
                width: 140px;
                height: 303px;
                background-color: rgba(0, 0, 0, 0.5);
                top: 0;
                border-radius: 4px;
                display: flex;
                align-items: center;
                justify-content: center;
                opacity: 0;
                .imgClose {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    position: relative;
                    top: -150px;
                    right: -60%;
                    color: #333;
                    z-index: 99;
                    font-size: 24px;
                    width: 15px;
                    height: 15px;
                }
            }
            .img_box:hover .mask {
                opacity: 1;
            }
        }
    }
}

.foot {
    width: 100%;
    text-align: center;
    line-height: 52px;
    box-shadow: 0 0px 5px 0px #d5d5d5;
    background-color: white;
    margin-top: auto;
}
:deep(.el-carousel__arrow) {
    display: none;
}
:deep(.el-input-group__append) {
    width: 32px;
    height: 32px;
}
.skipWay {
    display: flex;
    align-items: center;
}
:deep(.el-upload-list__item) {
    order: 1;
    width: 140px;
    height: 300px;
    border-radius: 4px;
    border: none;
    margin-right: 30px;
    overflow: inherit;
    img {
        width: 140px;
        height: 300px;
        border-radius: 4px;
    }
}
:deep(.el-upload--picture-card) {
    order: -1;
    margin-right: 30px;
    width: 140px;
    height: 300px;
    border-radius: 4px;
    background-color: #f6f6fa;
    border: none;
}
</style>
