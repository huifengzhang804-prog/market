<script setup lang="ts">
import { Obj } from '@/utils/types'
import { ref, toRef, watch, type PropType } from 'vue'
import QUpload from '@/components/q-upload/q-upload.vue'
import TitleBar from '@/components/TitleBar/TitleBar.vue'

type PropertiesType = {
  websiteForm: {
    SUPPLIER_WEB_SIT_NAME: string
    SUPPLIER_LOGIN_PAGE_BG: string
  }
  fieldsList: Record<string, Obj[]>
  changeKey: (key: string) => void
  handleChange: (key: string) => void
  viewImg: (key: string) => void
  websiteFormChange: (e: PropertiesType['websiteForm']) => void
}

const props = defineProps({
  properties: {
    type: Object as PropType<PropertiesType>,
    required: true,
  },
})
const websiteForm = ref<PropertiesType['websiteForm']>({} as PropertiesType['websiteForm'])
watch(
  () => props.properties.websiteForm,
  (val) => {
    websiteForm.value = val
  },
  {
    deep: true,
    immediate: true,
  },
)
const change = () => {
  props.properties.websiteFormChange(websiteForm.value)
}
</script>

<template>
  <el-col>
    <TitleBar name="供应商端" color="#555CFD" style="margin: 0; height: 50px; background-color: #f2f2f4" />
  </el-col>
  <el-col style="margin-top: 30px">
    <el-form-item label="网站名称" prop="SUPPLIER_WEB_SIT_NAME" class="mb-25">
      <el-input
        v-model="websiteForm.SUPPLIER_WEB_SIT_NAME"
        type="text"
        :maxlength="30"
        show-word-limit
        placeholder="请输入网站名称"
        @change="change"
      />
    </el-form-item>
  </el-col>
  <el-col :span="6">
    <el-form-item label="登录页" prop="SUPPLIER_LOGIN_PAGE_BG">
      <QUpload
        v-model:src="websiteForm.SUPPLIER_LOGIN_PAGE_BG"
        :class="[
          !websiteForm.SUPPLIER_LOGIN_PAGE_BG &&
            properties.fieldsList &&
            Object.keys(properties.fieldsList).indexOf('SUPPLIER_LOGIN_PAGE_BG') !== -1 &&
            'borderColor',
        ]"
        :isAutoUpload="true"
        :width="88"
        :height="88"
        tip="建议尺寸500*500px，大小1M以下"
        @change="properties.handleChange"
        @click="properties.changeKey('SUPPLIER_LOGIN_PAGE_BG')"
        @update:src="change"
      >
        <template v-if="websiteForm.SUPPLIER_LOGIN_PAGE_BG" #mask>
          <div class="mask">
            <i class="iconfont icon-yulan1" @click.stop="properties.viewImg(websiteForm.SUPPLIER_LOGIN_PAGE_BG)"></i>
            <i class="iconfont icon-shangchuanyunpan"></i>
          </div>
        </template>
      </QUpload>
    </el-form-item>
  </el-col>
</template>

<style lang="scss" scoped></style>
