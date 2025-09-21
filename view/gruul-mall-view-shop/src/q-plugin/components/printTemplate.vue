<script setup lang="ts">
import { ref, reactive, computed, type PropType } from 'vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getPrintTemplate, doUpdatePrintTemplate, doDelPrintTemplate, doGetPrintTemplateDetail } from '@/apis/order'
import {
    PrintTaskLink,
    PrintTemplateConfigCodeType,
    PrintTemplateConfigProductsType,
    PrintTemplateConfigStyle,
    PrintTemplateDTOConfig,
    PrinterBrand,
    PrinterSize,
    makeOptions,
    type PrintTemplate,
    type PrintTemplateDTO,
    type PrinterDTOMode,
} from './types'
import type { PickPageParams } from '@/utils/types'
import { get } from 'lodash-es'
import { Edit, QuestionFilled } from '@element-plus/icons-vue'

const props = defineProps({
    mode: {
        type: String as PropType<keyof typeof PrinterDTOMode>,
        default: 'STORE_PICKUP_SELF',
    },
})
const pageConfig = reactive<PickPageParams<'current' | 'size' | 'pages' | 'total'>>({
    current: 1, // 当前页码
    size: 10, // 每页条数
    pages: 0, // 总页数
    total: 0, // 总条数
})
const printerTempList = ref<PrintTemplate[]>([])
const initprinterTempList = async () => {
    const { code, data, msg } = await getPrintTemplate({
        ...pageConfig,
        mode: props.mode,
    })
    if (code === 200 && data) {
        pageConfig.pages = data.pages
        pageConfig.total = data.total
        printerTempList.value = data.records
    } else {
        ElMessage.error(msg || '获取打印模板失败')
    }
}
initprinterTempList()
const handleSizeChange = (value: number) => {
    pageConfig.size = value
    initprinterTempList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initprinterTempList()
}

