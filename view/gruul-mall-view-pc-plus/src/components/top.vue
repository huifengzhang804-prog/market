<script setup lang="ts">
import { ref } from 'vue'
import SliderCaptcha from '../components/slide-captcha/SliderCaptcha.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ShoppingCart, Lock, UserFilled, Key } from '@element-plus/icons-vue'
import { MOBILE } from '@/utils/RegexPool'
import { useUserStore } from '@/store/modules/user'
import { doGetCaptchaSlider, doPostSmsCode, doSignByUser } from '@/apis/sign'
import { doGetUserData } from '@/apis/afs'
import { doGetCategory, doGetOpeningUp } from '@/apis/platform'
import { useSearchBykeyword } from '@/store/modules/search'
import { useCardInfo } from '@/store/modules/cart'
import QIcon from '@/components/q-icon/q-icon.vue'
import { toNumber } from 'lodash-es'
import { GrantType } from '@/apis/sign/index.type'
import { doPostLogout } from '@/apis/sign'
import { usePropertiesListStore } from '@/store/modules/propertiesList'

import { storeToRefs } from 'pinia'
import { useRouterNewWindow } from '@/utils/useRouter'

const { getterPropertiesList } = storeToRefs(usePropertiesListStore())
const { openNewWindow } = useRouterNewWindow()
const $userStore = useUserStore()
const route = useRoute()
const $router = useRouter()
const searchContent = ref('')
const tel = ref('')
const code = ref()
// const signFlag = ref(false)
const showSliderCaptcha = ref(false)
const categorylist = <any>ref([])
const showcategory = ref(false)
const categoryindex = ref(-1)
const isLogin = $userStore.userInfo.token && $userStore.userInfo.info.nickname
const $cardInfo = useCardInfo()
let interval: NodeJS.Timeout | null = null
const Time = ref('')
const captchaList = ref()

getClassList()
initCard()

const noNeedTopPath = computed(() => {
  return (
    route.path !== '/settlement' &&
    route.path !== '/pay' &&
    route.path !== '/personalcenter/order/orderdetail' &&
    route.path !== '/personalcenter/order/aftersalesdetail' &&
    route.path !== '/personalcenter/order/writecomments' &&
    route.path !== '/personalcenter/order/applyaftersales' &&
    route.path !== '/personalcenter/order/applyaftersalesservice' &&
    route.path !== '/integralConfirm' &&
    route.path !== '/personalcenter/set/customerservice'
  )
})

const noNavTopPath = computed(() => {
  return (
    route.path !== '/personalcenter/order/myorder' &&
    route.path !== '/personalcenter/order/aftersales' &&
    route.path !== '/personalcenter/order/comment' &&
    route.path !== '/personalcenter/follow/goodscollection' &&
    route.path !== '/personalcenter/order/shopcollection' &&
    route.path !== '/personalcenter/assets/balance' &&
    route.path !== '/personalcenter/assets/integral' &&
    route.path !== '/personalcenter/assets/mycoupon' &&
    route.path !== '/personalcenter/set/information' &&
    route.path !== '/personalcenter/set/address' &&
    route.path !== '/personalcenter/follow/footprint'
  )
})

const shopToPath = computed(() => !route.path.includes('/shop'))

/**
 * 获取验证码
 */
const getCodeHandle = async () => {
  if (!MOBILE.test(tel.value)) {
    ElMessage.error('手机号有误')
    return
  }
  getCaptchaSlider()
}

const getCaptchaSlider = async () => {
  const res = await doGetCaptchaSlider(tel.value, 'LOGIN')
  if (res.code !== 200) {
    return
  }
  if (res.data?.captcha) {
    captchaList.value = res.data.captcha
    showSliderCaptcha.value = true
  } else {
    code.value = res.data?.smsCode
  }
}

/**
 * 登录
 */
