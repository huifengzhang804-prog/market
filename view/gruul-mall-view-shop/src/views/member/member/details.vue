<script lang="ts" setup>
import { useRoute } from 'vue-router'
import { doGetShopMemberDetails, doGetUserTags, doPostUserTag } from '@/apis/member/index'
import router from '@/router'
import type { Ref } from 'vue'
import { TagsInterface, compareTags, getInitialLabelsForMember } from './helper'
import { ElMessage } from 'element-plus'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import SetLabels from './components/SetLabels.vue'

const activeName = ref('TransationList')
const route = useRoute()
const memberInfo = ref({
    userHeadPortrait: '',
    userNickname: '',
    gender: '',
    birthday: '',
    userPhone: '',
    memberType: '',
    rankCode: '',
    shopConsumption: '',
    registerTime: '',
    tags: [],
})
const getMemberDetails = async () => {
    const memberId = route.query.id
    const { success, data } = await doGetShopMemberDetails(memberId as string)
    if (success) memberInfo.value = data as typeof memberInfo.value
}

const showSetLabelDialog = ref(false)

const initialLabels: Ref<TagsInterface[]> = ref([])

const shopTags = ref([])
const tabList = [
    {
        label: '订单明细',
        name: 'TransationList',
    },
]
const defineAsyncComponentReactive = reactive({
    TransationList: defineAsyncComponent(() => import('./components/details/TransationList.vue')),
})

const handleGiveAwayCoupon = () => {
    const memberId = route.query.id
    router.push({ path: '/member/list/coupon', query: { userIds: JSON.stringify([memberId]) } })
}
const handleCloseSetLabelDialog = () => {
    initialLabels.value = []
    showSetLabelDialog.value = false
}

const getShopTags = async () => {
    const shopStore = useShopInfoStore()
    const { success, data } = await doGetUserTags(shopStore.shopInfo.id)
    if (success) shopTags.value = data || []
}

const handleConfirmSetLabel = async () => {
    const memberId = route.query.id
    const result = compareTags(shopTags.value, initialLabels.value)
    const { success, msg } = await doPostUserTag({ ...result, userIdList: [memberId] })
    if (success) {
        ElMessage.success({ message: msg || '设置标签成功' })
        handleCloseSetLabelDialog()
        getShopTags()
        getMemberDetails()
    } else {
        ElMessage.error({ message: msg || '设置标签失败' })
    }
}

const handleSetTags = () => {
    initialLabels.value = getInitialLabelsForMember(memberInfo.value?.tags, shopTags.value)
    showSetLabelDialog.value = true
}
onMounted(() => {
    getShopTags()
    getMemberDetails()
})
</script>
<template>
    <div class="vip-details">
        <div class="vip-details__container">
            <!-- <div class="vip-details__container--title">基础信息</div> -->
            <div class="vip-details__container--btn">
                <el-button type="primary" @click="handleGiveAwayCoupon">送优惠券</el-button>
                <el-button type="primary" @click="handleSetTags">设置标签</el-button>
            </div>
            <el-row :gutter="8">
                <el-col :span="3">
                    <el-image :src="memberInfo.userHeadPortrait" style="width: 100px; height: 100px; border-radius: 10px" fix="fill" />
                </el-col>
                <el-col :span="16">
                    <div class="vip-details__container--nickname">{{ memberInfo.userNickname }}</div>
                    <el-row :gutter="8">
                        <el-col :span="8">
                            <span class="vip-details__container--gender">
                                性别：{{ memberInfo.gender === 'MALE' ? '男' : memberInfo.gender === 'MALE' ? 'FEMALE' : '未知' }}
                            </span>
                            <span class="vip-details__container--birthday">{{ memberInfo.birthday }}</span>
                        </el-col>
                        <el-col :span="8">手机号：{{ memberInfo.userPhone }}</el-col>
                        <el-col :span="8">会员类型：{{ memberInfo.memberType === 'PAID_MEMBER' ? '付费会员' : '免费会员' }}</el-col>
                    </el-row>
                    <el-row :gutter="8">
                        <el-col :span="8">会员等级：{{ memberInfo.memberType === 'PAID_MEMBER' ? 'SVIP' : 'VIP' }} {{ memberInfo.rankCode }}</el-col>
                        <el-col :span="8">本店消费：{{ (memberInfo.shopConsumption as unknown as number) / 10000 }}</el-col>
                        <el-col :span="8">注册时间：{{ memberInfo.registerTime }}</el-col>
                    </el-row>
                    <el-row :gutter="8">
                        <el-col :span="24"
                            >标签：
                            <span v-if="memberInfo.tags.length">{{ memberInfo?.tags?.map((item: any) => item.tagName)?.join('、') }}</span>
                            <span v-else>暂无标签</span>
                        </el-col>
                    </el-row>
                </el-col>
            </el-row>
        </div>
        <div class="vip-details__container">
            <!-- <div class="vip-details__container--title">交易明细</div> -->
            <el-tabs v-model="activeName">
                <el-tab-pane v-for="item in tabList" :key="item.label" :label="item.label" :name="item.name" />
            </el-tabs>
            <component :is="defineAsyncComponentReactive[activeName as keyof typeof defineAsyncComponentReactive]" />
        </div>
    </div>
    <el-dialog v-model="showSetLabelDialog" title="设置标签" destroy-on-close>
        <SetLabels v-model:initial-labels="initialLabels" />
        <template #footer>
            <el-button @click="handleCloseSetLabelDialog">取 消</el-button>
            <el-button type="primary" @click="handleConfirmSetLabel">确 定</el-button>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(vip-details) {
    @include e(container) {
        padding: 15px;

        @include m(title) {
            font-size: 18px;
            font-weight: bold;
            color: #515151;
            padding: 10px 0;
        }

        @include m(nickname) {
            font-size: 18px;
            font-weight: bold;
            color: #515151;
            padding-bottom: 10px;
        }

        @include m(btn) {
            margin-bottom: 10px;
            text-align: right;
        }
    }
}

:deep(.el-row) {
    padding-bottom: 8px;
}
</style>
