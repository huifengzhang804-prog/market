<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { isEqual } from 'lodash-es'
import { doSaveOSS, doGetOSS, getOSSList, changeOSSType, deleteOSSList } from '@/apis/setting'
import { enumServerFormCn, TencentConfig } from '@/views/set/components/fileSet'

type ServeType = 'TENCENT' | 'QUIDWAY' | 'QINIUO' | 'ALIYUN' | 'MINIO' | 'LOCAL'
const dialogFormVisible = ref(false)
const tableList = ref<TencentConfig[]>([])
const title = ref('')
const formRef = ref()
const enumServerForm = reactive([
    // 腾讯云
    {
        type: 'TENCENT',
        // 说明
        description: '',
        // 域名
        qcloudDomain: '',
        // 前缀
        qcloudPrefix: '',
        // appid
        qcloudAppId: '',
        // SecretId"
        qcloudSecretId: '',
        // SecretKey
        qcloudSecretKey: '',
        // BucketName"
        qcloudBucketName: '',
        // 腾讯云COS所属地区
        qcloudRegion: '',
    },
    // 华为云
    {
        type: 'QUIDWAY',
        // 说明
        description: '',
        // 华为appid
        quidwayAccessKeyId: '',
        // SecretId
        quidwayAccessKeySecret: '',
        // endPoint
        quidwayEndpoint: '',
        // bucketname
        obsBucketName: '',
        // 域名
        quidwayDomain: '',
        // 前缀
        quidwayPrefix: '',
    },
    //   七牛
    {
        type: 'QINIUO',
        // 说明
        description: '',
        // 域名
        qiniuDomain: '',
        // 前缀
        qiniuPrefix: '',
        // AccessKey
        qiniuAccessKey: '',
        // SecretKey
        qiniuSecretKey: '',
        // 桶
        qiniuBucketName: '',
    },
    //阿里云
    {
        type: 'ALIYUN',
        // 说明
        description: '',
        // 域名
        aliyunDomain: '',
        // 前缀
        aliyunPrefix: '',
        // endPoint
        aliyunEndPoint: '',
        // AccessKeyId
        aliyunAccessKeyId: '',
        // AccessKeySecret
        aliyunAccessKeySecret: '',
        // BucketName"
        aliyunBucketName: '',
    },
    //minio
    {
        type: 'MINIO', //类型
        // 说明
        description: '',
        minioAccessKey: '', //key
        minioSecretKey: '', //秘钥
        minioEndPoint: '', //端点
        minioBucketName: '', //bucket
        minioDomain: '', //域名
    },
    //local
    {
        type: 'LOCAL',
        // 说明
        description: '',
        localDomain: '', //域名
        localStoragePath: '', //存储路径
    },
])

const currentRadio = ref<keyof typeof widthMap>('TENCENT')
const currentRadioList = ref<any[]>([
    {
        name: 'TENCENT',
        title: '腾讯',
    },
    {
        name: 'QUIDWAY',
        title: '华为',
    },
    {
        name: 'QINIUO',
        title: '七牛',
    },
    {
        name: 'ALIYUN',
        title: '阿里',
    },
    {
        name: 'MINIO',
        title: 'MINO',
    },
    {
        name: 'LOCAL',
        title: '本地',
    },
])

const getServiceProvider = (name: string) => {
    const radio = currentRadioList.value.find((item) => item.name === name)
    return radio ? radio.title : ''
}

const currentForm = computed(() => {
    return enumServerForm.filter((item) => item.type === currentRadio.value)[0]
})

const using = ref('')

onMounted(() => {
    getOSSTableList()
})

const handleOperate = (type: keyof typeof widthMap, val: string) => {
    dialogFormVisible.value = true
    title.value = val
    currentRadio.value = type
    initForm(type)
}

const getOSSTableList = async () => {
    const { data, code, msg } = await getOSSList()
    if (code !== 200) {
        ElMessage.error(msg)
        return
    } else {
        tableList.value = data
    }
}