const signHandle = async () => {
  if (!tel.value) return ElMessage.error('请输入手机号')
  if (!code.value) return ElMessage.error('请输入验证码')
  try {
    const {
      code: codes,
      msg,
      data,
    } = await doSignByUser(GrantType.SMS_CODE, {
      mobile: tel.value,
      code: code.value,
    })
    if (codes !== 200) return ElMessage.error(msg)
    $userStore.addToken(data?.value, data.refreshToken.expiresIn)
    if (data?.refreshToken) {
      $userStore.addRefreshToken(data.refreshToken.value, data.refreshToken.expiresIn as number)
    }
    $userStore.loginTypeFalse()
    await getUserInfo()
    ElMessage.success('登录成功')
    $cardInfo.INIT_CARD()
  } catch (e) {
    ElMessage.error('登录失败')
  }
}
/**
 * @description: 未登录清空购物车缓存
 * @returns {*}
 */
function initCard() {
  if (!isLogin) {
    $cardInfo.DEL_CARD()
  }
}
/**
 *
 * 获取分类数据
 */
async function getClassList() {
  const { data, code, msg } = await doGetCategory({ size: 12 })
  if (code !== 200) {
    return ElMessage.error(msg || '分类数据获取失败')
  }
  categorylist.value = data.records
}
const gotomyorder = () => {
  if (!$userStore.getterToken) {
    $userStore.loginTypeTrue()
    return
  }
  $router.push({
    path: '/personalcenter/order/myorder',
    query: {},
  })
}
/* *商家入驻 */
const goMerchantEnter = () => {
  if (!$userStore.getterToken) {
    $userStore.loginTypeTrue()
    return
  } else {
    openNewWindow('/merchantEnter/merchantEnter')
  }
}
const gotoshoppingcart = () => {
  if (!$userStore.getterToken) return $userStore.loginTypeTrue()
  $router.push({
    path: '/shoppingcart',
    query: {},
  })
}
const handlemouseleave = () => {
  showcategory.value = false
  categoryindex.value = -1
}
const success = (val: any) => {
  let countdownTime = 60 * 1000
  interval = setInterval(() => {
    // 倒计时结束
    if (countdownTime < 1000) {
      if (interval) {
        clearInterval(interval)
      }
      Time.value = ''
      return
    }
    countdownTime = countdownTime - 1000
    let time
    let seconds = parseInt(((countdownTime % (1000 * 60)) / 1000).toString())
    time = '00:' + (seconds < 10 ? '0' + seconds : seconds)
    Time.value = time
  }, 1000)
  ElMessage.success('发送成功')
  showSliderCaptcha.value = false
  code.value = val.data
}
const gotoclassification = (Info: any, firstid?: any, firstname?: any, secondid?: any, secondname?: any) => {
  const { id, level, name, parentId } = Info
  $router.push({
    path: '/classification',
    query: { id, level, name, parentId, firstid, firstname, secondid, secondname },
  })
}
const showSearchVal = ref(false)
const gotosearch = () => {
  showSearchVal.value = true
  $router.push({
    path: '/searchPage',
  })
  // 存搜索关键字
  useSearchBykeyword().SET_KEYWORD(searchContent.value || ' ', selectValue.value, showSearchVal.value)
}
/**
 * @description: 获取用户消息
 */
async function getUserInfo() {
  const { data, code, msg } = await doGetUserData()
  if (code !== 200) return ElMessage.error(`${msg ? msg : '获取用户消息失败'}`)
  $userStore.addUserInfo(data)
}
const handleSignOut = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    code.value = ''
    $userStore.delToken()
    $userStore.delUserInfo()
    $cardInfo.DEL_CARD()
    $router.replace('/')
    doPostLogout()
  })
}
// 搜索框
const options = [
  {
    value: 'product',
    label: '商品',
  },
  {
    value: 'shop',
    label: '店铺',
  },
]
const navigationTitleval = ref<string>('')
const navigationTitleLists = {
  所有分类: '',
  首页: '/home',
  所有商品: '/classification',
  秒杀: '/seckill',
  领券中心: '/discountCoupon',
  积分商城: '/integralShop',
  id: '/activityClassIfication',
} as any
const navigationTitlId = ref<number>()
const navigationTitleFn = (item: { id?: number; link: string; text?: number; type: 'system' | 'activity' | 'custom' }, index?: number) => {
  navigationTitlId.value = item?.id || 0
  if (navigationTitleLists[item?.link] === '/discountCoupon' && !$userStore.getterToken) {
    $userStore.loginTypeTrue()
  }

  if (item?.type !== 'system')
    $router.push({
      path: navigationTitleLists['id'],
      query: { link: item?.link, type: item?.type },
    })
  else
    $router.push({
      path: navigationTitleLists[item?.link] || '/',
    })
}
watch(
  () => route.path,
  (NewVal, oldVal) => {
    navigationTitleval.value = NewVal
    if (NewVal !== '/home') useSearchBykeyword().showSearchVal = false
    if (NewVal === '/shoppingcart') {
      selectValue.value = 'product'
      shoppingcartPath.value = true
    } else shoppingcartPath.value = false
  },
)
const selectValue = ref<'product' | 'shop'>('product')
const shoppingcartPath = ref(false)

