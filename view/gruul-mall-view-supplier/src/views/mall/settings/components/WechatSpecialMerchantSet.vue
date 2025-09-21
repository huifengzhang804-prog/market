<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { applicationStatus, createApplication, applyBound, confirmApi } from '@/apis/mall/WechatSpecialMerchantSet'
import { ElMessage, ElMessageBox } from 'element-plus'
// 个体工商户
import SubjecInformation from '@/views/mall/settings/components/componentss/SubjecInformation.vue'
import ManagementInformation from '@/views/mall/settings/components/componentss/managementInformation.vue'
import IndustryQualification from '@/views/mall/settings/components/componentss/IndustryQualification.vue'
import settlementAccount from '@/views/mall/settings/components/componentss/settlementAccount.vue'
import SuperAdmin from '@/views/mall/settings/components/componentss/SuperAdmin.vue'
// 企业
import QSubjecInformation from '@/views/mall/settings/components/qiyecomponents/SubjecInformation.vue'
import QManagementInformation from '@/views/mall/settings/components/qiyecomponents/managementInformation.vue'
import QIndustryQualification from '@/views/mall/settings/components/qiyecomponents/IndustryQualification.vue'
import QsettlementAccount from '@/views/mall/settings/components/qiyecomponents/settlementAccount.vue'
import QSuperAdmin from '@/views/mall/settings/components/qiyecomponents/SuperAdmin.vue'

import showGuide from '@/views/mall/settings/components/guide.vue'
const apply = ref('1')
const textarea2 = ref('')
const input = ref('')

const subject = ref<boolean>(false)
// 主体信息
const SubjectInformationFn = () => {
    subject.value = true
}
const subjectClose = (val: boolean) => {
    subject.value = val
}

// 经营信息
const manage = ref<boolean>(false)
const ManagementInformationFn = () => {
    manage.value = true
}
const manageClose = (val: boolean) => {
    manage.value = val
}
// 行业资质
const qualification = ref<boolean>(false)
const qualificationInformationFn = () => {
    qualification.value = true
}
const qualificationClose = (val: boolean) => {
    qualification.value = val
}
// 账户类型
const account = ref<boolean>(false)
const accountFn = () => {
    account.value = true
}
const accountClose = (val: boolean) => {
    account.value = val
}
// 超级管理员
const administrator = ref<boolean>(false)
const AdministratorFn = () => {
    administrator.value = true
}
const administratorClose = (val: boolean) => {
    administrator.value = val
}

// 创建特约商户申请单 createApplication
const data = reactive({
    // businessCode: '',
    contactInfo: {
        contactType: '',
        contactName: '',
        contactIdDocType: null,
        contactIdNumber: null,
        contactIdDocCopy: null,
        contactIdDocCopyBack: null,
        contactPeriodBegin: null,
        contactPeriodEnd: null,
        businessAuthorizationLetter: null,
        mobilePhone: '',
        contactEmail: '',
    },
    subjectInfo: {
        subjectType: '',
        financeInstitution: true,
        businessLicenseInfo: {
            legalPerson: '',
            licenseAddress: '',
            licenseCopy: '',
            licenseNumber: '',
            merchantName: '',
            periodBegin: '',
            periodEnd: '',
        },
        identityInfo: {
            // authorizeLetterCopy: '', // 法定代表人说明函
            // cardPeriodBegin: '',
            cardPeriodEnd: '',
            idCardCopy: '',
            idCardNational: '',
            idCardName: '',
            idCardNumber: '',
            idCardAddress: '',
            cardPeriodBegin: '',
            // cardPeriodEnd: '',
        },
        // microBizInfo: false,
    },

    businessInfo: {
        salesInfo: {
            webInfo: {
                webAuthorisation: '',
            },
            // salesScenesType:'',
        },
        merchantShortname: '',
        servicePhone: '',
    },

    settlementInfo: {
        settlementId: '',
        qualificationType: '',
        qualifications: [''],
        // activitiesId: '',
        activitiesRate: '',
    },
    bankAccountInfo: {
        bankAccountType: '',
        accountName: '',
        accountBank: '',
        bankAddressCode: '', // 所在地编码
        bankBranchId: '', // 开户行的id
        accountNumber: '',
    },
})

