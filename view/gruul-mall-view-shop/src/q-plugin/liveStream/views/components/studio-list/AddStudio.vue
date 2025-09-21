<script setup lang="ts">
import Decimal from 'decimal.js'
import { disabledStartDate, diffMonth, disabledEndDate } from '@/q-plugin/liveStream/views/components/studio-list/formatTime'
import { ElMessage } from 'element-plus'
import { doPostCreateRoom, doGetLiveInfo } from '@/q-plugin/liveStream/apis'
import { uploadFile } from '@/q-plugin/liveStream/apis/upload'
import { useRoute } from 'vue-router'
import type { CreateRoom, ApiCreateRoom } from '@/q-plugin/liveStream/views/types'
// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
import { doGetLiveMemberList } from '@/q-plugin/liveStream/apis'
import { dayjs } from 'element-plus'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'
import DateUtil from '@/utils/date'

onMounted(async () => {
    const res = await doGetLiveMemberList({ size: 999 })
    anchorList.value = res.data.records
    await getLiveInfo()
})
interface Anchor {
    id: Long
    userName: string
    wechatNumber: string
}
const dateUtil = new DateUtil()
const anchorList = ref<Anchor[]>([])
const choosedAnchor = ref<Anchor>({
    id: '',
    userName: '',
    wechatNumber: '',
})

const changeAnchor = (e: Anchor) => {
    formData.value.anchorName = e.userName
    formData.value.anchorWechat = e.wechatNumber
}

const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
let valueKeys = ref('')
const isUploading = ref(false)
let keys = ref('')
const datePickerValue = ref<string[]>([])
const buttonlFn = (key: 'ossCoverImgUrl' | 'ossShareImgUrl' | 'ossFeedsImgUrl', valueKey: 'coverImg' | 'shareImg' | 'feedsImg') => {
    dialogVisible.value = true
    parameterId.value = key
    valueKeys.value = valueKey
    keys.value = key
}
const contentTypeToExtensionMap = {
    'image/jpeg': 'jpg',
    'image/png': 'png',
    'image/gif': 'gif',
    'image/webp': 'webp',
    // 可以添加更多的映射关系
}

const handleChangeTime = (e: Date[] | null) => {
    if (!e || !e[0] || !e[1]) {
        return
    }
    if (e[0].getTime() >= e[1].getTime()) {
        ElMessage.warning('结束时间大于开始时间')
    }
    formData.value.startTime = dateUtil.getYMDHMSs(e[0])
    formData.value.endTime = dateUtil.getYMDHMSs(e[1])
}

