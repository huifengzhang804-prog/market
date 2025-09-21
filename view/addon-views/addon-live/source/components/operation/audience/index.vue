<script lang="ts" setup>
import { Microphone, Mute, Wallet } from '@element-plus/icons-vue'
import { ElMessageBox, type CheckboxValueType } from 'element-plus'
import { type Ref, ref, reactive } from 'vue'

const showDialog = ref(false)
const searchOptions = reactive({
  keyword: '',
})
const currentUserIds: Ref<string[]> = ref([])
const checkedAudience: Ref<string[]> = ref([])
const checkAll = ref(false)
const isIndeterminate = ref(false)
const handleCheckAllChange = (val: CheckboxValueType) => {
  checkedAudience.value = val ? new Array(10).fill(0).map((_, index) => (index + 1).toString()) : []
  isIndeterminate.value = false
}
const handleCheckedAudienceChange = (value: CheckboxValueType[]) => {
  const checkedCount = value.length
  checkAll.value = checkedCount === 10
  isIndeterminate.value = checkedCount > 0 && checkedCount < 10
}
const handlePresentCoupon = (userIds: string[]) => {
  currentUserIds.value = userIds
  showDialog.value = true
}
const handleShutDownAudience = (userIds: string[]) => {
  ElMessageBox.confirm('请确认是否对用户禁言？？？').then(async () => {})
}
</script>

<template>
  <div class="audience">
    <div class="audience__title">观众</div>
    <el-form :model="searchOptions" :show-message="false" inline>
      <el-form-item>
        <el-input v-model="searchOptions.keyword" placeholder="请输入关键词" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-checkbox-group v-model="checkedAudience" @change="handleCheckedAudienceChange">
      <div v-for="i in 10" :key="i" class="audience__checkbox">
        <el-checkbox :label="i.toString()" />
        <span>用户昵称</span>
        <div class="audience__checkbox--icons">
          <el-icon>
            <Microphone />
          </el-icon>
          <el-icon @click="handleShutDownAudience([i.toString()])">
            <Mute />
          </el-icon>
          <el-icon @click="handlePresentCoupon([i.toString()])">
            <Wallet />
          </el-icon>
        </div>
      </div>
    </el-checkbox-group>
    <div class="audience__bottom">
      <el-checkbox v-model="checkAll" :indeterminate="isIndeterminate" @change="handleCheckAllChange">全选</el-checkbox>
      <el-button-group>
        <el-button type="primary" @click="handleShutDownAudience(checkedAudience)">批量禁言</el-button>
        <el-button type="primary" @click="handlePresentCoupon(checkedAudience)">批量送优惠券</el-button>
      </el-button-group>
    </div>
  </div>
  <el-dialog v-model="showDialog" :close-on-click-modal="false" destroy-on-close title="送优惠券" width="850px">
    <!--        <AudienceCoupon :user-ids="currentUserIds" />-->
    <div></div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="showDialog = false">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
@include b(audience) {
  border: 1px solid #efefef;
  padding: 10px;
}
</style>

<style lang="scss" scoped>
@include b(audience) {
  border: 1px solid #efefef;
  padding: 10px;

  @include e(title) {
    font-size: 20px;
    line-height: 30px;
    font-weight: 600;
    text-align: center;
  }

  .el-form--inline .el-form-item {
    margin-right: 15px;
    margin-bottom: 0;
  }

  .el-form {
    padding: 15px 0;
    border-bottom: 1px solid #efefef;
  }

  :deep(.el-checkbox-group) {
    font-size: 14px;
  }

  @include e(checkbox) {
    display: flex;
    width: 100%;
    justify-content: space-between;
    align-items: center;

    :deep(.el-checkbox__label) {
      display: none;
    }

    span {
      flex: 1;
      color: #000;
      margin-left: 8px;
    }

    @include m(icons) {
      .el-icon {
        color: #000;
        font-size: 18px;
      }

      .el-icon + .el-icon {
        margin-left: 5px;
      }
    }
  }

  @include e(bottom) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid #efefef;
    padding: 10px 0 0;
  }
}
</style>