const settlement = (val: any) => {
    console.log(val)

    data.bankAccountInfo.bankAccountType = val.bankAccountType
    data.bankAccountInfo.accountName = val.accountName
    data.bankAccountInfo.accountBank = val.accountBank
    data.bankAccountInfo.bankAddressCode = val.bankAddressCode
    data.bankAccountInfo.bankBranchId = val.bankBranchId
    data.bankAccountInfo.accountNumber = val.accountNumber
}
const industry = (val: any) => {
    console.log(val)

    data.settlementInfo.settlementId = val.activitiesId
    data.settlementInfo.qualificationType = val.qualificationType
    data.settlementInfo.qualifications[0] = val.qualifications.mediaId
    // data.settlementInfo.activitiesId = val.activitiesId
    data.settlementInfo.activitiesRate = val.activitiesRate
}
const management = (val: any) => {
    console.log(val)

    data.businessInfo.merchantShortname = val.merchantShortname || ''
    data.businessInfo.servicePhone = val.servicePhone || ''
    data.businessInfo.salesInfo.webInfo.webAuthorisation = val.webAuthorisation.mediaId || val.webAuthorisation.value.mediaId
}
const subjec = (val: any) => {
    data.subjectInfo.businessLicenseInfo.legalPerson = val.legalPerson
    data.subjectInfo.businessLicenseInfo.licenseAddress = val.licenseAddress
    data.subjectInfo.businessLicenseInfo.licenseCopy = val.licenseCopy.mediaId || val.licenseCopy.value.mediaId
    data.subjectInfo.businessLicenseInfo.licenseNumber = val.licenseNumber
    data.subjectInfo.businessLicenseInfo.merchantName = val.merchantName
    data.subjectInfo.businessLicenseInfo.periodBegin = val.contactPeriodBegin
    data.subjectInfo.businessLicenseInfo.periodEnd = val.contactPeriodEnd

    console.log('--------------------------------')

    data.subjectInfo.identityInfo.idCardCopy = val.idCardCopy.mediaId || val.idCardCopy.value.mediaId
    data.subjectInfo.identityInfo.idCardNational = val.idCardNational.mediaId || val.idCardNational.value.mediaId

    data.subjectInfo.identityInfo.idCardName = val.idCardName
    data.subjectInfo.identityInfo.idCardNumber = val.idCardNumber
    data.subjectInfo.identityInfo.idCardAddress = val.idCardAddress
    data.subjectInfo.identityInfo.cardPeriodBegin = val.cardPeriodBegin
    data.subjectInfo.identityInfo.cardPeriodEnd = val.cardPeriodEnd

    // data.subjectInfo.identityInfo = val.contactType
}
const superF = (val: any) => {
    console.log(val)
    data.contactInfo.contactType = val.contactType
    data.contactInfo.contactName = val.contactName
    data.contactInfo.contactIdDocType = val.contactIdDocType
    data.contactInfo.contactIdNumber = val.contactIdNumber
    data.contactInfo.contactIdDocCopy = val.contactIdDocCopy.mediaId || ''
    data.contactInfo.contactIdDocCopyBack = val.contactIdDocCopyBack.mediaId || ''
    data.contactInfo.contactPeriodBegin = val.contactPeriodBegin
    data.contactInfo.contactPeriodEnd = val.contactPeriodEnd
    data.contactInfo.businessAuthorizationLetter = val.businessAuthorizationLetter.mediaId || ''
    data.contactInfo.mobilePhone = val.mobilePhone
    data.contactInfo.contactEmail = val.contactEmail
}
// 企业的
const settlementQ = (val: any) => {
    data.bankAccountInfo.bankAccountType = val.bankAccountType
    data.bankAccountInfo.accountName = val.accountName
    data.bankAccountInfo.accountBank = val.accountBank
    data.bankAccountInfo.bankAddressCode = val.bankAddressCode
    data.bankAccountInfo.bankBranchId = val.bankBranchId
    data.bankAccountInfo.accountNumber = val.accountNumber
}
const industryQ = (val: any) => {
    console.log(val.settlementId)

    data.settlementInfo.settlementId = val.settlementInfo
    data.settlementInfo.qualificationType = val.qualificationType
    data.settlementInfo.qualifications = val.qualifications
    // data.settlementInfo.activitiesId = val.activitiesId
    data.settlementInfo.activitiesRate = val.activitiesRate
}
const managementQ = (val: any) => {
    console.log(val)

    data.businessInfo.merchantShortname = val.merchantShortname
    data.businessInfo.servicePhone = val.servicePhone

    // data.businessInfo.salesInfo.salesScenesType = 'SALES_SCENES_WEB'
    data.businessInfo.salesInfo.webInfo.webAuthorisation = val.webAuthorisation
}
const subjecQ = (val: any) => {
    data.subjectInfo.businessLicenseInfo = val.businessLicenseInfo
    // data.subjectInfo.organizationInfo =
    // data.subjectInfo.certificateLetterCopy = val.licenseNumber
    // data.subjectInfo.certificateLetterCopy = val.businessLicenseInfo.mediaId
    // data.subjectInfo.identityInfo = val.contactType
}
const superQ = (val: any) => {
    console.log(val.contactIdDocCopy)
    data.contactInfo.contactType = val.contactType
    data.contactInfo.contactName = val.contactName
    data.contactInfo.contactIdDocType = val.contactIdDocType
    data.contactInfo.contactIdNumber = val.contactIdNumber
    data.contactInfo.contactIdDocCopy = val.contactIdDocCopy.mediaId || ''
    data.contactInfo.contactIdDocCopyBack = val.contactIdDocCopyBack.mediaId || ''
    data.contactInfo.contactPeriodBegin = val.contactPeriodBegin
    data.contactInfo.contactPeriodEnd = val.contactPeriodEnd
    data.contactInfo.businessAuthorizationLetter = val.businessAuthorizationLetter
    data.contactInfo.mobilePhone = val.mobilePhone
    data.contactInfo.contactEmail = val.contactEmail
}

