<script setup lang="ts">
import VipDetailsTable from '@/views/baseVip/components/vip-details-table.vue'
import LabelViewDialog from './components/label-view-dialog.vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { doGetMemberDetail, doGetPaymentInfo, doPostMemberInfo } from '@/apis/member'
import type { ApiMemberInfoType } from './types'
import type { FormInstance, FormRules } from 'element-plus'

const $route = useRoute()
const $router = useRouter()
const { divTenThousand } = useConvert()
const memberInfo = ref<ApiMemberInfoType>({
    balance: '0',
    createTime: '',
    dealTotalMoney: '0',
    gender: 'UNKNOWN',
    growthValue: '',
    id: '',
    userHeadPortrait: '',
    userId: '',
    userNickname: '',
    userPhone: '',
    userTagVOList: [],
})
const showSetTagDialog = ref(false)
const paymentInfo = ref({
    rechargeNumber: '0',
    totalMoney: '0',
})
const formRef = ref<FormInstance>()
const editDialog = ref(false)
const rules = ref<FormRules>({
    userNickname: [
        { required: true, message: '请输入用户名称', trigger: 'blur' },
        { min: 1, max: 11, message: '用户名称最多为11位', trigger: 'blur' },
    ],
    gender: [{ required: true, message: '请选择用户性别', trigger: 'blur' }],
})
const currentUserTagIds = computed(() => {
    if (memberInfo.value.userTagVOList.length) {
        return memberInfo.value.userTagVOList.map((item) => item.tagId)
    } else {
        return []
    }
})

initMemberInfo()
initPaymentInfo()

const handleEditMemberInfo = () => {
    if (!formRef.value) return
    formRef.value.validate(async (valid) => {
        if (valid) {
            const { gender, userNickname, userHeadPortrait, userId } = memberInfo.value
            const { code, msg } = await doPostMemberInfo({
                nickname: userNickname,
                gender,
                avatar: userHeadPortrait,
                userId,
            })
            if (code === 200) {
                ElMessage.success('修改成功')
                editDialog.value = false
            } else {
                ElMessage.error(msg ? msg : '修改失败')
            }
        }
    })
}
const handleEditDialog = () => {
    editDialog.value = true
}
const handleShowTagDialog = () => {
    showSetTagDialog.value = true
}
const handleReset = () => {
    initMemberInfo()
}

async function initMemberInfo() {
    if ($route.query.userId) {
        const { code, data, msg } = (await doGetMemberDetail(String($route.query.userId))) as any
        if (code === 200) {
            memberInfo.value = data
        } else {
            ElMessage.error(msg ? msg : '获取会员信息失败')
        }
    } else {
        ElMessage.error('获取会员信息失败')
    }
}

async function initPaymentInfo() {
    if ($route.query.userId) {
        const { code, data, msg } = (await doGetPaymentInfo(String($route.query.userId))) as any
        if (code === 200) {
            paymentInfo.value = data
        } else {
            ElMessage.error(msg ? msg : '获取余额计费信息失败')
        }
    } else {
        ElMessage.error('获取余额计费信息失败')
    }
}

const handleNavToSendCoupon = () => {
    $router.push({
        name: 'memberCouponBaseInfo',
        query: { ids: JSON.stringify([memberInfo.value.userId]) },
    })
}
</script>

