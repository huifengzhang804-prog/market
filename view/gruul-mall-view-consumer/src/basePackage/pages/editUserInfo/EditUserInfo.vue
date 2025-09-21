<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/modules/user'
import { useSettingStore } from '@/store/modules/setting'
import { doPostUserData } from '@/apis/consumer/footprint/index'
import { getheader } from '@/libs/request/returnHeader'
import type { GenderType } from '@/store/modules/user/state'
import Auth from '@/components/auth/auth.vue'
import { doPostLogout } from '@/apis/sign/index'

type GenderItem = {
  text: string
  value: GenderType
}
type DateObj = {
  day: number // 选择了哪一天
  days: number // 这个月份有多少天
  isToday: boolean // 选择的日期是否今天
  month: number // 选择的月份
  result: string // 选择的日期整体值
  week: string // 选择日期所属的星期数
  year: number // 选择的年份
}

const $userStore = useUserStore()
const showName = ref(false)
// @ts-ignore
const uploadUrl = import.meta.env.VITE_UPLOAD_URI
const modalData = reactive({
  show: false,
  title: '昵称',
  gender: 'UNKNOWN' as GenderType,
  onConfirm: () => {
    if (modalData.title === '昵称') {
      // 修改昵称
      if (updateUserNames.value) {
        updateUserName.value = updateUserNames.value
        showName.value = false
        updateUserInfo()
        return
      }
      uni.showToast({ title: '请输入昵称', icon: 'none' })
    }
    updateUserInfo()
  },
  cancelFn: () => {
    if (modalData.title === '昵称') updateUserNames.value = updateUserName.value
  },
})
watch(
  () => modalData.show,
  (val) => {
    if (modalData.show) updateUserNames.value = updateUserName.value
  },
)

// 退出登录模态框数据
const logOutData = reactive({
  show: false,
})
const updateBirthday = ref('')
const avatarimg = ref('')
const calendarShow = ref(false)
const genderList: GenderItem[] = [
  { text: '男', value: 'MALE' },
  { text: '女', value: 'FEMALE' },
  { text: '未知', value: 'UNKNOWN' },
]
const updateUserName = ref('')
const updateUserNames = ref('')
const $settingStore = useSettingStore()
const updateUserGender = ref('暂不设置')

onLoad(() => {
  const info: any = $userStore.getterUserInfo?.info || {}
  if ($userStore.getterToken) {
    const { avatar, nickname, gender, birthday } = info
    avatarimg.value = avatar
    updateUserName.value = nickname
    updateUserNames.value = nickname
    modalData.gender = gender
    updateBirthday.value = birthday
    updateUserGender.value = genderList.filter((item) => {
      return item.value === modalData.gender
    })[0].text
  }
})

const handleModal = (e: string) => {
  modalData.show = true
  modalData.title = e
}

// 选择性别
const handleChooseGender = (item: GenderItem) => {
  modalData.gender = item.value
}
// 选择生日
const handleChooseCalendar = (date: DateObj) => {
  const { year, month, day } = date
  updateBirthday.value = `${year}-${zeroPadding(month)}-${zeroPadding(day)}`
  updateUserInfo()
}
function zeroPadding(number: number | string) {
  return +number > 9 ? number : '0' + number
}
const handleSignOut = () => {
  logOutData.show = true
}
// 退出登录
function logout() {
  logOutData.show = false
  $userStore.DEL_TOKEN()
  uni.navigateBack()
  doPostLogout()
}
// 上传头像
const handleChooseTx = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
    sourceType: ['album'], //从相册选择
    success: (res) => {
      // uploadFile 上传本地文件
      uni.uploadFile({
        // @ts-ignore
        url: import.meta.env.VITE_BASE_URL + uploadUrl,
        filePath: res.tempFilePaths[0], //  本地文件路径
        name: 'file',
        fileType: 'image',
        formData: res.tempFilePaths,
        header: getheader(),
        success: (uploadFileRes: any) => {
          if (uploadFileRes.statusCode !== 200) {
            uni.showToast({ title: '图片上传失败', icon: 'none' })
            return
          }
          if (uploadFileRes.data) {
            avatarimg.value = JSON.parse(uploadFileRes.data).data
            updateUserInfo()
            return
          }
        },
        fail: (err) => {},
        complete: (res) => {},
      })
    },
  })
}
// 更新用户信息
function updateUserInfo() {
  doPostUserData(updateUserName.value, modalData.gender, avatarimg.value, updateBirthday.value).then(() => {
    $userStore.PUT_USER_INFO(avatarimg.value, updateUserName.value, modalData.gender, updateBirthday.value)
    updateUserGender.value = genderList.filter((item) => {
      return item.value === modalData.gender
    })[0]?.text
  })
}
</script>