function getPossibleExtension(file: File) {
    const contentType = file.type
    return contentTypeToExtensionMap[contentType] || null
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = async (val: any) => {
    isUploading.value = true
    try {
        let file = await getFileFromUrl(val, valueKeys.value)
        console.log('文件', file)
        if (parameterId.value === 'ossCoverImgUrl') formData.value.ossCoverImgUrl = val
        if (parameterId.value === 'ossShareImgUrl') formData.value.ossShareImgUrl = val
        if (parameterId.value === 'ossFeedsImgUrl') formData.value.ossFeedsImgUrl = val
        if (file) {
            // 调起微信
            const res = await uploadFile(`gruul-mall-live/live/broadcast/${getPossibleExtension(file)}/uploadSourceMaterial`, file)
            if (formData.value[keys.value]) {
                formData.value[valueKeys.value] = res
            }
            console.log(formData.value)
        }
    } finally {
        isUploading.value = false
    }
}
const checkedFileLists = async (val: any) => {
    console.log(val)
    isUploading.value = true
    try {
        let file = await getFileFromUrl(val[0], valueKeys.value)
        console.log('文件', file)
        if (parameterId.value === 'ossCoverImgUrl') formData.value.ossCoverImgUrl = val?.shift() || ''
        if (parameterId.value === 'ossShareImgUrl') formData.value.ossShareImgUrl = val?.shift() || ''
        if (parameterId.value === 'ossFeedsImgUrl') formData.value.ossFeedsImgUrl = val?.shift() || ''
        if (file) {
            // 调起微信
            const res = await uploadFile(`gruul-mall-live/live/broadcast/${getPossibleExtension(file)}/uploadSourceMaterial`, file)
            if (formData.value[keys.value]) {
                formData.value[valueKeys.value] = res
            }
            console.log(formData.value)
        }
    } finally {
        isUploading.value = false
    }
}
// 选择素材 d

const ruleFormRef = ref()
const loading = ref(false)
const formData = ref<CreateRoom>({
    name: '',
    anchorName: '',
    anchorWechat: '',
    startTime: 0,
    endTime: 0,
    coverImg: '',
    shareImg: '',
    feedsImg: '',
    isFeedsPublic: 1,
    type: 0, //默认手机直播
    closeLike: 0, //是否关闭点赞 0：开启，1：关闭
    closeGoods: 0, //是否关闭货架
    closeComment: 0, //是否关闭评论
    closeReplay: 1, // 是否关闭回放
    closeShare: 0, //是否关闭分享
    closeKf: 1, //是否关闭客服
    ossCoverImgUrl: '',
    ossShareImgUrl: '',
    ossFeedsImgUrl: '',
})
const timeError = ref('')
const $router = useRouter()
const $route = useRoute()
const isEdit = ref(!!$route.query.id)
const rules: { [k: string]: any[] } = reactive({
    name: [
        { required: true, message: '请输入直播名称', trigger: 'blur' },
        { min: 3, max: 17, message: '直播间名字，最短3个汉字，最长17个汉字', trigger: 'blur' },
    ],
    anchorName: [
        { required: true, message: '请输入主播昵称', trigger: 'blur' },
        { min: 2, max: 15, message: '最短2个汉字，最长15个汉字', trigger: 'blur' },
    ],
    anchorWechat: [{ required: true, message: '请输入主播微信号', trigger: 'blur' }],
    startTime: [{ required: true, message: '请选择开始日期', trigger: ['blur', 'change'] }],
    endTime: [{ required: true, message: '请选择结束日期', trigger: ['blur', 'change'] }],
    ossCoverImgUrl: [
        {
            required: true,
            message: '请添加直播背景图',
            trigger: 'blur',
        },
    ],
    ossShareImgUrl: [
        {
            required: true,
            message: '请添加主播分享图',
            trigger: 'blur',
        },
    ],
    ossFeedsImgUrl: [
        {
            required: true,
            message: '请添加直播封面图',
            trigger: 'blur',
        },
    ],
})

/**
 * 获取直播间信息
 */
async function getLiveInfo() {
    if (!isEdit.value || !$route.query.id) return
    const {
        code,
        data: {
            roomName: name,
            anchorName,
            wechatNumber: anchorWechat,
            startTime,
            endTime,
            coverImg,
            shareImg,
            feedsImg,
            isFeedsPublic,
            type,
            closeLike,
            closeGoods,
            closeComment,
            closeReplay,
            closeShare,
            closeKf,
        },
    }: { code: number; data: ApiCreateRoom } = await doGetLiveInfo($route.query.id)
    choosedAnchor.value = anchorList.value.find((item) => item.userName === anchorName && item.wechatNumber === anchorWechat) || {
        id: '',
        userName: anchorName,
        wechatNumber: anchorWechat,
    }

    if (code !== 200) {
        ElMessage.error('获取直播间信息失败')
        return
    }
    const newData = {
        name,
        anchorName,
        anchorWechat,
        startTime: new Decimal(startTime).mul(1000).toNumber(),
        endTime: new Decimal(endTime).mul(1000).toNumber(),
        coverImg,
        shareImg,
        feedsImg,
        isFeedsPublic,
        type,
        closeLike,
        closeGoods,
        closeComment,
        closeReplay,
        closeShare,
        closeKf,
        ossCoverImgUrl: coverImg,
        ossShareImgUrl: shareImg,
        ossFeedsImgUrl: feedsImg,
    }
    datePickerValue.value = [startTime, endTime]
    formData.value = newData
    return true
}
/**
 * 表单提交
 */
const handleSubmit = async () => {
    if (isEdit.value) {
        $router.push({
            name: 'live',
        })
        return
    }
    const isValidate = await ruleFormRef.value.validate()
    if (!isValidateTime()) return

    if (!isValidate) return
    const time = {
        startTime: 0,
        endTime: 0,
    }
    if (formData.value.startTime && formData.value.endTime) {
        time.startTime = Number(formData.value.startTime) / 1000
        time.endTime = Number(formData.value.endTime) / 1000
    }
    if (!formData.value.coverImg) {
        //  微信方的直播背景图上传失败
        // 直播背景图上传失败
        formData.value.ossCoverImgUrl = ''
        ElMessage.error('微信方的直播背景图上传失败')
        return
    }
    if (!formData.value.shareImg) {
        formData.value.ossShareImgUrl = ''
        //  微信方的直播背景图上传失败
        // 直播背景图上传失败
        ElMessage.error('微信方的主播分享图上传失败')
        return
    }
    if (!formData.value.feedsImg) {
        formData.value.ossFeedsImgUrl = ''
        //  微信方的直播背景图上传失败
        // 直播背景图上传失败
        ElMessage.error('微信方的直播封面图上传失败')
        return
    }
    // 发起请求打开 loading
    console.log('formData.value', formData.value)
    loading.value = true
    const { code, data, msg } = await doPostCreateRoom({ ...formData.value, ...time })
    if ([1001, 100003].includes(code) && msg) {
        ElMessage.error(msg)
        loading.value = false
        return
    }
    if (code !== 200) {
        loading.value = false
        ElMessage.error('创建直播间失败')
        return
    }
    loading.value = false
    resset()
    ElMessage.success('创建直播间成功')
    $router.push({
        name: 'live',
    })
}
// const handleUploadImgSuccess = async (
//     key: 'ossCoverImgUrl' | 'ossShareImgUrl' | 'ossFeedsImgUrl',
//     valueKey: 'coverImg' | 'shareImg' | 'feedsImg',
//     e: File,
// ) => {
//     if (e) {
//         // 调起微信
//         const res = await uploadFile('gruul-mall-live/live/broadcast/uploadSourceMaterial', e)
//         if (formData.value[key]) {
//             formData.value[valueKey] = res
//             console.log('formData.value[valueKey] ', formData.value)
//         }
//     }
// }
const resset = () => {
    ruleFormRef.value.resetFields()
    formData.value.coverImg = ''
    formData.value.feedsImg = ''
    formData.value.shareImg = ''
    timeError.value = ''
    isEdit.value = false
}
/**
 * 时间校验 当前时间10分钟后
 */
function isValidateTime() {
    const startTime = dayjs(formData.value.startTime)
    const endTime = dayjs(formData.value.endTime)
    if (startTime.isAfter(endTime)) {
        timeError.value = '时间输入有误'
        return false
    }
    // 如果开始时间小于当前时间10分钟
    if (startTime.isBefore(dayjs().add(10, 'minute'))) {
        timeError.value = '必须在10分钟后'
        return false
    }
    timeError.value = ''
    return true
    // const time = new Date().getTime()
    // const maxMonths = new Decimal(new Date(diffMonth(6)).getTime())
    // // 十五分钟后 选中的时间大于当前时间往后推15分钟（毫秒）
    // const fifteenMinutesLater_S = startTime.greaterThan(time + 15 * 60 * 1000)
    // const sixMonths_S = startTime.lessThan(maxMonths)
    // // end 大于 45 分钟
    // const fifteenMinutesLater_E = endTime.greaterThan(time + 45 * 60 * 1000)
    // const sixMonths_E = endTime.lessThan(maxMonths)
    // // 时间差大于30分钟
    // const timeDifference30 = endTime.sub(startTime).greaterThan(30 * 60 * 1000)
    // // 时间差小于24小时
    // const timeDifference24H = endTime.sub(startTime).lessThanOrEqualTo(24 * 60 * 60 * 1000)
    // if (fifteenMinutesLater_S && sixMonths_S && fifteenMinutesLater_E && sixMonths_E && timeDifference30 && timeDifference24H) {
    //     timeError.value = ''
    //     return true
    // }
    // // 已知el组件BUG无需在意这个代码
    // // timeError.value = timeError.value === '时间输入有误' ? '时间输入有误 ' : '时间输入有误'
    // return false
}

// url 转 文件
function getFileFromUrl(url, fileName): Promise<File> {
    const type = `image/${url.split('.')[url.split('.').length - 1]}`
    return new Promise((resolve, reject) => {
        var blob = null
        var xhr = new XMLHttpRequest()
        xhr.open('GET', url)
        xhr.setRequestHeader('Accept', type)
        xhr.responseType = 'blob'
        // 加载时处理
        xhr.onload = () => {
            // 获取结果
            blob = xhr.response
            let file = new File([blob], fileName, { type })
            // 返回结果
            resolve(file)
        }
        xhr.onerror = (e) => {
            reject(e)
        }
        // 发送
        xhr.send()
    })
}
</script>

<template>
    <div v-loading="isUploading" class="add_studio">
        <h1 class="title">基本信息</h1>
        <el-form ref="ruleFormRef" :model="formData" :rules="rules" label-width="auto" :inline-message="false" :validate-on-rule-change="false">
            <el-form-item label="直播名称" prop="name">
                <el-input
                    v-model.trim="formData.name"
                    :minlength="3"
                    :disabled="isEdit"
                    style="width: 551px"
                    :maxlength="17"
                    placeholder="请输入直播名称"
                />
                <span class="msg">直播名称不超过17个字</span>
            </el-form-item>
            <!-- <el-form-item label="主播昵称" prop="anchorName">
                <el-input v-model.trim="formData.anchorName" :disabled="isEdit" style="width: 551px" maxlength="15" placeholder="请输入主播昵称" />
            </el-form-item>
            <el-form-item label="主播微信号" prop="anchorWechat">
                <el-input
                    v-model.trim="formData.anchorWechat"
                    :disabled="isEdit"
                    style="width: 551px"
                    maxlength="15"
                    placeholder="请输入主播微信号"
                />
            </el-form-item> -->
            <el-form-item label="选择主播" prop="anchorWechat">
                <el-select
                    v-model="choosedAnchor"
                    :disabled="isEdit"
                    placeholder="请选择主播"
                    style="width: 551px"
                    value-key="id"
                    @change="changeAnchor"
                >
                    <el-option v-for="item in anchorList" :key="item.id" :label="item.userName" :value="item"> </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="直播时间" required>
                <div style="width: 550px">
                    <el-date-picker
                        v-model="datePickerValue"
                        type="datetimerange"
                        range-separator="-"
                        start-placeholder="开始时间"
                        end-placeholder="结束时间"
                        :disabled="isEdit"
                        :disabled-date="disabledStartDate"
                        style="width: 550px"
                        @change="handleChangeTime"
                    />
                </div>
                <span class="msg" style="margin: 5px 0 0 0">
                    开播时间需要在当前时间的15分钟后 并且开始时间不能在6个月后,开播时间和结束时间间隔不得短于30分钟，不得超过24小时
                </span>
            </el-form-item>
            <el-form-item label="直播背景图" prop="ossCoverImgUrl" label-width="100px">
                <!-- <q-upload
                    v-model:src="formData.ossCoverImgUrl"
                    :disabled="isEdit"
                    :width="100"
                    :height="100"
                    :format="{ size: 2, width: 10000, height: 10000 }"
                    @before-update="(e) => handleUploadImgSuccess('ossCoverImgUrl', 'coverImg', e)"
                /> -->
                <el-row style="width: 100%">
                    <div v-if="!formData.ossCoverImgUrl" class="selectMaterialStyle" @click="buttonlFn('ossCoverImgUrl', 'coverImg')">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <img v-else :src="formData.ossCoverImgUrl" alt="" class="selectMaterialStyle" @click="buttonlFn('ossCoverImgUrl', 'coverImg')" />
                </el-row>
                <span class="msg" style="margin: 0"> 建议尺寸:1080像素 * 1920像素，图片大小不得超过2M </span>
            </el-form-item>
            <el-form-item label="主播分享图" prop="ossShareImgUrl" label-width="100px">
                <el-row style="width: 100%">
                    <!-- <q-upload
                        v-model:src="formData.ossShareImgUrl"
                        :disabled="isEdit"
                        :width="100"
                        :height="100"
                        :format="{ size: 1, width: 10000, height: 10000 }"
                        @before-update="(e) => handleUploadImgSuccess('ossShareImgUrl', 'shareImg', e)"
                    /> -->
                    <div v-if="!formData.ossShareImgUrl" class="selectMaterialStyle" @click="buttonlFn('ossShareImgUrl', 'shareImg')">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <img v-else :src="formData.ossShareImgUrl" alt="" class="selectMaterialStyle" @click="buttonlFn('ossShareImgUrl', 'shareImg')" />
                </el-row>
                <span class="msg" style="margin: 0">建议尺寸:800像素 * 640像素，图片大小不得超过1M</span>
            </el-form-item>

            <el-form-item label="直播封面图" prop="ossFeedsImgUrl" label-width="100px">
                <el-row style="width: 100%">
                    <!-- <q-upload
                        v-model:src="formData.ossFeedsImgUrl"
                        :disabled="isEdit"
                        :width="100"
                        :height="100"
                        :format="{ size: 0.1, width: 10000, height: 10000 }"
                        @before-update="(e) => handleUploadImgSuccess('ossFeedsImgUrl', 'feedsImg', e)"
                    /> -->

                    <div v-if="!formData.ossFeedsImgUrl" class="selectMaterialStyle" @click="buttonlFn('ossFeedsImgUrl', 'feedsImg')">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <img v-else :src="formData.ossFeedsImgUrl" alt="" class="selectMaterialStyle" @click="buttonlFn('ossFeedsImgUrl', 'feedsImg')" />
                </el-row>
                <span class="msg" style="margin: 0">建议尺寸:图片建议大小为300 * 300，图片大小不得超过100KB</span>
            </el-form-item>
            <el-form-item label="是否被推荐">
                <el-row style="width: 100%">
                    <el-radio-group v-model="formData.isFeedsPublic" :disabled="isEdit">
                        <el-radio :value="1">开启</el-radio>
                        <el-radio :value="0">关闭</el-radio>
                    </el-radio-group>
                </el-row>
                <span class="msg" style="margin: 0">开启后本场直播将有可能被官方推荐</span>
            </el-form-item>
            <el-form-item label="直播间功能">
                <el-checkbox v-model="formData.closeLike" :disabled="isEdit" :true-value="0" :false-value="1">点赞</el-checkbox>
                <el-checkbox v-model="formData.closeComment" :disabled="isEdit" :true-value="0" :false-value="1">评论</el-checkbox>
                <el-checkbox v-model="formData.closeGoods" :disabled="isEdit" :true-value="0" :false-value="1">商品货架</el-checkbox>
                <el-checkbox v-model="formData.closeReplay" :disabled="isEdit" :true-value="0" :false-value="1">回放</el-checkbox>
                <el-checkbox v-model="formData.closeShare" :disabled="isEdit" :true-value="0" :false-value="1">分享</el-checkbox>
                <el-checkbox v-model="formData.closeKf" :disabled="isEdit" :true-value="0" :false-value="1">客服</el-checkbox>
            </el-form-item>
        </el-form>
        <!-- 选择素材 e -->
        <selectMaterial
            :upload-files="1"
            :dialog-visible="dialogVisible"
            @select-material-fn="selectMaterialFn"
            @cropped-file-change="croppedFileChange"
            @checked-file-lists="checkedFileLists"
        />
        <!-- 选择素材 d -->
    </div>
    <div class="nav-button">
        <el-button round plain @click="$router.back()">返回</el-button>
        <QSafeBtn v-if="!isEdit" type="primary" round :loading="loading" @click="handleSubmit">确认</QSafeBtn>
    </div>
</template>

<style scoped lang="scss">
@include b(add_studio) {
    padding: 0 40px;
    height: calc(100vh - 125px);
    overflow-y: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;
    &::-webkit-scrollbar {
        display: none;
    }
}
@include b(title) {
    font-size: 14px;
    color: #606266;
    font-weight: 700;
    margin-bottom: 20px;
    margin-top: 30px;
}
@include b(msg) {
    font-size: 12px;
    margin-left: 12px;
    color: #c4c4c4;
}
@include b(nav-button) {
    margin-top: auto;
    align-items: center;
    position: sticky;
    bottom: 0;
    padding: 15px 0;
    display: flex;
    justify-content: center;
    box-shadow: 0 0 10px #d5d5d5;
    background-color: #fff;
    z-index: 100;
}
@include b(selectMaterialStyle) {
    width: 100px;
    height: 100px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    @include e(span) {
        color: #999;
        font-size: 20px;
    }
}
</style>
