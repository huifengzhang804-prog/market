<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { doGetUserData } from '@/apis/afs'
import { doPostUserData } from '@/apis/consumer'
import { MOBILE } from '@/utils/RegexPool'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'

const $userStore = useUserStore()
const input = ref()
const UserInfo = ref({
  avatar: '',
  nickname: '',
  gender: '',
  phone: '',
})

getUserInfo()

/**
 * @description: 获取用户消息
 */
async function getUserInfo() {
  const { data, code, msg } = await doGetUserData()
  if (code !== 200) return ElMessage.error(`${msg ? msg : '获取用户消息失败'}`)
  $userStore.addUserInfo(data)
  UserInfo.value = data
}
const handleSubmit = async () => {
  if (!$userStore.getterToken) {
    $userStore.loginTypeTrue()
    return
  }
  if (!UserInfo.value.nickname) {
    return ElMessage.error('昵称不能为空')
  }
  if (!UserInfo.value.gender) {
    return ElMessage.error('请选择性别')
  }
  // if (!UserInfo.value.avatar) {
  //     return ElMessage.error('请选择头像')
  // }
  // doPostUserData(UserInfo.value).then(() => {
  //     ElMessage.success('更新成功')
  //     $userStore.addUserInfo(UserInfo.value)
  // })
  const { code, msg } = await doPostUserData(UserInfo.value)
  if (code === 200) {
    ElMessage.success('更新成功')
    $userStore.addUserInfo(UserInfo.value)
  } else ElMessage.error(msg)
}
/**
 * @description: 手机号中间四位加密格式化
 * @returns {*} string
 */
function formatPhone(phone: string) {
  const isPhone = phone && MOBILE.test(phone)
  return isPhone ? phone.replace(phone.substring(3, 7), '****') : ''
}
</script>

<template>
  <div bg-white c-bc-ccc>
    <div c-w-1190 ma flex>
      <center-nav />
      <div c-w-1024 c-ml-49>
        <div c-h-66 c-w-1024 b-b c-bc-ccc c-pl-25 e-c-3 fw-700 c-fs-18 c-lh-66 text-left c-mb-16>个人信息</div>
        <div c-fs-12 c-c-101010 text-left c-mt-21>
          <div flex items-center c-mb-24>
            <div text-right c-w-73 c-mr-20>用户头像：</div>
            <img :src="UserInfo.avatar" c-w-73 c-h-73 e-br />
          </div>
          <div flex items-center c-mb-24>
            <div text-right c-w-73 c-mr-20>用户昵称：</div>
            <div c-w-200 c-h-34 b-1 c-bc-bbb>
              <el-input v-model="UserInfo.nickname" :maxlength="11" />
            </div>
          </div>
          <div flex items-center c-mb-24>
            <div text-right c-w-73 c-mr-20>性别：</div>
            <el-radio-group v-model="UserInfo.gender">
              <el-radio value="MALE" size="large"> 男 </el-radio>
              <el-radio value="FEMALE" size="large"> 女 </el-radio>
            </el-radio-group>
          </div>
          <div flex items-center c-mb-57>
            <div text-right c-w-73 c-mr-20>已验证手机：</div>
            <div>
              {{ formatPhone(UserInfo.phone) }}
            </div>
          </div>
          <div c-bg-e31436 e-c-f c-fs-14 text-center c-w-80 c-h-30 c-lh-30 c-ml-96 cursor-pointer @click="handleSubmit">保存信息</div>
        </div>
        <ToTopGoCar />
      </div>
    </div>
  </div>
</template>

<style scoped></style>