// 页面进来判断状态 applicationStatus
const queryResp = ref<any>({
    businessCode: '',
    signUrl1: '',
    applymentState: '',
})

// 判断是否是个体 企业
const switchover = ref<boolean>(true)
const subjectType = ref<string>('SUBJECT_TYPE_INDIVIDUAL')
const switchover1Fn = () => {
    switchover.value = true
    console.log(subjectType.value)
    data.subjectInfo.subjectType = subjectType.value
}

const switchover2Fn = () => {
    switchover.value = false
    // data.subjectInfo.subjectType = 'SUBJECT_TYPE_ENTERPRISE'
    data.subjectInfo.subjectType = subjectType.value
}

// 创建申请单
const create = async () => {
    const res1 = await applicationStatus()
    // data.businessCode = res1.data.businessCode
    data.subjectInfo.subjectType = subjectType.value
    const res = await createApplication(data)
    console.log(res)
    // console.log(data.businessCode)
}

// 已申请
const form = reactive({
    applicationStatus: '',
})
const rules = {
    applicationStatus: [{ required: true, message: '请填写申请单号', trigger: 'blur' }],
}
const applyShow = ref<boolean>(true)
const signUrl1 = ref<string>('')

const stae = ref('')
const isShow1 = ref<boolean>(false)
const isShow2 = ref<boolean>(false)
const isShow3 = ref<boolean>(false)
const isIf = ref<boolean>(false)
const isisIf = ref<boolean>(false)
const isisisIf = ref<boolean>(false)
const initial = ref<boolean>(false)
const queryStatus = async () => {
    const { data } = await applicationStatus()
    queryResp.value = data.queryResp
    queryResp.value.businessCode = data.queryResp.businessCode
    queryResp.value.signUrl = data.queryResp.signUrl
    queryResp.value.applymentState = data.queryResp.applymentState

    const state = ref([
        { sta: 'APPLYMENT_STATE_EDITTING', value: '编辑中' },
        { sta: 'APPLYMENT_STATE_AUDITING', value: '审核中' },
        { sta: 'APPLYMENT_STATE_REJECTED', value: '已驳回' },
        { sta: 'APPLYMENT_STATE_TO_BE_CONFIRMED', value: '已确认' },
        { sta: 'APPLYMENT_STATE_TO_BE_SIGNED', value: '已签约' },
        { sta: 'APPLYMENT_STATE_SIGNING', value: '待签约' },
        { sta: 'APPLYMENT_STATE_FINISHED', value: '已完成' },
        { sta: 'APPLYMENT_STATE_CANCELED', value: '已取消' },
    ])
    for (let i = 0; i < state.value.length; i++) {
        if (queryResp.value.applymentState === state.value[i].sta) {
            stae.value = state.value[i].value
        }
    }

    if (stae.value === '编辑中' || stae.value === '已驳回' || stae.value === '已取消') {
        isShow1.value = true
    } else if (stae.value === '已完成') {
        isShow3.value = true
    } else {
        isShow2.value = true
    }
    if (!queryResp.value.applymentState) {
        initial.value = false
    } else {
        initial.value = true
    }
}
// const jianyi = ref<boolean>(true)

