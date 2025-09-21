<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import QUpload from '@/components/q-upload/q-upload.vue'
import PageManage from '@/components/PageManage.vue'
import {
    doGetMemberBenefit,
    doPostMemberBenefit,
    doPutMemberBenefit,
    doPutMemberBenefitStatus,
    doDelMemberBenefit,
    doGetMemberBenefitDefault,
} from '@/apis/member'
import { type MemberRights, RightsType } from '@/apis/member/types'

type MemberBenefitInfoType = {
    size: number
    current: number
    total: number
    list: MemberRights[]
}

const ruleFormRef = ref()
const formData = reactive<MemberRights>({
    id: 0,
    rightsName: '',
    rightsIcon: '',
    rightsExplain: '',
    rightsType: 'USER_DEFINED',
    rightsSwitch: false,
})
const rules = reactive({
    rightsName: [{ required: true, message: '请输入权益名称', trigger: 'blur' }],
    rightsIcon: [{ required: true, message: '请上传权益图标', trigger: 'blur' }],
    rightsExplain: [{ required: true, message: '请输入权益说明', trigger: 'blur' }],
})
const rulesPopupShow = ref(false)
const memberBenefitInfo = reactive<MemberBenefitInfoType>({
    size: 10,
    current: 1,
    total: 0,
    list: [],
})
const isNew = ref(true)

initMemberBenefit()

const handleDelClick = (row: MemberRights) => {
    ElMessageBox.confirm('确定需要删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const { code, msg } = await doDelMemberBenefit([row.id])
        if (code === 200) {
            ElMessage.success('删除成功')
            initMemberBenefit()
        } else {
            ElMessage.error(msg || '删除失败')
        }
    })
}
const handleAdd = () => {
    rulesPopupShow.value = true
    isNew.value = true
}
const handleClose = () => {
    formData.rightsExplain = ''
    formData.rightsIcon = ''
    formData.rightsName = ''
    formData.rightsType = 'USER_DEFINED'
    formData.id = 0
}
/**
 * 分页器
 */
const handleSizeChange = (value: number) => {
    memberBenefitInfo.current = 1
    memberBenefitInfo.size = value
    initMemberBenefit()
}
const handleCurrentChange = (value: number) => {
    memberBenefitInfo.current = value
    initMemberBenefit()
}
/**
 * 设置会员规则
 */
const handleSetRule = (row: MemberRights) => {
    const { id, rightsExplain, rightsName, rightsIcon, rightsType } = row
    rulesPopupShow.value = true
    isNew.value = false
    formData.rightsExplain = rightsExplain
    formData.rightsIcon = rightsIcon
    formData.rightsName = rightsName
    formData.id = id
    formData.rightsType = rightsType
}
/**
 * 提交表单
 */
const handleSubmit = () => {
    ruleFormRef.value.validate(async (valid: boolean) => {
        if (valid) {
            console.log('submit')
            const { code, data, msg } = isNew.value ? await doPostMemberBenefit(formData) : await doPutMemberBenefit(formData)
            if (code === 200) {
                const tips = isNew.value ? '新增成功' : '设置成功'
                rulesPopupShow.value = false
                ElMessage.success(tips)
                initMemberBenefit()
            } else {
                ElMessage.error(msg || '设置失败')
            }
        }
    })
}
/**
 * 开启关闭会员权益
 * @param {boolean} switchType false为开启 true为关闭
 */
