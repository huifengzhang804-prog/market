<script lang="ts" setup>
import { InfoFilled } from '@element-plus/icons-vue'
import Barrage from './barrage.vue'
import Commodity from './commodity.vue'
import { type Ref, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

const formModel = reactive({
  comments: '',
})
const barrageRef: Ref<InstanceType<typeof Barrage> | null> = ref(null)
const sendBarrage = () => {
  if (!barrageRef.value) return
  if (!formModel.comments) return ElMessage.error('请输入消息')
  barrageRef.value.addBarrage(formModel.comments)
  formModel.comments = ''
}
</script>

<template>
  <div class="live">
    <div class="live__room">
      <img class="live__room--avatar" src="" />
      <div class="live__room--info">
        <span>直播间名称</span>
        <span>400 观看</span>
      </div>
    </div>
    <Barrage ref="barrageRef" />
    <Commodity />
    <div class="live__form">
      <el-form :model="formModel" :show-message="false" inline>
        <el-form-item>
          <el-input v-model="formModel.comments" />
        </el-form-item>
        <el-form-item>
          <el-icon class="icon">
            <InfoFilled />
          </el-icon>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="sendBarrage">发送</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@include b(live) {
  border: 1px solid #efefef;
  padding: 10px;
  position: relative;
  height: 100%;
  background-color: #666;
  @include e(room) {
    position: absolute;
    top: 15px;
    left: 15px;
    background-color: rgba(0, 0, 0, 0.3);
    border-radius: 15px;
    display: flex;
    padding: 10px;
    @include m(avatar) {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      flex-shrink: 0;
      margin-right: 10px;
    }
    @include m(info) {
      display: flex;
      height: 50px;
      flex-direction: column;
      justify-content: space-around;
    }
  }
  @include e(form) {
    position: absolute;
    left: 0;
    bottom: 0;
    right: 0;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #fff;
    border-top: 1px solid #efefef;
    .icon {
      font-size: 30px;
    }
  }
}

:deep(.el-form--inline .el-form-item) {
  margin-right: 0;
  margin-bottom: 0;
}

:deep(.el-form-item + .el-form-item) {
  margin-left: 5px;
}
</style>