const Bound = ref<boolean>(false)
const staeBound = ref('')
const applyBoundInt = async (applymentId: string) => {
    const { data } = await applyBound(applymentId)
    signUrl1.value = data.signUrl
    const state = ref([
        { sta: 'APPLYMENT_STATE_EDITTING', value: '编辑中' },
        { sta: 'APPLYMENT_STATE_AUDITING', value: '审核中' },
        { sta: 'APPLYMENT_STATE_REJECTED', value: '已驳回' },
        { sta: 'APPLYMENT_STATE_TO_BE_CONFIRMED', value: '已确认' },
        { sta: 'APPLYMENT_STATE_TO_BE_SIGNED', value: '已签约' },
        { sta: 'APPLYMENT_STATE_SIGNING', value: '待签约' },
        { sta: 'APPLYMENT_STATE_FINISHED', value: '已完成' },
        { sta: 'APPLYMENT_STATE_CANCELED', value: '已取消' },
    ])
    for (let i = 0; i < state.value.length; i++) {
        if (data.applymentState === state.value[i].sta) {
            staeBound.value = state.value[i].value
        }
    }
    if (data.applymentState === state.value[6].sta) {
        Bound.value = false
    } else {
        Bound.value = true
    }
}
// { sta: 'APPLYMENT_STATE_FINISHED', value: '已完成' },
const applyBoundFn = () => {
    applyBoundInt(form.applicationStatus)
}
const show = ref<boolean>(false)
const affirm = ref<boolean>(false)
const guideFn = () => {
    show.value = true
}
const showClose = (val: boolean) => {
    show.value = val
}
// 确认按钮
const confirmFn = async () => {
    const { data } = await confirmApi()
    ElMessage('请按操作指引操作后再确认')
}
// 重新填写
const fillAgain = () => {
    ElMessageBox.confirm('请确认是否重填申请单?', '请确认', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
    })
        .then(() => {
            ElMessage({
                type: 'success',
                message: '操作成功',
            })
            isShow1.value = false
            isShow2.value = false
            isShow3.value = false
            initial.value = false
            // if (applyShow.value === false) {
            //     applyShow.value = true
            // }
            applyShow.value = false

            apply.value = '1'
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作失败',
            })
        })
}
const primaryFn = () => {
    create()
}

onMounted(() => {
    queryStatus()
})
</script>

