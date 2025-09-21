<script setup lang="ts">
import { ElMessage } from 'element-plus'
import type { CascaderProps } from 'element-plus'
import { doGetCategory } from '@/apis/shops'
import { doAddBrand, getBrandDetail, doEditeBrand } from '@apis/brand'
// 选择素材 e
import SelectMaterial from '@/views/material/selectMaterial.vue'

type ShopCategoryItem = Record<'id' | 'name' | 'parentId' | 'level', string>
interface ShopCategoryList extends ShopCategoryItem {
    disabled?: boolean
    children: ShopCategoryList[]
}

const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
const buttonlFn = () => {
    dialogVisible.value = true
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    // console.log(val)
    submitForm.value.brandLogo = val
}
const checkedFileLists = (val: string[]) => {
    // console.log(val)
    submitForm.value.brandLogo = val?.shift() || ''
}
// 选择素材 d

const BrandFormRef = ref()
const $router = useRouter()
const $route = useRoute()
const ID = ref('')
const submitForm = ref({
    brandName: '',
    brandDesc: '',
    brandLogo: '',
    searchInitials: '',
    parentCategoryId: '',
    sort: 1,
})
const FormRules = reactive({
    brandName: [
        {
            required: true,
            message: '请填写品牌名称',
            trigger: 'blur',
        },
    ],
    brandLogo: [
        {
            required: true,
            message: '请添加品牌LOGO',
            trigger: 'blur',
        },
    ],
    searchInitials: [
        {
            required: true,
            message: '请填写检索首字母(大写)',
            trigger: 'blur',
            validator: isCapital,
        },
    ],
    parentCategoryId: [
        {
            required: true,
            message: '请选择上级类目',
            trigger: ['blur', 'change'],
        },
    ],
})
const platformCategoryList = ref<ShopCategoryList[]>([])
const shopCascaderProps = {
    expandTrigger: 'hover',
    label: 'name',
    value: 'id',
    emitPath: false,
}

init()

async function init() {
    await getPlatformCategory()
    const { brandId } = $route.query
    if (!brandId) return
    ID.value = brandId as string
    const { code, data } = await getBrandDetail(brandId as string)
    if (code !== 200) {
        ElMessage.error('获取品牌详情失败')
        return
    }
    submitForm.value = data
}

/**
 * 获取上级类目列表
 */
async function getPlatformCategory() {
    const { code, data } = await doGetCategory({
        current: 1,
        size: 1000,
    })
    if (code !== 200) {
        ElMessage.error('获取上级类目失败')
        return
    }
    platformCategoryList.value = checkCategoryEnable(1, data.records)
}
/**
 * 新增品牌
 */
async function AddBrand() {
    const { code } = await doAddBrand(submitForm.value)
    if (code !== 200) {
        ElMessage.error('保存失败')
        return
    }
}
/**
 * 编辑品牌
 */
async function EditeBrand() {
    const { code } = await doEditeBrand(submitForm.value)
    if (code !== 200) {
        ElMessage.error('保存失败')
        return
    }
}
/**
 * 检查是分类否可用
 * @param currentLevel
 * @param records
 */
function checkCategoryEnable(currentLevel: number, records: any[]) {
    const isLastLevel = currentLevel === 3
    for (let record of records) {
        if (isLastLevel) {
            record.disabled = false
            continue
        }
        const children = (record.children || record.secondCategoryVos || record.categoryThirdlyVos) as any[]
        delete record.secondCategoryVos
        delete record.categoryThirdlyVos
        const disable = !children || children.length === 0
        record.disabled = disable
        if (disable) {
            continue
        }
        checkCategoryEnable(currentLevel + 1, children)
        record.children = children
    }
    return records
}
const submitHandle = async () => {
    if (!BrandFormRef.value) return
    const isValidate = await BrandFormRef.value.validate()
    if (!isValidate) return
    if (ID.value) {
        await EditeBrand()
    } else {
        await AddBrand()
    }
    $router.push('/brandManage')
}
function isCapital(rule: any, value: any, callback: any) {
    if (!/^[A-Z]+$/.test(value)) {
        callback(new Error())
    }
    callback()
}
</script>

<template>
    <el-form ref="BrandFormRef" :model="submitForm" :rules="FormRules" label-width="100px">
        <el-form-item label="品牌名称" prop="brandName">
            <el-input v-model="submitForm.brandName" placeholder="请输入品牌名称" maxlength="30"></el-input>
        </el-form-item>
        <el-form-item label="品牌描述" prop="brandDesc">
            <el-input v-model="submitForm.brandDesc" :rows="4" type="textarea" maxlength="100" />
        </el-form-item>
        <el-form-item label="品牌LOGO" prop="brandLogo">
            <!-- <q-upload v-model:src="submitForm.brandLogo" /> -->

            <div v-if="!submitForm.brandLogo" class="selectMaterialStyle" @click="buttonlFn">
                <span class="selectMaterialStyle__span">+</span>
            </div>
            <img v-else class="selectMaterialStyle" :src="submitForm.brandLogo" alt="" @click="buttonlFn" />
        </el-form-item>
        <el-form-item label="检索首字母" prop="searchInitials">
            <el-input v-model="submitForm.searchInitials" placeholder="请输入检索首字母" maxlength="1"></el-input>
        </el-form-item>
        <el-form-item label="上级类目" prop="parentCategoryId">
            <el-cascader
                ref="platformCategoryRef"
                v-model="submitForm.parentCategoryId"
                clearable
                :options="platformCategoryList"
                :props="shopCascaderProps as CascaderProps"
                placeholder="请选择上级类目"
            />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
            <el-input-number v-model="submitForm.sort" :min="1" :max="1000" controls-position="right" />
        </el-form-item>
    </el-form>
    <div class="shopForm__tool">
        <el-button round @click="$router.push('/brandManage')"> 取消 </el-button>
        <el-button type="primary" round @click="submitHandle"> 保存 </el-button>
    </div>
    <!-- 选择素材 e -->
    <SelectMaterial
        :dialog-visible="dialogVisible"
        :upload-files="1"
        @select-material-fn="selectMaterialFn"
        @cropped-file-change="croppedFileChange"
        @checked-file-lists="checkedFileLists"
    />
    <!-- 选择素材 d -->
</template>

<style lang="scss" scoped>
@include b(shopForm) {
    overflow: hidden;
    // min-height: 800px;
    @include e(tool) {
        width: 1010px;
        @include flex();
        position: fixed;
        bottom: 10px;
        padding: 15px 0px;
        display: flex;
        justify-content: center;
        box-shadow: 0 0px 10px 0px #d5d5d5;
        background-color: white;
        margin-left: -14px;
        z-index: 999;
    }
}
@include b(selectMaterialStyle) {
    width: 60px;
    height: 60px;
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