navigationTitleval.value = route.path
if (route.path === '/') navigationTitleval.value = '/home'
if (route.path !== '/home') useSearchBykeyword().showSearchVal = false
else searchContent.value = ''
if (route.path === '/shoppingcart') {
  selectValue.value = 'product'
  shoppingcartPath.value = true
} else shoppingcartPath.value = false

const topComponents = ref()
topComponents.value = getterPropertiesList.value
navigationTitlId.value = toNumber(route.query?.id)

const goToHome = () => {
  useSearchBykeyword().showSearchVal = false
  navigationTitleFn({ link: '首页', type: 'system' })
}
</script>

<template>
  <div>
    <!-- 状态栏 -->
    <div v-if="route.path !== '/pay' && route.path !== '/personalcenter/set/customerservice'" class="topinfo">
      <div class="content">
        <div v-if="topComponents?.topComponents?.[0]?.show" class="top">
          <div>{{ topComponents?.topComponents?.[0]?.formData?.message }}</div>
          <div class="top__info">
            <div v-if="$userStore.userInfo.token && $userStore.userInfo.info.nickname">
              <el-dropdown>
                <div class="cursor" style="color: #fff; font-size: 12px">{{ $userStore.userInfo.info.nickname }}</div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="handleSignOut"> 退出登录 </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <div v-else class="cursor" @click="$userStore.loginTypeTrue()">登录/注册</div>
            <div class="top__info--userinfo" @click="gotomyorder">个人中心</div>
            <div
              v-if="topComponents?.topComponents?.[0]?.formData?.settledIn"
              class="top__info--userinfo"
              style="cursor: pointer"
              @click="goMerchantEnter"
            >
              商家入驻
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 搜索栏 -->
    <div v-if="noNeedTopPath" class="searchInfo">
      <div class="content">
        <div v-if="topComponents?.topComponents?.[1]?.show" class="search">
          <div style="display: flex; align-items: center">
            <div
              class="cursor search__logo"
              :style="{
                backgroundImage: 'url(' + topComponents?.topComponents?.[1]?.formData?.logo + ')',
              }"
              @click="goToHome"
            ></div>
            <div v-if="route.path === '/shoppingcart'" style="color: rgb(255, 121, 77); font-size: 28px; font-weight: 600; margin-left: 5px">
              购物车
            </div>
          </div>

          <template v-if="topComponents?.topComponents?.[1]?.formData?.search">
            <div :class="!shoppingcartPath ? 'search__input' : 'shoppingcartInput'">
              <div v-if="!shoppingcartPath" class="search__input--type">
                <el-select v-model="selectValue" class="search__input--type" suffix-icon="CaretBottom">
                  <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </div>
              <el-input
                v-model.trim="searchContent"
                :placeholder="`请输入您想找的${selectValue === 'product' ? '商品' : '店铺'}`"
                @keyup.enter="gotosearch"
              >
              </el-input>
              <div class="cursor search__input--btn" :style="navigationTitleval === '/shoppingcart' ? 'width: 124px' : ''" @click="gotosearch">
                <QIcon name="icon-sousuo2" size="18px" color="#fff" />&nbsp; 搜索
              </div>
            </div>
          </template>
          <div
            v-if="!shoppingcartPath"
            class="search__cart cursor"
            :style="topComponents?.topComponents?.[1]?.formData?.car ? '' : 'visibility: hidden'"
            cursor-pointer
            @click="gotoshoppingcart"
          >
            <div class="search__cart--icon">
              <QIcon name="icon-gouwuche1" size="24px" color="#fff" />
            </div>
            <div class="search__cart--text">购物车</div>
            <el-badge v-if="$cardInfo.count" :max="99" class="search__cart--num" :value="$cardInfo.count"></el-badge>
          </div>
        </div>
      </div>
    </div>
    <!-- 导航栏 -->
    <div
      v-if="noNeedTopPath && shopToPath && topComponents?.topComponents?.[2]?.show && noNavTopPath"
      :class="!shoppingcartPath ? 'navigation' : 'shoppingcartNavigation'"
    >
      <div class="navigation__title">
        <div
          v-for="(item, index) in topComponents?.topComponents?.[2]?.formData"
          :key="index"
          class="navigation__title--nav"
          :style="
            navigationTitleLists[item?.link] === navigationTitleval ||
            (item?.type !== 'system' && navigationTitleLists['id'] === navigationTitleval && route.query?.link === item?.link)
              ? 'font-weight: bold; color: #F54319'
              : ''
          "
        >
          <template v-if="item?.link !== '所有分类'">
            <span class="cursor" @click="navigationTitleFn(item, index)">{{ item?.text }}</span>
            <div style="height: 0px; transform: translateY(-8px)">
              <QIcon
                v-if="
                  navigationTitleLists[item?.link] === navigationTitleval ||
                  (item?.type !== 'system' && navigationTitleLists['id'] === navigationTitleval && route.query?.link === item?.link)
                "
                name="icon-xuanzhongzhuangtai"
                size="20px"
                style="display: inline-block; transform: translateY(3px)"
              />
            </div>
          </template>
          <div v-else-if="item?.link === '所有分类'" class="navigation__class" @mouseleave="handlemouseleave()" @mouseenter="showcategory = true">
            <QIcon name="icon-fenlei1" size="24px" color="#fff" style="display: inline-block; transform: translateY(3px)" />
            <span class="navigation__class--text">{{ item?.text }}</span>
            <div v-if="showcategory" class="navigation__class--showcategory">
              <div class="navigation__class--categorylistBox">
                <div
                  v-for="(first, indexs) in categorylist"
                  :key="indexs"
                  class="navigation__class--categorylist"
                  @mouseenter="categoryindex = indexs"
                  @click.stop="gotoclassification(first)"
                >
                  <span class="navigation__class--categorylisttext">{{ first.name }}</span>
                  <el-icon class="navigation__class--icons"><ArrowRight /></el-icon>
                </div>
              </div>
              <div v-if="categoryindex !== -1" class="navigation__class--secondCategoryVosBox">
                <div
                  v-for="second in categorylist[categoryindex]?.secondCategoryVos"
                  :key="second.id"
                  class="navigation__class--secondCategoryVos"
                  @click.stop="gotoclassification(second, categorylist[categoryindex].id, categorylist[categoryindex].name)"
                >
                  {{ second.name }}
                  <div class="navigation__categoryThirdlyVosBox">
                    <div
                      v-for="third in second.categoryThirdlyVos"
                      :key="third.id"
                      class="navigation__categoryThirdlyVosBox--categoryThirdlyVos"
                      @click.stop="
                        gotoclassification(third, categorylist[categoryindex].id, categorylist[categoryindex].name, second.id, second.name)
                      "
                    >
                      <img style="width: 56px; height: 56px" :src="third.categoryImg" />
                      <div style="margin-left: 16px; width: 74px">{{ third.name }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 账号登录弹窗 -->
    <el-dialog v-model="$userStore.userInfo.loginType" :show-close="false" width="351px" top="287px">
      <div class="dialog">
        <div class="dialog__title">登录/注册</div>
        <div class="dialog__input">
          <el-input
            v-model="tel"
            placeholder="请输入手机号"
            :prefix-icon="UserFilled"
            maxlength="11"
            @input="
              () => {
                tel = tel.replace(/\D/g, '')
              }
            "
          />
        </div>
        <div class="dialog__input dialog__inputs">
          <el-input v-model="code" placeholder="请输入验证码" :prefix-icon="Lock" />
          <div v-if="!Time" class="dialog__inputs--getcode" @click="getCodeHandle">获取</div>
          <div v-else class="dialog__inputs--getcodetime">
            {{ Time }}
          </div>
        </div>
        <div class="dialog__handle" @click="signHandle">登录</div>
        <div class="dialog__deal">注册/登录代表同意隐私政策和服务条款</div>
      </div>
      <slider-captcha
        v-model="showSliderCaptcha"
        :scale="1"
        :do-submit="doPostSmsCode"
        :get-form="() => tel"
        :captcha-list="captchaList"
        @success="success"
      />
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
@include b(cursor) {
  cursor: pointer;
}
@include b(content) {
  width: 1200px;
  margin: 0 auto;
}
@include b(topinfo) {
  background-color: #666666;
}
@include b(searchInfo) {
  background-color: #ffffff;
}
@include b(top) {
  height: 32px;
  font-size: 12px;
  color: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  @include e(info) {
    display: flex;
    align-items: center;
    @include m(userinfo) {
      margin-left: 32px;
      cursor: pointer;
    }
    div {
      &:hover {
        color: #f54319;
      }
    }
  }
}
@include b(search) {
  height: 110px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  @include e(logo) {
    width: 220px;
    height: 88px;
    background-size: contain;
    background-repeat: no-repeat;
    background-position: left center;
  }
  @include e(input) {
    width: 620px;
    height: 42px;
    border: 1px solid rgb(245, 67, 25);
    border-radius: 2px;
    margin-left: 157px;
    overflow: hidden;
    display: flex;
    @include m(type) {
      width: 76px;
      line-height: 42px;
      :deep(.el-select__wrapper) {
        box-shadow: unset !important;
        font-size: 14px;
      }
      :deep(.el-input__wrapper) {
        .el-input__inner {
          font-size: 14px;
        }
      }
    }
    @include m(btn) {
      width: 131px;
      font-size: 16px;
      color: #ffffff;
      background: linear-gradient(90deg, rgb(245, 67, 25) 0.763%, rgb(253, 146, 36) 100%);
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
  @include e(cart) {
    width: 138px;
    height: 42px;
    line-height: 42px;
    border: 1px solid rgb(245, 67, 25);
    border-radius: 2px;
    margin-left: 65px;
    overflow: hidden;
    display: flex;
    position: relative;
    @include m(icon) {
      width: 42px;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, rgb(245, 67, 25) 0.763%, rgb(253, 146, 36) 99.237%);
    }
    @include m(text) {
      font-size: 16px;
      color: #f54319;
      margin: 0 auto;
      position: relative;
    }
    @include m(num) {
      position: absolute;
      right: 1px;
      top: -7px;
    }
  }
}
@include b(navigation) {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  background-color: #fff;
  @include e(class) {
    position: relative;
    width: 160px;
    height: 42px;
    line-height: 38px;
    font-size: 16px;
    background: linear-gradient(90deg, rgb(245, 67, 25) 0.763%, rgb(253, 146, 36) 100%);
    z-index: 9;
    margin-right: -19px;
    @include m(icon) {
      transform: translateY(6px);
    }
    @include m(text) {
      margin-left: 15px;
      color: #fff;
    }
    @include m(showcategory) {
      display: flex;
    }
    @include m(categorylistBox) {
      width: 160px;
      height: 520px;
      border-right: 1px solid #d9d9d9;
      background: rgba(255, 255, 255, 0.8);
    }
    @include m(categorylisttext) {
      display: block;
      width: 103px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    @include m(categorylist) {
      display: flex;
      // height: 20px;
      line-height: 16px;
      cursor: pointer;
      color: #333333;
      text-align: left;
      padding: 24px 20px 0;
      &:hover {
        width: 160px;
        color: #f54319;
      }
    }
    @include m(icons) {
      float: right;
      transform: translateY(2px);
    }
    @include m(secondCategoryVosBox) {
      min-width: 888px;
      height: 520px;
      color: #333333;
      padding-left: 24px;
      background: rgba(255, 255, 255, 0.8);
      overflow-y: auto;
    }
    @include m(secondCategoryVos) {
      margin: 15px 24px 24px 0;
      text-align: left;
      font-size: 16px;
      font-weight: 600;
    }
  }
  @include e(categoryThirdlyVosBox) {
    display: flex;
    @include m(categoryThirdlyVos) {
      display: flex;
      align-items: center;
      // cursor: pointer;
      margin-right: 24px;
      font-weight: normal;
      &:hover {
        color: #f54319;
      }
    }
  }
  @include e(title) {
    width: 1200px;
    white-space: nowrap;
    display: flex;
    font-size: 16px;
    @include m(nav) {
      display: flex;
      justify-content: center;
      flex-direction: column;
      height: 42px;
      margin-right: 71px;
      color: #333;
    }
  }
}

@include b(shoppingcartLogo) {
  width: 194px;
  height: 62px;
}
@include b(shoppingcartInput) {
  width: 360px;
  height: 42px;
  line-height: 42px;
  display: flex;
  border: 1px solid rgb(245, 67, 25);
}
@include b(shoppingcartTopinfo) {
  margin-bottom: 18px;
}
@include b(shoppingcartNavigation) {
  display: none;
}

// 账号登录弹窗
@include b(dialog) {
  padding: 0 24px 23px;
  @include e(title) {
    font-size: 14px;
    font-weight: 700;
    color: #333;
  }
  @include e(input) {
    border-radius: 18px;
    border: 1px solid #dce0e7;
    margin: 20px 0 12px;
  }
  @include e(inputs) {
    display: flex;
    align-items: center;
    margin-top: 9px;
    @include m(getcode) {
      width: 40px;
      margin-right: 10px;
      color: #e31436;
      cursor: pointer;
    }
    @include m(getcodetime) {
      width: 60px;
    }
  }
  @include e(handle) {
    margin-top: 16px;
    border-radius: 18px;
    font-size: 14px;
    height: 36px;
    line-height: 36px;
    text-align: center;
    background-color: #e31436;
    color: #fff;
    cursor: pointer;
  }
  @include e(deal) {
    margin-top: 10px;
    font-size: 12px;
    text-align: center;
    color: #999;
  }
}
:deep() {
  .el-dialog {
    border-radius: 10px;
    .el-dialog__header {
      margin: 0px;
      padding: 5px 6px 20px 0px;
    }

    .el-dialog__body {
      padding: 0;
    }
    .router-link-active {
      color: #e31436;
    }
    a {
      text-decoration: none;
      color: #101010;
    }
    .el-input__wrapper {
      background-color: transparent !important;
    }
    input::-webkit-input-placeholder {
      color: #d9d9d9 !important;
    }
    .el-input.is-focus {
      outline: none;
      box-shadow: none;
      border: none;
    }
    .el-input__inner {
      font-size: 16px;
      color: #333;
    }
    .el-input .el-input__wrapper.is-focus {
      box-shadow: none !important;
    }
    .el-select .el-input.is-focus .el-input__wrapper {
      border-color: #333 !important;
      box-shadow: none !important;
    }
    .el-select .el-input .el-select__caret.el-icon {
      color: #333;
    }
    .el-select .el-input__inner {
      transform: translateX(10px);
    }
    .el-badge__content--danger {
      background-color: #f54319;
      height: 14px;
      font-size: 12px;
      zoom: 0.7;
      padding: 7px 5px;
      transform: translateX(-10px);
    }
    .router-link-active {
      color: #f54319;
    }
    .el-dropdown {
      color: #fff !important;
      font-size: 12px;
      transform: translateY(4px);
    }
  }
  .el-input__wrapper {
    background-color: transparent;
    box-shadow: none !important;
  }

  .el-input__wrapper:hover {
    box-shadow: none !important;
  }

  .el-input__wrapper.is-focus {
    box-shadow: none !important;
  }
}
:deep(.top__info .el-dropdown) {
  line-height: normal;
}
:deep(.top__info .el-dropdown > div:hover) {
  color: red !important;
}
</style>
