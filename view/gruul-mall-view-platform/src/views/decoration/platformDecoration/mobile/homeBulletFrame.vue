<template>
    <div class="content_container">
        <div class="content">
            <div class="cont_left">
                <div class="img_box">
                    <div v-if="homeBulletFrameForm.imageInfo.url" class="mask">
                        <div class="cont">
                            <img :src="homeBulletFrameForm.imageInfo.url" alt="" />
                            <QIcon name="icon-guanbi1" class="imgClose" size="10px" />
                        </div>
                    </div>
                </div>
            </div>
            <div class="cont_right">
                <el-form
                    ref="openScreenFrom"
                    :model="homeBulletFrameForm"
                    label-width="90px"
                    label-position="right"
                    :rules="rules"
                    style="max-width: 1150px"
                >
                    <el-form-item label="是否开启" prop="showFlag">
                        <el-radio-group v-model="homeBulletFrameForm.showFlag">
                            <el-radio :value="true">开启</el-radio>
                            <el-radio :value="false">关闭</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <div v-if="homeBulletFrameForm.showFlag">
                        <el-form-item label="展示时间" prop="displayTime" style="width: 400px">
                            <el-date-picker
                                v-model="homeBulletFrameForm.displayTime"
                                type="datetimerange"
                                range-separator="-"
                                start-placeholder="开始时间"
                                end-placeholder="结束时间"
                                format="YYYY/MM/DD HH:mm"
                                value-format="YYYY-MM-DD HH:mm"
                                :disabled-date="disabledDate"
                                @change="handleDateTimeChange"
                            />
                        </el-form-item>
                        <el-form-item label="展示时长" prop="showTime">
                            <el-input v-model="homeBulletFrameForm.imageInfo.showTime" placeholder="0-60秒" style="width: 310px" @input="handleInput">
                                <template #append>秒</template>
                            </el-input>
                        </el-form-item>
                        <el-form-item label="图片" prop="url">
                            <template #label>
                                图片
                                <el-tooltip
                                    class="box-item"
                                    effect="dark"
                                    content="建议：请上传600*800等比尺寸，格式为jpg/jpeg/gif/png/bmp的图片，建议大小不超过1M"
                                    placement="bottom"
                                >
                                    <QIcon name="icon-wenhao" color="#ccc" size="18px" style="margin-left: 3px" />
                                </el-tooltip>
                            </template>
                            <div style="display: flex; align-items: end">
                                <VueDraggableNext v-model="fileList" style="display: flex" @sort="handleSortfileList">
                                    <div v-if="fileList.length !== 1" class="selectMaterialStyle" @click="buttonlFnHost(fileList.length)">
                                        <el-icon style="font-size: 30px; font-weight: bold; color: #d8d8d8"><Plus /></el-icon>
                                    </div>
                                    <div
                                        v-for="(item, index) in fileList"
                                        :key="index"
                                        style="position: relative; margin-right: 10px"
                                        class="img_box"
                                    >
                                        <img :src="item" alt="" class="selectMaterialStyle" />
                                        <div class="mask">
                                            <el-button
                                                type="primary"
                                                style="background-color: #fff; color: #555cfd; border: none"
                                                @click.prevent.stop="buttonlFnHost(index)"
                                                >替换</el-button
                                            >
                                        </div>
                                    </div>
                                </VueDraggableNext>
                            </div>
                        </el-form-item>
                        <el-form-item label="链接" label-width="40px" style="margin-left: 85px">
                            <link-select
                                v-model:link="list[0].link"
                                style="width: 340px"
                                :noProTab="true"
                                :customizedProTab="$decorationStore.getEndpointType === 'WECHAT_MINI_APP'"
                                :isH5="false"
                                @update-value="updateValue"
                            />
                        </el-form-item>
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
        :upload-files="1"
        @select-material-fn="selectMaterialFnHost"
        @cropped-file-change="croppedFileChangeHost"
        @checked-file-lists="checkedFileListsHost"
    />