<template>
    <div class="vip-details">
        <!-- <div class="vip-details--title">用户信息</div> -->
        <div style="width: 100%; text-align: right">
            <el-button type="primary" @click="handleNavToSendCoupon">赠送优惠券</el-button>
            <el-button type="primary" @click="handleEditDialog">修改用户信息</el-button>
            <el-button type="primary" @click="handleShowTagDialog">设置标签</el-button>
        </div>
        <div class="vip-user">
            <div>
                <el-image style="width: 100px; height: 100px; border-radius: 50%" :src="memberInfo.userHeadPortrait" fit="cover" />
            </div>
            <div class="vip-user__right">
                <el-row>
                    <el-col :span="24">
                        <span class="vip-user__right--nickname">{{ memberInfo.userNickname }}</span>
                    </el-col>
                </el-row>
                <el-row style="width: 100%">
                    <el-col :span="8">
                        <span>性别：{{ memberInfo.gender === 'MALE' ? '男' : memberInfo.gender === 'FEMALE' ? '女' : '未知' }}</span>
                        <span class="ml-8">{{ memberInfo.birthday }}</span>
                    </el-col>
                    <el-col :span="8"> 手机号：{{ memberInfo.userPhone }}</el-col>
                    <el-col :span="8"> 生日：{{ memberInfo.birthday }} </el-col>
                </el-row>
                <el-row style="width: 100%">
                    <el-col :span="8">
                        会员等级：{{ memberInfo.member?.memberType === 'PAID_MEMBER' ? 'SVIP' : 'VIP' }}
                        {{ memberInfo.member?.rankCode || 0 }}
                    </el-col>
                    <el-col :span="8"> 成长值： {{ memberInfo.growthValue }}</el-col>
                    <el-col :span="8"> 会员类型：{{ memberInfo.member?.memberType === 'PAID_MEMBER' ? '付费会员' : '免费会员' }} </el-col>
                </el-row>
                <el-row style="width: 100%">
                    <el-col :span="8"> 注册时间：{{ memberInfo.createTime }}</el-col>
                    <el-col :span="8" class="padding-l"
                        >消费总金额：{{ memberInfo.dealTotalMoney && divTenThousand(memberInfo.dealTotalMoney) }}
                    </el-col>
                    <el-col :span="8">积分：{{ memberInfo.integralTotal || 0 }}</el-col>
                </el-row>
            </div>
        </div>
        <!-- <div class="vip-details--title">资产(消费)信息</div> -->
        <div class="stored-value">
            <!-- <div class="stored-value__title">储值账户</div> -->
            <el-row>
                <el-col :span="8">累计储值：{{ paymentInfo.totalMoney && divTenThousand(paymentInfo.totalMoney) }}</el-col>
                <el-col :span="8" class="padding-l">储值充值次数：{{ paymentInfo.rechargeNumber }}</el-col>
                <el-col :span="8" style="transform: translateX(-12px)">储值余额：{{ divTenThousand(memberInfo.balance) }}</el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    标签：
                    <span v-if="memberInfo.userTagVOList.length">
                        {{ memberInfo.userTagVOList.map((item) => item.tagName).join('、') }}
                        <!-- <span v-for="item in memberInfo.userTagVOList" :key="item.tagId" style="margin-right: 10px">{{ item.tagName }}</span> -->
                    </span>
                    <span v-else>暂无标签</span>
                </el-col>
            </el-row>
            <!-- <el-row>
                <el-col :span="4">可用余额：{{ divTenThousand(memberInfo.balance) }}</el-col>
                <el-col :span="4">充值次数：{{ paymentInfo.rechargeNumber }}</el-col>
                <el-col :span="4">累计余额：{{ paymentInfo.totalMoney && divTenThousand(paymentInfo.totalMoney) }}</el-col>
                <el-col :span="4">消费金额：{{ memberInfo.dealTotalMoney && divTenThousand(memberInfo.dealTotalMoney) }}</el-col>
            </el-row> -->
        </div>
        <!-- <div class="stored-value">
            <div class="stored-value__title">积分账户</div>
            <el-row>
                <el-col :span="4">可用积分：78.99</el-col>
                <el-col :span="4">充值次数：5</el-col>
                <el-col :span="4">累计余额：785.86</el-col>
                <el-col :span="4">消费金额：785.86</el-col>
            </el-row>
        </div> -->
        <!-- <div class="vip-details--title mb30">其他信息</div> -->
        <label-view-dialog
            v-model:label-view="showSetTagDialog"
            :user-ids="[memberInfo.userId]"
            :current-user-tag-ids="currentUserTagIds"
            @reset="handleReset"
        />
        <vip-details-table :user-id="memberInfo.userId"></vip-details-table>
        <!-- 修改用户信息弹窗 -->
        <el-dialog v-model="editDialog" title="修改用户信息">
            <el-form ref="formRef" :model="memberInfo" :rules="rules">
                <el-form-item label="昵称" prop="userNickname">
                    <el-input v-model="memberInfo.userNickname" maxlength="11" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                    <el-select v-model="memberInfo.gender" placeholder="请选择性别">
                        <el-option label="女" value="FEMALE" />
                        <el-option label="男" value="MALE" />
                        <el-option label="未知" value="UNKNOWN" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editDialog = false">取消</el-button>
                <el-button type="primary" @click="handleEditMemberInfo">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<style scoped lang="scss">
@include b(vip-details) {
    padding: 20px 30px 0px 30px;
    overflow-y: scroll;
    @include m(title) {
        font-size: 18px;
        font-weight: bold;
        color: #515151;
    }
}

@include b(vip-user) {
    padding: 20px;
    padding-bottom: 10px;
    @include flex;
    justify-content: flex-start;
    @include e(right) {
        flex: 1;
        margin-left: 3%;
        height: 100px;
        @include flex;
        flex-direction: column;
        align-items: flex-start;
        justify-content: space-between;
        @include m(nickname) {
            font-size: 1.4em;
            font-weight: bold;
        }
    }
}

@include b(stored-value) {
    margin-left: 145px;
    padding-bottom: 30px;
    font-family: Microsoft YaHei, Microsoft YaHei-Normal;
    color: #333333;
    line-height: 22px;
    @include e(title) {
        font-size: 14px;
        font-weight: bold;
        color: #333333;
        margin-bottom: 12px;
    }
}
.mb30 {
    margin-bottom: 30px;
}

.ml-8 {
    margin-left: 8px;
}

.el-row {
    margin-bottom: 8px;
}
</style>
