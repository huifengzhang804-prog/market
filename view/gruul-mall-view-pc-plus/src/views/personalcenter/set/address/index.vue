<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { MOBILE } from '@/utils/RegexPool'
import { doGetAddressList, doSetAddressDefault, doDelAddress, doEditAddress, doNewAddress, doGetAddressByLocation } from '@/apis/address'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import { ArrowRight } from '@element-plus/icons-vue'
import AmapChooseDialog from '@/views/personalcenter/set/address/components/AmapChooseDialog.vue'
import { Checked, Geometry, GeometryType } from '@/apis/address/types'

interface AddressItemType {
  id: string
  address: string
  area: [string, string, string?]
  isDefault: boolean
  mobile: string
  name: string
  fakeAddress?: string
  location: Geometry
}

const total = ref('0')
const addressList = ref<AddressItemType[]>([])
const formRef = ref()
const addressinfo = ref<AddressItemType>({
  id: '',
  address: '',
  area: [] as unknown as [string, string, string?],
  isDefault: false,
  mobile: '',
  name: '',
  fakeAddress: '', // 假的地址，用于展示门牌号等信息(不可包含字符串: '~' )
  location: {
    type: GeometryType.Point,
    coordinates: [] as unknown as [number, number],
  },
})

const rules = ref({
  name: [
    { required: true, message: '请输入姓名', trigger: 'change' },
    { min: 2, max: 5, message: '长度在 2 到 5 个字符', trigger: 'change' },
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'change' },
    { pattern: MOBILE, message: '手机号格式不正确', trigger: 'change' },
  ],
  area: [{ required: true, message: '请选择定位', trigger: 'change' }],
  fakeAddress: [
    {
      required: true,
      message: '请填写详细地址',
      trigger: 'change',
    },
    {
      validator: (rule: any, value: any, callback: any) => {
        if (value.includes('~')) {
          callback(new Error('详细地址不能包含~号'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
})

initList()

async function initList() {
  const { data, code, success, msg } = await doGetAddressList()
  if (code === 200 && success) {
    addressList.value = data.records
    total.value = data.total
  } else {
    ElMessage.error(msg)
  }
}
/**
 * 删除地址
 */
const handleDelAddress = async (id: string) => {
  const isValidate = await ElMessageBox.confirm('确定要删除该收货地址吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (!isValidate) return
  const { code } = await doDelAddress(id)
  if (code !== 200) {
    ElMessage.error('删除失败')
    return
  }
  ElMessage.success('删除成功')
  initList()
}
/**
 * 重置表单
 */
const reset = async () => {
  addressinfo.value = {
    id: '',
    address: '',
    area: [] as unknown as [string, string, string?],
    isDefault: false,
    mobile: '',
    name: '',
    fakeAddress: '',
    location: {
      type: GeometryType.Point,
      coordinates: [] as unknown as [number, number],
    },
  }
  formRef.value.clearValidate()
}
/**
 * 修改地址
 */
const handlePutAddress = async (address: AddressItemType) => {
  dialogVisible.value = true
  await nextTick()
  addressinfo.value = JSON.parse(JSON.stringify(address))
  addressinfo.value.fakeAddress = address.address.split('~')[2] || ''
  addressinfo.value.address = `${address.address.split('~')[0] || ''}~${address.address.split('~')[1] || ''}`
  formRef.value.clearValidate()
}
/**
 * 设置默认地址
 */
const handleChangeCheck = async (id: string) => {
  const { code, success } = await doSetAddressDefault(id, true)
  if (code === 200 && success) {
    ElMessage.success('设置成功')
    initList()
  }
}

const newAddress = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      // 处理下假地址
      const copyForm = JSON.parse(JSON.stringify(addressinfo.value))
      copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
      doNewAddress(copyForm).then((res) => {
        if (res.code === 200 && res.success) {
          ElMessage.success('添加成功')
          initList()
          dialogVisible.value = false
        } else {
          ElMessage.error(res.msg ? res.msg : '添加失败')
        }
      })
    }
  })
}
const editAddress = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      const copyForm = JSON.parse(JSON.stringify(addressinfo.value))
      copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
      doEditAddress(addressinfo.value.id, copyForm).then((res) => {
        if (res.code === 200 && res.success) {
          ElMessage.success('修改成功')
          initList()
          dialogVisible.value = false
        } else {
          ElMessage.error(res.msg ? res.msg : '修改失败')
        }
      })
    }
  })
}
const dialogVisible = ref(false)
const toAddAddress = async () => {
  dialogVisible.value = true
  await nextTick()
  formRef.value.clearValidate()
}
const showChooseMap = ref(false)
const toChooseAddress = () => {
  showChooseMap.value = true
}
const handleChooseRes = async (res: Checked) => {
  if (!res.longitude) return
  if (!res.name) return
  if (!res.address) return
  const checkedLocation: Geometry = {
    type: GeometryType.Point,
    coordinates: [Number(res.longitude), Number(res.latitude)],
  }
  addressinfo.value.location = checkedLocation

  const { area, address } = await doGetAddressByLocation(checkedLocation.coordinates, false)
  if (area) {
    addressinfo.value.area = area
  }
  if (address) {
    addressinfo.value.address = `${address}~${res.name}`
  }
}
const placeChoosed = (place: Checked) => {
  handleChooseRes(place)
}
</script>