</template>
<script setup lang="ts">
import { HomeBulletFrameFormType, LinkSelectItem } from './types'
import type { FormRules, FormInstance } from 'element-plus'
import selectMaterial from '@/views/material/selectMaterial.vue'
import type { Ref } from 'vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { Plus } from '@element-plus/icons-vue'
import { useDecorationStore } from '@/store/modules/decoration/index'
import { QueryByEndPoint, saveOrUpdate } from '@/apis/decoration'
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
const fileList = ref<any>([])
const openScreenFrom = ref<FormInstance>()
const homeBulletFrameForm = ref<HomeBulletFrameFormType>({
    showFlag: true, //是否展示
    startTime: startTimes, //展示开始时间
    endTime: endTimes, //展示结束时间
    endPoint: $decorationStore.getEndpointType, //终端
    displayTime: [startTimes, endTimes],
    imageInfo: {
        url: '', //图片URL
        showTime: '', //展示时间
        link: {
            id: '',
            type: 0,
            name: '',
            url: '',
            append: '',
        },
    },
})
const list = ref<{ link: LinkSelectItem }[]>([
    {
        link: {
            id: '',
            type: 0,
            name: '',
            url: '',
            append: '',
        },
    },
])
const chengeFormFlag = ref(false)
const validateImage = (rule: any, value: any, callback: any) => {
    if (!homeBulletFrameForm.value.imageInfo.url) {
        return callback(new Error('请上传图片'))
    }
    return callback()
}
const validateShowTime = (rule: any, value: any, callback: any) => {
    if (!homeBulletFrameForm.value.imageInfo.showTime) {
        return callback(new Error('请输入展示时长'))
    }
    return callback()
}

const rules = reactive<FormRules<HomeBulletFrameFormType>>({
    showFlag: [{ required: true, message: '', trigger: 'change' }],
    displayTime: [{ required: true, message: '请选择展示时间', trigger: 'change' }],
    url: [{ required: true, validator: validateImage, trigger: 'change' }],
    showTime: [{ required: true, validator: validateShowTime, trigger: 'change' }],
})

watch(
    () => homeBulletFrameForm.value,
    (newVal, oldVal) => {
        if (JSON.stringify(newVal) === JSON.stringify(oldVal)) {
            chengeFormFlag.value = true
        }
    },
    { deep: true },
)

const updateValue = () => {
    chengeFormFlag.value = true
}

/**
 * 根据终端查询广告信息
 */
const init = async () => {
    const { code, data } = await QueryByEndPoint(homeBulletFrameForm.value.endPoint)
    fileList.value = data.imageInfo.url.split(',')
    if (code === 200) {
        list.value[0].link = data.imageInfo.link && JSON.parse(data.imageInfo.link)
        homeBulletFrameForm.value = {
            ...data,
            displayTime: [data.startTime, data.endTime],
            imageInfo: {
                ...data.imageInfo,
            },
        }
    }
}
init()

/**
 * 数组转字符串
 * @param {Ref<string[]>} arr
 */
function arrToString(arr: Ref<any[]>) {
    return arr.value.join('')
}

/**
 * 时间选择范围
 */
const disabledDate = (time: Date) => {
    return time.getTime() < Date.now() - 8.64e7
}

/**
 * 修改时间
 * @param value
 */
const handleDateTimeChange = (value: any) => {
    if (!value) {
        homeBulletFrameForm.value.displayTime = []
    } else if (value && value.length === 2) {
        const start = new Date(value[0]) as any
        const end = new Date(value[1]) as any
        if (start.getTime() <= new Date().getTime()) {
            ElMessage.error('开始时间必须大于当前时间')
            homeBulletFrameForm.value.displayTime = []
        } else if (end - start <= 60 * 60 * 1000) {
            ElMessage.error('结束时间必须大于开始时间1小时')
            homeBulletFrameForm.value.displayTime = []
        } else {
            const [start, end] = value
            homeBulletFrameForm.value.startTime = start
            homeBulletFrameForm.value.endTime = end
        }
    }
}