const drawerVisible = ref(false)
const tempConfigForm: PrintTemplateDTOConfig = {
    title: {
        platformName: true,
        pickupCode: true,
        style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'],
    },
    shopName: {
        b: true,
        selected: true,
        style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'],
    },
    orderRemark: {
        b: true,
        selected: true,
        style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'],
    },
    products: {
        type: 'SPACE_BETWEEN',
        skuId: false,
        productName: true,
        specs: true,
    },
    orderStatistic: {
        payPrice: { b: true, selected: true, style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'] },
        productNum: true,
        totalPrice: true,
        freight: true,
        platformDiscount: true,
        shopDiscount: true,
        totalDiscount: true,
    },
    orderInfo: {
        orderNo: {
            b: true,
            selected: true,
            style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'],
        },
        payType: {
            b: true,
            selected: true,
            style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'],
        },
        orderTime: {
            b: true,
            selected: true,
            style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'],
        },
        payTime: {
            b: true,
            selected: true,
            style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'],
        },
    },
    targetInfo: {
        name: {
            b: true,
            selected: true,
            style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'],
        },
        mobile: {
            b: true,
            selected: true,
            style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'],
        },
        address: {
            b: true,
            selected: true,
            style: ['ALIGN_LEFT', 'HEIGHT_1', 'WIDTH_1', 'WEIGHT_1', 'SIZE_1'],
        },
    },
    code: {
        type: '',
        content: '',
        desc: '',
    },
    desc: '',
}
const printerTempFormRef = ref()
const printerTempForm = ref<PrintTemplateDTO>({
    id: '',
    mode: props.mode,
    name: '',
    brand: 'FEIE',
    link: 'CUSTOMER',
    size: 'V58',
    config: JSON.parse(JSON.stringify(tempConfigForm)),
})

/**
 * 商品编号/商品名称/商品规格：必须选中其中一个；未选中时需提示【商品编号/名称/规格最少选中1个】
 */
const validateConfigProducts = (rule: any, value: any, callback: any) => {
    if (
        printerTempForm.value.config.products.skuId ||
        printerTempForm.value.config.products.productName ||
        printerTempForm.value.config.products.specs
    ) {
        callback()
    } else {
        callback(new Error('商品编号/名称/规格最少选中1个'))
    }
}

const regNumberAndLetter = /^[0-9a-zA-Z]+$/
/**
 * 二维码/条形码只能选中一个或都不选，选中/未选中状态可相互切换；
 * 如果是条形码：数字/大(小)写字母 鼠标移动至问号处浮层展示对应文案（参考原型图）
 */
const validateConfigCode = (rule: any, value: any, callback: any) => {
    // 正则 数字/大(小)写字母
    if (printerTempForm.value.config.code.type !== 'BAR_CODE') {
        callback()
    } else if (printerTempForm.value.config.code.content) {
        if (regNumberAndLetter.test(printerTempForm.value.config.code.content)) {
            callback()
        } else {
            callback(new Error('条形码内容只允许输入数字/大(小)写字母'))
        }
    } else {
        callback(new Error('请输入条形码内容'))
    }
}
const rules = {
    name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
    brand: [{ required: true, message: '请选择打印机品牌', trigger: 'change' }],
    link: [{ required: true, message: '请选择打印类型', trigger: 'change' }],
    size: [{ required: true, message: '请选择打印纸宽', trigger: 'change' }],
    'config.products': [
        { required: true, message: '请设置商品信息', trigger: 'change' },
        { validator: validateConfigProducts, trigger: 'change' },
    ],
    'config.code': [{ validator: validateConfigCode, trigger: 'change' }],
}

const printerBrandOptions = makeOptions(PrinterBrand)
const PrinterLinkOptions = makeOptions(PrintTaskLink)
const PrinterSizeOptions = makeOptions(PrinterSize)
const PrintTemplateConfigCodeOptions = makeOptions(PrintTemplateConfigCodeType)
const PrintTemplateConfigProductsTypeOptions = makeOptions(PrintTemplateConfigProductsType)

const editPrinter = async (row: PrintTemplate) => {
    drawerVisible.value = true
    const { data, code } = await doGetPrintTemplateDetail({ id: row.id })
    if (code === 200 && data) {
        printerTempForm.value = data
    } else {
        ElMessage.error('获取模板详情失败')
    }
}

const delPrinterTemp = async (row: PrintTemplate) => {
    try {
        await ElMessageBox.confirm('模板删除后无法恢复，确定要删除该模板吗？', '删除模板', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        const { code, msg } = await doDelPrintTemplate({ id: row.id })
        if (code === 200) {
            initprinterTempList()
            ElMessage.success('删除模板成功')
        } else {
            ElMessage.error(msg || '删除模板失败')
        }
    } catch (e) {
        console.log(' ', e)
    }
}
const drawerSize = ref('')
/**
 * 获取当前屏幕尺寸
 */
const getScreenSize = () => {
    const { innerWidth } = window
    const leftWidth = innerWidth <= 1620 ? 0 : (innerWidth - 1440) / 2
    drawerSize.value = `calc(100% - 192px - ${leftWidth}px)`
}
getScreenSize()

const confirm = () => {
    printerTempFormRef.value.validate(async (valid: any) => {
        if (valid) {
            printerTempForm.value.mode = props.mode
            const { code, msg } = await doUpdatePrintTemplate(printerTempForm.value)
            if (code === 200) {
                ElMessage.success(`${printerTempForm.value.id ? '编辑' : '新增'}成功`)
                drawerVisible.value = false
                initprinterTempList()
            } else {
                ElMessage.error(msg || `${printerTempForm.value.id ? '编辑' : '新增'}失败`)
            }
        }
    })
}
/**
 * 结算信息是否整个展示
 */
const isShowAllOrderStatistic = computed(() => {
    return Object.keys(printerTempForm.value.config.orderStatistic)
        .filter((key) => key !== 'payPrice')
        .some((key) => {
            return printerTempForm.value.config.orderStatistic[key as keyof typeof printerTempForm.value.config.orderStatistic]
        })
})
/**
 * 订单信息是否整个展示
 */
const isShowAllOrderInfo = computed(() => {
    return Object.keys(printerTempForm.value.config.orderInfo).some((key) => {
        return printerTempForm.value.config.orderInfo[key as keyof typeof printerTempForm.value.config.orderInfo].selected
    })
})
/**
 * 收货人信息是否整个展示
 */
const isShowAllReceiverInfo = computed(() => {
    return Object.keys(printerTempForm.value.config.targetInfo).some((key) => {
        return printerTempForm.value.config.targetInfo[key as keyof typeof printerTempForm.value.config.targetInfo].selected
    })
})

const pickEnum = (enumeration: Obj, FieldList: string[]) => {
    return Object.keys(enumeration).reduce((prev, key) => {
        if (FieldList.includes(key)) {
            prev[key] = enumeration[key]
        }
        return prev
    }, {} as Obj)
}
const alignOptions = makeOptions(pickEnum(PrintTemplateConfigStyle, ['ALIGN_LEFT', 'ALIGN_CENTER', 'ALIGN_RIGHT']))
const fontHeightOptions = makeOptions(pickEnum(PrintTemplateConfigStyle, ['HEIGHT_1', 'HEIGHT_2']))
const fontWidthOptions = makeOptions(pickEnum(PrintTemplateConfigStyle, ['WIDTH_1', 'WIDTH_2']))
const fontWeightOptions = makeOptions(pickEnum(PrintTemplateConfigStyle, ['WEIGHT_1', 'WEIGHT_2']))
const fontSizeOptions = makeOptions(pickEnum(PrintTemplateConfigStyle, ['SIZE_1', 'SIZE_2']))
const dialogVisible = ref(false)
const dialogTitle = ref('')
const styleformRef = ref()
const styleForm = ref<{
    align: keyof typeof PrintTemplateConfigStyle
    fontHeight: keyof typeof PrintTemplateConfigStyle
    fontWidth: keyof typeof PrintTemplateConfigStyle
    fontWeight: keyof typeof PrintTemplateConfigStyle
    fontSize: keyof typeof PrintTemplateConfigStyle
}>({
    /**
     * 排列方式
     *  ALIGN_LEFT = '居左',
     *  ALIGN_CENTER = '居中',
     *  ALIGN_RIGHT = '居右',
     */
    align: 'ALIGN_LEFT',
    /**
     * 字体高度
     * HEIGHT_1 = '正常高度',
     * HEIGHT_2 = '双倍高度',
     */
    fontHeight: 'HEIGHT_1',
    /**
     * 字体宽度
     * WIDTH_1 = '正常宽度',
     * WIDTH_2 = '双倍宽度',
     */
    fontWidth: 'WIDTH_1',
    /**
     * 字体加粗
     * WEIGHT_1 = '正常字体粗细',
     * WEIGHT_2 = '字体加粗',
     */
    fontWeight: 'WEIGHT_1',
    /**
     * 字体大小
     * SIZE_1 = '正常字体大小',
     * SIZE_2 = '双倍字体大小',
     */
    fontSize: 'SIZE_1',
})

/**
 * 表示当前弹窗是修改的哪个样式?
 */
const targetStyle = ref<{ style: (keyof typeof PrintTemplateConfigStyle)[] }>({
    style: [],
})
/**
 * 缓存样式(用于取消时复原)
 */
const tempStyle = ref<typeof styleForm.value>()
/**
 * 根据字段来修改该字段的样式
 * @param path 打印模板form中的字段路径
 * @param FieldName 字段名称
 */
const editSomeStyle = (path: string, FieldName: string) => {
    // 打开样式弹窗
    dialogVisible.value = true
    dialogTitle.value = FieldName
    // 获取当前字段 如果包含.则表示是嵌套字段 继续往深层读取 这里使用lodash的get方法
    console.log(get(printerTempForm.value.config, path).style)
    targetStyle.value = get(printerTempForm.value.config, path)
    tempStyle.value = {
        align: (targetStyle.value.style.find((item) => alignOptions.find((i) => i.value === item)) || '') as keyof typeof PrintTemplateConfigStyle,
        fontHeight: (targetStyle.value.style.find((item) => fontHeightOptions.find((i) => i.value === item)) ||
            '') as keyof typeof PrintTemplateConfigStyle,
        fontWidth: (targetStyle.value.style.find((item) => fontWidthOptions.find((i) => i.value === item)) ||
            '') as keyof typeof PrintTemplateConfigStyle,
        fontWeight: (targetStyle.value.style.find((item) => fontWeightOptions.find((i) => i.value === item)) ||
            '') as keyof typeof PrintTemplateConfigStyle,
        fontSize: (targetStyle.value.style.find((item) => fontSizeOptions.find((i) => i.value === item)) ||
            '') as keyof typeof PrintTemplateConfigStyle,
    }
    // 对表单赋值
    styleForm.value = JSON.parse(JSON.stringify(tempStyle.value))
}
const quitEditStyle = () => {
    // 取消修改
    styleForm.value = JSON.parse(JSON.stringify(tempStyle.value))
    targetStyle.value.style = Object.values(styleForm.value)
    dialogVisible.value = false
}
const confirmStyle = () => {
    targetStyle.value.style = Object.values(styleForm.value)
    dialogVisible.value = false
}
const changeStyle = () => {
    targetStyle.value.style = Object.values(styleForm.value)
}
const computedStyleClass = (path: string) => ({
    align_left: get(printerTempForm.value.config, path).style.includes('ALIGN_LEFT'),
    align_center: get(printerTempForm.value.config, path).style.includes('ALIGN_CENTER'),
    align_right: get(printerTempForm.value.config, path).style.includes('ALIGN_RIGHT'),
    height_1: get(printerTempForm.value.config, path).style.includes('HEIGHT_1'),
    height_2: get(printerTempForm.value.config, path).style.includes('HEIGHT_2'),
    width_1: get(printerTempForm.value.config, path).style.includes('WIDTH_1'),
    width_2: get(printerTempForm.value.config, path).style.includes('WIDTH_2'),
    weight_1: get(printerTempForm.value.config, path).style.includes('WEIGHT_1'),
    weight_2: get(printerTempForm.value.config, path).style.includes('WEIGHT_2'),
    size_1: get(printerTempForm.value.config, path).style.includes('SIZE_1'),
    size_2: get(printerTempForm.value.config, path).style.includes('SIZE_2'),
})
const closeDrawer = () => {
    printerTempForm.value = {
        id: '',
        mode: props.mode,
        name: '',
        brand: 'FEIE',
        link: 'CUSTOMER',
        size: 'V58',
        config: JSON.parse(JSON.stringify(tempConfigForm)),
    }
    printerTempFormRef.value?.clearValidate()
}
const showEditIcon = (className: string) => {
    // .className 下的所有后代元素 .cup.edit_icon 显示
    const editIconList = document.querySelectorAll(`.${className} .cup.edit_icon`)
    editIconList.forEach((item) => {
        ;(item as HTMLDivElement).style.opacity = '1'
    })
}
const hideEditIcon = (className: string) => {
    // .className 下的所有后代元素 .cup.edit_icon 隐藏
    const editIconList = document.querySelectorAll(`.${className} .cup.edit_icon`)
    editIconList.forEach((item) => {
        ;(item as HTMLDivElement).style.opacity = '0'
    })
}
const regLetter = /[A-Za-z]+/
const codeContentMaxlength = computed(() => {
    // printerTempForm.value.config.code.content 是否包含字母，包含则最大长度为 14，否则为 22
    return printerTempForm.value.config.code.type === 'BAR_CODE' ? (regLetter.test(printerTempForm.value.config.code.content) ? 14 : 22) : 200
})
</script>

<template>
    <div class="q_plugin_container overf">
        <div class="handle_container fcenter">
            <el-button type="primary" @click="drawerVisible = true">新增模板</el-button>
        </div>
        <div class="table_container">
            <el-table :data="printerTempList" style="width: 100%">
                <template #empty>
                    <ElTableEmpty />
                </template>
                <el-table-column prop="name" label="模板名称" width="130" fixed="left"> </el-table-column>
                <el-table-column prop="brand" label="打印机品牌" width="130">
                    <template #default="{ row }">
                        <span>{{ PrinterBrand[(row as PrintTemplate).brand] }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="link" label="打印类型">
                    <template #default="{ row }">
                        <span>{{ PrintTaskLink[(row as PrintTemplate).link] }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="size" label="打印纸宽">
                    <template #default="{ row }">
                        <span>{{ PrinterSize[(row as PrintTemplate).size] }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="createDate" label="添加时间"> </el-table-column>
                <el-table-column label="操作" fixed="right" width="150">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="editPrinter(row)">编辑</el-button>
                        <el-button type="danger" link @click="delPrinterTemp(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <PageManage
            :page-size="pageConfig.size"
            :total="pageConfig.total"
            :page-num="pageConfig.current"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />

        <el-drawer v-model="drawerVisible" :title="printerTempForm.id ? '编辑模板' : '添加模板'" :size="drawerSize" @close="closeDrawer">
            <div class="drawer_container df">
                <div class="left">
                    <div class="title">打印预览</div>
                    <div class="tips">提示：当前小票只适用于{{ PrinterSize[printerTempForm.size] }}纸宽</div>
                    <div class="card">
                        <div class="title_container" :class="computedStyleClass('title')">
                            <div class="no_platform_name"><span v-if="printerTempForm.config.title.pickupCode"># 取餐号 </span>平台名称</div>
                        </div>
                        <div v-if="printerTempForm.config.shopName.selected" class="shop_name" :class="computedStyleClass('shopName')">店铺名称</div>
                        <template v-if="printerTempForm.config.orderRemark.selected">
                            <el-divider border-style="dotted"></el-divider>
                            <div class="cust_tips" :class="computedStyleClass('orderRemark')">
                                备注：顾客备注顾客备注顾客备注备注顾客备注备注顾客备注备注顾客备注备注顾客备注备注顾客备注顾客备注顾顾客备注顾
                            </div>
                        </template>
                        <template v-if="printerTempForm.config.products.type === 'SPACE_BETWEEN'">
                            <el-divider border-style="dotted">商品信息</el-divider>
                            <div class="product_container">
                                <div class="product_item">
                                    <div class="product_name">
                                        <span v-if="printerTempForm.config.products.skuId">181566054</span>
                                        <span v-if="printerTempForm.config.products.productName"
                                            >陶瓷水杯老式复古办公室茶缸子早餐大容量仿搪瓷杯茶杯
                                        </span>
                                        <span v-if="printerTempForm.config.products.specs">(中号茶杯)</span>
                                    </div>
                                    <div class="product_price">X1 &nbsp; 56.90</div>
                                </div>
                                <div class="product_item">
                                    <div class="product_name">
                                        <span v-if="printerTempForm.config.products.skuId">181566054</span>
                                        <span v-if="printerTempForm.config.products.productName"
                                            >陶瓷水杯老式复古办公室茶缸子早餐大容量仿搪瓷杯茶杯
                                        </span>
                                        <span v-if="printerTempForm.config.products.specs">(中号茶杯)</span>
                                    </div>
                                    <div class="product_price">X1 &nbsp; 56.90</div>
                                </div>
                            </div>
                        </template>
                        <template v-else>
                            <el-divider border-style="dotted">商品信息</el-divider>
                            <div class="product_list_table_container">
                                <div class="product_list_table_header">
                                    <div class="product_name">商品名称</div>
                                    <div class="product_num">数量</div>
                                    <div class="product_price">单价</div>
                                </div>
                                <el-divider border-style="dotted"></el-divider>
                                <div class="product_list_table_body">
                                    <div class="product_list_table_body_item">
                                        <div class="product_name">
                                            <span v-if="printerTempForm.config.products.skuId">11231214</span>
                                            <span v-if="printerTempForm.config.products.productName">伯牙绝弦 </span>
                                            <span v-if="printerTempForm.config.products.specs">(大杯 少冰 少糖)</span>
                                        </div>
                                        <div class="product_num">X1</div>
                                        <div class="product_price">56.90</div>
                                    </div>
                                    <div class="product_list_table_body_item">
                                        <div class="product_name">
                                            <span v-if="printerTempForm.config.products.skuId">11231214</span>
                                            <span v-if="printerTempForm.config.products.productName">伯牙绝弦 </span>
                                            <span v-if="printerTempForm.config.products.specs">(大杯 少冰 少糖)</span>
                                        </div>
                                        <div class="product_num">X1</div>
                                        <div class="product_price">56.90</div>
                                    </div>
                                </div>
                            </div>
                        </template>
                        <template v-if="isShowAllOrderStatistic">
                            <el-divider border-style="dotted"></el-divider>
                            <div class="computed_price_container">
                                <div v-if="printerTempForm.config.orderStatistic.productNum" class="computed_price_item">
                                    <div class="computed_price_item_label">商品数</div>
                                    <div class="computed_price_item_value">X2</div>
                                </div>
                                <div v-if="printerTempForm.config.orderStatistic.totalPrice" class="computed_price_item">
                                    <div class="computed_price_item_label">商品金额</div>
                                    <div class="computed_price_item_value">113.80</div>
                                </div>
                                <div v-if="printerTempForm.config.orderStatistic.freight" class="computed_price_item">
                                    <div class="computed_price_item_label">运费</div>
                                    <div class="computed_price_item_value">4.00</div>
                                </div>
                                <div v-if="printerTempForm.config.orderStatistic.platformDiscount" class="computed_price_item">
                                    <div class="computed_price_item_label">平台优惠</div>
                                    <div class="computed_price_item_value">5.00</div>
                                </div>
                                <div v-if="printerTempForm.config.orderStatistic.shopDiscount" class="computed_price_item">
                                    <div class="computed_price_item_label">店铺优惠</div>
                                    <div class="computed_price_item_value">3.00</div>
                                </div>
                                <div v-if="printerTempForm.config.orderStatistic.totalDiscount" class="computed_price_item">
                                    <div class="computed_price_item_label">总优惠</div>
                                    <div class="computed_price_item_value">8.00</div>
                                </div>
                            </div>
                        </template>
                        <template v-if="printerTempForm.config.orderStatistic.payPrice.selected">
                            <el-divider border-style="dotted"></el-divider>
                            <div class="pay_container" :class="computedStyleClass('orderStatistic.payPrice')">
                                实付
                                <div class="pay_price">￥109.80</div>
                            </div>
                        </template>
                        <template v-if="isShowAllOrderInfo">
                            <el-divider border-style="dotted"></el-divider>
                            <div class="order_container">
                                <div
                                    v-if="printerTempForm.config.orderInfo.orderNo.selected"
                                    class="order_item"
                                    :class="computedStyleClass('orderInfo.orderNo')"
                                >
                                    订单编号：SS273428473287482487237
                                </div>
                                <div
                                    v-if="printerTempForm.config.orderInfo.payType.selected"
                                    class="order_item"
                                    :class="computedStyleClass('orderInfo.payType')"
                                >
                                    支付方式：支付宝支付
                                </div>
                                <div
                                    v-if="printerTempForm.config.orderInfo.orderTime.selected"
                                    class="order_item"
                                    :class="computedStyleClass('orderInfo.orderTime')"
                                >
                                    下单时间：2024-07-24 17:17:14
                                </div>
                                <div
                                    v-if="printerTempForm.config.orderInfo.payTime.selected"
                                    class="order_item"
                                    :class="computedStyleClass('orderInfo.payTime')"
                                >
                                    支付时间：2024-07-24 17:17:14
                                </div>
                            </div>
                        </template>
                        <template v-if="isShowAllReceiverInfo">
                            <el-divider border-style="dotted"></el-divider>
                            <div class="express_container">
                                <div
                                    v-if="printerTempForm.config.targetInfo.name.selected"
                                    class="express_item"
                                    :class="computedStyleClass('targetInfo.name')"
                                >
                                    周杰伦
                                </div>
                                <div
                                    v-if="printerTempForm.config.targetInfo.mobile.selected"
                                    class="express_item"
                                    :class="computedStyleClass('targetInfo.mobile')"
                                >
                                    133****3333
                                </div>
                                <div
                                    v-if="printerTempForm.config.targetInfo.address.selected"
                                    class="express_item"
                                    :class="computedStyleClass('targetInfo.address')"
                                >
                                    宁波市江北区长兴路198号宁波柯力传感器科技有限公司物联网园区双K大厦508室
                                </div>
                            </div>
                        </template>
                        <template v-if="printerTempForm.config.code.type">
                            <el-divider border-style="dotted"></el-divider>
                            <div class="img_container">
                                <img
                                    v-if="printerTempForm.config.code.type === 'BAR_CODE'"
                                    class="img"
                                    src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/226717332ce4b0dd23b7a8b492.png"
                                />
                                <img
                                    v-else
                                    class="qr_img"
                                    src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/official-website/20240731/db1e265e159e4a83a882e0b5c7be5cbb.jpeg"
                                />
                                <div v-dompurify-html="printerTempForm.config.code.desc" class="img_tips"></div>
                            </div>
                        </template>
                        <template v-if="printerTempForm.config.desc">
                            <el-divider border-style="dotted"></el-divider>
                            <div v-dompurify-html="printerTempForm.config.desc" class="bottom_tips"></div>
                        </template>
                    </div>
                </div>
                <div class="center"></div>
                <div class="right">
                    <el-form ref="printerTempFormRef" :model="printerTempForm" :rules="rules" label-width="100px">
                        <div class="right_top">
                            <div class="right_title">模板信息</div>
                            <el-form-item label="模板名称" prop="name">
                                <el-input v-model="printerTempForm.name" placeholder="请输入模板名称" maxlength="20" show-word-limit></el-input>
                            </el-form-item>
                            <el-row :gutter="10">
                                <el-col :span="12">
                                    <el-form-item label="打印品牌" prop="brand">
                                        <el-select v-model="printerTempForm.brand" placeholder="请选择打印机品牌">
                                            <el-option v-for="item in printerBrandOptions" :key="item.value" :label="item.label" :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label="打印类型" prop="link">
                                        <el-select v-model="printerTempForm.link" placeholder="请选择打印类型">
                                            <el-option v-for="item in PrinterLinkOptions" :key="item.value" :label="item.label" :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-form-item label="打印纸宽" prop="size">
                                <el-radio-group v-model="printerTempForm.size">
                                    <el-radio v-for="item in PrinterSizeOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </div>
                        <div class="right_bottom">
                            <div class="right_title">自定义样式</div>
                            <el-form-item
                                label="头部内容"
                                class="header_eidt"
                                @mouseenter="showEditIcon('header_eidt')"
                                @mouseleave="hideEditIcon('header_eidt')"
                            >
                                <div class="title_config_container ccenter">
                                    <el-checkbox v-model="printerTempForm.config.title.platformName" disabled>平台名称</el-checkbox>
                                    <el-checkbox v-model="printerTempForm.config.title.pickupCode">取餐号</el-checkbox>
                                    <el-icon class="cup edit_icon" @click="editSomeStyle('title', '组合')"><Edit /></el-icon>
                                </div>
                                <el-checkbox v-model="printerTempForm.config.shopName.selected">店铺名称</el-checkbox>
                                <el-icon class="cup edit_icon" @click="editSomeStyle('shopName', '店铺名称')"><Edit /></el-icon>
                                <el-checkbox v-model="printerTempForm.config.orderRemark.selected">订单备注</el-checkbox>
                                <el-icon class="cup edit_icon" @click="editSomeStyle('orderRemark', '订单备注')"><Edit /></el-icon>
                            </el-form-item>
                            <el-form-item label="商品信息" prop="config.products">
                                <div class="fdc">
                                    <el-radio-group v-model="printerTempForm.config.products.type">
                                        <el-radio v-for="item in PrintTemplateConfigProductsTypeOptions" :key="item.value" :value="item.value">{{
                                            item.label
                                        }}</el-radio>
                                    </el-radio-group>
                                    <div>
                                        <el-checkbox v-model="printerTempForm.config.products.skuId">商品编号（SKUID）</el-checkbox>
                                        <el-checkbox v-model="printerTempForm.config.products.productName">商品名称</el-checkbox>
                                        <el-checkbox v-model="printerTempForm.config.products.specs">商品规格</el-checkbox>
                                    </div>
                                </div>
                            </el-form-item>
                            <el-form-item label="结算信息">
                                <el-checkbox v-model="printerTempForm.config.orderStatistic.productNum">商品数</el-checkbox>
                                <el-checkbox v-model="printerTempForm.config.orderStatistic.totalPrice">商品金额</el-checkbox>
                                <el-checkbox v-model="printerTempForm.config.orderStatistic.freight">运费</el-checkbox>
                                <el-checkbox v-model="printerTempForm.config.orderStatistic.platformDiscount">平台优惠</el-checkbox>
                                <el-checkbox v-model="printerTempForm.config.orderStatistic.shopDiscount">店铺优惠</el-checkbox>
                                <el-checkbox v-model="printerTempForm.config.orderStatistic.totalDiscount">总优惠</el-checkbox>
                                <el-checkbox v-model="printerTempForm.config.orderStatistic.payPrice.selected">实付</el-checkbox>
                                <!-- <el-icon class="cup edit_icon" @click="editSomeStyle('orderStatistic.payPrice', '结算信息实付')"><Edit /></el-icon> -->
                            </el-form-item>
                            <el-form-item
                                label="订单信息"
                                class="order_eidt"
                                @mouseenter="showEditIcon('order_eidt')"
                                @mouseleave="hideEditIcon('order_eidt')"
                            >
                                <el-checkbox v-model="printerTempForm.config.orderInfo.orderNo.selected">订单编号</el-checkbox>
                                <el-icon class="cup edit_icon" @click="editSomeStyle('orderInfo.orderNo', '订单编号')"><Edit /></el-icon>
                                <el-checkbox v-model="printerTempForm.config.orderInfo.payType.selected">支付方式</el-checkbox>
                                <el-icon class="cup edit_icon" @click="editSomeStyle('orderInfo.payType', '支付方式')"><Edit /></el-icon>
                                <el-checkbox v-model="printerTempForm.config.orderInfo.orderTime.selected">下单时间</el-checkbox>
                                <el-icon class="cup edit_icon" @click="editSomeStyle('orderInfo.orderTime', '下单时间')"><Edit /></el-icon>
                                <el-checkbox v-model="printerTempForm.config.orderInfo.payTime.selected">支付时间</el-checkbox>
                                <el-icon class="cup edit_icon" @click="editSomeStyle('orderInfo.payTime', '支付时间')"><Edit /></el-icon>
                            </el-form-item>
                            <el-form-item
                                :label="`${mode === 'INTRA_CITY' ? '收货人' : '自提点'}信息`"
                                class="people_eidt"
                                @mouseenter="showEditIcon('people_eidt')"
                                @mouseleave="hideEditIcon('people_eidt')"
                            >
                                <el-checkbox v-model="printerTempForm.config.targetInfo.name.selected">{{
                                    mode === 'INTRA_CITY' ? '收货人姓名' : '门店名称'
                                }}</el-checkbox>
                                <el-icon
                                    class="cup edit_icon"
                                    @click="editSomeStyle('targetInfo.name', mode === 'INTRA_CITY' ? '收货人姓名' : '门店名称')"
                                    ><Edit
                                /></el-icon>
                                <el-checkbox v-model="printerTempForm.config.targetInfo.mobile.selected">{{
                                    mode === 'INTRA_CITY' ? '收货人手机' : '负责人手机'
                                }}</el-checkbox>
                                <el-icon
                                    class="cup edit_icon"
                                    @click="editSomeStyle('targetInfo.mobile', mode === 'INTRA_CITY' ? '收货人手机' : '负责人手机')"
                                    ><Edit
                                /></el-icon>
                                <el-checkbox v-model="printerTempForm.config.targetInfo.address.selected">{{
                                    mode === 'INTRA_CITY' ? '收货人地址' : '门店地址'
                                }}</el-checkbox>
                                <el-icon
                                    class="cup edit_icon"
                                    @click="editSomeStyle('targetInfo.address', mode === 'INTRA_CITY' ? '收货人地址' : '门店地址')"
                                    ><Edit
                                /></el-icon>
                            </el-form-item>
                            <el-form-item label="条形/二维码" prop="config.code" class="code_edit code_edit_content">
                                <el-select
                                    v-model="printerTempForm.config.code.type"
                                    clearable
                                    value-on-clear=""
                                    placeholder="请选择条形码/二维码"
                                    style="margin-bottom: 10px"
                                >
                                    <el-option
                                        v-for="item in PrintTemplateConfigCodeOptions"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value"
                                    >
                                    </el-option>
                                </el-select>
                                <div class="fcenter">
                                    <div v-if="printerTempForm.config.code.type" class="code_title" style="color: #606266">
                                        {{ PrintTemplateConfigCodeType[printerTempForm.config.code.type] }}内容
                                    </div>
                                    <el-tooltip v-if="printerTempForm.config.code.type === 'BAR_CODE'" effect="dark" placement="top">
                                        <template #content>
                                            自定义的条形码内容必须遵守(其中一条)规则：
                                            <br />
                                            1、数字/大写字母：数字和大写字母混合的条形码，最多支持14位的数字、大写字母混合条形码
                                            <br />
                                            2、数字/大(小)写字母：数字和大小写字母混合的条形码，最多支持14位的数字、大写字母、小写字母混合条形码
                                            <br />
                                            3、纯数字：最多支持22位纯数字
                                        </template>
                                        <el-icon class="cup"><QuestionFilled /></el-icon>
                                    </el-tooltip>
                                </div>
                                <el-input
                                    v-if="printerTempForm.config.code.type"
                                    v-model="printerTempForm.config.code.content"
                                    type="textarea"
                                    :placeholder="`请输入${PrintTemplateConfigCodeType[printerTempForm.config.code.type]}内容`"
                                    :maxlength="codeContentMaxlength"
                                    show-word-limit
                                    style="margin-bottom: 10px"
                                ></el-input>
                            </el-form-item>
                            <el-form-item label="" prop="config.code.desc">
                                <div v-if="printerTempForm.config.code.type" class="code_title" style="color: #606266">
                                    {{ PrintTemplateConfigCodeType[printerTempForm.config.code.type] }}下方说明
                                </div>
                                <el-input
                                    v-if="printerTempForm.config.code.type"
                                    v-model="printerTempForm.config.code.desc"
                                    :placeholder="`请输入${PrintTemplateConfigCodeType[printerTempForm.config.code.type]}下方说明`"
                                    :maxlength="100"
                                    show-word-limit
                                    type="textarea"
                                ></el-input>
                            </el-form-item>
                            <el-form-item label="底部信息" prop="config.desc">
                                <el-input
                                    v-model="printerTempForm.config.desc"
                                    placeholder="请输入底部信息"
                                    :maxlength="100"
                                    show-word-limit
                                    type="textarea"
                                ></el-input>
                            </el-form-item>
                        </div>
                    </el-form>
                </div>
            </div>
            <template #footer>
                <div style="flex: auto">
                    <el-button @click="drawerVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirm">确定</el-button>
                </div>
            </template>
        </el-drawer>
        <el-dialog v-model="dialogVisible" :title="`${dialogTitle}字体设置`" width="600">
            <div>
                <el-form ref="styleformRef" :model="styleForm" label-width="80px">
                    <el-form-item label="排列方式" prop="align">
                        <el-radio-group v-model="styleForm.align" @change="changeStyle">
                            <el-radio v-for="item in alignOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="字体高度" prop="fontHeight">
                        <el-radio-group v-model="styleForm.fontHeight" @change="changeStyle">
                            <el-radio v-for="item in fontHeightOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="字体宽度" prop="fontWidth">
                        <el-radio-group v-model="styleForm.fontWidth" @change="changeStyle">
                            <el-radio v-for="item in fontWidthOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="字体加粗" prop="fontWeight">
                        <el-radio-group v-model="styleForm.fontWeight" @change="changeStyle">
                            <el-radio v-for="item in fontWeightOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="字体大小" prop="fontSize">
                        <el-radio-group v-model="styleForm.fontSize" @change="changeStyle">
                            <el-radio v-for="item in fontSizeOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-form>
            </div>
            <template #footer>
                <el-button @click="quitEditStyle">取 消</el-button>
                <el-button type="primary" @click="confirmStyle">确 定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<style lang="scss" scoped>
.drawer_container {
    height: 100%;
    overflow: hidden;
    .left {
        display: flex;
        flex-direction: column;
        align-items: center;
        height: 100%;
        padding: 10px;
        :deep(.el-divider--horizontal) {
            margin-top: 14px;
            margin-bottom: 14px;
        }
        .title {
            color: rgb(0, 0, 0);
            font-size: 16px;
            font-weight: 500;
            margin-bottom: 6px;
        }
        .tips {
            box-sizing: border-box;
            border: 1px solid rgb(85, 92, 253);
            border-radius: 4px;
            background: rgba(85, 92, 253, 0.05);
            color: rgb(102, 102, 102);
            font-size: 12px;
            font-weight: 500;
            padding: 5px 15px;
            margin-bottom: 16px;
        }
        .card {
            color: rgb(51, 51, 51);
            width: 303px;
            line-height: 1em;
            box-shadow: 0px 2px 13px 0px rgba(0, 0, 0, 0.1);
            background: rgb(255, 255, 255);
            padding-top: 23px;
            padding-left: 16px;
            padding-right: 16px;
            padding-bottom: 19px;
            max-height: calc(100% - 61px);
            overflow-y: scroll;
            display: flex;
            flex-direction: column;
            .title_container {
                display: flex;
            }
            .product_container {
                .product_item {
                    .product_name {
                        color: rgb(51, 51, 51);
                    }
                    .product_price {
                        color: rgb(102, 102, 102);
                        margin-left: auto;
                        display: flex;
                        flex-flow: row-reverse;
                    }
                }
            }
            .product_list_table_container {
                .product_list_table_header {
                    display: flex;
                    align-items: center;
                    .product_name {
                        color: rgb(51, 51, 51);
                        width: 180px;
                        flex-shrink: 0;
                    }
                    .product_price {
                        margin-left: auto;
                    }
                }
                .product_list_table_body {
                    .product_list_table_body_item {
                        display: flex;
                        align-items: baseline;
                        .product_name {
                            width: 180px;
                            color: rgb(51, 51, 51);
                        }
                        .product_price {
                            margin-left: auto;
                        }
                    }
                }
            }
            .computed_price_container {
                .computed_price_item {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    color: rgb(51, 51, 51);
                }
            }
            .pay_container {
                align-items: center;
                justify-content: space-between !important;
                color: rgb(51, 51, 51);
            }
            .order_container {
                color: rgb(51, 51, 51);
            }
            .express_container {
                color: rgb(51, 51, 51);
            }
            .img_container {
                display: flex;
                align-items: center;
                flex-direction: column;
                .img {
                    width: 170px;
                    height: 69px;
                    margin-bottom: 8px;
                }
                .qr_img {
                    width: 95px;
                    height: 95px;
                    margin-bottom: 8px;
                }
                .img_tips {
                    color: rgb(51, 51, 51);
                    white-space: pre-wrap; // 自动换行
                }
            }
            .bottom_tips {
                align-self: center;
                color: rgb(51, 51, 51);
                white-space: pre-wrap; // 自动换行
            }
            .align_left {
                display: flex;
                justify-content: flex-start;
                text-align: left;
            }
            .align_center {
                display: flex;
                justify-content: center;
                text-align: center;
            }
            .align_right {
                display: flex;
                justify-content: flex-end;
                text-align: right;
            }
            .height_1 {
                line-height: 1em;
            }
            .height_2 {
                line-height: 2em;
            }
            .width_1 {
                letter-spacing: 0;
            }
            .width_2 {
                letter-spacing: 0.05em;
            }
            .weight_1 {
                font-weight: normal;
            }
            .weight_2 {
                font-weight: bold;
            }
            .size_1 {
                font-size: 1rem;
            }
            .size_2 {
                font-size: 2rem;
            }
        }
    }
    .center {
        width: 6px;
        height: 150px;
        margin: 0 24px;
    }
    .right {
        padding: 10px;
        height: 100%;
        overflow-y: scroll;
        flex: 1;
        .edit_icon {
            margin-left: 5px;
            color: #999999;
            opacity: 0;
        }
        // 当某个元素后面紧跟着的兄弟元素是 .edit_icon 时，给这个元素 添加样式
        .el-checkbox:has(+ .edit_icon) {
            margin-right: 0;
        }
        .edit_icon:has(+ .el-checkbox) {
            margin-right: 30px;
        }
        .el-radio-group + .el-checkbox {
            margin-left: 30px;
        }

        .right_title {
            color: rgb(0, 0, 0);
            font-size: 24px;
            font-weight: bold;
            line-height: 24px;
            margin-bottom: 25px;
        }
        .title_config_container {
            margin-right: 30px;
            padding: 0 8px;
            border-radius: 5px;
            border: 1px dashed #c7c9fa;
        }
        .right_top {
            border-radius: 8px;
            background: rgb(247, 248, 250);
            margin-bottom: 12px;
            padding-left: 22px;
            padding-top: 21px;
            padding-bottom: 21px;
            padding-right: 54px;
        }
        .right_bottom {
            border-radius: 8px;
            background: rgb(247, 248, 250);
            padding-top: 26px;
            padding-left: 21px;
            padding-right: 20px;
            padding-bottom: 20px;
        }

        .code_edit {
            margin-bottom: 0;
        }

        .code_edit_content {
            :deep(.el-form-item__error) {
                margin-top: -8px;
            }
        }
    }
}
</style>
