<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, UploadProps } from 'element-plus'
import { Camera, CircleClose } from '@element-plus/icons-vue'
import { getheader } from '@/libs/request/returnHeader'
import { useUserStore } from '@/store/modules/user'

const $props = defineProps({
  modelValue: { type: String, default: '' },
})
const $emit = defineEmits(['update:modelValue'])

const evidence = ref<string[]>([])
const loading = ref(false)
const header = {
  Authorization: useUserStore().getterToken,
  'Device-Id': '1',
  Platform: 'PC',
  'Shop-Id': '1',
  'Client-Type': 'CONSUMER',
}
const uploadUrl = import.meta.env.VITE_BASE_URL + 'gruul-mall-carrier-pigeon/oss/upload'

const beforeImgUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/bmp']
  const isLt5M = rawFile.size < 5 * 1024 * 1024
  if (evidence.value.length >= 2) {
    ElMessage.error('上传文件数量不能超过5个！')
    return false
  }
  if (!whiteList.includes(rawFile.type)) {
    ElMessage.error('上传文件只能是 JPG,PNG,GIF,BMP 格式!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('上传文件大小不能超过 5MB!')
    return false
  }
  loading.value = true
  return true
}
const uploadImgSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
  loading.value = false
  evidence.value.push(response.data as string)
  $emit('update:modelValue', response.data)
}
</script>

<template>
  <div>
    <el-upload
      v-if="!evidence.length"
      v-loading="loading"
      :headers="header"
      :action="uploadUrl"
      :limit="1"
      :show-file-list="false"
      :before-upload="beforeImgUpload"
      :on-success="uploadImgSuccess"
    >
      <div c-w-120 c-h-40 b-1 c-bc-e31436 flex items-center justify-center c-mr-16 c-bg-fff5f7 style="float: left">
        <el-icon color="#e31436" size="28px">
          <Camera />
        </el-icon>
        <div c-ml-6 c-c-e31436>上传图片</div>
      </div>
    </el-upload>

    <div v-else class="imgBox">
      <img v-for="item in evidence" :key="item" :src="item" style="width: 100%; height: 100%" />
      <div v-show="evidence.length" class="delete" @click="evidence = []">
        <el-icon size="20">
          <CircleClose />
        </el-icon>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.imgBox {
  width: 80px;
  height: 80px;
  position: relative;
  .delete {
    position: absolute;
    right: 0;
    top: 2px;
  }
}
:deep(.el-upload) {
  float: left;
}
</style>