const handleSortfileList = () => {
    homeBulletFrameForm.value.imageInfo.url = arrToString(fileList)
}

// 选择素材
const dialogVisiblesHost = ref(false)
const selectMaterialFnHost = (val: boolean) => {
    if (fileList.value.length > 5) return
    dialogVisiblesHost.value = val
}
const indexs = ref<number>(-1)
let num = 0
const buttonlFnHost = (val: number) => {
    dialogVisiblesHost.value = true
    indexs.value = val
    num++
}

//  裁剪后返回的单个素材
const croppedFileChangeHost = (val: string) => {
    if (num === 0) fileList.value = [val]
    else fileList.value[indexs.value as any] = val
}

// 选中素材返回的素材合集
const checkedFileListsHost = (val: string[]) => {
    if (num === 0) {
        fileList.value = val
    } else if (Array.isArray(val)) {
        val.forEach((item) => {
            fileList.value[indexs.value++] = item
        })
    } else {
        fileList.value[indexs.value] = val
    }
    homeBulletFrameForm.value.imageInfo.url = arrToString(fileList)
}

/**
 * 最大只能输入60
 */
const handleInput = (value: string) => {
    const number = Number(value)
    if (isNaN(number) || number < 0 || number > 60) {
        homeBulletFrameForm.value.imageInfo.showTime = ''
    } else {
        homeBulletFrameForm.value.imageInfo.showTime = number.toString()
    }
}

/**
 * 保存
 */
const submitForm = async () => {
    await openScreenFrom.value!.validate((valid) => {
        if (valid && (chengeFormFlag.value || JSON.stringify(list.value[0].link) !== homeBulletFrameForm.value.imageInfo.link)) {
            if (list.value[0].link.name === '系统连接' && list.value[0].link.url === 'https:// ') {
                list.value[0].link.type = ''
            } else {
                list.value[0].link.type = 6
            }
            homeBulletFrameForm.value.imageInfo.link = JSON.stringify(list.value[0].link)
            saveOrUpdate(homeBulletFrameForm.value).then((res) => {
                if (res.code === 200) {
                    ElMessage.success('保存成功')
                    init()
                    chengeFormFlag.value = false
                }
            })
        }
    })
}
</script>

<style lang="scss" scoped>
.content_container {
    background-color: #fff;
    height: calc(100vh - 98px);
    display: flex;
    .content {
        flex: 1;
        padding: 50px 30px 0 30px;
        display: flex;
        .cont_left {
            width: 32%;
            display: flex;
            justify-content: center;
            .img_box {
                width: 250px;
                height: 542px;
                background-image: url('@/assets/image/advertisementBg.png');
                position: relative;
                background-size: 100%; /* 设置背景图片的宽度与盒子宽度相同 */
                .mask {
                    width: 100%;
                    height: 100%;
                    position: absolute;
                    top: 0;
                    background-color: rgba(0, 0, 0, 0.5);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    .cont {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        img {
                            width: 182px;
                            height: 242px;
                            border-radius: 10px;
                        }
                        .imgClose {
                            width: 20px;
                            height: 20px;
                            border-radius: 50%;
                            background-color: #ffffff;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            color: #333;
                            margin-top: 20px;
                            cursor: pointer;
                        }
                    }
                }
            }
        }
        .cont_right {
            flex: 1;
            overflow-y: scroll;
            .selectMaterialStyle {
                width: 245.28px;
                height: 303px;
                background-color: #f6f6fa;
                border-radius: 4px;
                display: flex;
                justify-content: center;
                align-items: center;
                margin-right: 10px;
                cursor: pointer;
            }
            .img_box:hover .mask {
                opacity: 1;
            }
            .mask {
                position: absolute;
                width: 245.28px;
                height: 303px;
                background-color: rgba(0, 0, 0, 0.5);
                top: 0;
                border-radius: 4px;
                display: flex;
                align-items: center;
                justify-content: center;
                opacity: 0;
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
:deep(.el-upload--picture-card:hover) {
    border: none;
}
:deep(.link-tips) {
    margin-top: 10px;
}
</style>
