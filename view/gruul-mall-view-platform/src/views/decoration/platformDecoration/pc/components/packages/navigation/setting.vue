<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import { VueDraggableNext } from 'vue-draggable-next'
import { ElMessage } from 'element-plus'
import navigation, { getDefault } from './navigation'
import { Plus } from '@element-plus/icons-vue'
import useGetPageData from '@/views/decoration/platformDecoration/pc/components/menu/action-menu/views/custom-page/useGetPageData'
import type { Navigation } from './navigation'
import type { PropType } from 'vue'
import { PlatformPageVO } from '@/apis/decoration/type'
import { L } from '@/apis/http.type'

const props = defineProps({
    formData: {
        type: Object as PropType<Navigation[]>,
        default: navigation,
    },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)
const { getData } = useGetPageData()
/**
 * 删除
 */
const del = (index: number) => {
    formData.value.splice(index, 1)
}

/**
 * 添加
 */
const add = () => {
    formData.value.push(getDefault())
}

const options = ref<{
    system: { name: string; id: string }[]
    activity: { name: string; id: string }[]
    custom: { name: string; id: string }[]
}>({
    system: [
        { name: '所有分类', id: '所有分类' },
        { name: '首页', id: '首页' },
        { name: '所有商品', id: '所有商品' },
        { name: '秒杀', id: '秒杀' },
        { name: '领券中心', id: '领券中心' },
        { name: '积分商城', id: '积分商城' },
    ],
    activity: [],
    custom: [],
})

/**
 * 校验函数
 */
const validate = () => {
    return formData.value.every((item) => {
        const { link, text } = item
        if (!link || !text) {
            ElMessage.error('当前导航栏未填写完整！')
            return false
        }
        return true
    })
}

onBeforeMount(() => {
    getOptions()
})

const getOptions = async () => {
    const activityData = (await getData('activity', 1, 9999)) as L<PlatformPageVO>
    const textData = (await getData('text', 1, 9999)) as L<PlatformPageVO>

    options.value.custom = textData.records
    options.value.activity = activityData.records
}

defineExpose({
    formRef: { validate },
})
</script>

<template>
    <div class="navigation-form">
        <div class="navigation-form__add">
            <el-button :icon="Plus" type="primary" :disabled="formData.length > 7" @click="add">添加</el-button>
            最多添加8个
        </div>

        <!-- 导航栏 -->
        <div class="m-t-12">
            <!-- 表头 -->
            <div class="navigation-form__header">
                <div class="navigation-form__header-name">展示名称</div>
                <div class="navigation-form__header-link">链接至</div>
                <div class="navigation-form__header-operate">操作</div>
            </div>

            <!-- 主体 -->
            <vue-draggable-next :list="formData" handle=".navigation-form__item-icon">
                <div v-for="(item, i) in formData" :key="i" class="navigation-form__item">
                    <!-- 拖动按钮 -->
                    <div class="navigation-form__item-icon cp">
                        <q-icon name="icon-svg1" size="28px" class="cp"></q-icon>
                    </div>

                    <!-- 展示名称 -->
                    <div class="navigation-form__item-name">
                        <el-input v-model="item.text" placeholder="请输入显示名称" maxlength="5" clearable />
                    </div>

                    <!-- 链接选项 -->
                    <div class="navigation-form__item-link">
                        <el-select v-model="item.type" placeholder="请选择类型" style="width: 122px" @change="item.link = ''">
                            <el-option label="系统页面" value="system" />
                            <el-option label="活动页面" value="activity" />
                            <el-option label="图文页面" value="custom" />
                        </el-select>

                        <el-select v-model="item.link" placeholder="请选择链接" style="width: 122px" :disabled="!item.type">
                            <el-option v-for="ite in options[item.type]" :key="ite.id" :label="ite.name" :value="ite.id" />
                        </el-select>
                    </div>

                    <!-- 删除按钮 -->
                    <div class="navigation-form__item-operate">
                        <q-icon v-show="formData.length > 1" name="icon-shanchu2" class="cp" size="18px" color="#E90000" @click="del(i)" />
                    </div>
                </div>
            </vue-draggable-next>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(navigation-form) {
    height: 530px;
    @include e(add) {
        display: flex;
        align-items: center;
        justify-content: space-between;
        color: #999;
        font-size: 12px;
    }

    @include e(header) {
        height: 50px;
        padding: 15px 0;
        background-color: #00000005;
        border: 1px solid #0000000f;
        border-radius: 2px;
        color: #333333;
        display: flex;
        font-size: 13px;
        font-weight: 500;
    }

    @include e(header-name) {
        padding-left: 54px;
        width: 171px;
    }

    @include e(header-link) {
        width: 274px;
        padding-left: 10px;
    }

    @include e(header-operate) {
        width: 43px;
        text-align: center;
    }

    @include e(item) {
        display: flex;
        align-items: center;
        height: 48px;
        border: 1px solid #0000000f;
        border-top: 0px;
    }

    @include e(item-icon) {
        width: 43px;
        text-align: center;

        &:hover {
            color: #409eff;
        }
    }

    @include e(item-name) {
        padding: 0 10px;
        width: 128px;
    }

    @include e(item-link) {
        padding: 0 10px;
        width: 274px;
        display: flex;
        justify-content: space-between;
    }

    @include e(item-operate) {
        width: 43px;
        text-align: center;
    }
}
</style>