const handleSwitchBenefit = async (switchType: boolean, id: string) => {
    const { code, msg } = await doPutMemberBenefitStatus(!switchType, id)
    if (code === 200) {
        const tips = switchType ? '关闭成功' : '开启成功'
        ElMessage.success(tips)
        initMemberBenefit()
    } else {
        ElMessage.warning(msg || '操作失败')
    }
}
const handleReset = async () => {
    const { code, data } = await doGetMemberBenefitDefault(formData.rightsType)
    if (code === 200) {
        const { rightsExplain, rightsName, rightsIcon, rightsType } = data
        formData.rightsExplain = rightsExplain
        formData.rightsIcon = rightsIcon
        formData.rightsName = rightsName
        formData.rightsType = rightsType
    } else {
        ElMessage.error('获取数据失败')
    }
}
async function initMemberBenefit() {
    const { current, size } = memberBenefitInfo
    const { code, data } = await doGetMemberBenefit({ current, size })
    if (code === 200) {
        memberBenefitInfo.list = data.records
        memberBenefitInfo.total = data.total
    }
}
function convertStatus(type: keyof typeof RightsType) {
    const enumType = {
        GOODS_DISCOUNT: '商品抵扣',
        INTEGRAL_MULTIPLE: '积分加倍',
        LOGISTICS_DISCOUNT: '物流优惠',
        PRIORITY_SHIPMENTS: '优先发货',
        QUICKNESS_AFS: '极速售后',
        EXCLUSIVE_SERVICE: '专属客服',
        USER_DEFINED: '自定义权益',
        GROWTH_VALUE_MULTIPLE: '成长值加倍',
    }
    return enumType[type]
}
</script>
<template>
    <div class="handle_container">
        <el-button round type="primary" @click="handleAdd">添加自定义权益</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="memberBenefitInfo.list"
            :header-row-style="{ fontSize: '14px', color: '#909399' }"
            :header-cell-style="{ background: '#F7F8FA', fontWeight: 700, color: '#000' }"
            :cell-style="{ fontSize: '14px', color: '#333333' }"
        >
            <template #empty><ElTableEmpty /></template>
            <el-table-column label="权益名称">
                <template #default="{ row }">
                    <div style="display: flex; align-items: center; height: 43px">
                        <img :src="row.rightsIcon" alt="" style="width: 40px; height: 40px; border-radius: 4px" />
                        <p style="margin-left: 10px">{{ row.rightsName }}</p>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="权益类型" width="220">
                <template #default="{ row }">
                    <div class="level">{{ convertStatus(row.rightsType) }}</div>
                </template>
            </el-table-column>
            <el-table-column label="权益说明">
                <template #default="{ row }">
                    <div class="pricing">{{ row.rightsExplain }}</div>
                </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="180" align="right">
                <template #default="{ row }: { row: any, $index: number }">
                    <el-link
                        style="margin-right: 12px; color: #f54319"
                        :underline="false"
                        type="primary"
                        size="small"
                        @click="handleSwitchBenefit(row.rightsSwitch, row.id)"
                    >
                        {{ row.rightsSwitch ? '关闭' : '开启' }}
                    </el-link>
                    <el-link :underline="false" type="primary" size="small" @click="handleSetRule(row)"> 设置规则 </el-link>
                    <el-link
                        v-if="row.rightsType === 'USER_DEFINED'"
                        style="margin-left: 12px"
                        :underline="false"
                        type="primary"
                        size="small"
                        @click="handleDelClick(row)"
                    >
                        删除
                    </el-link>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <PageManage
        :page-num="memberBenefitInfo.current"
        :page-size="memberBenefitInfo.size"
        :total="memberBenefitInfo.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="rulesPopupShow" width="40%" title="设置规则" align-center @close="handleClose">
        <el-form ref="ruleFormRef" :model="formData" :rules="rules" label-width="110px">
            <el-form-item label="权益类型">
                <div>{{ convertStatus(formData.rightsType) }}</div>
            </el-form-item>
            <el-form-item label="权益图标" prop="rightsIcon">
                <el-row style="width: 100%" align="bottom">
                    <!-- :format="{ size: 2, width: 100, height: 100 }" -->
                    <q-upload v-model:src="formData.rightsIcon" :width="100" :height="100" />
                    <el-button style="margin: 0 20px" round @click="handleReset">恢复默认</el-button>
                </el-row>
                <div style="font-size: 12px; color: #838383">
                    如不设置权益图片，则为默认官方图片<span style="color: #f12f22">（最佳尺寸100*100，图片大小不可超过159KB）</span>
                </div>
            </el-form-item>
            <el-form-item label="权益名称" porp="name" prop="rightsName">
                <el-input v-model="formData.rightsName" style="width: 85%" maxlength="10"></el-input>
            </el-form-item>
            <el-form-item label="权益说明" porp="instructions" prop="rightsExplain">
                <el-input v-model="formData.rightsExplain" style="width: 85%" maxlength="200"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="rulesPopupShow = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit"> 确认 </el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss">
@include b(level) {
    font-size: 14px;
    color: #999999;
}
@include b(pricing) {
    font-size: 14px;
    color: #666;
    @include utils-ellipsis(2);
}
@include b(interests) {
    width: 140px;
    font-size: 12px;
    color: #333333;
    @include utils-ellipsis(2);
}
</style>
