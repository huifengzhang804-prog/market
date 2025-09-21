<template>
    <div
        class="user"
        :style="{
            overflowY: 'scroll',
            left: '0px',
        }"
    >
        <div class="user__radius" :style="headStyleConfig.headBackGround">
            <!-- <top-bar /> -->
            <div class="center">个人中心</div>
            <div class="info">
                <div>
                    <img
                        src="https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIj8fkACXBjG5oxepn1slUQm5ibBia0PvZPdo7Gib583vmDKuYxQOkuWsAd1eotA6fbLXMYiaXAdgRu5A/132"
                        class="info__avatar"
                        alt="info"
                    />
                    <div class="info__name" :style="{ color: $props.formData.headStyle === 2 ? headStyleConfig.infoColor : '' }">元宵汤圆水饺</div>
                </div>
                <div>
                    <QIcon
                        v-if="$props.formData.scanCode"
                        name="icon-saoma"
                        :color="headStyleConfig.scanCodeColor"
                        style="margin-right: 16px"
                        size="24px"
                    />
                    <QIcon
                        v-if="$props.formData.membershipCode"
                        name="icon-fenxiaoma"
                        :color="headStyleConfig.membershipCodeColor"
                        style="margin-right: 10px"
                        size="24px"
                    />
                </div>
                <!-- <div
            @click="pickCodeClickHandle"
            class="info__qrcode"
            v-if="formData.codeStyle === 2 && formData.qrcodeVisible"
          >
          </div> -->
            </div>
            <div class="data" :style="{ color: headStyleConfig.infoColor }">
                <div class="data__item">
                    <div class="data__number">60.30</div>
                    <div class="data__text">储值</div>
                </div>
                <div>｜</div>
                <div class="data__item">
                    <div class="data__number">1024</div>
                    <div class="data__text">返利</div>
                </div>
                <div>｜</div>
                <div class="data__item">
                    <div class="data__number">50</div>
                    <div class="data__text">收藏</div>
                </div>
                <div>｜</div>
                <div class="data__item">
                    <div class="data__number">100</div>
                    <div class="data__text">足迹</div>
                </div>
            </div>
            <div v-if="$props.formData.hideCartInlet" class="radius">
                <div class="radius__inner" :style="{ backgroundColor: headStyleConfig.cardColor }">
                    <div class="radius__inner--item" :style="{ color: headStyleConfig.textColor }">
                        <span class="radius__inner--text">尊享会员</span>
                        <span class="radius__inner--split">|</span>
                        <span class="radius__inner--text" :style="{ color: headStyleConfig.textColor }">{{ formData.getCartText }}</span>
                    </div>
                    <span
                        class="radius__inner--item radius__inner--btn"
                        :style="{
                            backgroundColor: headStyleConfig.activateNowBtnColor,
                            color: headStyleConfig.activateNowColor,
                        }"
                        >立即开通</span
                    >
                </div>
            </div>
        </div>
        <div class="user__cover">
            <div class="order">
                <div class="order__all">
                    <div class="order__all--me">我的订单</div>
                    <div class="order__all--check">
                        查看全部订单
                        <span>></span>
                    </div>
                </div>
                <div class="order__quick">
                    <div v-for="item in orderInfo" :key="item.id" class="order__quick--item">
                        <img :src="item.url" class="order__quick--img" alt="waitIcon" />
                        <div class="order__quick--text">
                            {{ item.name }}
                        </div>
                    </div>
                </div>
            </div>
            <div class="user__menu">
                <div v-if="$props.formData.menuStyle === 1" class="user__menu--expand">
                    <Menu>
                        <block v-for="expandItem in $props.formData.menuList" :key="expandItem.menuName">
                            <menu-item
                                v-if="expandItem.showMenu && expandItem.menuName"
                                :class="{ splitFlag: expandItem.splitFlag }"
                                :img-url="expandItem.menuIconUrl"
                                >{{ expandItem.menuName }}
                            </menu-item>
                            <view v-if="!expandItem.menuName" style="display: block; height: 15px"> </view>
                        </block>
                    </Menu>
                </div>
                <GridMenu v-if="formData.menuStyle === 2" :grid-menu="$props.formData.menuScratchable"></GridMenu>
                <div class="version">
                    <!-- <div class="version__img">
                        <img class="miniBottomLog" :src="miniBottomLog" alt="miniBottomLog" />
                    </div> -->
                    <div class="version__text">启山智软商城系统{{ version }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import Menu from './components/menu.vue'
import MenuItem from './components/menuItem.vue'
import GridMenu from './components/gridMenu.vue'
import userCenterDefaultData from './user-center'
import type { UserCenterType } from './user-center'
import type { PropType } from 'vue'
const $props = defineProps({
    formData: {
        type: Object as PropType<UserCenterType>,
        default: userCenterDefaultData,
    },
})
const headStyleConfig = computed(() => {
    return {
        headBackGround:
            $props.formData.headStyle === 1
                ? 'background-color: rgb(254, 78, 99)'
                : `background-image: url(${$props.formData.customStyle.backgroundImage})`,
        cardColor: $props.formData.headStyle === 1 ? '#45403C' : $props.formData.customStyle.cardColor,
        textColor: $props.formData.headStyle === 1 ? '#E4CB98' : $props.formData.customStyle.textColor,
        membershipCodeColor: $props.formData.headStyle === 1 ? '#2c2c2c' : $props.formData.customStyle.membershipCodeColor,
        scanCodeColor: $props.formData.headStyle === 1 ? '#2c2c2c' : $props.formData.customStyle.scanCodeColor,
        activateNowColor: $props.formData.headStyle === 1 ? '#45403C' : $props.formData.customStyle.activateNowColor,
        activateNowBtnColor: $props.formData.headStyle === 1 ? '#E4CB98' : $props.formData.customStyle.activateNowBtnColor,
        infoColor: $props.formData.headStyle === 1 ? '#2c2c2c' : $props.formData.customStyle.infoColor,
    }
})
const orderInfo = computed(() => {
    return $props.formData.orderInfo
})
const miniBottomLog = 'https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200407/logo.png'
const version = '1.0.0'
</script>

<style lang="scss" scoped>
@import '@/assets/css/decoration/userCenter.scss';
</style>