<template>
  <u-cell-group>
    <u-cell-item :arrow="false" @click="handleChooseTx">
      <template #title>
        <text>头像</text>
      </template>
      <template #right-icon>
        <view style="position: relative">
          <!-- #ifdef MP-WEIXIN -->
          <view style="position: absolute; left: -60rpx; top: 50%; transform: translate(-50%, -50%)">
            <u-avatar style="width: 30rpx; height: 30rpx" :src="avatarimg" />
          </view>
          <!-- #endif -->
          <!-- #ifndef MP-WEIXIN -->
          <view style="position: absolute; left: -150%; top: 50%; transform: translate(-50%, -50%)">
            <u-avatar style="width: 50rpx; height: 50rpx" :src="avatarimg" />
          </view>
          <!-- #endif -->
          <u-icon name="arrow-up-fill" color="#000" size="18" class="icon" />
        </view>
      </template>
    </u-cell-item>
    <u-cell-item title="昵称" :arrow="false" @click="handleModal('昵称')">
      <template #right-icon>
        <text class="text">{{ updateUserName }}</text>
        <u-icon name="arrow-up-fill" color="#000" size="18" class="icon" />
      </template>
    </u-cell-item>
    <u-cell-item title="性别" :arrow="false" @click="handleModal('性别')">
      <template #right-icon>
        <text class="text">{{ updateUserGender }}</text>
        <u-icon name="arrow-up-fill" color="#000" size="18" class="icon" />
      </template>
    </u-cell-item>
    <u-cell-item title="生日" :arrow="false" @click="calendarShow = true">
      <template #right-icon>
        <text class="text">{{ updateBirthday }}</text>
        <u-icon name="arrow-up-fill" color="#000" size="18" class="icon" />
      </template>
    </u-cell-item>
  </u-cell-group>
  <!-- 修改昵称/性别 -->
  <u-modal
    v-model="modalData.show"
    :title="modalData.title"
    show-cancel-button
    :title-style="{ fontWeight: 700 }"
    :confirm-color="modalData.title === '昵称' ? '#2979ff' : '#19be6b'"
    :cancel-color="modalData.title === '昵称' ? '#606266' : '#ff9900'"
    @confirm="modalData.onConfirm"
    @cancel="modalData.cancelFn"
  >
    <view v-if="modalData.title === '昵称'" class="model_input">
      <u-input v-model.trim="updateUserNames" :border="false" maxlength="11" />
    </view>
    <view v-else style="display: flex; justify-content: space-around; margin: 25rpx 0">
      <text
        v-for="item in genderList"
        :key="item.text"
        class="gender"
        :class="{ genderActive: item.value === modalData.gender }"
        @click="handleChooseGender(item)"
      >
        {{ item.text }}
      </text>
    </view>
  </u-modal>
  <!-- 修改昵称/性别 -->
  <!-- 生日选择 -->
  <u-calendar v-model="calendarShow" mode="date" safe-area-inset-bottom @change="handleChooseCalendar"> </u-calendar>
  <!-- 生日选择 -->
  <!-- 退出登录 -->
  <u-modal
    v-model="logOutData.show"
    :show-confirm-button="false"
    title="退出登录"
    :title-style="{ color: 'rgba(16, 16, 16, 1)', fontSize: '32rpx', fontWeight: 700 }"
  >
    <view class="logOut-box">
      <text class="logOut-box__content">退出登录您将无法购买到心爱的商品</text>
      <view class="logOut-box__footer">
        <view class="logOut-box__footer--btn" @click="logout">退出</view>
        <view class="logOut-box__footer--btn logOut-box__footer--keep" @click="logOutData.show = false">保持登录</view>
      </view>
    </view>
  </u-modal>
  <view style="margin: 40rpx"> <u-button shape="circle" ripple @click="handleSignOut">退出登录</u-button> </view>
  <!-- 退出登录 -->
  <Auth />
</template>

<style scoped lang="scss">
page {
  background: #f7f7f7;
}
@include b(gender) {
  border: 1px solid #ccc;
  font-size: 14px;
  padding: 10rpx 35rpx;
  border-radius: 35rpx;
}
.genderActive {
  background: #19be6b;
  color: #fff;
}
@include b(avatar) {
  text-align: center;
  padding: 90rpx 0 48rpx 0;
  background: #fff;
  margin-bottom: 26rpx;
}
@include b(icon) {
  transform: rotate(90deg);
}
@include b(text) {
  margin-right: 15rpx;
  font-size: 26rpx;
  color: #9c9c9c;
}

@include b(logOut-box) {
  height: 200rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: center;
  @include e(title) {
  }
  @include e(content) {
    color: #fd0505;
    font-size: 28rpx;
    font-weight: 500;
  }
  @include e(footer) {
    width: 80%;
    display: flex;
    justify-content: space-evenly;
    @include m(btn) {
      padding: 10rpx 50rpx;
      border: 1px solid #ccc;
      border-radius: 15rpx;
    }
    @include m(keep) {
      background: #0f40f5;
      color: #fff;
    }
  }
}
.model_input {
  border: 1px solid #ccc;
  border-radius: 50rpx;
  margin: 20rpx 40rpx;
  padding: 0 20rpx;
}
.avatar__list {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
}
</style>
