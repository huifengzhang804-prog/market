<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { ElMessageBox } from 'element-plus'
import ModifySku from './modify-sku.vue'
import addCommodity from './add-commodity.vue'

const searchOptions = reactive({
  keyword: '',
})
const handleRemove = (row?: any) => {
  ElMessageBox.confirm('请确认是否从直播商品中移出？？？')
}
const showSkuDialog = ref(false)
const showAddDialog = ref(false)
</script>

<template>
  <div class="commodity">
    <div class="commodity__title">直播宝贝</div>
    <el-form :model="searchOptions" :show-message="false" inline>
      <el-form-item>
        <el-input v-model="searchOptions.keyword" placeholder="请输入关键词" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary">搜索</el-button>
      </el-form-item>
    </el-form>
    <div class="commodity-list">
      <div class="commodity-list__container">
        <div class="commodity-list__container--img">
          <img src="" />
          <div class="count">500</div>
        </div>
        <div class="commodity-list__container--info">
          <div class="title">商品名称商品名称商品名称商品商品商品商品商品商品商品名称称称...</div>
          <div class="price">
            <span class="currency">￥</span>
            <span>5.80</span>
          </div>
        </div>
      </div>
      <div class="commodity-list__operation">
        <el-button-group>
          <el-button type="primary" @click="showSkuDialog = true">改价</el-button>
          <el-button type="danger" @click="handleRemove">移出</el-button>
          <el-button type="warning">讲解</el-button>
        </el-button-group>
      </div>
    </div>
    <div class="commodity-add">
      <el-button type="primary" @click="showAddDialog = true">添加商品</el-button>
    </div>
  </div>
  <el-dialog v-model="showSkuDialog" :close-on-click-modal="false" destroy-on-close title="改价格">
    <ModifySku />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showSkuDialog = false">取消</el-button>
        <el-button type="primary" @click="showSkuDialog = false">确认</el-button>
      </span>
    </template>
  </el-dialog>
  <el-dialog v-model="showAddDialog" :close-on-click-modal="false" destroy-on-close title="选择商品">
    <add-commodity />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="showAddDialog = false">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
@include b(commodity) {
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

  @include b(commodity-list) {
    padding: 15px 0;
    border-bottom: 1px solid #efefef;

    @include e(container) {
      display: flex;
      overflow: hidden;

      @include m(img) {
        width: 70px;
        height: 70px;
        position: relative;

        .count {
          width: 60px;
          position: absolute;
          bottom: 10px;
          left: 5px;
          right: 5px;
          background-color: rgba(255, 255, 255, 0.6);
          height: 22px;
          line-height: 22px;
          text-align: center;
          color: #f00;
          border-radius: 10px;
        }
      }

      img {
        width: 70px;
        height: 70px;
        flex-shrink: 0;
        border-radius: 10px;
      }

      @include m(info) {
        margin-left: 8px;
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .title {
          text-overflow: -o-ellipsis-lastline;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          line-clamp: 2;
          -webkit-box-orient: vertical;
          font-size: 14px;
          line-height: 20px;
        }

        .price {
          color: #f00;
          font-weight: 600;

          .currency {
            font-size: 1.2em;
          }
        }
      }
    }

    @include e(operation) {
      margin-top: 15px;
      text-align: right;
    }
  }
  @include b(commodity-add) {
    padding: 15px;
    button {
      width: 100%;
    }
  }
}
</style>
