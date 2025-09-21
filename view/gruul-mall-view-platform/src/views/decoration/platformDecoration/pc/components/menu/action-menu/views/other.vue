<script setup lang="ts">
import upload from '@/views/decoration/platformDecoration/pc/components/upload/index.vue'
import { defaultOtherData } from '../../common'
import type { FormInstance } from 'element-plus'
import type { ComponentItem } from '../../../packages/index/formModel'

const other = inject('otherData', ref(defaultOtherData()))

const getValid = inject('getValid', (_: any): void => {})

const startComponents = inject('startComponents', []) as ComponentItem[]
const endComponents = inject('endComponents', []) as ComponentItem[]

const rules = {
    couponImg: [{ required: true, message: '领券中心图片为必传项', trigger: 'change' }],
    integralImg: [{ required: true, message: '积分商城图片为必传项。', trigger: 'change' }],
    seckillImg: [{ required: true, message: '秒杀主图为必传项。', trigger: 'change' }],
}

const formRef = ref<FormInstance>()

const validate = () => {
    formRef.value?.validate()
}

getValid(validate)
</script>

<template>
    <div class="other">
        <el-scrollbar height="384" always>
            <el-form ref="formRef" :model="other" :rules="rules" :label-width="63">
                <div class="title">活动主图</div>
                <el-form-item label="领券中心" prop="couponImg">
                    <upload v-model="other.couponImg" width="203px" height="58px" :form-ref="formRef" validate-key="couponImg" />
                    <span class="text">1920*450~550</span>
                </el-form-item>

                <el-form-item label="积分商城" prop="integralImg">
                    <upload v-model="other.integralImg" width="203px" height="58px" :form-ref="formRef" validate-key="integralImg" />
                    <span class="text">1920*450~550</span>
                </el-form-item>

                <el-form-item label="秒杀主图" prop="seckillImg">
                    <upload v-model="other.seckillImg" width="203px" height="58px" :form-ref="formRef" validate-key="seckillImg" />
                    <span class="text">1920*450~550</span>
                </el-form-item>

                <div class="title">侧边栏</div>
                <el-form-item label="联系客服" class="other__switch">
                    <el-switch v-model="other.service" />
                </el-form-item>

                <el-form-item label="购物车" class="other__switch">
                    <el-switch v-model="other.car" />
                </el-form-item>

                <div class="title">固定组件</div>
                <el-form-item v-for="item in [...startComponents, ...endComponents]" :key="item.id" :label="item.label" class="other__switch">
                    <el-switch v-model="item.show" />
                </el-form-item>
            </el-form>
        </el-scrollbar>
    </div>
</template>

<style lang="scss" scoped>
@include b(other) {
    padding: 18px 13px;

    @include e(switch) {
        :deep(.el-form-item__content) {
            justify-content: flex-end;
            .el-form-item__error {
                padding-top: 0px;
                top: 96%;
            }
        }

        .el-switch {
            --el-switch-on-color: #f54319;
        }
    }
}

@include b(text) {
    color: #999;
    line-height: 24px;
}

@include b(el-form-item) {
    margin-bottom: 12px;

    :deep(.el-form-item__label) {
        font-size: 12px;
        padding-right: 6px;
    }

    :deep(.el-form-item__error) {
        padding-top: 0px;
        top: 95%;
    }
}

@include b(title) {
    font-size: 14px;
    color: #333;
    margin-bottom: 8px;
    font-weight: 700;
}

@include b(el-scrollbar) {
    margin-right: -13px;
    padding-right: 13px;
}
</style>