<template>
  <div bg-white c-bc-ccc>
    <div c-w-1190 ma flex>
      <center-nav />
      <div c-w-1024 c-ml-49>
        <div c-h-66 c-w-1024 b-b c-bc-ccc c-pl-25 e-c-3 fw-700 c-fs-18 c-lh-66 text-left c-mb-16 class="fcenter">
          <div class="">我的地址</div>
          <div class="mla">
            <el-button type="primary" @click="toAddAddress">新增收货地址</el-button>
          </div>
        </div>

        <div b-1 c-bc-eaeaea c-fs-14 c-c-101010>
          <div e-c-6 flex c-h-49 c-lh-49 b-b c-bc-eaeaea c-bg-f5f5f5>
            <div c-ml-15>收货人</div>
            <div c-ml-95>联系电话</div>
            <div c-ml-80>收货地址</div>
            <div c-ml-375>操作</div>
          </div>
          <div v-for="item in addressList" :key="item.id" min-h-16 flex items-center b-b c-bc-eaeaea>
            <div c-w-72 text-left c-ml-15>
              {{ item.name }}
            </div>
            <div c-w-69 text-left c-ml-60>
              {{ item.mobile }}
            </div>
            <div c-w-400 c-ml-70 text-left c-lh-20>
              <div>{{ item.address.split('~')[1] }}</div>
              <div>
                <q-address :address="item.area" />
                {{ item.address.split('~')[0] }}{{ item.address.split('~')[2] }}
              </div>
            </div>
            <div c-c-0066FF c-ml-30>
              <span c-mr-11 cursor-pointer @click.stop="handlePutAddress(item)">修改</span><text e-c-9>|</text
              ><span c-ml-11 cursor-pointer @click.stop="handleDelAddress(item.id)">删除</span>
            </div>
            <div v-if="item.isDefault" b-1 c-bc-F54319 c-br-31 c-c-F54319 c-h-26 c-lh-26 c-ml-90 c-w-76>默认地址</div>
            <div v-else c-c-0066FF c-ml-90 cursor-pointer @click.stop="handleChangeCheck(item.id)">设为默认地址</div>
          </div>
        </div>
        <el-dialog v-model="dialogVisible" :title="addressinfo.id ? '编辑收货地址' : '新增收货地址'" width="500" @close="reset">
          <el-form ref="formRef" :model="addressinfo" label-width="80px" :rules="rules">
            <el-form-item label="收货人" prop="name">
              <el-input v-model="addressinfo.name" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号" prop="mobile">
              <el-input v-model="addressinfo.mobile" placeholder="请输入正确的11位手机号码" :maxlength="11" />
            </el-form-item>
            <el-form-item label="定位地址" prop="area" class="address_form_item">
              <div class="fcenter cup" @click="toChooseAddress">
                <div class="address_area">
                  <div v-if="addressinfo.address" class="address_name">{{ addressinfo.address.split('~')[1] || '' }}</div>
                  <div v-else style="color: rgb(0, 102, 255)">打开地图选择定位</div>
                  <div v-if="addressinfo.area" class="address_detail_name">
                    {{ addressinfo.area?.join?.('') }}{{ addressinfo.address.split('~')[0] }}
                  </div>
                </div>
                <el-icon style="color: rgb(0, 102, 255)"><ArrowRight /></el-icon>
              </div>
            </el-form-item>
            <el-form-item label="详细地址" prop="fakeAddress">
              <el-input v-model="addressinfo.fakeAddress" :rows="5" type="textarea" placeholder="例如: [3单元203室]" :maxlength="20" />
            </el-form-item>
            <el-form-item label="" prop="isDefault">
              <el-checkbox v-model="addressinfo.isDefault" label="设为默认收货地址" size="large" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div>
              <el-button @click="dialogVisible = false">取消</el-button>
              <el-button type="primary" @click="() => (addressinfo.id ? editAddress() : newAddress())">确认</el-button>
            </div>
          </template>
        </el-dialog>
        <AmapChooseDialog v-model="showChooseMap" :initLocation="addressinfo.location.coordinates" @placeChoose="placeChoosed"></AmapChooseDialog>
        <ToTopGoCar />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.address_form_item {
  .fcenter {
    width: 100%;
    .address_area {
      padding: 1px 11px;
      display: flex;
      flex-direction: column;
      max-width: calc(100% - 80px);
      text-align: left;
      line-height: 18px;
      .address_name {
        font-size: 16px;
        font-weight: bold;
      }
      .address_detail_name {
        color: #a8abb2;
      }
    }
  }
}
</style>
