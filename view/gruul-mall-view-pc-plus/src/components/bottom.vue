<template>
  <div style="margin-top: 20px">
    <div v-if="bottomComponents?.bottomComponents?.[0]?.show" class="bottomServe">
      <div class="bottomServe__serve">
        <img :src="bottomComponents?.bottomComponents?.[0]?.formData?.img" />
      </div>
      <div class="bottomServe__link" />
    </div>
    <div style="background-color: #fff">
      <div v-if="bottomComponents?.bottomComponents?.[1]?.show" class="bottom">
        <div class="bottom__left">
          <div
            class="bottom__left--logo"
            :style="{
              backgroundImage: 'url(' + bottomComponents?.bottomComponents?.[1]?.formData?.logo?.img + ')',
            }"
            @click="$router.push('/home')"
          />
          <a
            v-for="(item, index) in bottomComponents?.bottomComponents?.[1]?.formData?.logo?.logoInfo"
            :key="index"
            :href="item?.link"
            class="bottom__left--text"
          >
            {{ item?.title }}
          </a>
          <div class="bottom__left--line" />
        </div>
        <div class="bottom__right">
          <div v-for="(item, index) in bottomComponents?.bottomComponents?.[1]?.formData?.module" :key="index" style="width: 176px">
            <div class="bottom__right--title">
              {{ item?.moduleName }}
            </div>
            <a v-for="(ite, inde) in item?.moduleTitle" :key="inde" :href="ite?.link" class="bottom__right--details">{{ ite?.title }}</a>
          </div>
        </div>
        <div v-if="bottomComponents?.bottomComponents?.[1]?.formData?.QRcode" class="bottom__right--code">
          <img :src="bottomComponents?.bottomComponents?.[1]?.formData?.QRcode" />
          <!-- <span>扫码下载手机端</span> -->
        </div>
      </div>
    </div>
    <div v-if="bottomComponents?.bottomComponents?.[2]?.show" class="lowest">
      <a v-for="(item, index) in bottomComponents?.bottomComponents?.[2]?.formData" :key="index" :href="item?.link">{{ item?.title }}</a>
    </div>
  </div>
</template>
<script setup lang="ts">
import Storage from '@/libs/storage'
import { usePropertiesListStore } from '@/store/modules/propertiesList'

import { storeToRefs } from 'pinia'

const { getterPropertiesList } = storeToRefs(usePropertiesListStore())
const bottomComponents = ref()
bottomComponents.value = getterPropertiesList.value
</script>
<style lang="scss" scoped>
@include b(bottomServe) {
  background-color: #fff;
  box-shadow: 0px -2px 6px 0px rgba(0, 0, 0, 0.1);
  text-align: left;
  @include e(serve) {
    width: 1920px;
    height: 96px;
    margin: 0 auto;
    img {
      width: 100%;
      height: 100%;
    }
  }
  @include e(link) {
    width: 1200px;
    height: 1px;
    background-color: #dedede;
    margin: 0 auto;
    transform: translateY(-1px);
  }
}

@include b(bottom) {
  text-align: left;
  width: 1190px;
  margin: 0 auto;
  padding-top: 32px;
  display: flex;
  padding-bottom: 47px;
  font-family: PingFang SC;
  @include e(left) {
    width: 322px;
    height: 163px;
    transform: translateY(-16px);
    @include m(logo) {
      width: 230px;
      height: 92px;
      margin-bottom: 10px;
      background-size: contain;
      background-repeat: no-repeat;
      background-position: left center;
      cursor: pointer;
    }
    @include m(text) {
      font-size: 14px;
      color: #666;
      line-height: 20px;
      display: block;
      text-decoration: none;
      margin-top: 6px;
      height: 20px;
      overflow: hidden;
      width: 322px;
    }
    @include m(line) {
      float: right;
      height: 99px;
      width: 1px;
      background-color: rgba(0, 0, 0, 0.05);
      transform: translateY(-119px);
    }
  }
  @include e(right) {
    width: 780px;
    height: 163px;
    padding-left: 60px;
    display: flex;
    color: #333;
    @include m(title) {
      font-size: 14px;
      font-weight: bold;
      line-height: 20px;
      padding-bottom: 8px;
    }
    @include m(details) {
      font-size: 12px;
      margin-top: 16px;
      line-height: 17px;
      text-decoration: none;
      display: table;
      color: #333;
    }
    @include m(code) {
      width: 88px;
      height: 88px;
      margin-top: 29px;
      img {
        width: 100%;
        height: 100%;
      }
      span {
        display: inline-block;
        font-size: 12px;
        line-height: 17px;
        margin-top: 8px;
      }
    }
  }
}
@include b(lowest) {
  width: 100%;
  background-color: #666;
  padding: 12px 0;
  text-align: center;
  font-size: 12px;
  font-weight: 300;
  a {
    color: #fff;
    line-height: 16px;
    text-decoration: none;
    margin-left: 20px;
  }
}
</style>