const validFormat = () => {
    const current = { ...toRaw(currentForm.value) }
    Reflect.deleteProperty(current, 'type')
    if (isEqual(current, originData) && using.value === currentRadio.value) {
        ElMessage.error('内容未更改，请更改后提交')
        return false
    }
    console.log(currentForm.value, 'currentForm.value')
    if (Object.values(currentForm.value).includes('')) {
        ElMessage.error('请填写完成表单')
        return false
    }
    return true
}
const submitHandle = async () => {
    if (!validFormat()) return
    const { code, msg } = await doSaveOSS(currentForm.value)
    if (code === 200) {
        ElMessage.success(title.value + '成功')
        initForm(currentRadio.value)
        dialogFormVisible.value = false
        getOSSTableList()
        formRef.value.resetFields()
    } else {
        ElMessage.error(msg || title.value + '失败')
    }
}
let originData: any
async function initForm(val: any) {
    const { code, data, success } = await doGetOSS(val)
    if (code === 200 && success) {
        if (data?.using) using.value = data?.using
        switch (currentRadio.value as ServeType) {
            case 'TENCENT':
                enumServerForm[0] = { ...enumServerForm[0], ...data }
                break
            case 'QUIDWAY':
                enumServerForm[1] = { ...enumServerForm[1], ...data }
                break
            case 'QINIUO':
                enumServerForm[2] = { ...enumServerForm[2], ...data }
                break
            case 'ALIYUN':
                enumServerForm[3] = { ...enumServerForm[3], ...data }
                break
            case 'MINIO':
                enumServerForm[4] = { ...enumServerForm[4], ...data }
                break
            case 'LOCAL':
                enumServerForm[5] = { ...enumServerForm[5], ...data }
                break
            default:
                break
        }
        originData = { ...data }
    }
}

/**
 * 映射每个表单label的宽度
 */
const widthMap = {
    TENCENT: '240px',
    QUIDWAY: '210px',
    QINIUO: '180px',
    ALIYUN: '230px',
    MINIO: '160px',
    LOCAL: '170px',
} as const

/**
 * 启用禁用状态
 */
const changeOSS = async (type: string) => {
    const { code, msg } = await changeOSSType(type)
    if (code !== 200) {
        ElMessage.success(msg)
    }
    getOSSTableList()
}

// 删除
const handleDelete = (type: string) => {
    ElMessageBox.confirm('删除后无法恢复，确定删除吗？', '删除确认', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
        center: true,
    })
        .then(async () => {
            const { code, msg } = await deleteOSSList(type)
            if (code !== 200) {
                ElMessage.error(msg)
            } else {
                ElMessage.success('删除成功')
                getOSSTableList()
            }
        })
        .catch(() => {})
}
const typeList = (tableList: TencentConfig[]) => {
    return currentRadioList.value.filter((item) => !tableList.some((excludeType: TencentConfig) => excludeType.type === item.name))
}
</script>