<template>
    <div style="padding: 0 20px">
        <!-- 判断状态显示页面 -->
        <div v-if="initial">
            <!-- 未申请的三种情况 -->
            <div v-if="isShow1">
                <el-form>
                    <div>
                        <el-form-item label="申请单号">
                            <el-text class="mx-1">{{ queryResp.businessCode }}</el-text>
                            <el-button style="margin-left: 20px" @click="isIf = true">查询申请状态</el-button>
                        </el-form-item>
                    </div>
                    <div v-if="isIf">
                        <el-form-item label="申请状态">
                            <el-text class="mx-1">{{ stae }}</el-text>
                        </el-form-item>
                        <el-form-item label="签约链接">
                            <el-text class="mx-1"><img :src="queryResp.signUrl" alt="" /></el-text>
                        </el-form-item>
                        <el-form-item label="操作建议">
                            <el-text class="mx-1">
                                <el-button @click="fillAgain">重填签约申请单</el-button>进入编辑页面，系统将历史数据获取过来</el-text
                            >
                        </el-form-item>
                    </div>
                </el-form>
            </div>
            <div v-else-if="isShow2">
                <el-form>
                    <h2 style="margin-left: 50px">签约状态</h2>
                    <el-form-item label="申请单号">
                        <el-text class="mx-1">{{ queryResp.businessCode }}</el-text>
                        <el-button style="margin-left: 20px" @click="isisIf = true">点击查询</el-button>
                    </el-form-item>
                    <div v-if="isisIf">
                        <p style="text-align: right"><el-button @click="fillAgain">重填签约申请单</el-button></p>
                        <el-form-item label="申请状态">
                            <el-text class="mx-1">{{ stae }}</el-text>
                        </el-form-item>
                        <el-form-item label="签约链接">
                            <el-text class="mx-1"><img :src="queryResp.signUrl" alt="" /></el-text>
                        </el-form-item>
                        <el-form-item label="操作建议">
                            <el-text class="mx-1">请使用微信扫码，根据引导完成后续步骤</el-text>
                        </el-form-item>
                    </div>
                </el-form>
            </div>
            <div v-else-if="isShow3">
                <el-form label-width="130px">
                    <h2 style="margin: 50px 300px">签约状态</h2>
                    <el-form-item label="申请单号">
                        <el-text class="mx-1">{{ queryResp.businessCode }}</el-text>
                        <el-button style="margin-left: 20px" @click="isisisIf = true">点击查询</el-button>
                    </el-form-item>
                </el-form>
                <div v-if="isisisIf">
                    <p style="text-align: right"><el-button @click="fillAgain">重填签约申请单</el-button></p>

                    <el-form-item label="申请状态">
                        <el-text class="mx-1">{{ stae }}</el-text>
                    </el-form-item>
                    <!-- <el-form-item label="商户号">
                        <span>{{}}</span>
                    </el-form-item> -->
                    <el-form-item label="操作建议">
                        <span
                            >操作建议： 后续请按【操作指引】完成对应设置，设置完成后再【确认提交】
                            <a style="color: #3b8eea" @click="guideFn">操作指引</a>
                        </span>
                        <showGuide v-model:show="show" @show-close="showClose"></showGuide>
                        <el-checkbox v-model="affirm" label="我已确认本店铺微信特约商户(与本平台)签约信息无误，并可用于微信分账使用" size="large" />
                        <p style="text-align: center; margin-right: 300px; margin-top: 30px">
                            <el-button type="primary" :disabled="affirm === false ? true : false" @click="confirmFn">确认</el-button>
                        </p>
                    </el-form-item>
                </div>
            </div>
        </div>
        <div v-else>
            <!-- 个体工商户 -->
            <div v-if="switchover">
                <SubjecInformation v-model:subject="subject" @subject-close="subjectClose" @subjec="subjec"> </SubjecInformation>
                <ManagementInformation v-model:manage="manage" @manage-close="manageClose" @management="management"> </ManagementInformation>
                <IndustryQualification
                    v-model:qualification="qualification"
                    @qualification-close="qualificationClose"
                    @industry="industry"
                ></IndustryQualification>
                <settlementAccount v-model:account="account" @account-close="accountClose" @settlement="settlement"> </settlementAccount>
                <SuperAdmin v-model:administrator="administrator" @administratorClose="administratorClose" @superF="superF"></SuperAdmin>
            </div>
            <!-- 企业 -->
            <div v-else>
                <QSubjecInformation v-model:subject="subject" @subject-close="subjectClose" @subjecQ="subjecQ"> </QSubjecInformation>
                <QManagementInformation v-model:manage="manage" @manage-close="manageClose" @managementQ="managementQ"> </QManagementInformation>
                <QIndustryQualification
                    v-model:qualification="qualification"
                    @qualification-close="qualificationClose"
                    @industryQ="industryQ"
                ></QIndustryQualification>
                <QsettlementAccount v-model:account="account" @account-close="accountClose" @settlementQ="settlementQ"> </QsettlementAccount>
                <QSuperAdmin v-model:administrator="administrator" @administratorClose="administratorClose" @superQ="superQ"></QSuperAdmin>
            </div>
            <!-- <div class="mb-2 flex items-center text-sm" style="line-height: 40px">
                是否已申请特约商户:
                <el-radio-group v-model="apply" class="ml-4">
                    <label style="margin: 0 40px"><el-radio label="1" size="large"
                            @click="applyShow = true">未申请</el-radio></label>
                    <label><el-radio label="2" size="large" @click="applyShow = false">已申请</el-radio></label>
                </el-radio-group>
            </div> -->
            <div v-if="applyShow">
                <el-row class="mar">
                    <el-col :span="3">
                        <div class="grid-content ep-bg-purple" style="line-height: 40px">主体类型：</div>
                    </el-col>
                    <el-col :span="10">
                        <div class="grid-content ep-bg-purple-light">
                            <el-radio-group v-model="subjectType" class="ml-4">
                                <label style="margin: 0 50px">
                                    <el-radio label="SUBJECT_TYPE_INDIVIDUAL" size="large" @click="switchover1Fn">个体工商户</el-radio>
                                </label>
                                <label>
                                    <el-radio label="SUBJECT_TYPE_ENTERPRISE" size="large" @click="switchover2Fn">企业</el-radio>
                                </label>
                            </el-radio-group>
                        </div>
                    </el-col>
                </el-row>
                <div class="mar">
                    <p>主体信息：</p>
                    <p>
                        请填写商家的营业执照/登记证书、经营者/法人的证件等信息<el-button link class="flr">
                            <span @click="SubjectInformationFn">去填写 <i> > </i></span>
                        </el-button>
                    </p>
                </div>
                <div class="mar">
                    <p>经营信息：</p>
                    <p>
                        请填写商家的经营业务信息、售卖商品/提供服务场景信息<el-button link class="flr">
                            <span @click="ManagementInformationFn">去填写 <i> > </i></span>
                        </el-button>
                    </p>
                </div>
                <div class="mar">
                    <p>行业资质：</p>
                    <p>
                        请填写商家所属行业、特殊资质等信息<el-button link class="flr">
                            <span @click="qualificationInformationFn">去填写 <i> > </i></span>
                        </el-button>
                    </p>
                </div>
                <div class="mar">
                    <p>结算账户：</p>
                    <p>
                        请填写商家提现收矿的银行账户信息<el-button link class="flr">
                            <span @click="accountFn">去填写 <i> > </i></span>
                        </el-button>
                    </p>
                </div>
                <div class="mar">
                    <p>超级管理员：</p>
                    <p>
                        请填写商家的超级管理员信息，
                        超级管理员需在开户后进行签约，并接收日常重要管理信息和进行资金操作，请确定其为商户法定代表人或负责人<el-button
                            link
                            class="flr"
                        >
                            <span @click="AdministratorFn">去填写 <i> > </i></span>
                        </el-button>
                    </p>
                </div>
                <div style="margin: 30px auto; text-align: center">
                    <el-button type="primary" @click="primaryFn">提交</el-button>
                </div>
            </div>
            <div v-else>
                <!-- 已申请 -->
                <el-form :model="form" label-width="130px" style="padding: 0 20px" :rules="rules">
                    <el-form-item label="申请单号" prop="applicationStatus">
                        <el-input v-model="form.applicationStatus" placeholder="请输入" style="width: 300px" /><el-button @click="applyBoundFn"
                            >点击查询</el-button
                        >
                    </el-form-item>
                    <!-- <p style="text-align: right"><el-button @click="fillAgain">重填签约申请单</el-button></p> -->

                    <el-form-item label="申请状态">
                        <el-text class="mx-1">{{ staeBound }}</el-text>
                    </el-form-item>
                    <div v-if="Bound">
                        <el-form-item label="签约链接">
                            <el-text class="mx-1">
                                <img :src="signUrl1" alt="" />
                            </el-text>
                        </el-form-item>
                        <el-form-item label="操作建议">
                            <el-text class="mx-1">请使用微信扫码，根据引导完成后续步骤</el-text>
                        </el-form-item>
                    </div>
                    <div v-else>
                        <!-- <el-form-item label="商户号">
                            <span>{{}}</span>
                        </el-form-item> -->
                        <el-form-item label="操作建议">
                            <span
                                >操作建议： 后续请按【操作指引】完成对应设置，设置完成后再【确认提交】
                                <a style="color: #3b8eea" @click="guideFn">操作指引</a>
                            </span>
                            <showGuide v-model:show="show" @show-close="showClose"></showGuide>
                            <el-checkbox
                                v-model="affirm"
                                label="我已确认本店铺微信特约商户(与本平台)签约信息无误，并可用于微信分账使用"
                                size="large"
                            />
                            <p style="text-align: center; margin-right: 300px; margin-top: 30px">
                                <el-button type="primary" :disabled="affirm === false ? true : false" @click="confirmFn">确认</el-button>
                            </p>
                        </el-form-item>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.flr {
    float: right;
    margin-right: 10px;
    color: #409eff;
}

i {
    font-style: normal;
    font-family: 'Lucida Sans', 'Lucida Sans Regular', 'Lucida Grande', 'Lucida Sans Unicode', Geneva, Verdana, sans-serif;
}

.mar {
    margin: 20px 0;

    p {
        &:nth-of-type(2) {
            display: flex;
            justify-content: space-between;
            width: 920px;
            color: #ccc;
            padding-bottom: 20px;
            border-bottom: 1px solid #f2f2f2;

            span {
                float: right;
            }
        }
    }
}
</style>
