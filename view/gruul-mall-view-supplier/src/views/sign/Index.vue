<template>
    <div class="container">
        <div class="container__form">
            <img :src="loginPage" class="container__form--cover" />
            <div class="container__form--main">
                <div class="logo">
                    <img :src="loginLogo" />
                    <span>供应商端</span>
                </div>
                <SignInByPhone />
            </div>
        </div>
        <footer class="container__footer">
            <a :href="copyrightInformationLink" target="blank" class="medusa__rights">{{ copyrightInformation }}</a>
            <a :href="FilingInformationLink" target="blank" style="display: flex; align-items: center">
                <img style="width: 14px" src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/gruul/20200321/gongan.jpg" />
                <span class="pointer">{{ FilingInformation }}</span>
            </a>
        </footer>
    </div>
</template>
<script lang="ts" setup>
import { queryConfigByModule } from '@/apis'
import SignInByPhone from './components/SignInByPhone.vue'
import { configurePlatform } from '@/store/modules/configurePlatform'

const configure = configurePlatform()
// 登录页logo
const loginLogo = ref()
// 登录页面背景图
const loginPage = ref()
// 版权信息
const copyrightInformation = ref('')
// 版权信息链接
const copyrightInformationLink = ref('')
// 备案信息
const FilingInformation = ref('')
// 备案信息链接
const FilingInformationLink = ref('')

const getConfigByModule = () => {
    loginLogo.value = configure.getLoginLogo
    loginPage.value = configure.getSupplierLoginPageBg
    copyrightInformation.value = configure.getCopyrightInfo
    copyrightInformationLink.value = configure.getCopyrightUrl
    FilingInformation.value = configure.getRecorderInfo
    FilingInformationLink.value = configure.getRecorderUrl
}
getConfigByModule()
</script>
<style lang="scss" scoped>
// @import '@/assets/css/sign/sign.scss';
@include b(container) {
    display: flex;
    height: 100%;
    flex-direction: column;
    justify-content: space-between;
    @include e(form) {
        flex: 1;
        padding: 0 80px;
        display: flex;
        align-items: center;
        justify-content: center;
        @include m(cover) {
            width: 35%;
        }
        @include m(main) {
            box-shadow: 0px 0px 16px rgba(7, 21, 70, 0.15);
            border-radius: 20px;
            margin-left: 100px;
            padding: 36px 80px 60px;
            background-color: #fff;
            @include b(logo) {
                display: flex;
                justify-content: center;
                align-items: flex-end;
                img {
                    width: 180px;
                }
                span {
                    margin-left: 100px;
                    color: #ff794d;
                    font-size: 24px;
                    flex-shrink: 0;
                }
            }
        }
    }
    &__footer {
        display: flex;
        flex-direction: column;
        align-items: center;
        font-size: 12px;
        line-height: 21px;
        color: #999;
        padding-bottom: 20px;
        cursor: pointer;
        a {
            color: #999;
        }
    }
}
</style>