<template>
    <div class="handle_container">
        <el-button :disabled="!typeList(tableList)[0]?.name" type="primary" @click="handleOperate(typeList(tableList)[0]?.name as any, '新增')"
            >新增</el-button
        >
        <span style="color: #999; margin-left: 10px">只需配置1个服务商即可，处于开启状态的只能有1个；</span>
    </div>
    <div class="table_container">
        <el-table
            :data="tableList"
            :header-row-style="{ fontSize: '14px' }"
            :header-cell-style="{ background: '#F7F8FA', color: '#333333', height: '48px' }"
            :cell-style="{ fontSize: '14px', color: '#333' }"
        >
            <template #empty><ElTableEmpty /></template>
            <el-table-column label="服务商" width="120">
                <template #default="{ row }">{{ getServiceProvider(row.type) }}</template>
            </el-table-column>
            <el-table-column label="说明">
                <template #default="{ row }">{{ row.description }}</template>
            </el-table-column>
            <el-table-column label="创建时间" width="180">
                <template #default="{ row }">{{ row.createTime }}</template>
            </el-table-column>
            <el-table-column label="更新时间" width="180">
                <template #default="{ row }">{{ row.updateTime }}</template>
            </el-table-column>
            <el-table-column label="状态" width="140">
                <template #default="{ row }">
                    <el-switch v-model="row.using" :disabled="row.using" @change="changeOSS(row.type)" />
                </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="right">
                <template #default="{ row }">
                    <el-button type="primary" link @click="handleOperate(row.type, '编辑')">编辑</el-button>
                    <el-button v-if="!row.using" type="danger" style="margin-left: 10px" link @click="handleDelete(row.type)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <el-dialog v-model="dialogFormVisible" :title="title + 'OSS配置'" width="792">
        <div style="padding: 16px">
            <!-- <el-tabs v-model="currentRadio" tab-position="left" class="demo-tabs" @tab-click="initForm"> -->
            <!-- <el-tab-pane v-for="item in currentRadioList" :key="item.name" :label="item.title" :name="item.name"> -->
            <el-form ref="formRef" label-width="250px" :model="currentForm">
                <el-form-item label="服务商" prop="type" :label-width="widthMap[currentRadio as keyof typeof widthMap]">
                    <el-select v-model="currentRadio" :disabled="title === '编辑'" placeholder="" @change="initForm(currentRadio)">
                        <el-option
                            v-for="item in title === '新增' ? typeList(tableList) : currentRadioList"
                            :key="item.name"
                            :label="item.title"
                            :value="item.name"
                        >
                        </el-option>
                    </el-select>
                </el-form-item>
                <div v-for="(val, key, index) in currentForm" :key="index" :data-val="val" class="form-container">
                    <el-form-item
                        v-if="key !== 'type'"
                        :prop="key"
                        :label-width="widthMap[currentRadio as keyof typeof widthMap]"
                        :rules="[{ required: true, message: '', trigger: 'blur' }]"
                    >
                        <template #label>
                            {{ enumServerFormCn[key as keyof typeof enumServerFormCn].name }}
                            <span v-if="enumServerFormCn[key as keyof typeof enumServerFormCn]?.info">
                                ({{ enumServerFormCn[key as keyof typeof enumServerFormCn].info }})
                            </span>
                        </template>
                        <el-input
                            v-model="currentForm[key]"
                            class="cusInput"
                            :maxlength="enumServerFormCn[key as keyof typeof enumServerFormCn].name === '说明' ? 30 : 200"
                            :placeholder="enumServerFormCn[key as keyof typeof enumServerFormCn].placeholder"
                        />
                    </el-form-item>
                </div>
            </el-form>
            <!-- </el-tab-pane>
            </el-tabs> -->
        </div>
        <template #footer>
            <div class="preservation">
                <el-button @click="dialogFormVisible = false">取消</el-button>
                <el-button type="primary" @click="submitHandle">保存</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<style lang="scss">
@import './messageBox.scss';
</style>
<style lang="scss" scoped>
@include b(cusInput) {
    height: 30px;
}
@include b(btn-icon) {
    position: absolute;
    inset: 0;
}
@include b(handle_container) {
    display: flex;
    justify-content: start;
    align-items: center;
}
@include b(text-color) {
    position: relative;
    color: #fff;
    background-color: transparent;
    z-index: 3;
}

@include b(active) {
    background-color: #409eff;
    color: #fff;
    @include b(el-icon) {
        position: absolute;
        top: 0;
        right: 0;
    }
}

@include b(el-radio-group) {
    :deep(.el-radio-button__inner) {
        --el-radio-button-checked-text-color: --el-text-color-regular;
        --el-radio-button-checked-bg-color: #fff;
    }
}

@include b(el-form-item) {
    :deep(.el-form-item__label) {
        justify-content: flex-end;
        display: flex;
        align-items: center;
    }
}

@include b(label-icon) {
    margin: 0 5px;
    font-size: 18px;
    cursor: pointer;
}

.group {
    margin-bottom: 20px;
}
@include b(custom-tabs-label) {
    width: 45px;
    text-align: left;
}
@include b(preservation) {
    text-align: center;
}
</style>
